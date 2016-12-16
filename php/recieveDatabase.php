<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT `events`.*,`locations`.`address`,`locations`.`longitude`,`locations`.`latitude` FROM `events` JOIN `locations` ON `events`.`locationId` = `locations`.`id`");
	$eventsGet->execute();
	
	//Iterate, put rows in emparray
	while($row = $eventsGet->fetch()) {
		$emparray[] = $row;
	}
	
	//Echo json with rows
	echo json_encode($emparray);
?>