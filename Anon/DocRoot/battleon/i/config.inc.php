<?php
$cfg['sql']['host'] = "localhost";
$cfg['sql']['user'] = "root";
$cfg['sql']['pass'] = "boangni23";
$cfg['sql']['name'] = "herosmash";

$con = mysql_connect($cfg['sql']['host'], $cfg['sql']['user'], $cfg['sql']['pass']) or die();
mysql_select_db($cfg['sql']['name'], $con) or die();

?>