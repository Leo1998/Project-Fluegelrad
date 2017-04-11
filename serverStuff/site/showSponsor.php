<?php
	//Spam protection, IP ban, Initalize PDO
	$hostRequired=false;
	require('../scripts/sitePrepare.php');
	
	$statement = $pdo->prepare("SELECT * FROM `sponsors` WHERE `sponsors`.`id` = ?");
	$statement->execute(array($k));
	
	if($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$sponsor = $row;
	}else{
		header("Location: ../index.php?m=10");
		exit();
	}
	
	$sponsor["description"] = nl2br($sponsor["description"]);
	
	
	if(isset($_SESSION['hostId'])){
		$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	}else{
		$hostStuff = "const hostStuff = null;";
	}
	
	
	echo "
<script type=\"text/javascript\">
	const data = ".json_encode($sponsor,JSON_PRETTY_PRINT).";
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
				
				
				var sponsorName = document.createElement("h1");
				sponsorName.appendChild(document.createTextNode(data["name"]));
				
				var sponsorImg = document.createElement('img');
				sponsorImg.class = "eventImg";
				sponsorImg.src = "../"+data["imagePath"];
				sponsorImg.alt = "Bild nicht verfügbar";
				sponsorImg.style.height = "200px";
				
				var description = document.createElement("p");
				description.innerHTML = data["description"];
				
				var phone = document.createElement("label");
				if(data["phone"] != null){
					phone.innerHTML = "Tel: "+data["phone"]+"<br>";
				}else{
					phone.innerHTML = "Tel: Nicht angegeben<br>"
				}
				
				var mail = document.createElement("label");
				if(data["mail"] != null){
					mail.innerHTML = "E-Mail: "+data["mail"]+"<br>";
				}else{
					mail.innerHTML = "E-Mail: Nicht angegeben<br>"
				}
				
				var web = document.createElement("label");
				if(data["web"] != null){
					web.innerHTML = "Website: "+data["web"]+"<br>";
				}else{
					web.innerHTML = "Website: Nicht angegeben<br>"
				}
				
				
				var sponsorDiv = document.getElementById("sponsor");
				sponsorDiv.appendChild(sponsorName);
				sponsorDiv.appendChild(sponsorImg);
				sponsorDiv.appendChild(description);
				sponsorDiv.appendChild(phone);
				sponsorDiv.appendChild(mail);
				sponsorDiv.appendChild(web);
			}
		</script>
	</head>

	<body onload='init();'>

	  <header>
	   <a id="logo" href="./"><span>Do</span>-Aktiv</a> 
	  </header>
	  
	  <nav>
		<ul>
			<li class="active">Sponsor</li>
			<li><a href="../index.php">Home</li>
			<li><a href="createEvent.php">Event erstellen</a></li>
			<li><a href="eventList.php">Eventliste</a></li>
			<li id= "loginfield"></li>
		</ul>
	  </nav>

	  <main role="main">


	  <section>
			<ul id="sponsor" name="sponsor" style="list-style-type: none;">
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
