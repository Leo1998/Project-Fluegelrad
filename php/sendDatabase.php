<?php
	//Get event-key(k)
	if(isset($_GET['k'])) {
		$k = $_GET['k'];
	} else {
		exit("?k is not set");
	}
	
	//check token & id
	require('checker.php');
	
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
	}else{
		exit("max participants already reached");
	}
?>