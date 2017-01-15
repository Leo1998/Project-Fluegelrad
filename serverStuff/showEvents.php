<?php
	//Spam protection, IP ban, Initalize PDO
	$type=0;
	require('spamProtector.php');
	
	//Get events
	//SQL Statement
	$statement = $pdo->prepare("SELECT `events`.*,`locations`.`address`,`locations`.`longitude`,`locations`.`latitude` , `sponsors`.* , `sponsors`.`name` AS `hostName`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id`
		JOIN `hosts` ON `hosts`.`id` = `events`.`hostId`
		JOIN `sponsors` ON `sponsors`.`id` = `hosts`.`sponsorId`");
	$statement->execute();
	
	$eventArray = array();
	
	//Iterate, put rows in emparray
	while($row = $statement->fetch()) {
		$eventArray[] = $row;
	}
	
	//SQL Statement
	$statement = $pdo->prepare("SELECT * from `imagePaths`");
	$statement->execute();
	
	$imageArray = array();
	
	//Iterate, put rows in emparray
	while($row = $statement->fetch()) {
		$imageArray[] = $row;
	}
	
	foreach ($eventArray as $event) {
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