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
		LEFT JOIN (SELECT * from `imagePaths` GROUP BY `imagePaths`.`eventId`) AS `imagePaths` ON `imagePaths`.`eventId` = `events`.`id`
		LEFT JOIN (SELECT AVG(`rating`) AS `rating`,`eventId` FROM `eventRatings` GROUP BY `eventId`) AS `ratings` ON `ratings`.`eventId` = `events`.`id`
		ORDER BY `events`.`dateStart` , `events`.`dateEnd`");
	$statement->execute();
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$desc = explode("\n",$row["description"]);
		unset($row["description"]);
		if(isset($desc[0])){
			$row["description1"] = $desc[0];
		}else{
			$row["description1"] = "";
		}
		if(isset($desc[1])){
			$row["description2"] = $desc[1];
		}else{
			$row["description2"] = "";
		}
		$eventArray[] = $row;
	}
	
	if(isset($_SESSION['hostId'])){
		$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	}else{
		$hostStuff = "const hostStuff = null;";
	}
	
	echo "
<script type=\"text/javascript\">
	const data = ".json_encode($eventArray,JSON_PRETTY_PRINT).";
	$hostStuff
</script>
		";
?>

<html>
<head>
	<title>Eventliste</title>
    <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
	<meta charset="utf-8">
	<meta name="author" content="@Firmenname" /> <!-- Hier sollte der Name des Autors, der Inhalte erstellt, rein. -->
	<meta name="Description" content="Ersellen sie ihr Event!" /> 
    <link href="css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" /> <!-- Hier sollte der Pfad zur CSS-datei eingetragen werden, 
           die für die Bildschirmausgabe zuständig ist. Je nachdem in welchem Verzeichnis sich diese Datei befindet muss der Pfad angepasst werden. -->
	
    <script>
		function init(){
			for(var i = 0 ; i in data ; i++){
				addEvent(data[i]);
			}
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
				
				var createEvent = document.createElement('a');
				createEvent.href = "createEvent.php";
				createEvent.innerHTML = "Neues Event erstellen";
				
				document.getElementById("createEventDiv").appendChild(createEvent);
			}else{
				var login = document.createElement("a");
				login.href = "login.html";
				login.innerHTML = "Anmelden";
				
				document.getElementById("header").appendChild(login);
			}
		}
		
		function addEvent(event){
			var li = document.createElement('li');
			li.id = "event"+event["id"];
			
			var name = document.createElement('a');
			name.innerHTML = event["name"]+"<br>";
			name.href = "showEvent.php?k="+event["id"];
			name.class = "eventName";
			
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
					participants.innerHTML = event["participants"]+"/"+event["maxParticipants"]+"  Teilnehmern<br>";
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
			dateLoc.innerHTML = "Von "+event["dateStart"]+  " Uhr bis "+event["dateEnd"]+" Uhr <br>Ort: "+event["address"]+"<br>";
			
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
			li.appendChild(sponsorImg);
			li.appendChild(img);
			li.appendChild(price);
			li.appendChild(participants);
			li.appendChild(dateLoc);
			li.appendChild(desc);
			
			document.getElementById('events').appendChild(li);
			
			document.getElementById('events').appendChild(document.createElement('br'));
		}
	</script>
</head>
<body onload='init();'>
  <header>
   <a id="logo" href="./"><span>Do</span>-Aktiv</a> 
  </header>
  
  <nav>
  	<ul>
		<li><a href="home.php">Home</a></li>
		<li><a href="createEvent.php">Event erstellen</a></li>
   		<li class="active">Eventliste</li>
		<li class ="loginfield"></li>
  	</ul>
  </nav>

  <main role="main">


  <section>
	
	<ul id="events" name="events" style="list-style-type: none;">
	</ul>
	<br>
	<div id="createEventDiv"></div>
	
    </section>    
   
  	
    </main>
  	
    <footer>
		 
	</footer>

</body>
</html>
