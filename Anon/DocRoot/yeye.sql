/*
SQLyog Ultimate v8.62 
MySQL - 5.1.41 : Database - herosmash
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`herosmash` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `herosmash`;

/*Table structure for table `hs_users` */

DROP TABLE IF EXISTS `hs_users`;

CREATE TABLE `hs_users` (
  `id` smallint(3) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `password` char(32) NOT NULL,
  `age` tinyint(2) unsigned NOT NULL,
  `emailActive` tinyint(1) unsigned NOT NULL,
  `email` varchar(50) NOT NULL,
  `banned` tinyint(1) unsigned NOT NULL,
  `signupip` varchar(15) NOT NULL,
  `loginip` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_users` */

LOCK TABLES `hs_users` WRITE;

insert  into `hs_users`(`id`,`username`,`password`,`age`,`emailActive`,`email`,`banned`,`signupip`,`loginip`) values (1,'Mystical','5f4dcc3b5aa765d61d8327deb882cf99',15,5,'mysticaltm@live.com',0,'',''),(2,'Zeroskull','5f4dcc3b5aa765d61d8327deb882cf99',15,5,'idk',0,'',''),(3,'DarkZeroskull','5f4dcc3b5aa765d61d8327deb882cf99',15,5,'yeah',0,'','');

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
