# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 5.7.17)
# Database: myips
# Generation Time: 2017-05-03 03:19:14 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table access_points
# ------------------------------------------------------------

CREATE TABLE `access_points` (
  `model` varchar(50) NOT NULL,
  `mac_addr` varchar(17) NOT NULL,
  `lan_addr` varchar(15) NOT NULL,
  `position` point DEFAULT NULL,
  `lan_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`model`,`mac_addr`,`lan_addr`),
  UNIQUE KEY `name_UNIQUE` (`model`),
  UNIQUE KEY `mac_addr_UNIQUE` (`mac_addr`),
  UNIQUE KEY `lan_addr_UNIQUE` (`lan_addr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table data
# ------------------------------------------------------------

CREATE TABLE `data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `actual_position` point DEFAULT NULL,
  `estimated_position` point DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table detected_devices
# ------------------------------------------------------------

CREATE TABLE `detected_devices` (
  `dev_mac_addr` varchar(17) NOT NULL,
  `ap_mac_addr` varchar(17) NOT NULL,
  `signal_strength` int(3) NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`dev_mac_addr`,`ap_mac_addr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table fingerprints
# ------------------------------------------------------------

CREATE TABLE `fingerprints` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `position` point DEFAULT NULL,
  `ap2_1_rss` int(3) DEFAULT NULL,
  `ap2_2_rss` int(3) DEFAULT NULL,
  `ap2_3_rss` int(3) DEFAULT NULL,
  `ap2_4_rss` int(3) DEFAULT NULL,
  `ap2_5_rss` int(3) DEFAULT NULL,
  `ap2_6_rss` int(3) DEFAULT NULL,
  `ap2_7_rss` int(3) DEFAULT NULL,
  `ap2_8_rss` int(3) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table statistics
# ------------------------------------------------------------

CREATE TABLE `statistics` (
  `dev_mac_addr` varchar(17) NOT NULL,
  `frequency` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`dev_mac_addr`),
  UNIQUE KEY `dev_mac_addr_UNIQUE` (`dev_mac_addr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
