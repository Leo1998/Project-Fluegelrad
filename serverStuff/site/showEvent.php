<?php
	session_start();
	
	if(isset($_GET['k'])){
		$k = $_GET['k'];
	}else{
		header("Location: eventList.php");
		exit();
	}
	
	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	$statement = $pdo->prepare("SELECT 
		`events`.`name` , `events`.`price` , `events`.`maxParticipants` , `events`.`participants` , `events`.`dateStart` , `events`.`dateEnd` , `events`.`description` , `events`.`ageMin` , `events`.`ageMax` ,`events`.`formId` ,  
		`locations`.`address` AS `location.address` , `locations`.`longitude` AS `location.longitude` , `locations`.`latitude` AS `location.latitude` ,
		`sponsors`.`name` AS `host.name`, `sponsors`.`imagePath` AS `host.image`, `sponsors`.`description` AS `host.description`, `sponsors`.`id` AS `host.id`,`sponsors`.`web` AS `host.web`,
		`ratings`.`rating`
		FROM `events` 
		JOIN `locations` ON `events`.`locationId` = `locations`.`id` AND `events`.`id` = :eventId
		JOIN `hosts` ON `events`.`hostId` = `hosts`.`id` AND `events`.`id` = :eventId
		JOIN `sponsors` ON `hosts`.`sponsorId` = `sponsors`.`id` AND `events`.`id` = :eventId
		LEFT JOIN (SELECT AVG(`rating`) AS `rating`,`eventId` FROM `eventRatings` GROUP BY `eventId`) AS `ratings` ON `ratings`.`eventId` = `events`.`id` AND `events`.`id` = :eventId
		WHERE `events`.`id` = :eventId");
	$statement->bindParam('eventId', $k,PDO::PARAM_INT);
	$statement->execute();
	
	if($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$event = $row;
	}else{
		header("Location: eventList.php");
		exit();
	}
	
	$event["description"] = nl2br($event["description"]);
	
	
	$statement = $pdo->prepare("SELECT 
		`sponsors`.`name`, `sponsors`.`imagePath`, `sponsors`.`id`,`sponsors`.`web`
		FROM `sponsors`
		JOIN `sponsoring` ON `sponsors`.`id` = `sponsoring`.`sponsorId` AND `sponsoring`.`eventId` = :eventId");
	$statement->bindParam('eventId', $k,PDO::PARAM_INT);
	$statement->execute();
	
	if($row = $statement->fetchAll(PDO::FETCH_ASSOC)) {
		$sponsors = $row;
	}
	
	
	$statement = $pdo->prepare("SELECT 
		`imagePaths`.`path`,`imagePaths`.`description`
		FROM `imagePaths`
		WHERE `imagePaths`.`eventId` = :eventId");
	$statement->bindParam('eventId', $k,PDO::PARAM_INT);
	$statement->execute();
	
	if($row = $statement->fetchAll(PDO::FETCH_ASSOC)) {
		$images = $row;
	}
	
	
	$event["images"] = $images;
	$event["sponsors"] = $sponsors;
	
	
	if(isset($_SESSION['hostId'])){
		$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	}else{
		$hostStuff = "const hostStuff = null;";
	}
	
	
	echo "
<script type=\"text/javascript\">
	const data = ".json_encode($event,JSON_PRETTY_PRINT).";
	$hostStuff
</script>
		";
?>

<html>

<head>
    <title></title>
    <script>
		var mapDiv;
	
		function init(){
			if(hostStuff != null){
				var div = document.createElement('div');
				div.id = "Host";
				
				var nameLabel = document.createTextNode(hostStuff["name"]);
				var image = document.createElement('img');
				image.src= hostStuff["image"];
				image.alt= "Bild nicht verfügbar";
				image.title= "Vorschau";
				image.style.height="50px";
				
				var logout = document.createElement("a");
				logout.href = "logout.php";
				logout.innerHTML = "Abmelden";
				
				div.appendChild(nameLabel);
				div.appendChild(image);
				div.appendChild(logout);
				
				document.getElementById("header").appendChild(div);
			}else{
				var login = document.createElement("a");
				login.href = "login.html";
				login.innerHTML = "Anmelden";
				
				document.getElementById("header").appendChild(login);
			}
			
			document.title = data["name"];
			
			
			var eventName = document.createElement("h1");
			eventName.appendChild(document.createTextNode(data["name"]));
			
			var rating = document.createElement("label");
			if(data["rating"] != null){
				rating.innerHTML = "Bewertung: "+data["rating"]+"/5<br>";
			}
			
			var imagesDiv = document.createElement("div");
			imagesDiv.id = "imagesDiv";
			
			var participants = document.createElement("label");
			if(data["participants"] >= 0){
				if(data["maxParticipants"] >= 0){
					participants.innerHTML = data["participants"]+"/"+data["maxParticipants"]+"Teilnehmer<br>";
				}else{
					participants.innerHTML = data["participants"]+" Teilnehmer angemeldet<br>";
				}
			}else{
				if(data["maxParticipants"] >= 0){
					participants.innerHTML = "Maximale Teilnehmerzahl: "+data["maxParticipants"]+"<br>";
				}else{
					participants.innerHTML = "Dieses Event besitzt keine Teilnehmerangaben<br>";
				}
			}
			
			var price = document.createElement("label");
			if(data["price"]>0){
				price.innerHTML = "Kosten: "+data["price"]+"€<br><br>";
			}else{
				price.innerHTML = "Kostenlos<br><br>";
			}
			
			var description = document.createElement("label");
			description.innerHTML = data["description"]+"<br>";
			
			mapDiv = document.createElement("div");
			
			var hostName = document.createElement("div");
			hostName.innerHTML = data["host.name"];
			
			var hostImage
			
			var sponsorsDiv = document.createElement("div");
			sponsorsDiv.id = "sponsorsDiv";
			
			var oul = document.getElementById("event");
			oul.appendChild(eventName);
			oul.appendChild(rating);
			oul.appendChild(imagesDiv);
			oul.appendChild(participants);
			oul.appendChild(price);
			oul.appendChild(description);
		}
		
		function addSponsor(sponsor){
			
		}
		
		function addImage(image){
			
		}
	</script>
	</head>

	<body onload='init();'>
		<header id="header">
			<a href="home.php"> Home</a>
			<a href="eventList.php"> Eventliste</a>
		</header>
		
		<article>
			<oul id="event" name="event" style="list-style-type: none;">
			</oul>
		</article>
	</body>
</html>