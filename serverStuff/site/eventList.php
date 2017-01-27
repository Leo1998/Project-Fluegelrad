<?php
	session_start();
	
	function removeNumerical($array){
		for($i = 0; array_key_exists($i,$array); $i++){
			unset($array[$i]);
		}
		return $array;
	}
	
	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	//GET EVENTS
	$eventArray = array();
	$statement = $pdo->prepare("SELECT 
		`events`.`id` , `events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` ,  
		`locations`.`address` AS `location.address` ,
		`sponsors`.`imagePath` AS `sponsorImage`,
		`imagePaths`.`path` AS `image`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id`
		JOIN `hosts` ON `events`.`hostId` = `hosts`.`id`
		JOIN `sponsors` ON `sponsors`.`id` = `hosts`.`sponsorId`
		JOIN (SELECT * from `imagePaths` GROUP BY `imagePaths`.`eventId`) AS `imagePaths` ON `imagePaths`.`eventId` = `events`.`id`
		ORDER BY `events`.`dateStart` , `events`.`dateEnd`");
	$statement->execute();
	
	while($row = $statement->fetch()) {
		$eventArray[] = removeNumerical($row);
	}
	
	echo "
		<script type=\"text/javascript\">
			const data = ".json_encode($eventArray).";
		</script>
	";
?>

<html>

<head>
    <title>Eventliste</title>
    <script>
		function init(){
			
		}
		
		function addEvent(event,sponsors){
			
		}
	</script>
	</head>

	<body onload='init();'>
		<oul id="events" name="events">
		</oul>
	</body>
</html>