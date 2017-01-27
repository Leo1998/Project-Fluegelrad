<?php
	session_start();
	
	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	if(!isset($_SESSION('hostId'))){
		echo "Sie sind nicht angemeldet";
	}else{
		echo "Hallo Host Nr.".$_SESSION('hostId');
	}
?>