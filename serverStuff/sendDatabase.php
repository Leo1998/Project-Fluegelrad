<?php
	//Get event-key(k)
	if(isset($_GET['k'])) {
		$k = $_GET['k'];
	} else {
		exit("Error: ?k is not set");
	}
	
	//check token & id
	require('checker.php');
	
	//Check, if User already participating
	$participatingGet = $pdo->prepare("SELECT * FROM `participating` WHERE `userId` = ? AND `eventId` = ?");
	$participatingGet->execute(array($u,$k));
	
	while($row = $participatingGet->fetch()) {
		exit("Error: User is already participating");
	}
	
	//Get current participants from event
	$participantsGet = $pdo->prepare("SELECT participants,maxParticipants FROM events WHERE id = ?");
	$participantsGet->execute(array($k));
	while($row = $participantsGet->fetch()) {
		$participants = intval($row['participants']);
		$maxParticipants = intval($row['maxParticipants']);
	}
	
	//If maxParticipants is not reached, increase participants by 1
	if($participants < $maxParticipants){
		$participantsSet = $pdo->prepare("UPDATE events SET participants = ? WHERE id = ?");
		$participantsSet->execute(array($participants + 1, $k));
		$participatingAdd = $pdo->prepare("INSERT INTO `participating` (`userId`, `eventId`) VALUES (:userId, :eventId);");
		$participatingAdd->bindParam('userId', $u, PDO::PARAM_INT);
		$participatingAdd->bindParam('eventId', $k, PDO::PARAM_INT);
		$participatingAdd->execute();
	}else{
		exit("Error: max participants already reached");
	}
?>