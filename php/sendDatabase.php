<?php
	$pdo = new PDO('mysql:host=pipigift.ddns.net;dbname=fluegelrad', 'testuser', '123456');
	
	if(isset($_GET['k'])) {
		$k = $_GET['k'];
	} else {
		die("Bitte ?k übergeben");
	}

	if(isset($_GET['p'])) {
		$p = $_GET['p'];
	} else {
		die("Bitte ?p übergeben");
	}
	
	$statement = $pdo->prepare("UPDATE events SET participants = ? WHERE primary_key = ?");
	$statement->execute(array($p, $k));
?>