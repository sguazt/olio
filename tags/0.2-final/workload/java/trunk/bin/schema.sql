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
 * 
 */

-- MySQL dump 10.13  Distrib 5.1.25-rc, for sun-solaris2.10 (sparc)
--
-- Host: localhost    Database: bpwebapp
-- ------------------------------------------------------
-- Server version	5.1.25-rc-standard-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ADDRESS`
--

DROP TABLE IF EXISTS `ADDRESS`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ADDRESS` (
  `ADDRESSID` INTEGER NOT NULL AUTO_INCREMENT,
  `STATE` varchar(25) DEFAULT NULL,
  `COUNTRY` varchar(55) DEFAULT NULL,
  `LATITUDE` double DEFAULT NULL,
  `LONGITUDE` double DEFAULT NULL,
  `CITY` varchar(55) DEFAULT NULL,
  `ZIP` varchar(12) DEFAULT NULL,
  `STREET1` varchar(55) DEFAULT NULL,
  `STREET2` varchar(55) DEFAULT NULL,
  PRIMARY KEY (`ADDRESSID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `PERSON`
--

DROP TABLE IF EXISTS `PERSON`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `PERSON` (
  `USERID` INTEGER NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(25) NOT NULL,
  `PASSWORD` varchar(25) DEFAULT NULL,
  `SUMMARY` text,
  `TELEPHONE` varchar(25) DEFAULT NULL,
  `IMAGETHUMBURL` varchar(100) DEFAULT NULL,
  `IMAGEURL` varchar(100) DEFAULT NULL,
  `FIRSTNAME` varchar(25) DEFAULT NULL,
  `LASTNAME` varchar(25) DEFAULT NULL,
  `EMAIL` varchar(90) DEFAULT NULL,
  `TIMEZONE` varchar(100) DEFAULT NULL,
  `ADDRESS_ADDRESSID` INTEGER DEFAULT NULL,
  PRIMARY KEY (`USERID`),
  KEY `FK_PERSON_ADDRESS_ADDRESSID` (`ADDRESS_ADDRESSID`),
  CONSTRAINT `FK_PERSON_ADDRESS_ADDRESSID` FOREIGN KEY (`ADDRESS_ADDRESSID`) REFERENCES `ADDRESS` (`ADDRESSID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

CREATE UNIQUE INDEX PERSON_USER_IDX  on PERSON (username);

--
-- Table structure for table `SOCIALEVENT`
--

DROP TABLE IF EXISTS `SOCIALEVENT`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `SOCIALEVENT` (
  `SOCIALEVENTID` INTEGER NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` VARCHAR(500) NOT NULL,
  `TITLE` varchar(100) DEFAULT NULL,
  `SUBMITTERUSERNAME` varchar(25) DEFAULT NULL,
  `SUMMARY` varchar(100) DEFAULT NULL,
  `TELEPHONE` varchar(20) DEFAULT NULL,
  `IMAGETHUMBURL` varchar(100) DEFAULT NULL,
  `IMAGEURL` varchar(100) DEFAULT NULL,
  `LITERATUREURL` varchar(100) DEFAULT NULL,
  `EVENTTIMESTAMP` datetime DEFAULT NULL,
  `TOTALSCORE` INTEGER DEFAULT NULL,
  `NUMBEROFVOTES` INTEGER  DEFAULT NULL,
  `DISABLED` INTEGER DEFAULT NULL,
  `CREATEDTIMESTAMP` datetime DEFAULT NULL,
  `ADDRESS_ADDRESSID` INTEGER DEFAULT NULL,
  PRIMARY KEY (`SOCIALEVENTID`),
  KEY `FK_SOCIALEVENT_ADDRESS_ADDRESSID` (`ADDRESS_ADDRESSID`),
  CONSTRAINT `FK_SOCIALEVENT_ADDRESS_ADDRESSID` FOREIGN KEY (`ADDRESS_ADDRESSID`) REFERENCES `ADDRESS` (`ADDRESSID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;
 
-- CREATE INDEX SOCIALEVENT_DATE_IDX on SOCIALEVENT (EVENTTIMESTAMP);
CREATE INDEX SOCIALEVENT_EVENTTIMESTAMP_IDX on SOCIALEVENT (eventtimestamp);
CREATE INDEX SOCIALEVENT_CREATEDTIMESTAMP_IDX on SOCIALEVENT (createdtimestamp);
CREATE INDEX SOCIALEVENT_SUBMITTERUSERNAME_IDX  on SOCIALEVENT (submitterUserName);

--
-- Table structure for table `COMMENTS_RATING`
--

DROP TABLE IF EXISTS `COMMENTS_RATING`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `COMMENTS_RATING` (
  `commentsid` INTEGER NOT NULL AUTO_INCREMENT,
  `CREATIONTIME` datetime DEFAULT NULL,
  `COMMENTS` text,
  `RATING` INTEGER DEFAULT NULL,
  `USERNAME_USERNAME` varchar(25) DEFAULT NULL,
  `SOCIALEVENT_SOCIALEVENTID` INTEGER DEFAULT NULL,
  PRIMARY KEY (`commentsid`),
  KEY `FK_COMMENTS_RATING_SOCIALEVENT_SOCIALEVENTID` (`SOCIALEVENT_SOCIALEVENTID`),
  KEY `FK_COMMENTS_RATING_USERNAME_USERNAME` (`USERNAME_USERNAME`),
  CONSTRAINT `FK_COMMENTS_RATING_USERNAME_USERNAME` FOREIGN KEY (`USERNAME_USERNAME`) REFERENCES `PERSON` (`USERNAME`),
  CONSTRAINT `FK_COMMENTS_RATING_SOCIALEVENT_SOCIALEVENTID` FOREIGN KEY (`SOCIALEVENT_SOCIALEVENTID`) REFERENCES `SOCIALEVENT` (`SOCIALEVENTID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


/*!40000 ALTER TABLE `COMMENTS_RATING` DISABLE KEYS */;
/*!40000 ALTER TABLE `COMMENTS_RATING` ENABLE KEYS */;

--
-- Table structure for table `ID_GEN`
--

DROP TABLE IF EXISTS `ID_GEN`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `ID_GEN` (
  `GEN_KEY` varchar(50) NOT NULL,
  `GEN_VALUE` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`GEN_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `INVITATION`
--

DROP TABLE IF EXISTS `INVITATION`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `INVITATION` (
  `INVITATIONID` INTEGER NOT NULL AUTO_INCREMENT,
  `ISACCEPTED` tinyint(1) DEFAULT '0',
  `REQUESTOR_USERNAME` varchar(25) DEFAULT NULL,
  `CANDIDATE_USERNAME` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`INVITATIONID`),
  KEY `FK_INVITATION_CANDIDATE_USERNAME` (`CANDIDATE_USERNAME`),
  KEY `FK_INVITATION_REQUESTOR_USERNAME` (`REQUESTOR_USERNAME`),
  CONSTRAINT `FK_INVITATION_REQUESTOR_USERNAME` FOREIGN KEY (`REQUESTOR_USERNAME`) REFERENCES `PERSON` (`USERNAME`),
  CONSTRAINT `FK_INVITATION_CANDIDATE_USERNAME` FOREIGN KEY (`CANDIDATE_USERNAME`) REFERENCES `PERSON` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;



--
-- Table structure for table `PERSON_PERSON`
--

DROP TABLE IF EXISTS `PERSON_PERSON`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `PERSON_PERSON` (
  `Person_USERID` INTEGER NOT NULL,
  `friends_USERID` INTEGER NOT NULL,
  PRIMARY KEY (`Person_USERID`,`friends_USERID`),
  KEY `FK_PERSON_PERSON_friends_USERID` (`friends_USERID`),
  CONSTRAINT `FK_PERSON_PERSON_Person_USERID` FOREIGN KEY (`Person_USERID`) REFERENCES `PERSON` (`USERID`),
  CONSTRAINT `FK_PERSON_PERSON_friends_USERID` FOREIGN KEY (`friends_USERID`) REFERENCES `PERSON` (`USERID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `PERSON_SOCIALEVENT`
--

DROP TABLE IF EXISTS `PERSON_SOCIALEVENT`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `PERSON_SOCIALEVENT` (
  `SOCIALEVENTID` INTEGER NOT NULL,
  `USERNAME` varchar(25) NOT NULL,
  PRIMARY KEY (`SOCIALEVENTID`,`USERNAME`),
  KEY `FK_PERSON_SOCIALEVENT_USERNAME` (`USERNAME`),
  CONSTRAINT `FK_PERSON_SOCIALEVENT_USERNAME` FOREIGN KEY (`USERNAME`) REFERENCES `PERSON` (`USERNAME`),
  CONSTRAINT `FK_PERSON_SOCIALEVENT_SOCIALEVENTID` FOREIGN KEY (`SOCIALEVENTID`) REFERENCES `SOCIALEVENT` (`SOCIALEVENTID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;


--
-- Table structure for table `SOCIALEVENTTAG`
--

DROP TABLE IF EXISTS `SOCIALEVENTTAG`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `SOCIALEVENTTAG` (
  `SOCIALEVENTTAGID` INTEGER NOT NULL AUTO_INCREMENT,
  `TAG` varchar(30) DEFAULT NULL,
  `REFCOUNT` INTEGER DEFAULT NULL,
  PRIMARY KEY (`SOCIALEVENTTAGID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `SOCIALEVENTTAG_SOCIALEVENT`
--

DROP TABLE IF EXISTS `SOCIALEVENTTAG_SOCIALEVENT`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `SOCIALEVENTTAG_SOCIALEVENT` (
  `SOCIALEVENTTAGID` INTEGER NOT NULL,
  `SOCIALEVENTID` INTEGER NOT NULL,
  PRIMARY KEY (`SOCIALEVENTTAGID`,`SOCIALEVENTID`),
  KEY `FK_SOCIALEVENTTAG_SOCIALEVENT_SOCIALEVENTID` (`SOCIALEVENTID`),
  CONSTRAINT `FK_SOCIALEVENTTAG_SOCIALEVENT_SOCIALEVENTID` FOREIGN KEY (`SOCIALEVENTID`) REFERENCES `SOCIALEVENT` (`SOCIALEVENTID`),
  CONSTRAINT `FK_SOCIALEVENTTAG_SOCIALEVENT_SOCIALEVENTTAGID` FOREIGN KEY (`SOCIALEVENTTAGID`) REFERENCES `SOCIALEVENTTAG` (`SOCIALEVENTTAGID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
SET character_set_client = @saved_cs_client;

CREATE INDEX SOCIALEVENT_TAG on SOCIALEVENTTAG_SOCIALEVENT (socialeventid);

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

CREATE INDEX SOCIALEVENT_COMMENTS on COMMENTS_RATING (socialevent_socialeventid);

-- Dump completed on 2008-12-04 20:45:41
