<?php
	session_start();

	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	//GET EVENTS
	$eventArray = array();
	$statement = $pdo->prepare("SELECT 
		`events`.`id` , `events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` ,  
		`locations`.`address` AS `address` ,
		`sponsors`.`imagePath` AS `sponsorImage`,
		`imagePaths`.`path` AS `image`,
		`ratings`.`rating`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id`
		JOIN `hosts` ON `events`.`hostId` = `hosts`.`id`
		JOIN `sponsors` ON `sponsors`.`id` = `hosts`.`sponsorId`
		JOIN (SELECT * from `imagePaths` GROUP BY `imagePaths`.`eventId`) AS `imagePaths` ON `imagePaths`.`eventId` = `events`.`id`
		LEFT JOIN (SELECT AVG(`rating`) AS `rating`,`eventId` FROM `eventRatings` GROUP BY `eventId`) AS `ratings` ON `ratings`.`eventId` = `events`.`id`
		ORDER BY `events`.`dateStart` , `events`.`dateEnd`");
	$statement->execute();
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$desc = split("\n",$row["description"]);
		unset($row["description"]);
		$row["description1"] = $desc[0];
		if(isset($desc[1])){
			$row["description2"] = $desc[1];
		}else{
			$row["description2"] = "";
		}
		$eventArray[] = $row;
	}
	
	echo "
<script type=\"text/javascript\">
	const data = ".json_encode($eventArray,JSON_PRETTY_PRINT).";
</script>
	";
?>

<html>

<head>
    <title>Eventliste</title>
    <script>
		function init(){
			for(var i = 0 ; i in data ; i++){
				addEvent(data[i]);
			}
		}
		
		function addEvent(event){
			var li = document.createElement('li');
			li.id = "event"+event["id"];
			
			var name = document.createElement('span');
			name.class = 'eventName';
			name.innerHTML = event["name"]+"<br>";
			
			var rating = document.createElement('span');
			rating.class = 'eventRating';
			if(event["rating"] != null){
				rating.innerHTML = "Bewertung: "+event["rating"]+"<br>";
			}
			
			var img = document.createElement('img');
			img.class = "eventImg";
			img.src = "../"+event["image"];
			img.alt = "Bild nicht verfügbar";
			img.style.height = "200px";
			
			var price = document.createElement('span');
			price.class = 'eventPrice';
			if(event["price"]>0){
				price.innerHTML = "<br>Kosten: "+event["price"]+"€<br>";
			}else{
				price.innerHTML = "<br>Kostenlos<br>";
			}
			
			var participants = document.createElement('span');
			participants.class = 'eventPart';
			if(event["participants"] >= 0){
				if(event["maxParticipants"] >= 0){
					participants.innerHTML = event["participants"]+"/"+event["maxParticipants"]+"Teilnehmer<br>";
				}else{
					participants.innerHTML = event["participants"]+" Teilnehmer angemeldet<br>";
				}
			}else{
				if(event["maxParticipants"] >= 0){
					participants.innerHTML = "Maximale Teilnehmerzahl: "+event["maxParticipants"]+"<br>";
				}else{
					participants.innerHTML = "Dieses Event besitzt keine Teilnehmerangaben<br>";
				}
			}
			
			var dateLoc = document.createElement('span');
			dateLoc.class = 'eventDateLoc';
			dateLoc.innerHTML = "Von "+event["dateStart"]+" bis "+event["dateEnd"]+" ,Ort: "+event["address"]+"<br>";
			
			var desc = document.createElement('span');
			desc.class = 'eventDesc';
			desc.innerHTML = event["description1"]+"<br>"+event["description2"]+"<br>";
			
			var sponsorImg = document.createElement('img');
			sponsorImg.class = "sponsorImg";
			sponsorImg.src = "../"+event["sponsorImage"];
			sponsorImg.alt = "Sponsorbild nicht verfügbar";
			sponsorImg.style.height = "100px";
			
			li.appendChild(name);
			li.appendChild(rating);
			li.appendChild(img);
			li.appendChild(price);
			li.appendChild(participants);
			li.appendChild(dateLoc);
			li.appendChild(desc);
			li.appendChild(sponsorImg);
			
			document.getElementById('events').appendChild(li);
			
			document.getElementById('events').appendChild(document.createElement('br'));
		}
	</script>
	</head>

	<body onload='init();'>
		<oul id="events" name="events" style="list-style-type: none;">
		</oul>
	</body>
</html>