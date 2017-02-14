<?php
/**
	HeroSmash Character Deletion Mod for Private Servers
	File: cf-deletechar.asp or cf-deletechar.php
	Author: Mystical
**/

/** Retrieve Configs **/
require_once("config.inc.php");

/** Check if character deletion request is being processed... **/
if(isset($_POST['uuu'])) {

	/** Connect to Database **/
	$con = mysql_connect($cfg['sql']['host'], $cfg['sql']['user'], $cfg['sql']['pass']) or die();
	mysql_select_db($cfg['sql']['name'], $con) or die();
	
	/** Retrieve POST data **/
	$user = $_POST['uuu'];	//Username
	$pass = $_POST['ttt'];	//Password
	$cid = $_POST['cid'];	//Character ID
	
	/** Check user and pass to ensure no tampering happened **/
	$userdata = mysql_query("SELECT * FROM hs_users WHERE username='$user' AND password='$pass' LIMIT 1")or die();
	$num = mysql_num_rows($userdata);
	$userinfo = mysql_fetch_array($userdata);
	$userid = $userinfo['id'];
	
	/** If character exists being deletion **/
	if($num > 0) {
		$getchar = mysql_query("SELECT * FROM hs_users_characters WHERE userid=$userid AND id=".$cid)or die();
		$num2 = mysql_num_rows($getchar);
		if($num2 > 0) {
			$deleteCharacter = mysql_query("DELETE FROM hs_users_characters WHERE id=".$cid) or die();
			$deleteItems = mysql_query("DELETE FROM hs_users_items WHERE userid=".$cid) or die();
			$deleteFriends = mysql_query("DELETE FROM hs_users_friends WHERE userid=".$cid) or die();
			$deleteFactions = mysql_query("DELETE FROM hs_users_factions WHERE userid=".$cid) or die();
		}
	}
	
	/** Close mySQL Connection **/
	mysql_close();
}
	
?>