<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT `events`.*,`locations`.`address`,`locations`.`longitude`,`locations`.`latitude` FROM `events` JOIN `locations` ON `events`.`locationId` = `locations`.`id`");
	$eventsGet->execute();
	
	$eventArray = array();
	
	//Iterate, put rows in emparray
	while($row = $eventsGet->fetch()) {
		$eventArray[] = $row;
	}
	
	$emparray['events'] = $eventArray;
	
	//SQL Statement
	$pathsGet = $pdo->prepare("SELECT * from `imagePaths`");
	$pathsGet->execute();
	
	$imageArray = array();
	
	//Iterate, put rows in emparray
	while($row = $pathsGet->fetch()) {
		$imageArray[] = $row;
	}
	
	$emparray['images'] = $imageArray;
	
	//Echo json with rows
	echo json_encode($emparray);
?>