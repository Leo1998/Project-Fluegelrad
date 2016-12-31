<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT 
		`events`.`id` , `events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` , `events`.`ageMin` , `events`.`ageMax` ,`events`.`formId` ,  
		`locations`.`address`,`locations`.`longitude`,`locations`.`latitude` ,
		`sponsors`.`mail` , `sponsors`.`phone` , `sponsors`.`web`,
		`sponsors`.`name` AS `hostName`, `sponsors`.`description` AS `hostDescription`, `sponsors`.`image` AS `hostImage`, `sponsors`.`id` AS `hostId`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id`
		JOIN `hosts` ON `hosts`.`id` = `events`.`hostId`
		JOIN `sponsors` ON `sponsors`.`id` = `hosts`.`sponsorId`");
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