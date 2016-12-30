<?php
	//Spam protection, IP ban, Initalize PDO
	$type=0;
	require('spamProtector.php');
	
	//Get events
	//SQL Statement
	$eventsGet = $pdo->prepare("SELECT `events`.*,`locations`.`address`,`locations`.`longitude`,`locations`.`latitude` , `sponsors`.* , `sponsors`.`name` AS `hostName`
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
	
	//SQL Statement
	$pathsGet = $pdo->prepare("SELECT * from `imagePaths`");
	$pathsGet->execute();
	
	$imageArray = array();
	
	//Iterate, put rows in emparray
	while($row = $pathsGet->fetch()) {
		$imageArray[] = $row;
	}
	
	foreach ($eventArray as &$event) {
		$name = $event['name'];
		$description = str_replace("\n", "<br>", $event['description']);
		$price = $event['price'];
		$maxParticipants = $event['maxParticipants'];
		$participants = $event['participants'];
		$dateStart = $event['dateStart'];
		$dateEnd = $event['dateEnd'];
		$ageMin = $event['ageMin'];
		$ageMax = $event['ageMax'];
		$address = $event['address'];
		echo "<p><b>$name</b></p>";
		echo "<p>$description</p>";
		
	}
	unset($event);
?>