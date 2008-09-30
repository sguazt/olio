/* Copyright © 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: ThreadConnection.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package com.sun.web20.loader.framework;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wraps a java.sql.connection on a per-thread basis.
 */
public class ThreadConnection {

    private static Logger logger =
            Logger.getLogger(ThreadConnection.class.getName());

    private static ThreadLocal<ThreadConnection> resource =
            new ThreadLocal<ThreadConnection>() {
        public ThreadConnection initialValue() {
            return new ThreadConnection();
        }
    };

    private static boolean COMMIT_TX = Boolean.parseBoolean(
                                    System.getProperty("commit.tx", "true"));
    private static final List<ThreadConnection> CONNECTIONLIST =
            Collections.synchronizedList(new ArrayList<ThreadConnection>());

    public static String connectionURL;

    private Connection conn;
    private String statementText;
    private PreparedStatement statement;
    private int currentBatch;
    private boolean closed = false;

    /**
     * The batch buffer buffers the loadables to be added in a batch.
     * These have to be per-thread and the only reason they are
     * maintained by the ThreadConnection. Otherwise we need to keep
     * allocating and collecting. And since we already have the threadlocal,
     * there is no more overhead getting to it.
     */
    Loadable[] batchBuffer;

    private ThreadConnection() {
        CONNECTIONLIST.add(this);
    }

    public static ThreadConnection getInstance() {
        return resource.get();
    }

    boolean ensureConnection() {
        if (closed) {
            logger.severe("Connection used after closure!");
            Loader.increaseErrorCount();
            return false;
        }

        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(connectionURL);
                statement = null;
                statementText = null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to DB", e);
            Loader.increaseErrorCount();
            return false;
        }
        return true;
    }

    boolean resetConnection() {
        if (closed) {
            logger.severe("Connection used after closure!");
            Loader.increaseErrorCount();
            return false;
        }

        try {
            conn = DriverManager.getConnection(connectionURL);
            statement = null;
            statementText = null;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error connecting to DB", e);
            Loader.increaseErrorCount();
            return false;
        }
        return true;
    }

    public PreparedStatement prepareStatement(String statementText)
            throws SQLException {
        if (conn == null)
            ensureConnection();
        if (!statementText.equals(this.statementText)) {
            this.statementText = statementText;
            statement = conn.prepareStatement(statementText);
        }
        return statement;
    }

    public void addBatch() throws SQLException {
        statement.addBatch();
        ++currentBatch;
    }

    public void executeUpdate() throws SQLException {
        if (ensureConnection()) {
            statement.executeUpdate();
            if (COMMIT_TX)
                conn.commit();
        }
    }

    void processBatch(String name, int batchCount, Queue<Loadable> queue) {
        // First we need to save the load objects from the queue
        // so we do not loose them in case we need to retry.
        if (batchBuffer == null) {
            batchBuffer = new Loadable[Loader.BATCHSIZE];
        }
        int count = 0;
        for (; count < Loader.BATCHSIZE; count++) {
            Loadable l = queue.poll();
            if (l == null)
                break;
            batchBuffer[count] = l;
        }

        if (count == 0) // Nothing to load.
            return;

        // Then we load our objects into the DB, retrying the whole
        // saved ones in case we run into a closed connection.
        if (!ensureConnection())
            return;

        String batchName;
        if (batchCount > 0)
            batchName = "object batch " + (batchCount - count + 1) + " - " +
                                batchCount + '.';
        else
            batchName = "final " + count +  " object batch.";

        int flushed = 0;
        for (int retry = 0; retry < 2; retry++) {
            try {
                for (int i = flushed; i < count; i++) {
                    batchBuffer[i].load();

                    // Each Loadable object may load more than 1 record.
                    // So we need to check for the number of records
                    // in the batch. If it is more than batchsize, we
                    // need to flush the records, too.
                    if (currentBatch >= Loader.BATCHSIZE) {
                        flush();
                        flushed += currentBatch;
                        currentBatch = 0;
                        logger.finer(name + ": Flushed " + flushed +
                                " records in " + batchName);
                    }
                }
                if (currentBatch > 0) {
                    flush();
                    flushed += currentBatch;
                    currentBatch = 0;
                    logger.finer(name + ": Flushed final " + flushed +
                            " records in " + batchName);
                }
                logger.fine(name + ": Loaded " + batchName);
                break; // We won't retry if everything is OK.
            } catch (BatchUpdateException e) {
                if (retry == 0) {
                    resetConnection();
                    logger.log(Level.WARNING, name +
                                                ": Retry loading.", e);
                } else {
                    int[] stats = e.getUpdateCounts();
                    int successes = 0;
                    for (int stat : stats) {
                        if (stat != Statement.EXECUTE_FAILED)
                            ++successes;
                    }
                    if (successes == 0) {
                        logger.log(Level.WARNING, name +
                                ": Failed to update.", e);
                        Loader.increaseErrorCount();
                    }
                }
            } catch (SQLException e) {
                if (retry == 0) {
                    resetConnection();
                    logger.log(Level.WARNING, name + ": Retry loading.",
                                                                    e);
                } else {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    Loader.increaseErrorCount();
                }
            }
        }

        // Once we're done with this buffer, don't hold on to the objects.
        // Let them get GC'd so we don't bloat memory. Minimal CPU cost
        // for such tight loop and setting all entries to null.
        for (int i = 0; i < batchBuffer.length; i++)
            batchBuffer[i] = null;
    }

    void flush() throws SQLException {
        statement.executeBatch();
        if (COMMIT_TX)
            conn.commit();
    }


    void close() throws SQLException {
        closed = true;
        if (statement != null)
            statement.close();
        if (conn != null)
            conn.close();
    }

    static void closeConnections() {
        synchronized (CONNECTIONLIST) {
            for (ThreadConnection c : CONNECTIONLIST)
                try {
                    c.close();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    Loader.increaseErrorCount();
                }
            CONNECTIONLIST.clear();
        }
    }
}
