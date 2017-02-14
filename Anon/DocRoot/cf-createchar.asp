<?php
/**
	HeroSmash Character Creation Mod for Private Servers
	File: cf-createchar.asp or cf-createchar.php
	Author: Mystical
**/	

/** Retrieve configs **/
require_once("config.inc.php");

/** Check if character creation request is being processed... **/
if(isset($_POST['uuu'])) {
	
	/** Connect to Database **/
	$con = mysql_connect($cfg['sql']['host'], $cfg['sql']['user'], $cfg['sql']['pass']) or 
	die("status=failed&strMsg=The character creation server could not be reached! Please try again later!");
	mysql_select_db($cfg['sql']['name'], $con) or die("status=failed&strMsg=The character creation server could not be reached! Please try again later!");
	
	/** Retrieve post data **/
	$user = $_POST['uuu'];
	$pass = $_POST['ttt'];
	$char_name = $_POST['strCharName'];
	$char_gender = $_POST['strGender'];
	$char_eye = $_POST['intEyeID'];
	$char_mouth = $_POST['intMouthID'];
	$char_nose = $_POST['intNoseID'];
	$char_skincolor = $_POST['intColorSkin'];
	$char_eyecolor = $_POST['intColorEye'];
	$char_haircolor = $_POST['intColorHair'];
	$char_basecolor = $_POST['intColorBase'];
	$char_trimcolor = $_POST['intColorTrim'];
	$char_accecolor = $_POST['intColorAccessory'];
	$char_mask = $_POST['intMask'];
	$char_hair = $_POST['intHair'];
	$char_gloves = $_POST['intGloves'];
	$char_cape = $_POST['intCape'];
	$char_helm = $_POST['intHelm'];
	$char_scar = $_POST['intScar'];
	$char_armor = $_POST['intArmor'];
	$char_weapon = $_POST['intWeapon'];
	
	/** Retrieve user data **/
	$userdata = mysql_query("SELECT id FROM hs_users WHERE username='$user' AND password='$pass' LIMIT 1") or die("Query failed with error: ".mysql_error());
	$num = mysql_num_rows($userdata);
	$userinfo = mysql_fetch_array($userdata);
	$userid = $userinfo['id'];
	$num2 = mysql_num_rows(mysql_query("SELECT id FROM hs_users_characters WHERE sName='".$char_name."'"));
	
	/** Check if User Account Exists, or Character Name is already taken **/
	if ($num == 0) {
		$error = 1;
		echo 'status=failed&strMsg=The user account was not found!';
	} else if ($num2 > 0) {
		$error = 1;
		echo 'status=failed&strMsg=Character name is already taken!';
	} else {
		
		/** Add the character to the account! **/
		$update = mysql_query("insert  into `hs_users_characters`(`userid`,`sName`,`strGender`,`iLvl`,`iEye`,`iMouth`,`iNose`,`ia0`,`iUpgDays`,`iUpg`,`iBankSlots`,`iHouseSlots`,`iBagSlots`,`intCoins`,`intGold`,`intExp`,`intActivationFlag`,`intAccessLevel`,`intColorSkin`,`intColorEye`,`intColorHair`,`intColorBase`,`intColorTrim`,`intColorAccessory`,`lastArea`,`HairID`,`strHairName`,`strHairFilename`,`strQuests`,`curServer`,`bPet`,`bTT`,`bParty`,`bCloak`,`bHelm`,`bGoto`,`bWhisper`,`bFriend`,`bSoundOn`,`monkill`)". 
		"values 
		(".$userid.",
		'".$char_name."',
		'".$char_gender."',
		1,
		".$char_eye.",
		".$char_mouth.",
		".$char_nose.",
		0,
		0,
		0,
		20,
		0,
		20,
		0,
		0,
		0,
		5,
		0,
		".$char_skincolor.",
		".$char_eyecolor.",
		".$char_haircolor.",
		".$char_basecolor.",
		".$char_trimcolor.",
		".$char_accecolor.",
		'battleon-3',
		1,
		'None',
		'None',
		'00000000000000000000000000000000000000000000000000',
		'Offline',
		1,
		1,
		1,
		1,
		1,
		1,
		1,
		1,
		1,
		37)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		/** Sorry for the long mySQL Insert Statement that was for easy editing ^_^ **/
		
		/** Retrieve newly created character data **/
		$querywhat = mysql_query("SELECT * FROM hs_users_characters WHERE userid=".$userid." ORDER BY id DESC LIMIT 1") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		$getuid = mysql_fetch_array($querywhat);
		$charid = $getuid['id'];
		
		/** Begin Adding Items **/
		
		/** Add armor if defined by user **/
		if($char_armor > 0) {
			$armor = mysql_query("insert into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
		(1,$char_armor,$charid,0,'',1,'ar',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add armor if defined by user **/
		if($char_scar > 0) {
			$scar = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(1,$char_scar,$charid,0,'',1,'sc',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add helm if defined by user **/
		if($char_helm > 0) {
			$helm = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(1,$char_helm,$charid,0,'',1,'he',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add cape if defined by user **/
		if($char_cape > 0) {
			$cape = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(0,$char_cape,$charid,0,'',1,'ba',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add gloves if defined by user **/
		if($char_gloves > 0) {
			$gloves = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(0,$char_gloves,$charid,0,'',1,'gl',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add weapon if defined by user **/
		if($char_weapon > 0) {
			$weapon = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(0,$char_weapon,$charid,0,'',1,'we',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add hair if defined by user **/
		if($char_hair > 0) {
			$hair = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(0,$char_hair,$charid,0,'',1,'ha',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		/** Add mask if defined by user **/
		if($char_mask > 0) {
			$mask = mysql_query("insert  into `hs_users_items`(`iQty`,`itemid`,`userid`,`classXP`,`className`,`equipped`,`sES`,`bBank`,`iLvl`,`EnhID`) values 
			(0,$char_mask,$charid,0,'',1,'ma',0,1,-1)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		}
		
		/** Add Friends List **/
		$friend = mysql_query("insert  into `hs_users_friends`(`userid`) values ($charid)") or die("status=failed&strMsg=There was a problem creating the character, Please report this immediately!");
		
		/** Close mySQL Connection **/
		mysql_close();
		
		echo "status=Success";
	}
}
?>