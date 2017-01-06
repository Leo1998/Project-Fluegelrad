<?php

	function getPost(id){
		if(isset($_POST[id])){
			return $_POST[id];
		} else {
			exit("Error: ".id." could not be found");
		}
	}
	
	$newLocation = false;
	
	
	
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