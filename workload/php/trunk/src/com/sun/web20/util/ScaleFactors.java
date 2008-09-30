/* Copyright © 2008 Sun Microsystems, Inc. All rights reserved
 *
 * Use is subject to license terms.
 *
 * $Id: ScaleFactors.java,v 1.1.1.1 2008/09/29 22:33:08 sp208304 Exp $
 */
package com.sun.web20.util;

import java.util.logging.Logger;

/**
 * This static class all the scale factors used for loading the data and
 * load generation.
 */
public class ScaleFactors {

    /** The ratio between loaded and active users. */
    public static final int USERS_RATIO = 10;
    public static int activeUsers = -1;

    /** The total number of loaded users */
    public static int users;
    public static int events;
    public static int tagCount;

    private static Logger logger =
                        Logger.getLogger(ScaleFactors.class.getName());

    /**
     * Sets the number of users for the run/load.
     * @param userCount
     */
    public static void setActiveUsers(int userCount) {
        if (userCount < 25) {
            logger.warning("Trying to load for " + userCount + " concurrent " +
                    "users which is below the minimum of 25 users. " +
                    "Adjusting to 25 users");
            userCount = 25;
        }
        if (activeUsers == -1) {
            activeUsers = userCount;
            users = activeUsers * USERS_RATIO;
            tagCount = getTagCount(users);
            events = tagCount * 3;
        }
    }

    /**
     * From http://tagsonomy.com/index.php/dynamic-growth-of-tag-clouds/
     * "As of this writing, a little over 700 users have tagged it, with 450+
     * with 450+ unique tags, roughly two-thirds of which tags were (of course)
     * used by one and only one user."<br>
     *
     * This function uses a cumulative half logistic distribution to determine
     * the tag growth. We have tested the distribution to be close to the
     * quote above. I.e. 175 multi-user tags for 700 users. The quote above
     * gives us one-third of 450 which is 150. Close enough.
     *
     * @param users The users of the data loaded.
     * @return The tag count at this users
     */
    public static int getTagCount(int users) {
        double prob = cumuHalfLogistic(users, 10000);
        // We limit to 5000 tags
        return (int) Math.round(5000 * prob);
    }

    /**
     * The cumulative half logistic distribution itself, in the flesh!
     * @param x The value on the x axis, in this case the number of users
     * @param scale Determines the x-stretch of the curve how far it takes
     *        for the probability to converge to 1
     * @return The resulting probability or y axis
     */
    private static double cumuHalfLogistic(double x, double scale) {
        double power = -x / scale;
        return (1d - Math.exp(power)) / (1d + Math.exp(power));
    }
}
