<?php
	//Get event-key(k)
	if(isset($_GET['k'])) {
		$k = $_GET['k'];
	} else {
		exit("Error: ?k is not set");
	}
	
	//Get rating(r)
	if(isset($_GET['r'])) {
		$r = $_GET['r'];
	} else {
		exit("Error: ?r is not set");
	}
	
	//check token & id
	require('checker.php');
	
	
	//Check Requirements
	$statement = $pdo->prepare("SELECT `participants`,`dateStart` FROM `events` WHERE `id` = ?");
	$statement->execute(array($k));
	
	if($row = $statement->fetch(PDO::FETCH_ASSOC)){ //1. Requirement: Event has to exist
		$dateStart = strtotime($row['dateStart']);
		if($dateStart < time()){ //2. Requirement: Event has already started
			if($row['participants'] < 0){ //Requirement #3 only if participants are counted
				//No more Requirement
			}else{
				
				$statement = $pdo->prepare("SELECT * FROM `participating` WHERE `userId` = ? AND `eventId` = ?");
				$statement->execute(array($u,$k));
				
				if($row = $statement->fetch(PDO::FETCH_ASSOC)){ //3.Requirement: User has to participate
					//Requirements met
				}else{
					exit("Error: User has to participate to rate this event");
				}
			}
		}else{
			exit("Error: Can not rate before start");
		}
	}else{
		exit("Error: Unknown Event-Key");
	}
	
	
	//Check if user has already rated
	$statement = $pdo->prepare("SELECT * FROM `eventRatings` WHERE `userId` = ? AND `eventId` = ?");
	$statement->execute(array($u,$k));
	
	if($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		//User has already rated -> Update rating
		$statement = $pdo->prepare("UPDATE `eventRatings` SET `rating` = ? WHERE `userId` = ? AND `eventId` = ?");
		$statement->execute(array($r,$u,$k));
		echo "Rating updated to ".$r;
	}else{
		//User has not rated -> Insert rating
		$statement = $pdo->prepare("INSERT INTO `eventRatings` (`userId`,`eventId`,`rating`) VALUES (?,?,?)");
		$statement->execute(array($u,$k,$r));
		echo "Rating uploaded";
	}
?>