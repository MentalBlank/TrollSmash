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

/*Table structure for table `hs_classes` */

DROP TABLE IF EXISTS `hs_classes`;

CREATE TABLE `hs_classes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `className` varchar(60) NOT NULL,
  `classid` tinyint(3) NOT NULL,
  `skills` text NOT NULL,
  `sStats` text,
  `sDesc` text,
  `sClassCat` varchar(10) DEFAULT 'M1',
  `aMRM` text,
  `passives` varchar(40) DEFAULT '1,2',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=17 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_classes` */

LOCK TABLES `hs_classes` WRITE;

UNLOCK TABLES;

/*Table structure for table `hs_events_wars` */

DROP TABLE IF EXISTS `hs_events_wars`;

CREATE TABLE `hs_events_wars` (
  `name` varchar(60) NOT NULL DEFAULT 'New War',
  `intWarTotal` int(11) NOT NULL DEFAULT '9999999',
  `intWar1` int(11) NOT NULL DEFAULT '0',
  `intWar2` int(11) NOT NULL DEFAULT '0',
  `intWar3` int(11) NOT NULL DEFAULT '0',
  `intWar4` int(11) NOT NULL DEFAULT '0',
  `intWar5` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `hs_events_wars` */

LOCK TABLES `hs_events_wars` WRITE;

insert  into `hs_events_wars`(`name`,`intWarTotal`,`intWar1`,`intWar2`,`intWar3`,`intWar4`,`intWar5`) values ('Good vs Evil',9999999,101,225,0,0,0);

UNLOCK TABLES;

/*Table structure for table `hs_factions` */

DROP TABLE IF EXISTS `hs_factions`;

CREATE TABLE `hs_factions` (
  `id` mediumint(10) NOT NULL AUTO_INCREMENT,
  `sName` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

/*Data for the table `hs_factions` */

LOCK TABLES `hs_factions` WRITE;

insert  into `hs_factions`(`id`,`sName`) values (2,'Good'),(3,'Evil');

UNLOCK TABLES;

/*Table structure for table `hs_hairs` */

DROP TABLE IF EXISTS `hs_hairs`;

CREATE TABLE `hs_hairs` (
  `hairID` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sFile` varchar(60) NOT NULL,
  `sName` varchar(32) NOT NULL,
  `sGen` varchar(2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=378 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_hairs` */

LOCK TABLES `hs_hairs` WRITE;

UNLOCK TABLES;

/*Table structure for table `hs_hairs_shop` */

DROP TABLE IF EXISTS `hs_hairs_shop`;

CREATE TABLE `hs_hairs_shop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hairs` text NOT NULL,
  `hairsM` text NOT NULL,
  `hairsF` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=101 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_hairs_shop` */

LOCK TABLES `hs_hairs_shop` WRITE;

UNLOCK TABLES;

/*Table structure for table `hs_items` */

DROP TABLE IF EXISTS `hs_items`;

CREATE TABLE `hs_items` (
  `id` smallint(3) unsigned NOT NULL AUTO_INCREMENT,
  `itemID` mediumint(6) unsigned NOT NULL,
  `sLink` varchar(28) NOT NULL,
  `sElmt` varchar(8) NOT NULL,
  `bStaff` tinyint(1) unsigned NOT NULL,
  `iRng` tinyint(2) unsigned NOT NULL,
  `iDPS` smallint(4) unsigned NOT NULL,
  `bCoins` tinyint(1) unsigned NOT NULL,
  `sES` varchar(6) NOT NULL,
  `sType` varchar(11) NOT NULL,
  `iCost` int(10) NOT NULL,
  `iRty` tinyint(3) unsigned NOT NULL,
  `iLvl` tinyint(3) unsigned NOT NULL,
  `sIcon` varchar(9) NOT NULL,
  `iQty` tinyint(1) unsigned NOT NULL,
  `iHrs` tinyint(2) unsigned NOT NULL,
  `sFile` varchar(50) NOT NULL,
  `iStk` tinyint(3) unsigned NOT NULL,
  `sDesc` text NOT NULL,
  `bUpg` tinyint(1) unsigned NOT NULL,
  `sName` varchar(40) NOT NULL,
  `bTemp` tinyint(1) unsigned NOT NULL,
  `sFaction` varchar(13) NOT NULL,
  `iClass` tinyint(1) unsigned NOT NULL,
  `FactionID` tinyint(1) unsigned NOT NULL,
  `iReqRep` mediumint(6) unsigned NOT NULL,
  `iReqCP` tinyint(1) unsigned NOT NULL,
  `classID` tinyint(2) unsigned NOT NULL,
  `EnhID` tinyint(2) NOT NULL,
  `sReqQuests` varchar(20) NOT NULL,
  `isFounder` tinyint(1) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1417 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_items` */

LOCK TABLES `hs_items` WRITE;

insert  into `hs_items`(`id`,`itemID`,`sLink`,`sElmt`,`bStaff`,`iRng`,`iDPS`,`bCoins`,`sES`,`sType`,`iCost`,`iRty`,`iLvl`,`sIcon`,`iQty`,`iHrs`,`sFile`,`iStk`,`sDesc`,`bUpg`,`sName`,`bTemp`,`sFaction`,`iClass`,`FactionID`,`iReqRep`,`iReqCP`,`classID`,`EnhID`,`sReqQuests`,`isFounder`) values (1,48,'Darkness','None',0,10,0,0,'ar','Armor',7500,10,1,'iiclass',0,50,'Darkness.swf',1,'I cast darkness into your armor! This look is popular with super vigilantes.',0,'Darkness Armor',0,'',0,0,0,0,0,1,'',0),(2,245,'miniScythe','None',0,10,40,0,'we','Axe',0,10,1,'iwaxe',1,50,'miniScythe.swf',1,'It\'s cute \'cause it\'s mini.',0,'Mini Scythe',0,'0',0,0,0,0,0,1,'',0),(3,187,'PinkCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',0,50,'PinkCape.swf',1,'',0,'PinkCape',0,'',0,0,0,0,0,1,'',0),(4,238,'AlienGloves','None',0,10,0,0,'gl','Gloves',0,10,1,'iigloves',0,50,'AlienGloves.swf',1,'Powered by alien technology',0,'Alien Gloves',0,'',0,0,0,0,0,1,'',0),(5,157,'HairTuck','None',0,10,0,0,'ha','Hair',0,10,1,'iihair',1,50,'Tuck.swf',1,'',0,'HairTuck',0,'',0,0,0,0,0,1,'',0),(6,60,'scar6','None',0,10,0,0,'sc','Scar',2500,10,1,'iiscar',0,50,'scar06.swf',1,'The deadly Fire Squirrel Gang is legendary for their deadly arts, ruthless acrobatics, and love for walnuts.',0,'Fire Squirrel Gang',0,'',0,0,0,0,0,1,'',0),(968,64,'Rocky','None',0,10,0,0,'ar','Armor',6000,10,1,'iiclass',1,50,'HS_Armor_rocky.swf',1,'This armor will make you the ultimate rock star. You are so hard core.',0,'Rocky Armor',0,'',0,0,0,0,0,1,'',0),(969,63,'Frostbite','None',0,10,0,0,'ar','Armor',6500,10,1,'iiclass',1,50,'Frostbite.swf',1,'Take a frostbite out of crime! You really are the coolest hero around.',0,'Frostbite Armor',0,'',0,0,0,0,0,1,'',0),(970,66,'Luchateer','None',0,10,0,0,'ar','Armor',8500,10,1,'iiclass',1,50,'HS_Luchateer.swf',1,'Break out of the ring and break into the skulls of your enemies.',0,'Luchateer Armor',0,'',0,0,0,0,0,1,'',0),(971,185,'YellowCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'YellowCape.swf',1,'',0,'YellowCape',0,'',0,0,0,0,0,1,'',0),(972,186,'ToxicGreenCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'ToxicGreenCape.swf',1,'',0,'ToxicGreenCape',0,'',0,0,0,0,0,1,'',0),(973,188,'OrangeCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'OrangeCape.swf',1,'',0,'OrangeCape',0,'',0,0,0,0,0,1,'',0),(974,189,'GreenCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'Green1Cape.swf',1,'',0,'Green1Cape',0,'',0,0,0,0,0,1,'',0),(975,190,'DarkToxicGreenCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'DarkToxicGreenCape.swf',1,'',0,'DarkToxicGreenCape',0,'',0,0,0,0,0,1,'',0),(976,191,'RedCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'DarkRedCape.swf',1,'',0,'DarkRedCape',0,'',0,0,0,0,0,1,'',0),(977,192,'DarkOrangeCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'DarkOrangeCape.swf',1,'',0,'DarkOrangeCape',0,'',0,0,0,0,0,1,'',0),(978,193,'DarkGreenCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'DarkGreenCape.swf',1,'',0,'DarkGreenCape',0,'',0,0,0,0,0,1,'',0),(979,194,'DarkBrownCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'DarkBrownCape.swf',1,'',0,'DarkBrownCape',0,'',0,0,0,0,0,1,'',0),(980,195,'DarkBlueCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'DarkBlueCape.swf',1,'',0,'DarkBlueCape',0,'',0,0,0,0,0,1,'',0),(981,196,'BrownCape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'BrownCape.swf',1,'',0,'BrownCape',0,'',0,0,0,0,0,1,'',0),(982,197,'Blue1Cape','None',0,10,0,0,'ba','Cape',310,10,1,'iicape',1,50,'Blue1Cape.swf',1,'',0,'Blue1Cape',0,'',0,0,0,0,0,1,'',0),(983,47,'RedCape','None',0,10,0,0,'ba','Cape',1337,10,1,'iicape',1,50,'RedCape.swf',1,'A cape this super will always billow in the nonexistent breeze.',0,'Red Cape',0,'',0,0,0,0,0,1,'',0),(984,54,'pwrglove','None',0,10,0,0,'gl','Gloves',12800,10,1,'iigloves',1,50,'pwrglove.swf',1,'How can you be super without your super power gloves? You have the powweerrr....gloves!',0,'Power Glove',0,'',0,0,0,0,0,1,'',0),(985,199,'hoodFullM','None',1,10,0,0,'he','Helm',0,10,1,'iihelm',1,50,'HoodFull-M.swf',1,'',0,'reuse',0,'',0,0,0,0,0,1,'',0),(986,206,'helmPlainF','None',1,10,0,0,'he','Helm',0,10,1,'iihelm',1,50,'Helm-Plain-F.swf',1,'',0,'reuse',0,'',0,0,0,0,0,1,'',0),(987,211,'helmCatF','None',1,10,0,0,'he','Helm',0,10,1,'iihelm',1,50,'Helm-Cat-F.swf',1,'',0,'reuse',0,'',0,0,0,0,0,1,'',0),(988,201,'hoodedVisor','None',0,10,0,0,'he','Helm',730,10,1,'iihelm',1,50,'HoodedVisor.swf',1,'',0,'Hooded Visor',0,'',0,0,0,0,0,1,'',0),(989,202,'helmPlain','None',0,10,0,0,'he','Helm',730,10,1,'iihelm',1,50,'HelmPlain.swf',1,'',0,'Helm Plain',0,'',0,0,0,0,0,1,'',0),(990,203,'helmPlainFPony','None',0,10,0,0,'he','Helm',730,10,1,'iihelm',1,50,'Helm-PlainFPony.swf',1,'',0,'Helm-PlainFPony',0,'',0,0,0,0,0,1,'',0),(991,204,'helmPlainFLong','None',0,10,0,0,'he','Helm',730,10,1,'iihelm',1,50,'Helm-PlainFLong.swf',1,'',0,'Helm-PlainFLong',0,'',0,0,0,0,0,1,'',0),(992,205,'helmPlainFFullPony','None',0,10,0,0,'he','Helm',730,10,1,'iihelm',1,50,'Helm-PlainFFullPony.swf',1,'',0,'Helm-PlainFFullPony',0,'',0,0,0,0,0,1,'',0),(993,231,'cowl','None',0,10,0,0,'he','Helm',750,10,1,'iihelm',1,50,'Cowl.swf',1,'',0,'Cowl',0,'',0,0,0,0,0,1,'',0),(994,200,'hoodedFull','None',0,10,0,0,'he','Helm',830,10,1,'iihelm',1,50,'HoodFull.swf',1,'',0,'HoodFull',0,'',0,0,0,0,0,1,'',0),(995,198,'metalSpikes','None',0,10,0,0,'he','Helm',850,10,1,'iihelm',1,50,'Metal-Spikes.swf',1,'',0,'Metal-Spikes',0,'',0,0,0,0,0,1,'',0),(996,218,'DomeHelmRed','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmRed.swf',1,'',0,'DomeHelmRed',0,'',0,0,0,0,0,1,'',0),(997,219,'DomeHelmRedLongF','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmRedLong.swf',1,'',0,'DomeHelmRedLong',0,'',0,0,0,0,0,1,'',0),(998,220,'DomeHelmPurp','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmPurp.swf',1,'',0,'DomeHelmPurp',0,'',0,0,0,0,0,1,'',0),(999,221,'DomeHelmPurpLong','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmPurpLong.swf',1,'',0,'DomeHelmPurpLong',0,'',0,0,0,0,0,1,'',0),(1000,222,'DomeHelmOrange','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmOrange.swf',1,'',0,'DomeHelmOrange',0,'',0,0,0,0,0,1,'',0),(1001,223,'DomeHelmBlueLongF','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmOrangeLong.swf',1,'',0,'DomeHelmOrangeLong',0,'',0,0,0,0,0,1,'',0),(1002,224,'DomeHelmDarkLongF','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmDarkLong.swf',1,'',0,'DomeHelmDarkLong',0,'',0,0,0,0,0,1,'',0),(1003,225,'DomeHelmDark','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmDark.swf',1,'',0,'DomeHelmDark',0,'',0,0,0,0,0,1,'',0),(1004,226,'DomeHelmBlue','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmBlue.swf',1,'',0,'DomeHelmBlue',0,'',0,0,0,0,0,1,'',0),(1005,227,'DomeHelmBlueLongF','None',0,10,0,0,'he','Helm',900,10,1,'iihelm',1,50,'DomeHelmBlueLong.swf',1,'',0,'DomeHelmBlueLong',0,'',0,0,0,0,0,1,'',0),(1006,229,'cowlBolt','None',0,10,0,0,'he','Helm',950,10,1,'iihelm',1,50,'CowlBolt.swf',1,'',0,'CowlBolt',0,'',0,0,0,0,0,1,'',0),(1007,212,'flaredMaskM','None',0,10,0,0,'he','Helm',1000,10,1,'iihelm',1,50,'FlaredMaskM.swf',1,'',0,'FlaredMask Male',0,'',0,0,0,0,0,1,'',0),(1008,213,'flaredMaskFPigtail','None',0,10,0,0,'he','Helm',1000,10,1,'iihelm',1,50,'FlaredMaskFPigtail.swf',1,'',0,'FlaredMaskFPigtail',0,'',0,0,0,0,0,1,'',0),(1009,214,'flaredMaskFMed','None',0,10,0,0,'he','Helm',1000,10,1,'iihelm',1,50,'FlaredMaskFMed.swf',1,'',0,'FlaredMaskFMed',0,'',0,0,0,0,0,1,'',0),(1010,215,'flaredMaskFFullPony','None',0,10,0,0,'he','Helm',1000,10,1,'iihelm',1,50,'FlaredMaskFullPony.swf',1,'',0,'FlaredMaskFullPony',0,'',0,0,0,0,0,1,'',0),(1011,216,'flaredMaskFLong','None',0,10,0,0,'he','Helm',1000,10,1,'iihelm',1,50,'FlaredMaskFLong.swf',1,'',0,'FlaredMaskFLong',0,'',0,0,0,0,0,1,'',0),(1012,207,'helmCat','None',0,10,0,0,'he','Helm',1100,10,1,'iihelm',1,50,'HelmCat.swf',1,'',0,'Helm Cat',0,'',0,0,0,0,0,1,'',0),(1013,208,'helmCatFPony','None',0,10,0,0,'he','Helm',1100,10,1,'iihelm',1,50,'HelmCatFPony.swf',1,'',0,'HelmCatFPony',0,'',0,0,0,0,0,1,'',0),(1014,209,'helmCatFLong','None',0,10,0,0,'he','Helm',1100,10,1,'iihelm',1,50,'HelmCatFLong.swf',1,'',0,'HelmCatFLong',0,'',0,0,0,0,0,1,'',0),(1015,210,'helmCatFFullpony','None',0,10,0,0,'he','Helm',1100,10,1,'iihelm',1,50,'HelmCatFFullPony.swf',1,'',0,'HelmCatFFullPony',0,'',0,0,0,0,0,1,'',0),(1016,230,'twotonecowl','None',0,10,0,0,'he','Helm',1100,10,1,'iihelm',1,50,'Cowl2tone.swf',1,'',0,'Cowl2tone',0,'',0,0,0,0,0,1,'',0),(1017,228,'cowlSpike','None',0,10,0,0,'he','Helm',1400,10,1,'iihelm',1,50,'CowlSpike.swf',1,'',0,'CowlSpike',0,'',0,0,0,0,0,1,'',0),(1018,217,'flameHead','None',0,10,0,0,'he','Helm',14050,10,1,'iihelm',1,50,'FlameHead.swf',1,'',0,'Flame Head',0,'',0,0,0,0,0,1,'',0),(1019,176,'Glasses1','None',0,10,0,0,'ma','Mask',425,10,1,'iimask',1,50,'glasses1.swf',1,'',0,'glasses1',0,'',0,0,0,0,0,1,'',0),(1020,177,'Glasses2','None',0,10,0,0,'ma','Mask',520,10,1,'iimask',1,50,'glasses2.swf',1,'',0,'glasses2',0,'',0,0,0,0,0,1,'',0),(1021,46,'BlueTieMask','None',0,10,0,0,'ma','Mask',550,10,1,'iimask',1,50,'BlueTieMask.swf',1,'Conceal your true identity with a super stylish piece of cloth! It never fails.',0,'Blue Tie Mask',0,'',0,0,0,0,0,1,'',0),(1022,182,'sportGoggles','None',0,10,0,0,'ma','Mask',577,10,1,'iimask',1,50,'sportGoggles.swf',1,'',0,'sportGoggles',0,'',0,0,0,0,0,1,'',0),(1023,175,'WhiteSkull','None',0,10,0,0,'ma','Mask',676,10,1,'iimask',1,50,'WhiteSkull.swf',1,'',0,'WhiteSkull',0,'',0,0,0,0,0,1,'',0),(1024,174,'BluePointyMaskM','None',0,10,0,0,'ma','Mask',744,10,1,'iimask',1,50,'BluePointyMaskM.swf',1,'',0,'BluePointyMaskM',0,'',0,0,0,0,0,1,'',0),(1025,178,'glasses3D','None',0,10,0,0,'ma','Mask',744,10,1,'iimask',1,50,'glasses3D.swf',1,'',0,'glasses3D',0,'',0,0,0,0,0,1,'',0),(1026,179,'LightningMask','None',0,10,0,0,'ma','Mask',744,10,1,'iimask',1,50,'LightningMask.swf',1,'',0,'LightningMask',0,'',0,0,0,0,0,1,'',0),(1027,180,'LightningMaskM','None',0,10,0,0,'ma','Mask',744,10,1,'iimask',1,50,'LightningMaskM.swf',1,'',0,'LightningMaskM',0,'',0,0,0,0,0,1,'',0),(1028,173,'BluePointyMask','None',0,10,0,0,'ma','Mask',833,10,1,'iimask',1,50,'BluePointyMask.swf',1,'',0,'BluePointyMask',0,'',0,0,0,0,0,1,'',0),(1029,181,'RedGoggles','None',0,10,0,0,'ma','Mask',954,10,1,'iimask',1,50,'RedGoggles.swf',1,'',0,'RedGoggles',0,'',0,0,0,0,0,1,'',0),(1030,183,'scar9','None',0,10,0,0,'sc','Scar',0,10,1,'iiscar',1,50,'scar09.swf',1,'',0,'scar09',0,'',0,0,0,0,0,1,'',0),(1031,184,'scar10','None',0,10,0,0,'sc','Scar',0,10,1,'iiscar',1,50,'scar10.swf',1,'',0,'scar10',0,'',0,0,0,0,0,1,'',0),(1032,57,'scar3','None',0,10,0,0,'sc','Scar',700,10,1,'iiscar',1,50,'scar03.swf',1,'You are one unlucky individual. With many stories to tell.',0,'Lotso Scars',0,'',0,0,0,0,0,1,'',0),(1033,58,'scar4','None',0,10,0,0,'sc','Scar',1000,10,1,'iiscar',1,50,'scar04.swf',1,'This is what happens when a tattoo gun decks you in the face. Gives a whole new meaning to having a black eye!',0,'Black Spot',0,'',0,0,0,0,0,1,'',0),(1034,61,'scar8','None',0,10,0,0,'sc','Scar',1000,10,1,'iiscar',1,50,'scar08.swf',1,'Those who wear the mark of the Death Heart should be feared. Their hugs can be lethal and their kisses are said to contain cooties.',0,'Death Heart Tattoo',0,'',0,0,0,0,0,1,'',0),(1035,56,'scar2','None',0,10,0,0,'sc','Scar',1100,10,1,'iiscar',1,50,'scar02.swf',1,'This scar marks your strength, agility, and your uncanny knack to upset robots.',0,'Single Scar',0,'',0,0,0,0,0,1,'',0),(1036,55,'scar01','None',0,10,0,0,'sc','Scar',1500,10,1,'iiscar',1,50,'scar01.swf',1,'Double scars, all the way!',0,'Double Scar',0,'',0,0,0,0,0,1,'',0),(1037,62,'CyberBlue','None',0,10,0,0,'ar','Armor',5500,10,1,'iiclass',1,50,'CyberBlue.swf',1,'It\'s both cyber and blue. The best of both worlds. Like strawberries and cool whip.',0,'Cyber Blue Armor',0,'',0,0,0,0,0,1,'',0),(1038,65,'Reptillio','None',0,10,0,0,'ar','Armor',7200,10,1,'iiclass',1,50,'HS_Armor_Liz.swf',1,'It\'s a REPTILLIO!!!! Run Run Run!',0,'Reptillio Armor',0,'',0,0,0,0,0,1,'',0),(1039,41,'LongMessy','None',0,10,0,0,'ha','Hair',400,10,1,'iihair',1,50,'MessyLongHair.swf',1,'Your hair is not messy; it\'s stylishly unruly. Just the way you like it.',0,'Messy Long Hair',0,'',0,0,0,0,0,1,'',0),(1040,53,'FullCowlBlack','None',0,10,0,0,'he','Helm',1200,10,1,'iihelm',1,50,'FullCowlBlack.swf',1,'Worried about someone finding out your true identity? This black cowl covers your face completely! Even the Dread Pirate Roberts won\'t know who you are.',0,'Black Cowl Full',0,'',0,0,0,0,0,1,'',0),(1041,59,'scar5','None',0,10,0,0,'sc','Scar',1850,10,1,'iiscar',1,50,'scar05.swf',1,'Hey, you got something on your face. But doesn\'t X mark the spot...',0,'The X',0,'',0,0,0,0,0,1,'',0),(1042,237,'Thwacker','None',0,10,0,0,'we','Axe',0,10,1,'iwaxe',1,50,'Thwacker.swf',1,'It feels good to use this axe. Real good.',0,'Thwacker',0,'',0,0,0,0,0,1,'',0),(1043,241,'allYourMace','None',0,10,0,0,'we','Axe',0,10,1,'iwaxe',1,50,'allYourMace.swf',1,'Belongs to us!',0,'All Your Mace',0,'',0,0,0,0,0,1,'',0),(1044,242,'boomStick','None',0,10,0,0,'we','Axe',0,10,1,'iwaxe',1,50,'boomStick.swf',1,'Cause it\'s a stick with dynamite on it. Get it',0,'Boom Stick',0,'',0,0,0,0,0,1,'',0),(1045,243,'heavyHammer','None',0,10,0,0,'we','Axe',0,10,1,'iwaxe',1,50,'heavyHammer.swf',1,'It\'s bashing time!!',0,'Heavy Hammer',0,'',0,0,0,0,0,1,'',0),(1046,244,'meatClub','None',0,10,0,0,'we','Axe',0,10,1,'iwaxe',1,50,'meatClub.swf',1,'You should have seen the chicken this leg came from.',0,'Meat Club',0,'',0,0,0,0,0,1,'',0),(1047,2,'Axe02','None',0,0,0,0,'we','Axe',1000,1,1,'iwaxe',1,50,'axe02.swf',1,'Just as the name suggests, this ferocious axe is carved from solid bone.',0,'Bone Axe',0,'',0,0,0,0,0,1,'',0),(1048,239,'laserForce','None',0,10,0,0,'gl','Gloves',0,10,1,'iigloves',1,50,'LaserForce.swf',1,'As a member of the Laser Force you get this mighty weapon. These were found at a flea market.',0,'Laser Force',0,'',0,0,0,0,0,1,'',0),(1049,240,'lightBlade','None',0,10,0,0,'gl','Gloves',0,10,1,'iigloves',1,50,'lightBlade.swf',1,'This is one sweet glove. Seriously.',0,'Light Blade',0,'',0,0,0,0,0,1,'',0),(1050,234,'Overkill','None',0,10,0,0,'gl','Gloves',0,10,1,'iigloves',1,50,'Overkill.swf',1,'Lives up to it\'s name.',0,'Overkill',0,'',0,0,0,0,0,1,'',0),(1051,81,'DangerClaw','None',0,10,0,0,'gl','Gloves',14000,10,1,'iigloves',1,50,'DangerClaw.swf',1,'Danger is your middle name. How convenient that it\'s also the name of your power gloves!',0,'Danger Claw',0,'',0,0,0,0,0,1,'',0),(1052,82,'Ghoulinator','None',0,10,0,0,'gl','Gloves',25000,10,1,'iigloves',1,50,'Ghoulinator.swf',1,'Great Ghouls! Strange ectoplasmic matter flows through these powerful claws.',0,'Ghoulinator',0,'',0,0,0,0,0,1,'',0),(1053,232,'classicRayGun','None',0,10,0,0,'we','Gun',0,10,1,'iwgun',1,50,'classicRayGun.swf',1,'This throw back to all the classic sci-fi movie still packs a punch.',0,'Classic Ray Gun',0,'',0,0,0,0,0,1,'',0),(1054,233,'reynoldsBlaster','None',0,10,0,0,'we','Gun',0,10,1,'iwgun',1,50,'reynoldsBlaster.swf',1,'You know what I mean....',0,'Reynolds Blaster',0,'',0,0,0,0,0,1,'',0),(1055,78,'Oblivinator','None',0,10,0,0,'we','Gun',3890,10,1,'iwgun',1,50,'Oblivinator.swf',1,'If the size of this beast doesn\'t scare your enemies away, the giant yellow blast beam certainly will! Send them to OBLIVION!',0,'The Oblivinator',0,'',0,0,0,0,0,1,'',0),(1056,75,'PosPlazmatron','None',0,10,0,0,'we','Gun',11500,10,1,'iwgun',1,50,'PosativePlazmatron.swf',1,'Stay positive -- use the Plazmatron! The Plazmatron shoots intense positive pulses at anything unlucky enough to be on the receiving end.',0,'Positive Plazmatron',0,'',0,0,0,0,0,1,'',0),(1057,76,'NegPlazmatron','None',0,10,0,0,'we','Gun',11500,10,1,'iwgun',1,50,'NegativePlazmatron.swf',1,'It\'s alright to be Negative sometimes... just as long as you do something with it! The Negative Plazmatron shoots an intense negative pulse at your enemies.',0,'Negative Plazmatron',0,'',0,0,0,0,0,1,'',0),(1058,67,'rapiZAP','None',0,10,0,0,'we','Gun',12000,10,1,'iwgun',1,50,'rapiZAP.swf',1,'Ready to ZAP some baddies? Now you can with the RapiZap v1.0! Comes standard with an infinite amount of laser beams!',0,'RapiZAP v1.0',0,'',0,0,0,0,0,1,'',0),(1059,69,'shotBlaster','None',0,10,0,0,'we','Gun',12000,10,1,'iwgun',1,50,'shotBlaster.swf',1,'Pew pew pew! That\'s the sound of VICTORY!',0,'Shot Blaster',0,'',0,0,0,0,0,1,'',0),(1060,79,'OrangeElectronomer','None',0,10,0,0,'we','Gun',13500,10,1,'iwgun',1,50,'OrangeElectronomer.swf',1,'Orange you glad you have an Electronomer? Shoot bolts of citric acid lightning at your enemies -- Vitamin C critical hit!',0,'Orange Electronomer',0,'',0,0,0,0,0,1,'',0),(1061,77,'MagmaTorch','None',0,10,0,0,'we','Gun',15000,10,1,'iwgun',1,50,'MagmaTorch.swf',1,'This is one hot weapon! Whoever said \"don\'t play with fire\" obviously never played any BattleOn Games.',0,'Magma Torch',0,'',0,0,0,0,0,1,'',0),(1062,235,'handHacker','None',0,10,0,0,'we','Sword',0,10,1,'iwsword',1,50,'handHacker.swf',1,'Hack those hands like there is no tomorrow.',0,'Hand Hacker',0,'',0,0,0,0,0,1,'',0),(1063,236,'shortSword','None',0,10,0,0,'we','Sword',0,10,1,'iwsword',1,50,'shortSword.swf',1,'This little guy has a huge inferiority complex.',0,'Short Super Sword',0,'',0,0,0,0,0,1,'',0),(1416,51,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',25,'What is this thing?  Well its small so, I\'d say I can carry up to 25 of \'em.',0,'Combobulator',1,'',0,0,0,0,0,1,'',0),(1415,52,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',5,'Bulky and sharp... I\'d better not try to carry more than 5 at a time.',0,'Propeller',1,'',0,0,0,0,0,1,'',0),(1414,49,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',25,'If a Diva can wield this it probably doesn\'t weigh that much.  I won\'t hold more than 25, just in case.',0,'Diva Maul',1,'',0,0,0,0,0,1,'',0),(1413,50,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',5,'Jetpacks are big... I don\'t think I can carry more than 5 at a time.',0,'Jetpack',1,'',0,0,0,0,0,1,'',0),(1412,29,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',7,'A scrap piece of metal casing once belonging to a truck.',0,'Metal Casing',1,'',0,0,0,0,0,1,'4',0),(1411,30,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',4,'Don\'t blow a gasket! Oh wait...',0,'Hardened Gasket',1,'',0,0,0,0,0,1,'4',0),(1410,31,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',1,'An assortment of electronics varying in size and shape. Good thing you\'re just borrowing them.',0,'Various Electronics',1,'',0,0,0,0,0,1,'6',0),(1409,26,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',10,'Thanks to you, this camera has been rewired. Or destroyed. Same thing, right',0,'Rewired Camera',1,'',0,0,0,0,0,1,'5',0),(1408,32,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',8,'Wonder if this guy ever played a classic 80\'s arcade game',0,'Defeated Pack Man',1,'',0,0,0,0,0,1,'7',0),(1406,278,'AlphaElf','None',0,10,0,0,'ar','Armor',0,10,1,'iiclass',1,50,'AlphaElfArmor.swf',1,'',0,'Elf Armor',0,'',0,0,0,0,0,1,'',0),(1407,28,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',10,'You defeated a Diva! I bet she\'s going to tell her big sister on you now.',0,'Diva Defeated',1,'',0,0,0,0,0,1,'3',0),(1405,277,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',5,'Well these parts are spare now but up until recently they appear to have been used.',0,'Spare Parts',1,'',0,0,0,0,0,1,'37',0),(1404,275,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',10,'This is one of the presents hidden around the city by Doodie.  Collect 10 of them and bring them back to Hermie.',0,'Present Found',1,'',0,0,0,0,0,1,'34',0),(1403,298,'AlphaXmasMask','None',0,10,0,0,'ma','Mask',0,10,1,'iimask',1,50,'AlphaXmasMask.swf',1,'There are mask, and then there are Smashmas masks. This is one of those masks.',0,'Smashmas Mask',0,'',0,0,0,0,0,1,'',0),(1402,293,'AlphaElfHat','None',0,10,0,0,'he','Helm',1200,10,1,'iihelm',1,50,'AlphaElfHat.swf',1,'Don\'t you just look adorable.',0,'Elf Hat',0,'',0,0,0,0,0,1,'',0),(1401,283,'AlphaDeerLong','None',0,10,0,0,'ha','Hair',500,10,1,'iihair',1,50,'AlphaDeerLong.swf',1,'These reindeer antlers look perfect on the Long Hairstyle. It looks great on you!',0,'Antlers Long',0,'',0,0,0,0,0,1,'',0),(1400,282,'AlphaDeerEmo','None',0,10,0,0,'ha','Hair',500,10,1,'iihair',1,50,'AlphaDeerEmo.swf',1,'These reindeer antlers look perfect on the Emo Hairstyle. It looks great on you!',0,'Antlers Emo',0,'',0,0,0,0,0,1,'',0),(1399,281,'AlphaDeerDusk','None',0,10,0,0,'ha','Hair',500,10,1,'iihair',1,50,'AlphaDeerDusk.swf',1,'These reindeer antlers look perfect on the Dusk Hairstyle. It looks great on you!',0,'Antlers Dusk',0,'',0,0,0,0,0,1,'',0),(1398,292,'CandyCane','None',0,10,0,0,'we','Axe',5000,10,1,'iwaxe',1,50,'candyCane.swf',1,'It\'s a candy cane for your hand. Your welcome.',0,'CandyCane',0,'',0,0,0,0,0,1,'',0),(1397,291,'xmaswarrior','None',0,10,0,0,'ar','Armor',10000,10,1,'iiclass',1,50,'HS_Armor_xmaswarrior.swf',1,'Some people call it over the top. You call it your warrior gear.',0,'SmashmasWarriorArmor',0,'',0,0,0,0,0,1,'',0),(1396,273,'snowman','None',0,10,0,0,'ar','Armor',8000,10,1,'iiclass',1,50,'HS_Snowman.swf',1,'You braved the shivery north, and all you got was this crummy armor. Just kidding. It\'s Wicked AWESOME!',0,'Snowman Armor',0,'',0,0,0,0,0,1,'',0),(1395,299,'AlphaDarkXmasMask','None',0,10,0,0,'ma','Mask',1000,10,1,'iimask',1,50,'AlphaDarkXmasMask.swf',1,'There are mask, and then there are Smashmas masks. This is one of those masks.',0,'Dark Smashmas Mask',0,'',0,0,0,0,0,1,'',0),(1394,294,'AlphaDarkElfHat','None',0,10,0,0,'he','Helm',1200,10,1,'iihelm',1,50,'AlphaDarkElfHat.swf',1,'You may be adorable in this hat but it\'s a fiendish adorable.',0,'Dark Elf Hat',0,'',0,0,0,0,0,1,'',0),(1393,286,'AlphaDarkDeerLong','None',0,10,0,0,'ha','Hair',500,10,1,'iihair',1,50,'AlphaDarkDeerLong.swf',1,'These reindeer antlers look perfect on the Long Hairstyle. It looks great on you!',0,'Dark Antlers Long',0,'',0,0,0,0,0,1,'',0),(1392,285,'AlphaDarkDeerEmo','None',0,10,0,0,'ha','Hair',500,10,1,'iihair',1,50,'AlphaDarkDeerEmo.swf',1,'These reindeer antlers look perfect on the Emo Hairstyle. It looks great on you!',0,'Dark Antlers Emo',0,'',0,0,0,0,0,1,'',0),(1391,284,'AlphaDarkDeerDusk','None',0,10,0,0,'ha','Hair',500,10,1,'iihair',1,50,'AlphaDarkDeerDusk.swf',1,'These reindeer antlers look perfect on the Dusk Hairstyle. It looks great on you!',0,'Dark Antlers Dusk',0,'',0,0,0,0,0,1,'',0),(1390,296,'DarkCandyCane','None',0,10,0,0,'we','Axe',5000,10,1,'iwaxe',1,50,'DarkcandyCane.swf',1,'It\'s an evil candy cane for your hand. Your welcome.',0,'Dark Candy Cane',0,'',0,0,0,0,0,1,'',0),(1389,297,'darkxmas','None',0,10,0,0,'ar','Armor',10000,10,1,'iiclass',1,50,'HS_Armor_darkxmas.swf',1,'Some people call it over the top. You call it your warrior gear.',0,'DarkSmashmasWarriorArmor',0,'',0,0,0,0,0,1,'',0),(1388,295,'Darksnowman','None',0,10,0,0,'ar','Armor',8000,10,1,'iiclass',1,50,'HS_DarkSnowman.swf',1,'This snowman just so happens to be made entirely from yellow snow. EEEEEEEWWWWWWWWWW!',0,'Dark Snowman Armor',0,'',0,0,0,0,0,1,'',0),(1387,280,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',5,'Certainly in such a state of disarray that no one but an elf could possibly make any use of these scraps.',0,'Scrap Parts',1,'',0,0,0,0,0,1,'39',0),(1386,274,'','None',0,10,0,0,'None','Item',300,18,1,'iiitem',2,50,'',10,'Proof of an elf\'s failure it\'s not even an ideal stocking stuffer.',0,'Coal',0,'',0,0,0,0,0,1,'',0),(1385,279,'AlphaDarkElf','None',0,10,0,0,'ar','Armor',0,10,1,'iiclass',1,50,'AlphaDarkElfArmor.swf',1,'',0,'Dark Elf Armor',0,'',0,0,0,0,0,1,'',0),(1384,276,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',10,'You have hidden a gift for Doodie.  No, the gift wasn\'t FOR Doodie, the HIDING of the gift was for Doodie!',0,'Present Hidden',1,'',0,0,0,0,0,1,'36',0),(1383,27,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',5,'You saved a Patrolman! They are forever in your debt.',0,'Recovered Patrolman',1,'',0,0,0,0,0,1,'12',0),(1382,36,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',20,'Apparently someone is a little camera shy...',0,'Smashed Camera',1,'',0,0,0,0,0,1,'11',0),(1381,33,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',10,'Good thing you killed the bot first!',0,'Killbot Defeated',1,'',0,0,0,0,0,1,'8',0),(1379,35,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',8,'How easily the artificial mind of a police bot can be... oh well, time to break some more bad bots!',0,'Broken APB',1,' ',0,0,0,0,0,1,'10',0),(1380,34,'','None',0,10,0,0,'None','Quest Item',0,10,1,'iiqitem',1,50,'',9,'What the truck!',0,'Truck Raider Defeated',1,'',0,0,0,0,0,1,'9',0),(1377,255,'tat19','None',0,10,0,0,'sc','Scar',0,10,1,'iiscar',0,50,'scar19.swf',1,'This mark will get you noticed.',0,'Dark Blade',0,'',0,0,0,0,0,1,'',0),(1376,164,'twoBangPigtail','None',0,10,0,0,'ha','Hair',4321,10,1,'iihair',1,50,'TentiHawkBlack-Green.swf',1,'Uhhh, your hair\'s got a mind of its own.',0,'Black Tentacle Hair',0,'',0,0,0,0,0,1,'',0),(1375,133,'OneBangPigtail','None',0,10,0,0,'ha','Hair',1230,10,1,'iihair',1,50,'OneBangPigtail.swf',1,'Sassy pigtails with a bit of bangs.',0,'OneBangPigtail',0,'',0,0,0,0,0,1,'',0),(1374,141,'PullBackPigtail','None',0,10,0,0,'ha','Hair',1230,10,1,'iihair',1,50,'PullbackPigtail.swf',1,'Parted down the middle and put up into two even pig tails so you can kick butt!',0,'PullBackPigtail',0,'',0,0,0,0,0,1,'',0),(1373,107,'EmoPigtail','None',0,10,0,0,'ha','Hair',1230,10,1,'iihair',1,50,'Emo-Pigtail.swf',1,'So emo kawaii!',0,'EmoPigtail',0,'',0,0,0,0,0,1,'',0),(1372,120,'MedCombedPony','None',0,10,0,0,'ha','Hair',1230,10,1,'iihair',1,50,'MedCombed-FullPony.swf',1,'Serious pony tail time!',0,'MedCombedPony',0,'',0,0,0,0,0,1,'',0),(1371,44,'MohawkShort','None',0,10,0,0,'ha','Hair',800,10,1,'iihair',1,50,'MohawkShortHair.swf',1,'Everyone loves a short Mohawk. Especially babies.',0,'Short Mohawk',0,'',0,0,0,0,0,1,'',0),(1370,143,'hairSamrye','None',0,10,0,0,'ha','Hair',730,10,1,'iihair',1,50,'Samrye.swf',1,'Piece-y bangs and long hair, perfect for flight.',0,'hairSamrye',0,'',0,0,0,0,0,1,'',0),(1369,100,'zagShort','None',0,10,0,0,'ha','Hair',730,10,1,'iihair',1,50,'ZagShort.swf',1,'Sideswept and piece-y is the way to go!',0,'ZagShort',0,'',0,0,0,0,0,1,'',0),(1368,172,'zagLong','None',0,10,0,0,'ha','Hair',730,10,1,'iihair',1,50,'ZagLong.swf',1,'Shaggy, side swept hair that reaches to your waist.',0,'zagLong',0,'',0,0,0,0,0,1,'',0),(1367,170,'zagHeadbandLong','None',0,10,0,0,'ha','Hair',730,10,1,'iihair',1,50,'ZagHeadbandLong.swf',1,'Shaggy, side swept hair that reaches to your waist.',0,'zagHeadbandLong',0,'',0,0,0,0,0,1,'',0),(1365,168,'zagFullPony','None',0,10,0,0,'ha','Hair',730,10,1,'iihair',1,50,'ZagFullPony.swf',1,'Shaggy, side swept hair with a ponytail.',0,'zagFullPony',0,'',0,0,0,0,0,1,'',0),(1366,169,'ZagHeadband','None',0,10,0,0,'ha','Hair',730,10,1,'iihair',1,50,'ZagHeadband.swf',1,'Shaggy, side swept hair that reaches to the middle of your back.',0,'ZagHeadband',0,'',0,0,0,0,0,1,'',0),(1364,167,'ZagBraid','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'ZagBraid.swf',1,'Shaggy, side swept hair with a braid.',0,'ZagBraid',0,'',0,0,0,0,0,1,'',0),(1363,166,'HairZag','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Zag.swf',1,'Shaggy, side swept hair.',0,'HairZag',0,'',0,0,0,0,0,1,'',0),(1362,165,'twoBangLongPony','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'TwoBangPony.swf',1,'A long ponytail with stylish bangs to frame the face.',0,'twoBangLongPony',0,'',0,0,0,0,0,1,'',0),(1361,162,'twoBangLong','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'TwoBangLong.swf',1,'Long hair with stylish bangs to frame the face.',0,'twoBangLong',0,'',0,0,0,0,0,1,'',0),(1360,110,'FullBangLong','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'FullBang-Long.swf',1,'Classic heroine hair.',0,'FullBangLong',0,'',0,0,0,0,0,1,'',0),(1358,108,'EmoPony','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Emo-Pony.swf',1,'No fuss, no muss, still emo!',0,'EmoPony',0,'',0,0,0,0,0,1,'',0),(1359,116,'HeadbandLong','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Headband-Long.swf',1,'Cute and practical, this headband keeps your long locks out of the way.',0,'HeadbandLong',0,'',0,0,0,0,0,1,'',0),(1356,113,'GlamF','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Glam-F.swf',1,'A glamorous bob with a fringe.',0,'GlamF',0,'',0,0,0,0,0,1,'',0),(1357,114,'Hawk','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Hawk.swf',1,'Faux hawk, this rocks!',0,'Hawk',0,'',0,0,0,0,0,1,'',0),(1355,105,'EmoHighPony','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Emo-HighPony.swf',1,'This pony tail holds your hair up out of the way of your fighting.',0,'EmoHighPony',0,'',0,0,0,0,0,1,'',0),(1354,139,'PullBackLongPony','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'PullbackLongPony.swf',1,'Parted down the middle and pulled back, this is one long ponytail.',0,'PullbackLongPony',0,'',0,0,0,0,0,1,'',0),(1353,144,'hairSlick','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'Slick.swf',1,'Short and seriously classic.',0,'hairSlick',0,'',0,0,0,0,0,1,'',0),(1352,148,'StraitBangLong','None',0,10,0,0,'ha','Hair',640,10,1,'iihair',1,50,'StraitBangLong.swf',1,'Bette bangs and shoulder length hair, a deadly combination!',0,'StraitBangLong',0,'',0,0,0,0,0,1,'',0),(1351,134,'OneBangPony','None',0,10,0,0,'ha','Hair',620,10,1,'iihair',1,50,'OneBangPony.swf',1,'Short ponytail with a bit of bangs.',0,'OneBangPony',0,'',0,0,0,0,0,1,'',0),(1350,130,'OneBangLong','None',0,10,0,0,'ha','Hair',620,10,1,'iihair',1,50,'OneBangLong.swf',1,'Long, waist length hair swept back except for a single lock of hair in the front.',0,'OneBangLong',0,'',0,0,0,0,0,1,'',0),(1349,138,'PullBackLong','None',0,10,0,0,'ha','Hair',620,10,1,'iihair',1,50,'PullBackLong.swf',1,'Parted down the middle and slicked back, the rest of this hair hangs down to the waist.',0,'PullBackLong',0,'',0,0,0,0,0,1,'',0),(1348,128,'OneBang','None',0,10,0,0,'ha','Hair',620,10,1,'iihair',1,50,'OneBang.swf',1,'Closely cropped hair with just a bit of bangs for fun.',0,'OneBang',0,'',0,0,0,0,0,1,'',0),(1347,102,'Dusk','None',0,10,0,0,'ha','Hair',620,10,1,'iihair',1,50,'Dusk.swf',1,'This is a long, shaggy pixie cut.',0,'Dusk',0,'',0,0,0,0,0,1,'',0),(1346,98,'Brush','None',0,10,0,0,'ha','Hair',620,10,1,'iihair',1,50,'Brush.swf',1,'No brush needed for this cut! Just a bit of pomade and away you go!',0,'Brush',0,'',0,0,0,0,0,1,'',0),(1345,97,'Breezy','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Breezy.swf',1,'So short your ears will show, but easy to take care of!',0,'Breezy',0,'',0,0,0,0,0,1,'',0),(1343,95,'Blaze','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Blaze.swf',1,'This hair is aerodynamic!',0,'Blaze',0,'',0,0,0,0,0,1,'',0),(1344,96,'Bob','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Bob.swf',1,'This short fringe frames your face.',0,'Bob',0,'',0,0,0,0,0,1,'',0),(1342,99,'Curl','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Curl.swf',1,'Elvis would be proud of this hair.',0,'Curl',0,'',0,0,0,0,0,1,'',0),(1341,93,'AngledShortM','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Angled-Short-M.swf',1,'A very unruly choppy bob cut, perfect for the masculine hero!',0,'Unruly Choppy Bob',0,'',0,0,0,0,0,1,'',0),(1340,92,'AngledShortF','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Angled-Short-F.swf',1,'A very adorable layered bob cut for the most super heroines!',0,'Cutie Choppy Bob',0,'',0,0,0,0,0,1,'',0),(1339,112,'FullBangPony','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'FullBang-Pony.swf',1,'Classic and cute, this hair style never gets in the way of fighting.',0,'FullBangPony',0,'',0,0,0,0,0,1,'',0),(1338,111,'FullBangMed','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'FullBang-Med.swf',1,'Wash and wear hair!',0,'FullBangMed',0,'',0,0,0,0,0,1,'',0),(1336,106,'EmoMed','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Emo-Med.swf',1,'This emo fringe frames your face.',0,'EmoMed',0,'',0,0,0,0,0,1,'',0),(1337,103,'EmoBraid','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Emo-Braid.swf',1,'Your identity is mostly concealed with the fringe around your face and the festive braid in back.',0,'EmoBraid',0,'',0,0,0,0,0,1,'',0),(1335,115,'HeadbandFull','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'Headband-Full.swf',1,'Stylish and practical, this headband keeps your hair out of the way.',0,'HeadbandFull',0,'',0,0,0,0,0,1,'',0),(1334,109,'FullBangBraid','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'FullBang-Braid.swf',1,'A braid with a flair.',0,'FullBangBraid',0,'',0,0,0,0,0,1,'',0),(1333,119,'MedCombedBraid','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'MedCombed-Braid.swf',1,'This braid is a timeless classic.',0,'MedCombedBraid',0,'',0,0,0,0,0,1,'',0),(1332,126,'MohawkMedHair','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'MohawkMedHair.swf',1,'Liberty spikes all the way!',0,'MohawkMedHair',0,'',0,0,0,0,0,1,'',0),(1331,137,'PullBackFullPony','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'PullBackFullPony.swf',1,'Parted down the middle and pulled back, this short pony tail will stay in place.',0,'PullBackFullPony',0,'',0,0,0,0,0,1,'',0),(1330,136,'PullBackBraid','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'PullbackBraid.swf',1,'Parted down the middle and pulled back, this elegant braid will stay in place.',0,'PullbackBraid',0,'',0,0,0,0,0,1,'',0),(1329,129,'oneBangBraid','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'OneBangBraid.swf',1,'Elegantly braided hair with just a bit of bangs escaping for fun.',0,'oneBangBraid',0,'',0,0,0,0,0,1,'',0),(1327,145,'Speed','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'SpeedBack.swf',1,'A pretty, windswept bob.',0,'Speed',0,'',0,0,0,0,0,1,'',0),(1328,146,'SpikeShort','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'SpikeShort.swf',1,'Short and spikey all over.',0,'SpikeShort',0,'',0,0,0,0,0,1,'',0),(1326,131,'OneBangLongPony','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'OneBangLongPony.swf',1,'A fun ponytail with just a bit of bangs escaping in front.',0,'OneBangLongPony',0,'',0,0,0,0,0,1,'',0),(1325,149,'StraitBangMed','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'StraitBangMed.swf',1,'Bette bangs and a bob, a deadly combination!',0,'StraitBangMed',0,'',0,0,0,0,0,1,'',0),(1324,151,'StyledShort','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'StyledShort.swf',1,'Short, stylish and chic. What more could you want',0,'StyledShort',0,'',0,0,0,0,0,1,'',0),(1323,153,'SwoopHairLong','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'SwoopHairLong.swf',1,'cute and styled to the side, just grazing the shoulders in back.',0,'SwoopHairLong',0,'',0,0,0,0,0,1,'',0),(1322,171,'zagHeadbandMed','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'ZagHeadbandMed.swf',1,'Shaggy, side swept hair that brushes your shoulders.',0,'zagHeadbandMed',0,'',0,0,0,0,0,1,'',0),(1321,163,'twoBangMed','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'TwoBangMed.swf',1,'Shoulder length hair with stylish bangs to frame the face.',0,'twoBangMed',0,'',0,0,0,0,0,1,'',0),(1320,161,'twoBangFullPony','None',0,10,0,0,'ha','Hair',530,10,1,'iihair',1,50,'TwoBangFullPony.swf',1,'A fun ponytail with stylish bangs to frame the face.',0,'TwoBangFullPony',0,'',0,0,0,0,0,1,'',0),(1319,160,'twoBangBraid','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'TwoBangBraid.swf',1,'Braided hair with stylish bangs to frame the face.',0,'twoBangBraid',0,'',0,0,0,0,0,1,'',0),(1318,155,'SwoopHairShort','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'SwoopHairShort.swf',1,'A cute bob that\'s styled to one side.',0,'SwoopHairShort',0,'',0,0,0,0,0,1,'',0),(1316,152,'Swoop','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'SwoopHair.swf',1,'Cute and styled to the side.',0,'Swoop',0,'',0,0,0,0,0,1,'',0),(1317,154,'SwoopHairMed','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'SwoopHairMed.swf',1,'A cute bob that\'s styled to one side.',0,'SwoopHairMed',0,'',0,0,0,0,0,1,'',0),(1315,150,'StraitBangShort','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'StraitBangShort.swf',1,'Bette bangs and a bob, a deadly combination!',0,'StraitBangShort',0,'',0,0,0,0,0,1,'',0),(1314,140,'PullBackMed','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'PullBackMed.swf',1,'Parted down the middle and slicked back, this hairstyle just touches the shoulders.',0,'PullBackMed',0,'',0,0,0,0,0,1,'',0),(1313,132,'OneBangMed','None',0,10,0,0,'ha','Hair',480,10,1,'iihair',1,50,'OneBangMed.swf',1,'Shoulder length tresses with a bit of bangs.',0,'OneBangMed',0,'',0,0,0,0,0,1,'',0),(1312,125,'MessyShort','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'MessyShortHair.swf',1,'Short n\' messy, you\'ve got better things to worry about than your hair style!',0,'MessyShort',0,'',0,0,0,0,0,1,'',0),(1311,124,'MedMessy','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'MessyMedHair.swf',1,'A short, asymmetrical cut.',0,'MedMessy',0,'',0,0,0,0,0,1,'',0),(1310,127,'MohawkShort','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'MohawkShortHair.swf',1,'Sleek, roman inspired mohawk.',0,'MohawkShort',0,'',0,0,0,0,0,1,'',0),(1309,122,'LongMessy','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'MessyLongHair.swf',1,'A short, asymmetrical cut.',0,'LongMessy',0,'',0,0,0,0,0,1,'',0),(1308,118,'HeadbandShort','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'Headband-Short.swf',1,'Cute and practical, this headband keeps your hair out of the way.',0,'HeadbandShort',0,'',0,0,0,0,0,1,'',0),(1307,117,'HeadbandMed','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'Headband-Med.swf',1,'Cute and practical, this headband keeps your hair out of the way.',0,'HeadbandMed',0,'',0,0,0,0,0,1,'',0),(1306,104,'Emo','None',0,10,0,0,'ha','Hair',460,10,1,'iihair',1,50,'Emo.swf',1,'Perfectly emo.',0,'Emo',0,'',0,0,0,0,0,1,'',0),(1305,43,'SpikeShort','None',0,10,0,0,'ha','Hair',450,10,1,'iihair',1,50,'SpikeShortHair.swf',1,'It takes a super amount of hair product to keep your \'do super spiky!',0,'Short Spiked Hair',0,'',0,0,0,0,0,1,'',0),(1304,142,'HairRusk','None',0,10,0,0,'ha','Hair',380,10,1,'iihair',1,50,'Rusk.swf',1,'Short and chic on the sides, messy on top.',0,'HairRusk',0,'',0,0,0,0,0,1,'',0),(1303,135,'PullBack','None',0,10,0,0,'ha','Hair',380,10,1,'iihair',1,50,'PullBack.swf',1,'Parted down the middle and completely slicked back, you won\'t ever have a hair out of place with this style.',0,'PullBack',0,'',0,0,0,0,0,1,'',0),(1302,121,'MessyF','None',0,10,0,0,'ha','Hair',380,10,1,'iihair',1,50,'Messy-F.swf',1,'A spikey, dangerous bob.',0,'MessyF',0,'',0,0,0,0,0,1,'',0),(1301,123,'MessyM','None',0,10,0,0,'ha','Hair',380,10,1,'iihair',1,50,'Messy-M.swf',1,'A spikey, dangerous cut.',0,'MessyM',0,'',0,0,0,0,0,1,'',0),(1300,159,'TwoBang','None',0,10,0,0,'ha','Hair',380,10,1,'iihair',1,50,'TwoBangBack.swf',1,'',0,'TwoBangBack',0,'',0,0,0,0,0,1,'',0),(1298,101,'Cut','None',0,10,0,0,'ha','Hair',220,10,1,'iihair',1,50,'Cut.swf',1,'Military chic!',0,'Cut',0,'',0,0,0,0,0,1,'',0),(1299,158,'TwoBang','None',0,10,0,0,'ha','Hair',290,10,1,'iihair',1,50,'TwoBang.swf',1,'Short hair with stylish bangs to frame the face.',0,'TwoBang',0,'',0,0,0,0,0,1,'',0);

UNLOCK TABLES;

/*Table structure for table `hs_maps` */

DROP TABLE IF EXISTS `hs_maps`;

CREATE TABLE `hs_maps` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `fileName` varchar(128) NOT NULL,
  `mapitems` varchar(128) NOT NULL,
  `monsternumb` varchar(128) NOT NULL,
  `monsterid` varchar(128) NOT NULL,
  `monsterframe` varchar(512) NOT NULL,
  `sExtra` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=141 DEFAULT CHARSET=latin1;

/*Data for the table `hs_maps` */

LOCK TABLES `hs_maps` WRITE;

insert  into `hs_maps`(`id`,`name`,`fileName`,`mapitems`,`monsternumb`,`monsterid`,`monsterframe`,`sExtra`) values (1,'battleon','Battleon/town-libertysquarenewyears.swf','','','','',''),(2,'park','scroll/war-test.swf','1:26','26,26,26,26,8,26,26,8,8,8,24,24,10,10,10,10,24,24,24,24','8,10,24,26','d1,d1,d1,d1,d1,d2,d2,d2,d2,d2,daV2,daV2,daV2,daV2,daV2,daV1,daV1,daV1,daV1,daV1',''),(3,'mainstreet','scroll/town-mainstreetholiday.swf','5:275,6:276','9,9,6,7,27,27,25,25,25,27,25,27,8,8,8,27,6,27,9,9,28,28,28,28,28,28,28,28,28,28,28,28,28','6,7,8,9,25,27,28','Stone,Stone,Stone,Stone,Enter,Enter,Enter,Enter,Enter,Enter,Enter,Enter,Enter,Enter,Enter,Stone,Stone,Stone,Stone,Stone,Stone,Stone,Stone,Stone,Second,Second,Second,Second,Enter,Enter,Enter,Enter,Enter','');

UNLOCK TABLES;

/*Table structure for table `hs_monsters` */

DROP TABLE IF EXISTS `hs_monsters`;

CREATE TABLE `hs_monsters` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sRace` varchar(32) NOT NULL DEFAULT 'None',
  `MonID` int(11) NOT NULL,
  `intMPMax` int(5) NOT NULL DEFAULT '100',
  `intGold` int(5) NOT NULL DEFAULT '350',
  `intLevel` int(3) NOT NULL DEFAULT '1',
  `strDrops` varchar(128) NOT NULL DEFAULT '0:0',
  `intExp` int(5) NOT NULL DEFAULT '450',
  `iDPS` int(3) NOT NULL DEFAULT '1',
  `intHPMax` int(5) NOT NULL,
  `strElement` varchar(32) NOT NULL DEFAULT 'None',
  `intRSC` int(3) NOT NULL DEFAULT '0',
  `strLinkage` varchar(32) NOT NULL,
  `strMonFileName` varchar(128) NOT NULL,
  `strMonName` varchar(64) NOT NULL,
  `intRep` int(3) NOT NULL DEFAULT '15',
  `react` varchar(60) NOT NULL DEFAULT ' ',
  `pvpscore` int(2) NOT NULL DEFAULT '50',
  PRIMARY KEY (`id`,`MonID`)
) ENGINE=MyISAM AUTO_INCREMENT=1339 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_monsters` */

LOCK TABLES `hs_monsters` WRITE;

insert  into `hs_monsters`(`id`,`sRace`,`MonID`,`intMPMax`,`intGold`,`intLevel`,`strDrops`,`intExp`,`iDPS`,`intHPMax`,`strElement`,`intRSC`,`strLinkage`,`strMonFileName`,`strMonName`,`intRep`,`react`,`pvpscore`) values (1,'Human',8,100,33,1,'27:1,32:1,50:.70',11,1,300,'None',0,'flySWAT','robo-flySWAT.swf','Jetpack Patrolman',15,' ',50),(2,'Human',10,100,33,1,'35:1,280:1,52:.70,277:1',11,1,300,'None',0,'DavinciBot3','SteampunkRobot3.swf','Hacked APB',15,' ',50),(3,'Human',24,100,33,1,'33:1,280:1,51:.90,277:1',11,1,430,'None',0,'KillerBot1','KillerBot1.swf','Davinci Killbot',15,' ',50),(4,'Human',26,100,33,1,'28:1,280:1,49:.90,277:1',11,1,430,'None',0,'DemoDiva','DemoDiva1.swf','Demo Diva',15,' ',50),(5,'Human',6,100,33,1,'280:1,29:.60,30:.65,277:1',11,1,430,'None',0,'truck','mon-truck.swf','Mechanics Truck',15,' ',50),(6,'Human',7,100,33,1,'31:1',11,1,850,'None',0,'DoorGeneric','DoorGeneric.swf','Store Door',15,' ',50),(7,'Human',9,100,33,1,'34:1',11,1,430,'None',0,'goonChainsaw','GoonChainsaw.swf','Truck Raider',15,' ',50),(8,'Human',25,100,33,1,'280:1,277:1',11,1,430,'None',0,'DavinciBot1','SteampunkRobot1.swf','APB',15,' ',50),(9,'Human',27,100,33,1,'280:1,277:1,81:.25',11,1,430,'None',0,'KillerBot1','KillerBot1.swf','Destroyer',15,' ',50),(10,'Human',28,100,33,1,'36:1,280:1,277:1',11,1,1,'None',0,'monCamera2','monCamera2.swf','Rewired Camera',15,' ',50);

UNLOCK TABLES;

/*Table structure for table `hs_passives` */

DROP TABLE IF EXISTS `hs_passives`;

CREATE TABLE `hs_passives` (
  `name` varchar(60) DEFAULT NULL,
  `icon` varchar(20) DEFAULT NULL,
  `desc` text,
  `ref` varchar(10) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `auraID` int(10) NOT NULL DEFAULT '0',
  UNIQUE KEY `id` (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=18 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_passives` */

LOCK TABLES `hs_passives` WRITE;

insert  into `hs_passives`(`name`,`icon`,`desc`,`ref`,`type`,`id`,`auraID`) values ('Blank','isp2','Temporary Passive Skill','p1','passive',1,0);

UNLOCK TABLES;

/*Table structure for table `hs_quests` */

DROP TABLE IF EXISTS `hs_quests`;

CREATE TABLE `hs_quests` (
  `id` int(5) NOT NULL,
  `sFaction` char(32) NOT NULL DEFAULT 'None',
  `iLvl` int(10) NOT NULL DEFAULT '1',
  `factionID` tinyint(3) NOT NULL DEFAULT '1',
  `iWar` tinyint(3) NOT NULL,
  `iClass` tinyint(2) NOT NULL DEFAULT '0',
  `iReqRep` int(11) NOT NULL DEFAULT '0',
  `iValue` int(11) NOT NULL DEFAULT '0',
  `iSlot` int(11) NOT NULL DEFAULT '-1',
  `iGold` int(32) NOT NULL DEFAULT '0',
  `iRep` int(11) NOT NULL DEFAULT '0',
  `turnin` text,
  `sEndText` text,
  `bUpg` tinyint(2) NOT NULL DEFAULT '0',
  `iReqCP` int(11) NOT NULL DEFAULT '0',
  `iExp` int(11) NOT NULL DEFAULT '100',
  `sName` varchar(32) DEFAULT NULL,
  `oItems` text,
  `rewType` varchar(5) NOT NULL DEFAULT 'S',
  `oRewards` text,
  `bOnce` tinyint(3) NOT NULL DEFAULT '0',
  `sDesc` text
) ENGINE=MyISAM DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_quests` */

LOCK TABLES `hs_quests` WRITE;

insert  into `hs_quests`(`id`,`sFaction`,`iLvl`,`factionID`,`iWar`,`iClass`,`iReqRep`,`iValue`,`iSlot`,`iGold`,`iRep`,`turnin`,`sEndText`,`bUpg`,`iReqCP`,`iExp`,`sName`,`oItems`,`rewType`,`oRewards`,`bOnce`,`sDesc`) values (9,'Good',1,2,0,0,0,0,-1,44,200,'34:9','You got \'em, did ya? I\'m sure that\'ll throw a wrench in DaVinci\'s plans!',0,0,120,'Pre-emptive Protection','34','S','',0,'I\'ve still got lots to do here but I did notice a few goons heading EAST off towards Stone Avenue. I don\'t know exactly what they\'re up to but I know it can\'t be good.  Stop by and let them know they gotta go through us first!\r\n\r\n~-Head to Stone Avenue\r\n-Defeat 9 Truck Raiders\r\n-Return to Demolicious'),(10,'Good',1,2,0,0,0,0,-1,50,250,'35:8','You seem to be enjoying turning those machines to rubble.',0,0,150,'Take Down!','35','S','',0,'He is a crafty one, I\'ll give him that.  Somehow DaVinci managed to hack an entire batch of flying Artificial Police Bots (APB).  He\'s using our own police force against us!  That\'s lower than low.  We\'ve gotta stop \'em. Fly over to the park and show \'em we aint gonna back down!\r\n\r\n~-Head to Aurora Park\r\n-Break 8 hacked APBs\r\n-Return to Demolicious'),(8,'Good',1,2,0,0,0,0,-1,50,50,'33:10','Great Work.',0,0,100,'Demolicious\' Assistant','33','S','',0,'Hiya honey, we need your help protecting the monument from Luigi DaVinci.  His Killbots have been swarming the park.  I\'m certain they plan to rip that monument apart to get to the Pandorian Malachite.\r\n\r\n~-Head to Aurora Park\r\n-Defeat 10 Killbots\r\n-Return to Demolicious'),(11,'Good',1,2,0,0,0,0,-1,42,200,'36:10','A minor set back for someone like DaVinci.  I\'m sure there will be more where that came from.',0,0,100,'Camera SMASHING Spree!','36','S','',0,'The APBs have been reporting errors with the security cameras all over the city.  No doubt it\'s part of DaVinci\'s master plan.  If you spot any rewired camera\'s, SMASH \'em up good will ya?\\\r\n\r\n~-Fly around Main Street and Stone Ave\r\n-SMASH 12 Rewired Cameras\r\n-Return to Demolicious'),(12,'Good',1,2,0,0,0,0,-1,100,250,'27:5','Hope dis battle ends soon... we gotta avoid any more casualties.',0,0,30,'Get \'em Outta There!','27','S','',0,'The Demo Divas can fend for themselves but this war is takin\' its toll on the city\'s patrolmen.  For the sake of all our tax dollars, get the broken down patrolmen out of there!\r\n\r\n~-Head to Aurora Park\r\n-Assist 5 Broken Patrolmen out of the park.\r\n-Return to Demolicious'),(36,'Evil',1,3,0,0,0,1,2,300,500,'276:10','Ha ha ha! That\'s one for the books.  Best prank ever!',0,0,100,'Hide the Presents','276','S','',1,'Listen up! I\'ve got a fun game for those puny orphans.  I\'ve stolen all their presents and I want you to hide them around the city.  Only the strong survive!  Let\'s make \'em work for their goodies this year.'),(39,'None',1,1,0,0,0,0,-1,30,0,'280:5','Ok, let\'s see what I can do with this!',0,0,30,'Something from Nothing?','280','R','279:274',0,'I\'m kind of in the mood for some tinkering, so if you want to help me out I might have something for you, too.  I need lots of SPARE PARTS.  I\'m sure you can find them on the robots swarming the city.'),(34,'Good',1,2,0,0,0,1,1,300,500,'275:10','You did it! You did it! OH MY! Thank you so much!  I\'m sure the orphans will be ever grateful.',0,0,100,'Find the Presents','275','S','',1,'Please help me, Doodie has been stealing all of the orphan\'s presents and hiding them around the city.  Please find them and bring them back so the children can have a happy holiday.'),(37,'None',1,1,0,0,0,0,-1,30,0,'277:5','Ah hah! This will do...',0,0,30,'Something from Something?','277','R','278:274',0,'Ya know, I think I might be able to spread some holiday cheer to you!  It might take a few tries, but if you bring me enough SPARE PARTS, I might be able to turn them into into something VERY special.  Check  the robots around the city for spare parts.'),(3,'Evil',1,3,0,0,0,0,-1,30,50,'28:10','Excellent! That was quick work.  Got enough in ya for another round?',0,0,100,'DaVinci\'s Assistant','28','S','',0,'Hey there, you look like a powerful one.  I\'ve got a proposition for ya, if ya think you\'ve got what it takes!  That nuisance Demolicious has her bothersome little Divas ravaging the park.  Take out a few of \'em for me and I might consider letting you join the strongest force in Liberty City.\r\n\r\n~-Head to Aurora Park\r\n- Defeat 10 Demo Diva\'s at Aurora Park.\r\n- Return to Luigi DaVinci.'),(7,'Evil',1,3,0,0,0,0,-1,50,250,'32:8','HA!  You are quite the kidder... wa?  You mean you took them out.  Really?  Maybe you are ready for bigger fish to fry.',0,0,150,'Her New Associates','32','S','',0,'That devious Demolicious has gone to measures unbelievably low.  Now she has the local patrolmen fighting for her... not that it will matter.  Either way they will be dealt with. I guess the question is: Who\'s side do you see yourself on?\r\n\r\n~-Head to Aurora Park\r\n-Defeat 8 Jet-pack Patrolmen\r\n-Return to Luigi DaVinci'),(6,'Evil',1,3,0,0,0,0,-1,100,250,'31:1','Ah, yes. Yes! Just what I was looking for...  What are you waiting for?  A thank-you?  Don\'t you have Divas to destroy?',0,0,30,'Mandatory Supplies','31','S','',0,'Always an eye for an eye.  The world will not buckle unless we force it to!  If they are going to blind my newly opened eyes, we shall reopen them wider!  Quickly, head to Eolon\'s Electronics and bring me some \'additional supplies\' so I can rewire more cameras.\r\n\r\n~-Eolon\'s Electronics is East of DaVinci on Stone Ave\r\n-Break inside and collect Various Electronics.\r\n-Return to Luigi DaVinci'),(5,'Evil',1,3,0,0,0,0,-1,40,200,'26:10','Excellent, who knew you would actually pull it off.  We should be able to see their every move now... What\'s this... Demolicious is smashing them up again!',0,0,100,'A Closer Examination','26','S','',0,'It seems the City has set up surveillance all over the park.  I am not sure why, but I am sure it is giving Demolicious the upper hand.  Rewire any cameras you find so we gain the advantage.  If you don\'t know how you should probably just join the weaklings.\r\n\r\n~-Head to Aurora Park.\r\n-Rewire 10 cameras.\r\n-Return to Luigi DaVinci.'),(4,'Evil',1,3,0,0,0,0,-1,44,200,'29:7,30:4','Would you look at that! You actually did it.  Well, hmm...  these will do nicely but there isn\'t near enough.',0,0,120,'The Necessary Supplies','29:30','S','',0,'Rrrraaaah! That Demolicious and her unbreakable perseverance!  Listen kid,  I need to replenish my \'servants\'.  Just east of here is Stone Ave.  I saw some Edward\'s Mechanics trucks head that way.  Bring me back some supplies.  If you don\'t return, I\'ll assume you didn\'t have it in you to pull it off.\r\n\r\n~-Find \'Edward\'s Mechanics Trucks on Stone Avenue\r\n -borrow 7 metal casings\r\n -procure 4 hardened gaskets \r\n -Return to Luigi DaVinci'),(24,'Evil',1,3,2,0,0,0,-1,35,15,'50:1','',0,0,20,'Big Favor for DaVinci','50','S','',0,''),(23,'Evil',1,3,2,0,0,0,-1,30,10,'49:5','',0,0,15,'Favor for DaVinci','49','S','',0,''),(26,'Good',1,2,1,0,0,0,-1,35,15,'52:1','',0,0,20,'Big Favor for Demolicious','52','S','',0,''),(25,'Good',1,2,1,0,0,0,-1,30,10,'51:5','',0,0,15,'Favor for Demolicious','51','S','',0,'');

UNLOCK TABLES;

/*Table structure for table `hs_servers` */

DROP TABLE IF EXISTS `hs_servers`;

CREATE TABLE `hs_servers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `ip` varchar(15) NOT NULL,
  `count` smallint(5) NOT NULL DEFAULT '0',
  `max` smallint(5) NOT NULL DEFAULT '255',
  `online` tinyint(1) NOT NULL DEFAULT '1',
  `ichat` tinyint(1) NOT NULL DEFAULT '0',
  `upgrade` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

/*Data for the table `hs_servers` */

LOCK TABLES `hs_servers` WRITE;

insert  into `hs_servers`(`id`,`name`,`ip`,`count`,`max`,`online`,`ichat`,`upgrade`) values (1,'Mystical','5.90.1.51',0,255,0,2,0);

UNLOCK TABLES;

/*Table structure for table `hs_settings` */

DROP TABLE IF EXISTS `hs_settings`;

CREATE TABLE `hs_settings` (
  `name` varchar(64) NOT NULL,
  `loginkey` varchar(60) NOT NULL DEFAULT 'N7B5W8W1Y5B1R5O7B2',
  `news` text NOT NULL,
  `version` varchar(60) NOT NULL,
  `message` text NOT NULL,
  `gameFile` varchar(64) NOT NULL,
  `gameFilePTR` varchar(64) NOT NULL,
  `newsFile` varchar(64) NOT NULL,
  `mapFile` varchar(64) NOT NULL,
  `bookFile` varchar(64) NOT NULL,
  `xprate` smallint(2) NOT NULL,
  `maxlevel` smallint(2) NOT NULL DEFAULT '100',
  `goldrate` tinyint(1) NOT NULL,
  `mondmgrate` int(11) NOT NULL,
  `event` text,
  `accesscode` varchar(60) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*Data for the table `hs_settings` */

LOCK TABLES `hs_settings` WRITE;

insert  into `hs_settings`(`name`,`loginkey`,`news`,`version`,`message`,`gameFile`,`gameFilePTR`,`newsFile`,`mapFile`,`bookFile`,`xprate`,`maxlevel`,`goldrate`,`mondmgrate`,`event`,`accesscode`) values ('LOL','N7B5W8W1Y5B1R5O7B2','Mystical\'s Super City needs you!  As the city\'s newest Hero or Villain you will run and fly around the city performing acts of great awesomness!  Explore the city with hundreds of other players fighting your enemies, increasing your Fame, and using that Fame to customize your Hero or Villain with new costumes, gloves, capes, weapons, masks, markings, helmets, and much more!  This is ALPHA TESTING Phase.  Later on, you will be able to get super-abilities, too!','e80','Staff will never ask for your password - if someone asks for your password, report them for Griefing. NEVER give out your password, no matter what.','','','news/News-27Aug10.swf','news/Map-27Aug10.swf','news/Book-17Aug10.swf',1,100,1,0,'','none');

UNLOCK TABLES;

/*Table structure for table `hs_shops` */

DROP TABLE IF EXISTS `hs_shops`;

CREATE TABLE `hs_shops` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `shopid` int(11) NOT NULL,
  `strName` varchar(32) NOT NULL,
  `items` text NOT NULL,
  `sField` varchar(16) NOT NULL DEFAULT '',
  `bStaff` tinyint(1) NOT NULL DEFAULT '0',
  `bHouse` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2006 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_shops` */

LOCK TABLES `hs_shops` WRITE;

insert  into `hs_shops`(`id`,`shopid`,`strName`,`items`,`sField`,`bStaff`,`bHouse`) values (1,2,'TITANiumThreads','62,64,63,65,48,66,185,186,187,188,189,190,191,192,193,194,195,196,197,47,54,41,199,206,211,201,202,203,204,205,231,200,198,218,219,220,221,222,223,224,225,226,227,229,212,213,214,215,216,207,208,209,210,230,53,228,217,176,177,46,182,175,174,178,179,180,173,181,183,184,57,58,61,56,55,59,60,60','',0,0),(2,3,'BOOMingdales','237,241,242,243,244,245,2,238,239,240,234,54,81,82,232,233,78,75,76,67,69,79,77,235,236','',0,0),(3,4,'Beleen\'s Beauty Botique','157,101,158,159,123,121,135,142,41,43,104,117,118,122,127,124,125,132,140,150,152,154,155,160,161,163,171,153,151,149,131,145,146,129,136,137,126,119,109,115,106,103,111,112,92,93,99,95,96,97,98,102,128,138,130,134,148,144,139,105,113,114,108,116,110,162,165,166,167,168,169,170,172,100,143,44,120,107,141,133,164','',0,0),(2004,6,'Evil Elf Shop','295,297,296,284,285,286,294,299','',0,0),(2005,5,'Good Elf Shop','273,291,292,281,282,283,293,298','',0,0);

UNLOCK TABLES;

/*Table structure for table `hs_skills` */

DROP TABLE IF EXISTS `hs_skills`;

CREATE TABLE `hs_skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `anim` varchar(60) NOT NULL,
  `desc` text NOT NULL,
  `iscrit` varchar(32) NOT NULL DEFAULT 'false',
  `damage` varchar(12) NOT NULL DEFAULT '1.0',
  `mana` int(11) NOT NULL DEFAULT '0',
  `icon` varchar(32) NOT NULL,
  `range` int(5) NOT NULL DEFAULT '303',
  `dsrc` varchar(8) NOT NULL,
  `ref` varchar(3) NOT NULL,
  `tgt` varchar(2) NOT NULL DEFAULT 'h',
  `typ` varchar(3) NOT NULL DEFAULT 'p',
  `str1` varchar(32) NOT NULL,
  `auto` varchar(6) NOT NULL DEFAULT 'false',
  `cd` int(5) NOT NULL,
  `aura` int(4) NOT NULL DEFAULT '0',
  `tgtMax` int(10) NOT NULL DEFAULT '0',
  `tgtMin` int(5) NOT NULL DEFAULT '0',
  `fx` varchar(20) DEFAULT 'm',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=71 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_skills` */

LOCK TABLES `hs_skills` WRITE;

insert  into `hs_skills`(`id`,`name`,`anim`,`desc`,`iscrit`,`damage`,`mana`,`icon`,`range`,`dsrc`,`ref`,`tgt`,`typ`,`str1`,`auto`,`cd`,`aura`,`tgtMax`,`tgtMin`,`fx`) values (1,'Auto Attack','Attack1,Attack2','A basic attack, taught to all adventurers.','false','1.0',0,'iwd1',303,'AP2','aa','h','p','','true',2000,0,0,0,'m');

UNLOCK TABLES;

/*Table structure for table `hs_skills_auras` */

DROP TABLE IF EXISTS `hs_skills_auras`;

CREATE TABLE `hs_skills_auras` (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL DEFAULT 'Aura',
  `seconds` int(12) NOT NULL DEFAULT '5',
  `iscrit` varchar(12) NOT NULL DEFAULT 'false',
  `reduction` varchar(11) NOT NULL DEFAULT '0',
  `damage` varchar(10) NOT NULL DEFAULT '1.5',
  `cat` varchar(12) NOT NULL DEFAULT '',
  `type` varchar(15) NOT NULL DEFAULT 'passive',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=14 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_skills_auras` */

LOCK TABLES `hs_skills_auras` WRITE;

UNLOCK TABLES;

/*Table structure for table `hs_users` */

DROP TABLE IF EXISTS `hs_users`;

CREATE TABLE `hs_users` (
  `id` smallint(3) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(60) NOT NULL,
  `password` char(32) NOT NULL,
  `access` smallint(21) NOT NULL DEFAULT '60',
  `age` tinyint(2) unsigned NOT NULL,
  `active` tinyint(1) unsigned NOT NULL,
  `email` varchar(50) NOT NULL,
  `banned` tinyint(1) unsigned NOT NULL,
  `signupip` varchar(15) NOT NULL,
  `loginip` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_users` */

LOCK TABLES `hs_users` WRITE;

insert  into `hs_users`(`id`,`username`,`password`,`access`,`age`,`active`,`email`,`banned`,`signupip`,`loginip`) values (1,'Mystical','d006e383610a0d6b332505c23936a733',60,15,1,'mysticaltm@live.com',0,'','5.90.1.51'),(2,'Zeroskull','d006e383610a0d6b332505c23936a733',60,15,1,'idk',0,'','5.90.1.51'),(3,'DarkZeroskull','5f4dcc3b5aa765d61d8327deb882cf99',60,15,1,'yeah',0,'',''),(4,'Xiaggy','f141f0b51dd83e244624586e12609034',60,15,1,'agaicra@ambut.org',0,'','5.39.183.30'),(5,'TheLazyMan','d006e383610a0d6b332505c23936a733',60,15,1,'imba@live.com',0,'',''),(6,'HCRonin105','9da25877625704ff2b9935d75122d8d2',60,15,1,'Hiro.Cain@hotmail.com',0,'','5.16.98.132'),(7,'HardcoreMan123','21f0b63d297aa90454b2ee66e333aef5',60,15,1,'STO2_divi_rules@live.com',0,'5.211.205.86','5.211.205.86'),(8,'Akomismo','775a6c7f59cf9422d1ba16a2ee6ee312',60,15,1,'jaidjiasjdias2@yahoo.com',0,'5.111.33.149','5.111.33.149'),(9,'AmrNashaat','431304d578917699ee7f89462e34ca7a',60,15,1,'Lol@hotmail.com',0,'5.58.231.129','5.58.231.129'),(10,'Zanjjm123','bb386c84d6eb5369af84b44594d369b1',60,15,1,'zanjjm@hotmail.com',0,'5.170.77.186','5.170.77.186'),(11,'zanjjm12345667','bb386c84d6eb5369af84b44594d369b1',60,15,1,'zanjjm@hotmail.com',0,'5.170.77.186','5.170.77.186'),(12,'Heroman123','bb386c84d6eb5369af84b44594d369b1',60,15,1,'zanjjm@live.com',0,'5.170.77.186','5.170.77.186'),(13,'kamenashi','77d3f30118d8f234b71b07e8fbbb6061',60,15,1,'kamenashi24@yahoo.com',0,'','5.118.144.66'),(14,'jerico24','77d3f30118d8f234b71b07e8fbbb6061',60,15,1,'jerico_cutie24@yahoo.com',0,'','5.118.144.66');

UNLOCK TABLES;

/*Table structure for table `hs_users_characters` */

DROP TABLE IF EXISTS `hs_users_characters`;

CREATE TABLE `hs_users_characters` (
  `id` mediumint(11) NOT NULL AUTO_INCREMENT,
  `userid` mediumint(11) NOT NULL,
  `sName` varchar(60) NOT NULL,
  `strGender` char(1) NOT NULL,
  `iLvl` smallint(11) NOT NULL DEFAULT '1',
  `iEye` smallint(5) NOT NULL DEFAULT '0',
  `iMouth` smallint(5) NOT NULL DEFAULT '12',
  `iNose` smallint(5) NOT NULL DEFAULT '2',
  `ia0` mediumint(10) NOT NULL DEFAULT '0',
  `iUpgDays` mediumint(10) NOT NULL DEFAULT '0',
  `iUpg` tinyint(1) NOT NULL DEFAULT '0',
  `iBankSlots` mediumint(10) NOT NULL DEFAULT '40',
  `iHouseSlots` mediumint(10) NOT NULL DEFAULT '0',
  `iBagSlots` mediumint(10) NOT NULL DEFAULT '0',
  `intCoins` int(11) NOT NULL DEFAULT '0',
  `intGold` int(11) NOT NULL DEFAULT '0',
  `intExp` int(11) NOT NULL DEFAULT '0',
  `intActivationFlag` smallint(5) NOT NULL DEFAULT '5',
  `intAccessLevel` smallint(5) NOT NULL DEFAULT '0',
  `intColorSkin` int(40) NOT NULL DEFAULT '0',
  `intColorEye` int(40) NOT NULL DEFAULT '0',
  `intColorHair` int(40) NOT NULL DEFAULT '0',
  `intColorBase` int(40) NOT NULL DEFAULT '0',
  `intColorTrim` int(40) NOT NULL DEFAULT '0',
  `intColorAccessory` int(40) NOT NULL DEFAULT '0',
  `lastArea` varchar(40) NOT NULL DEFAULT 'battleon-3',
  `HairID` smallint(5) NOT NULL DEFAULT '1',
  `strHairName` varchar(60) NOT NULL DEFAULT 'None',
  `strHairFilename` varchar(60) NOT NULL DEFAULT 'None',
  `strQuests` varchar(60) NOT NULL DEFAULT '00000000000000000000000000000000000000000000000000',
  `curServer` varchar(40) NOT NULL DEFAULT 'Offline',
  `bPet` tinyint(1) NOT NULL DEFAULT '1',
  `bTT` tinyint(1) NOT NULL DEFAULT '1',
  `bParty` tinyint(1) NOT NULL DEFAULT '1',
  `bCloak` tinyint(1) NOT NULL DEFAULT '1',
  `bHelm` tinyint(1) NOT NULL DEFAULT '1',
  `bGoto` tinyint(1) NOT NULL DEFAULT '1',
  `bWhisper` tinyint(1) NOT NULL DEFAULT '1',
  `bFriend` tinyint(1) NOT NULL DEFAULT '1',
  `bSoundOn` tinyint(1) NOT NULL DEFAULT '1',
  `monkill` mediumint(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;

/*Data for the table `hs_users_characters` */

LOCK TABLES `hs_users_characters` WRITE;

insert  into `hs_users_characters`(`id`,`userid`,`sName`,`strGender`,`iLvl`,`iEye`,`iMouth`,`iNose`,`ia0`,`iUpgDays`,`iUpg`,`iBankSlots`,`iHouseSlots`,`iBagSlots`,`intCoins`,`intGold`,`intExp`,`intActivationFlag`,`intAccessLevel`,`intColorSkin`,`intColorEye`,`intColorHair`,`intColorBase`,`intColorTrim`,`intColorAccessory`,`lastArea`,`HairID`,`strHairName`,`strHairFilename`,`strQuests`,`curServer`,`bPet`,`bTT`,`bParty`,`bCloak`,`bHelm`,`bGoto`,`bWhisper`,`bFriend`,`bSoundOn`,`monkill`) values (27,8,'0911A1','M',3,5,0,0,0,0,0,20,0,20,0,5146,275,5,0,15982797,6684672,3678742,3765088,10038573,16777215,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,0,48),(28,7,'ERZA','F',2,0,0,0,0,0,0,20,0,20,0,1683,0,5,0,13088131,10432464,4786437,3765088,10038573,16777215,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,40),(25,6,'HIRO','M',5,14,0,11,0,0,0,20,0,20,0,118877,303,5,0,13088131,65535,13028046,10540396,10038573,16777215,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,68),(2,2,'MYSTICALX','F',100,8,7,7,0,0,0,20,0,20,0,533563,0,5,60,16764057,16777215,6182021,65793,65793,65793,'battleon-3',1,'None','None','01100000000000000000000000000000000000000000000000','Offline',1,1,0,1,1,0,0,0,0,303),(23,4,'XIAGGY','M',1,0,1,11,0,0,0,20,0,20,0,0,0,5,0,15388042,16777215,9265949,9673634,14922046,4786437,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,37),(24,3,'DAEMON','M',1,0,0,0,0,0,0,20,0,20,0,0,0,5,60,13088131,16777215,15828225,14515004,3366536,3559294,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,37),(22,2,'ALIENATOR','M',6,0,0,0,0,0,0,20,0,20,0,188475,450,5,0,13088131,16777215,3678742,3157797,5674563,3560001,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,0,52),(29,9,'SLAYER','M',100,0,0,0,0,0,0,20,0,20,0,165825,0,5,40,13088131,16777215,3678742,3765088,10038573,16777215,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,42),(31,12,'ZANJJM','M',1,0,0,0,0,0,0,20,0,20,0,0,0,5,0,13088131,16777215,3678742,3765088,10038573,16777215,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,37),(34,14,'KAMENASHI','M',5,7,0,0,0,0,0,20,0,20,0,1320016,0,5,0,13088131,2635172,3033928,2500663,16777215,16777215,'battleon-3',1,'None','None','00000000000000000000000000000000000000000000000000','Offline',1,1,1,1,1,1,1,1,1,41);

UNLOCK TABLES;

/*Table structure for table `hs_users_factions` */

DROP TABLE IF EXISTS `hs_users_factions`;

CREATE TABLE `hs_users_factions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL DEFAULT '0',
  `factionid` int(11) NOT NULL DEFAULT '0',
  `iRep` int(11) NOT NULL DEFAULT '0',
  `sName` varchar(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;

/*Data for the table `hs_users_factions` */

LOCK TABLES `hs_users_factions` WRITE;

insert  into `hs_users_factions`(`id`,`userid`,`factionid`,`iRep`,`sName`) values (3,2,1,302500,'Mystical Networks CEO'),(4,2,4,302500,'Moderator'),(6,2,2,10,'Good'),(9,25,2,200,'Good'),(10,2,3,10,'Evil');

UNLOCK TABLES;

/*Table structure for table `hs_users_friends` */

DROP TABLE IF EXISTS `hs_users_friends`;

CREATE TABLE `hs_users_friends` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `friendid` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_users_friends` */

LOCK TABLES `hs_users_friends` WRITE;

insert  into `hs_users_friends`(`id`,`userid`,`friendid`) values (2,2,'25,22,26,27,29'),(11,2,'25,22,26,27,29'),(12,22,'2'),(13,23,''),(14,24,''),(15,25,'2'),(17,27,'2'),(18,29,'2'),(20,31,''),(23,34,'');

UNLOCK TABLES;

/*Table structure for table `hs_users_items` */

DROP TABLE IF EXISTS `hs_users_items`;

CREATE TABLE `hs_users_items` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `iQty` smallint(3) NOT NULL DEFAULT '1',
  `itemid` mediumint(6) unsigned NOT NULL,
  `userid` smallint(3) unsigned NOT NULL,
  `classXP` mediumint(6) unsigned DEFAULT '0',
  `className` char(18) DEFAULT '',
  `equipped` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `sES` varchar(8) NOT NULL,
  `bBank` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `iLvl` smallint(3) unsigned NOT NULL DEFAULT '1',
  `EnhID` tinyint(2) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=latin1 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `hs_users_items` */

LOCK TABLES `hs_users_items` WRITE;

insert  into `hs_users_items`(`id`,`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values (27,1,66,2,9653,'Luchateer Armor',0,'ar',0,1,-1),(28,0,235,2,0,'',0,'we',0,1,-1),(29,0,171,2,0,'',0,'ha',0,1,-1),(89,1,48,22,1186,'',1,'ar',0,1,-1),(90,1,45,22,0,'',1,'sc',0,1,-1),(91,0,190,22,0,'',1,'ba',0,1,-1),(92,0,76,22,0,'',1,'we',0,1,-1),(93,0,41,22,0,'',1,'ha',0,1,-1),(94,0,46,22,0,'',1,'ma',0,1,-1),(95,1,48,23,0,'',1,'ar',0,1,-1),(96,1,45,23,0,'',1,'sc',0,1,-1),(97,0,54,23,0,'',1,'gl',0,1,-1),(98,0,98,23,0,'',1,'ha',0,1,-1),(99,1,48,24,0,'',1,'ar',0,1,-1),(100,1,261,24,0,'',1,'sc',0,1,-1),(101,0,188,24,0,'',1,'ba',0,1,-1),(102,0,54,24,0,'',1,'gl',0,1,-1),(103,0,79,24,0,'',1,'we',0,1,-1),(104,0,172,24,0,'',1,'ha',0,1,-1),(105,0,177,24,0,'',1,'ma',0,1,-1),(106,1,85,25,418,'',0,'ar',0,1,-1),(107,1,45,25,0,'',1,'sc',0,1,-1),(108,0,47,25,0,'',1,'ba',0,1,-1),(109,0,54,25,0,'',1,'gl',0,1,-1),(110,0,235,25,0,'',0,'we',0,1,-1),(111,0,98,25,0,'',1,'ha',0,1,-1),(112,0,179,25,0,'',1,'ma',0,1,-1),(113,1,48,25,906,'Darkness Armor',1,'ar',0,1,1),(114,1,2,25,0,'',1,'we',0,1,1),(115,1,82,25,0,'',0,'gl',0,1,1),(116,1,60,2,0,'',1,'sc',0,1,1),(117,1,279,2,35975,'Dark Elf Armor',1,'ar',0,0,-1),(118,1,299,2,0,'',1,'ma',0,1,1),(119,1,294,2,0,'',1,'he',0,1,1),(120,1,286,2,0,'',1,'ha',0,1,1),(121,1,194,2,0,'',1,'ba',0,1,1),(123,1,48,27,2757,'',1,'ar',0,1,-1),(124,1,45,27,0,'',1,'sc',0,1,-1),(125,0,47,27,0,'',1,'ba',0,1,-1),(126,0,234,27,0,'',1,'gl',0,1,-1),(127,0,235,27,0,'',1,'we',0,1,-1),(128,0,98,27,0,'',1,'ha',0,1,-1),(132,1,66,28,804,'',1,'ar',0,1,-1),(133,0,47,28,0,'',1,'ba',0,1,-1),(134,0,81,28,0,'',1,'gl',0,1,-1),(135,0,235,28,0,'',1,'we',0,1,-1),(136,0,105,28,0,'',1,'ha',0,1,-1),(137,0,38,29,0,'',1,'ha',0,1,-1),(138,1,296,2,0,'',1,'we',0,1,1),(139,1,278,2,0,'Elf Armor',0,'ar',0,0,-1),(141,0,41,31,0,'',1,'ha',0,1,-1),(146,1,48,34,302500,'',1,'ar',0,1,-1),(147,1,247,34,0,'',1,'sc',0,1,-1),(148,0,47,34,0,'',1,'ba',0,1,-1),(149,0,81,34,0,'',1,'gl',0,1,-1),(150,0,235,34,0,'',1,'we',0,1,-1),(151,0,100,34,0,'',1,'ha',0,1,-1),(152,5,28,34,0,'',0,'None',0,1,1),(153,1,49,34,0,'',0,'None',0,1,1),(154,10,26,34,0,'',0,'None',0,1,1);

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
