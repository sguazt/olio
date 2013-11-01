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
if (!isset($_SESSION))
{
	session_start();
}
require_once("../etc/config.php");
$connection = DBConnection::getInstance();
$un = isset($_SESSION["uname"]) ? $_SESSION["uname"] : NULL;
$events = Events_Controller::getInstance();
$flag = true;
$url = RequestUrl::getInstance();
$page= isset($_SESSION["page"]) ? $_REQUEST['page'] : NULL;
$href = $url->getGetRequest();

if(!is_null($page)){
  $href = substr($href, 0, strrpos($href,"&"));
}
if($href==""){
  $href = "?";
}

//Start Pagination
if(!is_null($page)){
    $numPages  =isset($_SESSION["numPages"]) ? $_SESSION["numPages"] : 0;
    $_SESSION["currentpage"]=$page;
    $curr_page = $_SESSION["currentpage"];
    $prev_page = $_SESSION["currentpage"] - 1;
    $next_page = $_SESSION["currentpage"] + 1;
    $offset = ($page * 10) - 10;
    if($offset < 0) {
    $offset = 0;
    }
    if($prev_page < 0) {
    $prev_page = 1;
    }
    if($next_page >  $numPages) {
    $next_page = $numPages;
    }
}else{
    $query = "select count(*) as count From SOCIALEVENT as se,PERSON_SOCIALEVENT as ps where se.socialeventid=ps.socialeventid and se.eventtimestamp>=CURRENT_TIMESTAMP and ps.username='$un'";
    $result = $connection->query($query);
    $row = $result->getArray();
    $count = $row['count'];
    unset($result);
    $numPages  = ceil($count / 10);;
    $_SESSION["numPages"] = $numPages;
    $prev_page = 1;
    $next_page = 2;
    $curr_page = 1;
    $offset = 0;
    unset($_SESSION["currentpage"]);
}    
ob_start();
require("../views/paginate.php");
$paginateView = ob_get_clean();
//End Pagination

$upcomingEvents = $events->getUpcomingEventsForUser($un,$connection,$flag,$offset);
ob_start();
require("../views/upcomingEvents.php");
$fillContent = ob_get_clean();
require_once("../views/site.php");
?>

