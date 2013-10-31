<?php
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
    
/**
 * PHP Template.
 * Author: Sheetal Patil. Sun Microsystems, Inc.
 */
?>
<?php
$date = getDate();
$this_hour = $date["hours"];
$hours = Array("01","02","03","04","05","06","07","08","09","10","11","12","13","14","15",
        "16","17","18","19","20","21","22","23","24");
for ($i=1; $i<=24; $i++) {
    if(isset($hour) && $i == $hour){
        echo '<option selected="selected" value="'.$hour.'">'.$hour.'</option>';
    }else if($i == $this_hour && !isset($hour) ){
      echo '<option selected="selected" value="'.$hours[$i-1].'">'.$hours[$i-1].'</option>';
    }else{
      echo '<option value="'.$hours[$i-1].'">'.$hours[$i-1].'</option>';
    }
}
?>
