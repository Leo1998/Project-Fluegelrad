<?php
	//Get event-key(k)
	if(isset($_GET['k'])) {
		$k = $_GET['k'];
	} else {
		exit("Error: ?k is not set");
	}
	
	//check token & id
	require('checker.php');
	
	//Check if User is already participating
	$statement = $pdo->prepare("SELECT * FROM `participating` WHERE `userId` = ? AND `eventId` = ?");
	$statement->execute(array($u,$k));
	
	if($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		exit("Error: User is already participating");
	}
	
	//Get current participants from event
	$statement = $pdo->prepare("SELECT participants,maxParticipants FROM events WHERE id = ?");
	$statement->execute(array($k));
	if($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$participants = intval($row['participants']);
		$maxParticipants = intval($row['maxParticipants']);
	}else{
		exit("Error: Event-key unknown");
	}
	
	if($participants >= 0){	
		//If maxParticipants is not reached, increase participants by 1
		if($participants < $maxParticipants || $maxParticipants < 0){
			$statement = $pdo->prepare("UPDATE events SET participants = ? WHERE id = ?");
			$statement->execute(array($participants + 1, $k));
			$statement = $pdo->prepare("INSERT INTO `participating` (`userId`, `eventId`) VALUES (:userId, :eventId);");
			$statement->bindParam('userId', $u, PDO::PARAM_INT);
			$statement->bindParam('eventId', $k, PDO::PARAM_INT);
			$statement->execute();
		}else{
			exit("Error: max participants already reached");
		}
	}else{
		exit("Error: Can not participate for this event");
	}
?>