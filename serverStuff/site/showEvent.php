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
<!doctype html>
<html>
<head>
    <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
	<meta charset="utf-8">
	<meta name="author" content="@Firmenname" /> <!-- Hier sollte der Name des Autors, der Inhalte erstellt, rein. -->
	<meta name="Description" content="Ersellen sie ihr Event!" /> 
    <link href="css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" /> <!-- Hier sollte der Pfad zur CSS-datei eingetragen werden, 
           die für die Bildschirmausgabe zuständig ist. Je nachdem in welchem Verzeichnis sich diese Datei befindet muss der Pfad angepasst werden. -->
	<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.3/cookieconsent.min.css" /> <!-- Cookie Message -->
	<script src="//cdnjs.cloudflare.com/ajax/libs/cookieconsent2/3.0.3/cookieconsent.min.js"></script>
	<script>
		window.addEventListener("load", function(){
		window.cookieconsent.initialise({
			"palette": {
				"popup": {
					"background": "#ffffff",
					"text": "#000000"
				},
				"button": {
					"background": "#dddddd",
					"text": "#000000"
				}
			},
			"theme": "edgeless",
			"position": "bottom-right"
		})});
		var mapDiv;
	
		function init(){
			if(hostStuff != null){
				var div = document.createElement('div');
				div.id = "Host";
				
				var nameLabel = document.createTextNode(hostStuff["name"]);
				var image = document.createElement('img');
				image.src= "../"+hostStuff["image"];
				image.alt= "Bild nicht verfügbar";
				image.title= "Vorschau";
				image.style.height="50px";
				
				var logout = document.createElement("a");
				logout.href = "logout.php";
				logout.innerHTML = "Abmelden";
				
				div.appendChild(nameLabel);
				div.appendChild(image);
				//div.appendChild(logout);
				document.getElementById("loginfield").appendChild(logout);
				//document.getElementById("header").appendChild(div);
			}else{
				var login = document.createElement("a");
				login.href = "login.php";
				login.innerHTML = "Anmelden";
				
				document.getElementById("loginfield").appendChild(login);
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
			
			var hostName = document.createElement("a");
			hostName.innerHTML = "Veranstalter:"+data["host.name"];
			hostName.href = "showSponsor.php?k="+data["host.id"];
			
			var hostImage = document.createElement('img');
			hostImage.src= "../"+data["host.image"];
			hostImage.alt= "Bild nicht verfügbar";
			hostImage.title= "VeranstalterBild";
			hostImage.style.height="100px";
			
			var sponsorsDiv = document.createElement("div");
			sponsorsDiv.id = "sponsorsDiv";
			
			for(var i = 0 ; i in data["images"] ; i++){
				addImage(data["images"][i],imagesDiv);
			}
			
			for(var i = 0 ; i in data["sponsors"] ; i++){
				addSponsor(data["sponsors"][i],imagesDiv);
			}
			
			var ul = document.getElementById("event");
			ul.appendChild(eventName);
			ul.appendChild(rating);
			ul.appendChild(imagesDiv);
			ul.appendChild(participants);
			ul.appendChild(price);
			ul.appendChild(description);
			ul.appendChild(hostName);
			ul.appendChild(hostImage);
			ul.appendChild(sponsorsDiv);
		}
		
		function addSponsor(sponsor,div){
			
		}

		
		function addImage(image,div){
			var imageDiv = document.createElement("div");
			imageDiv.class = "imageDiv";
			
			var image = document.createElement('img');
			image.src= "../"+image["path"];
			image.alt= "Bild nicht verfügbar";
			image.title= "EventBild";
			image.style.height="100px";
			
			var description = document.createTextNode(image["description"]);
			
			imageDiv.appendChild(image);
			imageDiv.appendChild(description);
			
			div.appendChild(imageDiv);
		}
	</script>
</head>

<body onload='init();'>

  <header>
   <a id="logo" href="./"><span>Do</span>-Aktiv</a> 
  </header>
  
  <nav>
  	<ul>
		<li class="active">Event</li>
   		<li><a href="../index.php">Home</li>
   		<li><a href="createEvent.php">Event erstellen</a></li>
   		<li><a href="eventList.php">Eventliste</a></li>
		<li id="loginfield"></li>
  	</ul>
  </nav>

  <main role="main">


  <section>
		<ul id="event" name="event" style="list-style-type: none;">
		</ul>
	
    </section>    
   
  	<aside>
		<!-- Sidebar -->
    	
	</aside>
	
    </main>
  	
    <footer>
		<!-- Footer -->
	</footer>

</body>
</html>
