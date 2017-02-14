<?php
/**
	HeroSmash Login Mod for Private Servers
	File: cf-userlogin.asp or cf-userlogin.php
	Author: Mystical
**/

/** Retrieve Configs **/
require_once("config.inc.php");

/** Check if user login request is being processed... **/
if(isset($_POST['strUsername'])) {

	/** Connect to Database **/
	$con = mysql_connect($cfg['sql']['host'], $cfg['sql']['user'], $cfg['sql']['pass']) or 
	die("<login bSuccess='0' sMsg='Login server could not be reached! Please try again later.'/>");
	mysql_select_db($cfg['sql']['name'], $con) or die("<login bSuccess='0' sMsg='Login server could not be reached! Please try again later.'/>");

	/** Strip Slash Username and Password Post data for mySQL Queries **/
	$user = mysql_real_escape_string(stripslashes($_POST["strUsername"]));
	$pass = md5(mysql_real_escape_string(stripslashes($_POST["strPassword"])));

	/** Retrieve IP Address **/
	if ($_SERVER['HTTP_X_FORWARD_FOR']) {
		$ip = $_SERVER['HTTP_X_FORWARD_FOR'];
	} else {
		$ip = $_SERVER['REMOTE_ADDR'];
	}

	/** Check for Tampering of Data **/
	if (!preg_match('/^[a-z0-9\s_-]+$/i', $user) || ($user == "")) {
		die("<login bSuccess='0' sMsg='Username must contain letters, spaces or numbers!'/>");
	}
	/** Retrieve User Data **/
	$userdata = mysql_query("SELECT * FROM hs_users WHERE username='$user' AND password='$pass' LIMIT 1")or die("<login bSuccess='0' sMsg='Unable to retrieve data for \"$user$\", Please contact staff!'/>");
	$num = mysql_num_rows($userdata);
	$userinfo = mysql_fetch_array($userdata);
	$userid = $userinfo['id'];

	/** Check User Status **/
	if ($num == 0) {
		/** User Data not found **/
		echo '<login bSuccess="0" sMsg="The username and password you entered did not match. Please check the spelling and try again."/>';
	} else if ($userinfo['active'] < 1) {
		/** Not Activated **/
		echo '<login bSuccess="0" sMsg="The user account is currently not activated, Please contact staff to request activation, thank you! ^_^"/>';
	} else if ($userinfo['banned'] > 0) {
		/** Banned **/
		echo '<login bSuccess="0" sMsg="The user account is currently banned, Contact staff for more info!"/>';
	} else {
		
		$updateIP = mysql_query("UPDATE hs_users SET loginip='$ip' WHERE id=$userid");
		
		/** Retrieve login news message **/
		$newsmsg = mysql_fetch_array(mysql_query("SELECT news FROM hs_settings LIMIT 1"));
		echo '<login bSuccess="1" news="'.$newsmsg['news'].'" userid="'.$userid.'" iAccess="'.$userinfo['access'].'" iUpg="'.$userinfo['upgrade'].'" iAge="'.$userinfo['age'].'" sToken="'.$userinfo['password'].'" iSendEmail="5" strEmail="'.$userinfo['email'].'" bCCOnly="0" iUpgMAS="3" iAQWstatus="1">';
		
		/** Retrieve server list **/
		$getserv = mysql_query("SELECT * FROM hs_servers LIMIT 10")or die("Query failed with error: ".mysql_error());
		while ($char = mysql_fetch_array($getserv)) {
			echo '<servers sName="'. $char["name"] .'" sIP="'. $char["ip"] .'" iCount="'. $char["count"] .'" iMax="'. $char["max"] .'" bOnline="'. $char["online"] .'" iChat="'. $char["ichat"] .'" bUpg="'. $char["upgrade"] .'" />';
		}
		
		/** Retrieve character data **/
		$getchar = mysql_query("SELECT * FROM hs_users_characters WHERE userid=$userid ORDER BY id ASC LIMIT 5")or die("Query failed with error: ".mysql_error());
		while ($char = mysql_fetch_array($getchar)) {
			echo '<chars charID="'. $char["id"] .'" sName="'. $char["sName"] .'" strGender="'. $char["strGender"] .'" intColorSkin="'. $char["intColorSkin"] .'" intColorEye="'. $char["intColorEye"] .'" intColorHair="'. $char["intColorHair"] .'" intColorBase="'. $char["intColorBase"] .'" intColorTrim="'. $char["intColorTrim"] .'" intColorAccessory="'. $char["intColorAccessory"] .'" iEye="'. $char["iEye"] .'" iNose="'. $char["iNose"] .'" iMouth="'. $char["iMouth"] .'" iLvl="'. $char["iLvl"] .'" strHairName="'. $char["strHairName"] .'" strHairFilename="'. $char["strHairFilename"] .'">';
			
			/** Retrieve currently equipped items info **/
			$getitem = mysql_query("SELECT * FROM hs_users_items WHERE equipped=1 AND userid=".$char["id"])or die("Query failed with error: ".mysql_error());
			while ($item = mysql_fetch_array($getitem)) {
				$queryitem = mysql_query("SELECT * FROM hs_items WHERE itemID=".$item['itemid']);
				$getinfo = mysql_fetch_array($queryitem);
				$check = mysql_num_rows($queryitem);
				
				/** If Item Exists, Add it. **/
				if($check > 0) {
					echo '<items sES="'. $getinfo["sES"] .'" sType="'. $getinfo["sType"] .'" sFile="'. $getinfo["sFile"] .'" sLink="'. $getinfo["sLink"] .'"/>';
				}
			}
			echo '</chars>';
		}
		echo '</login>';
		
		/** Close mySQL Connection **/
		mysql_close();
		
	}
}
?>
