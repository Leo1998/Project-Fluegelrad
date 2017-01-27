<?php

	function getMatching($eventId,$array){
		$res = array();
		foreach($array as $content){
			if($content['eventId'] == $eventId){
				$res[] = $content;
			}
		}
		return $res;
	}
	
	function removeNumerical($array){
		for($i = 0; array_key_exists($i,$array); $i++){
			unset($array[$i]);
		}
		return $array;
	}
	
	//-- GET IMAGES --
	//SQL Statement
	$statement = $pdo->prepare("SELECT * from `imagePaths`");
	$statement->execute();
	
	$imageArray = array();
	
	//Iterate, put rows in emparray
	while($row = $statement->fetch()) {
		$imageArray[] = removeNumerical($row);
	}
	
	//-- GET SPONSORS --
	//SQL Statement
	$statement = $pdo->prepare("SELECT * from `sponsoring`");
	$statement->execute();
	
	$sponsoringArray = array();
	
	//Iterate, put rows in emparray
	while($row = $statement->fetch()) {
		$sponsoringArray[] = removeNumerical($row);
	}
	
	
	//-- GET EVENTS --
	//SQL Statement
	$statement = $pdo->prepare("SELECT 
		`events`.`id` , `events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` , `events`.`ageMin` , `events`.`ageMax` ,`events`.`formId` ,  
		`locations`.`address` AS `location.address` , `locations`.`longitude` AS `location.longitude` , `locations`.`latitude` AS `location.latitude` ,
		`hosts`.`sponsorId` AS `hostId`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id`
		JOIN `hosts` ON `events`.`hostId` = `hosts`.`id`");
	$statement->execute();
	
	$eventArray = array();
	
	//Iterate, put rows in emparray
	while($row = $statement->fetch()) {
		$row['images'] = getMatching($row['id'],$imageArray);
		$row['sponsors'] = getMatching($row['id'],$sponsoringArray);
		$eventArray[] = removeNumerical($row);
	}
	
	
	//-- GET SPONSORS --
	//SQL Statement
	$statement = $pdo->prepare("SELECT * from `sponsors`");
	$statement->execute();
	
	$sponsorArray = array();
	
	//Iterate, put rows in emparray
	while($row = $statement->fetch()) {
		$sponsorArray[] = removeNumerical($row);
	}
	
	
	//Put Sponsors and Events in emparray
	$emparray = array(
		'events' => $eventArray,
		'sponsors' => $sponsorArray,
	);
?>