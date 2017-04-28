<?php
	if(isset($_GET['k'])){
		$k = $_GET['k'];
	}else{
		header("Location: ../index.php?m=10");
		exit();
	}
	
	//Spam protection, IP ban, Initalize PDO
	$hostRequired=false;
	require('../scripts/sitePrepare.php');
	
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
		//header("Location: eventList.php");
		exit();
	}
	
	$event["description"] = nl2br($event["description"]);
	
	
	$statement = $pdo->prepare("SELECT 
		`sponsors`.`name`, `sponsors`.`imagePath`, `sponsors`.`id`,`sponsors`.`web`
		FROM `sponsors`
		JOIN `sponsoring` ON `sponsors`.`id` = `sponsoring`.`sponsorId` AND `sponsoring`.`eventId` = :eventId");
	$statement->bindParam('eventId', $k,PDO::PARAM_INT);
	$statement->execute();
	
	$sponsors = null;
	if($row = $statement->fetchAll(PDO::FETCH_ASSOC)) {
		$sponsors= $row;
	}
	
	
	$statement = $pdo->prepare("SELECT 
		`imagePaths`.`path`,`imagePaths`.`description`
		FROM `imagePaths`
		WHERE `imagePaths`.`eventId` = :eventId");
	$statement->bindParam('eventId', $k,PDO::PARAM_INT);
	$statement->execute();
	
	$images = null;
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
					var navLogout = document.createElement("a");
					navLogout.href = "logout.php";
					navLogout.innerHTML = "Abmelden";
					
					document.getElementById("loginfield").appendChild(navLogout);
					
					var div = document.createElement('div');
					
					div.appendChild(document.createTextNode("Angemeldet als:"));
					div.appendChild(document.createElement("br"));
					div.appendChild(document.createTextNode(hostStuff["name"]));
					div.appendChild(document.createElement("br"));
					
					var image = document.createElement('img');
					image.src= "../"+hostStuff["image"];
					image.alt= "Bild nicht verfügbar";
					image.title= "Vorschau";
					image.style.width="100px";
					image.style.height="100px";
					div.appendChild(image);
					
					div.appendChild(document.createElement("br"));
					
					var logout = document.createElement("a");
					logout.href = "logout.php";
					logout.innerHTML = "Abmelden";
					div.appendChild(logout);
					
					document.getElementById("hostInfos").appendChild(div);
					
					document.getElementById("hostInfos").className = "show";
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
				
				if(data["images"] !== null){
					for(var i = 0 ; i in data["images"] ; i++){
						imagesDiv.appendChild(addImage(data["images"][i]));
					}
				}
				if(data["sponsors"] !== null){
					for(var i = 0 ; i in data["sponsors"] ; i++){
						sponsorsDiv.appendChild(addSponsor(data["sponsors"][i]));
					}
				}
				
				var ul = document.getElementById("event");
				ul.appendChild(eventName);
				ul.appendChild(rating);
				ul.appendChild(imagesDiv);
				ul.appendChild(participants);
				ul.appendChild(price);
				ul.appendChild(description);
				ul.appendChild(hostName);
				ul.appendChild(document.createElement("br"));
				ul.appendChild(hostImage);
				ul.appendChild(sponsorsDiv);
			}
			
			function addSponsor(sponsor){
				var name = sponsor["name"]+"";
				var src = "../"+sponsor["imagePath"];
				var id = sponsor["id"]+0;
				var web = sponsor["web"]+"";
				
				var sponsorDiv = document.createElement("div");
				sponsorDiv.class = "sponsorDiv";
				
				var sponsorName = document.createElement("a");
				sponsorName.innerHTML = name;
				sponsorName.href = "showSponsor.php?k="+id;
				
				var image = document.createElement('img');
				image.src= src;
				image.alt= src;
				image.title= "SponsorBild";
				image.style.height="100px";
				
				var sponsorWeb = document.createElement("a");
				sponsorWeb.innerHTML = web;
				sponsorWeb.href = web;
				
				sponsorDiv.style.textAlign= "center";
				
				sponsorDiv.appendChild(image);
				sponsorDiv.appendChild(document.createElement("br"));
				sponsorDiv.appendChild(sponsorName);
				sponsorDiv.appendChild(document.createTextNode(" Web:"));
				sponsorDiv.appendChild(sponsorWeb);
				
				return sponsorDiv;
			}

			
			function addImage(image){
				var src = "../"+image["path"];
				var desc = image["description"]+"";
				
				var imageDiv = document.createElement("div");
				imageDiv.class = "imageDiv";
				
				var image = document.createElement('img');
				image.src= src;
				image.alt= src;
				image.title= "EventBild";
				image.style.width="50%";
				
				var description = document.createTextNode(desc);
				
				imageDiv.style.textAlign= "center";
				
				imageDiv.appendChild(image);
				imageDiv.appendChild(document.createElement("br"));
				imageDiv.appendChild(description);
				
				return imageDiv;
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
		
		<div id="hostInfos"></div>
	</body>
</html>
