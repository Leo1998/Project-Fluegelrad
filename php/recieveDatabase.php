<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT * FROM events");
	$eventsGet->execute();
	
	//Iterate, put rows in emparray
	while($row = $eventsGet->fetch()) {
		$emparray[] = $row;
	}
	
	//Echo json with rows
	echo json_encode($emparray);
?>