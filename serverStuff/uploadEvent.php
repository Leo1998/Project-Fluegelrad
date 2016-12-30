<?php
	//Get values from form, exit if values not set
	if(isset($_POST['eventName'])){
		$eventName = $_POST['eventName'];
	} else {
		exit("Error: eventName could not be found");
	}
	
	if(isset($_POST['price'])){
		$price = $_POST['price'];
	} else {
		exit("Error: price could not be found");
	}
	
	if(isset($_POST['participants'])){
		$participants = $_POST['participants'];
	} else {
		exit("Error: participants could not be found");
	}
	
	if(isset($_POST['dateStart'])){
		$dateStart = $_POST['dateStart'];
	} else {
		exit("Error: dateStart could not be found");
	}
	
	if(isset($_POST['dateEnd'])){
		$dateEnd = $_POST['dateEnd'];
	} else {
		exit("Error: dateEnd could not be found");
	}
	
	if(isset($_POST['description'])){
		$description = $_POST['description'];
	} else {
		exit("Error: description could not be found");
	}
	
	if(isset($_POST['ageMin'])){
		$ageMin = $_POST['ageMin'];
	} else {
		exit("Error: ageMin could not be found");
	}
	
	if(isset($_POST['ageMax'])){
		$ageMax = $_POST['ageMax'];
	} else {
		exit("Error: ageMax could not be found");
	}
	
	if(isset($_POST['address'])){
		$address = $_POST['address'];
	} else {
		exit("Error: address could not be found");
	}
	
	if(isset($_POST['latitude'])){
		$latitude = $_POST['latitude'];
	} else {
		exit("Error: latitude could not be found");
	}
	
	if(isset($_POST['longitude'])){
		$longitude = $_POST['longitude'];
	} else {
		exit("Error: longitude could not be found");
	}
	
	//load spamprotector & pdo
	$type=1;
	require('spamProtector.php');
	
	//create new location
	$locationInsert = $pdo->prepare("INSERT INTO `locations` (`id`,`latitude`,`longitude`,`address`) VALUES (NULL, ? , ? , ?)");
	$locationInsert->execute(array($latitude,$longitude,$address));
	$locationId = intval($pdo->lastInsertId());
	
	//insert event
	$eventInsert = $pdo->prepare("INSERT INTO `events` (`id`, `name`, `price`, `locationId`, `maxParticipants`, `participants`, `hostId`, `dateStart`, `dateEnd`, `description`, `ageMin`, `ageMax`, `formId`)
								VALUES (NULL, ?, ?, ?, ?, '0', ?, ?, ?, ?, ?, ?, '0')");
	$eventInsert->execute(array($eventName,$price,$locationId,$participants,2,$dateStart,$dateEnd,$description,$ageMin,$ageMax));
	
	echo 'Event erstellt';
?>