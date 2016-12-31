<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT 
		`events`.`id` , `events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` , `events`.`ageMin` , `events`.`ageMax` ,`events`.`formId` ,  
		`locations`.`address` AS `location.address` , `locations`.`longitude` AS `location.longitude` , `locations`.`latitude` AS `location.latitude` ,
		`sponsors`.`mail` AS `host.mail` , `sponsors`.`phone` AS `host.phone` , `sponsors`.`web` AS `host.web` , `sponsors`.`name` AS `host.name` , `sponsors`.`description` AS `host.description` , `sponsors`.`image` AS `host.image` , `sponsors`.`id` AS `host.id` 
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