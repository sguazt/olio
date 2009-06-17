 /*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olio.workload.loader;

import org.apache.olio.workload.util.UserName;
import org.apache.olio.workload.loader.framework.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * The tag loader.
 */
public class IDGen extends Loadable {

    // this class was created to reset ID_GEN table which is used by JPA
   
    static Logger logger = Logger.getLogger(IDGen.class.getName());
   
    
    public IDGen() {}


    public String getClearStatement() {
        return "truncate TABLE ID_GEN";
    }

    public void prepare() {
        
    }


   
    
    public void load() {
        ThreadConnection c = ThreadConnection.getInstance();
        
        //let each object insert the row in this table
        /*
        try {
            
            PreparedStatement s = c.prepareStatement(STATEMENT);
            s.setString (1, id);
            s.setString(2, tag);
            s.setInt(3, 0); // Initialize it to 0 first, count and add later.
            c.addBatch();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            LoadController.increaseErrorCount();
        }
         */
    }

    
}
