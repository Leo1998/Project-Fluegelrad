<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT 
		`events`.`id` , `events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` , `events`.`ageMin` , `events`.`ageMax` ,`events`.`formId` ,  
		`locations`.`address`,`locations`.`longitude`,`locations`.`latitude` ,
		`hosts`.`sponsorId` AS `hostId`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id`
		JOIN `hosts` ON `events`.`hostId` = `hosts`.`id`");
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
	
	//SQL Statement
	$sponsorsGet = $pdo->prepare("SELECT * from `sponsors`");
	$sponsorsGet->execute();
	
	$sponsorArray = array();
	
	//Iterate, put rows in emparray
	while($row = $sponsorsGet->fetch()) {
		$sponsorArray[] = $row;
	}
	
	$emparray['sponsors'] = $sponsorArray;
	
	//Echo json with rows
	echo json_encode($emparray);
?>