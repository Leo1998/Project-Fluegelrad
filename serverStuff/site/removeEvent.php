<?php
	//Spam protection, IP ban, Initalize PDO
	$hostRequired=true;
	require('../scripts/sitePrepare.php');
	
	if(isset($_GET['k'])) {
		
	} else {
		header("Location: home.php?m=10");
		exit();
	}
?>