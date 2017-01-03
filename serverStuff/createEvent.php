<?php
	//Spam protection, IP ban, Initalize PDO
	$type=0;
	require('spamProtector.php');
	
	$locationsGet = $pdo->prepare("SELECT * FROM `locations`");
	$locationsGet->execute();
	
	$locations = array();
	
	//Iterate, put rows in emparray
	while($row = $locationsGet->fetch()) {
		$locations[] = $row;
	}
	
	
?>