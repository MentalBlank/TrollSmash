<?php
/**
	HeroSmash Battleon Account Signup Mod for HeroSmash Servers
	File: GameAPI_createAccount.asp or GameAPI_createAccount.php
	Author: Mystical
**/

/** Retrieve Configs **/
require_once("config.inc.php");

/** Check if signup request is being processed... **/
if(isset($_POST['uuu'])) {

	/** Strip Slash Username and Password Post data for mySQL Queries **/
	$user = mysql_real_escape_string(stripslashes($_POST["uuu"]));
	$pass = md5(mysql_real_escape_string(stripslashes($_POST["ppp"])));
	$email = mysql_real_escape_string(stripslashes($_POST["eee"]));
	
	/** Connect to Database **/
	$con = mysql_connect($cfg['sql']['host'], $cfg['sql']['user'], $cfg['sql']['pass']) or 
	die("status=failed&strMsg=The character creation server could not be reached! Please try again later!");
	mysql_select_db($cfg['sql']['name'], $con) or die("status=failed&strMsg=The character creation server could not be reached! Please try again later!");
	

	/** Check for Tampering of Data **/
	if (!preg_match('/^[a-z0-9\s_-]+$/i', $user) || ($user == "")) {
		die("<login bSuccess='0' sMsg='Username must contain letters, spaces or numbers!'/>");
	}
	
	
	/** Check if username is available **/
	$getuservar = mysql_query("SELECT * FROM hs_users WHERE username='$user' LIMIT 1");
	$num = mysql_num_rows($getuservar);
	
	if($num > 0) {
		echo "status=Failed&sMsg=Username is already taken!";
	} else {
		mysql_query("insert  into `hs_users`(`username`,`password`,`age`,`active`,`email`,`banned`,`signupip`,`loginip`) values ('$user','$pass',15,1,'$email',0,'$ip','')") or die ("status=Failed&sMsg=".mysql_error());
		echo "status=Success";
	}
}
?>
