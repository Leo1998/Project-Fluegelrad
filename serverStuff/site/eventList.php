<?php
	//Spam protection, IP ban, Initalize PDO
	$hostRequired=false;
	require('../scripts/sitePrepare.php');
	
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
		WHERE `events`.`dateEnd` > STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s')
		ORDER BY `events`.`dateStart` , `events`.`dateEnd`");
	$time = date("Y-m-d H:i:s", time());
	$statement->execute(array($time));
	
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$desc = $row["description"];
		$descArr = explode("\\n",$row["description"]);
		if(count($descArr) > 1){
			$desc = $descArr[0]."<br>".$descArr[1]."<br>...";
		}else{
			if(isset($descArr[0])){
				$desc = $descArr[0];
			}
		}
		if(strlen($desc) > 100){
			$desc = substr($desc,0,100)."...";
		}
		$row['description'] = $desc;
		$eventArray[] = $row;
	}
	
	$expiredArray = array();
	
	if(isset($_SESSION['hostId'])){
		//GET EXPIRED EVENTS
		$statement = $pdo->prepare("SELECT
			`events`.`id` , `events`.`name` , `events`.`dateEnd`
			FROM `events`
			WHERE `events`.`hostId` = ? AND `events`.`dateEnd` < STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s')");
		$statement->execute(array($_SESSION['hostId'],$time));
		
		$expiredArray = $statement->fetchAll(PDO::FETCH_ASSOC);
	}
	
	echo "
<script type=\"text/javascript\">
	const data = ".json_encode($eventArray,JSON_PRETTY_PRINT).";
	const expired = ".json_encode($expiredArray,JSON_PRETTY_PRINT).";
	$hostStuff
</script>
		";
?>
<!doctype html>
<html>
	<head>
		<title>Eventliste</title>
		<script src="http://www.openlayers.org/api/OpenLayers.js"></script>
		<meta charset="utf-8">
		<meta name="Description" content="Liste aller Events" http-equiv="pragma" content="no-cache"/> 
		<link href="css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" />
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
			function init(){
				for(var i = 0 ; i in data ; i++){
					addEvent(data[i]);
				}
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
					
					if(expired.length > 0){
						document.getElementById("expiredHeader").innerHTML = "Sie haben folgende ausgelaufenen Events";
						
						var ul = document.getElementById("expiredEvents");
						
						for(var i = 0 ; i in expired ; i++){
							var li = document.createElement("li");
							
							var name = document.createElement('a');
							name.innerHTML = expired[i]["name"]+"<br>";
							name.href = "expiredEvent.php?k="+expired[i]["id"];
							name.class = "eventName";
							
							li.appendChild(name);
							li.appendChild(document.createTextNode("Ausgelaufen seit: "+expired[i]["dateEnd"]));
							
							ul.appendChild(li);
							ul.appendChild.createElement("br");
						}
					}else{
						document.getElementById("expiredHeader").innerHTML = "Sie haben keine ausgelaufenen Events";
					}
					
				}else{
					var login = document.createElement("a");
					login.href = "login.php";
					login.innerHTML = "Anmelden";
					
					document.getElementById("loginfield").appendChild(login);
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
				desc.innerHTML = event["description"]+"<br>";
				
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
			
				<li><a href="../index.php">Home</a></li>
				<li><a href="createEvent.php">Event erstellen</a></li>
				<li class="active">Eventliste</li>
				<li id ="loginfield"></li>
				
			</ul>
			
		</nav>

		<main role="main">

			<section>
			
				<ul id="events" name="events" style="list-style-type: none;">
				</ul>
				<br>
				<h2 id="expiredHeader"></h2>
				<ul id="expiredEvents" style="list-style-type: none;">
				</ul>
			
			</section>    
			
		</main>
		
		<div id="hostInfos"></div>
	</body>
</html>
