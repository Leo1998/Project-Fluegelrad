<?php
	session_start();
	
	if(isset($_SESSION['hostId'])){
		$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	}else{
		$hostStuff = "const hostStuff = null;";
	}
	
	if(isset($_GET['m'])){
		switch ($_GET['m']) {
			case 0:	//0 : Expired Events
				if(isset($_SESSION['hostId'])){
					$message = "\"Willkommen ".$_SESSION['name']."! <br> Sie haben ausgelaufene Events\"";
				}else{
					$message = "null";
				}
				break;
			case 1: //1 : Host just logged in
				if(isset($_SESSION['hostId'])){
					$message = "\"Willkommen ".$_SESSION['name']."!\"";
				}else{
					$message = "null";
				}
				break;
			case 2: //2 : Invalid Password/Hostname
				$message = "\"Falsches Passwort oder <br> unbekannter Hostname\"";
				break;
			case 3: //3 : Has to be logged in
				$message = "\"Sie müssen angemeldet sein <br> um den Inhalt dieser Seite zu sehen\"";
				break;
			case 4: //4 : Logged out
				$message = "\"Sie wurden erfolgreich abgemeldet\"";
				break;
			case 10: //4 : Undefined Error
				$message = "\"Ein Fehler ist aufgetreten<br> Bitte versuchen sie es später erneut\"";
				break;
			default:
				$message = "null";
				break;
		}
	}else{
		$message = "null";
	}
	
	$message = "const message = ".$message.";";
	
	echo "
<script type=\"text/javascript\">
	$hostStuff
	$message
</script>
		";
?>
<!doctype html>

<html>
	<head>
		<title>Home</title>
		<script src="http://www.openlayers.org/api/OpenLayers.js"></script>
		<meta charset="utf-8">
		<meta name="author" content="@Firmenname" /> <!-- Hier sollte der Name des Autors, der Inhalte erstellt, rein. -->
		<meta name="Description" content="Ersellen sie ihr Event!" /> 
		<link href="site/css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" /> <!-- Hier sollte der Pfad zur CSS-datei eingetragen werden, 
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
					navLogout.href = "site/logout.php";
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
					logout.href = "site/logout.php";
					logout.innerHTML = "Abmelden";
					div.appendChild(logout);
					
					document.getElementById("hostInfos").appendChild(div);
					
					document.getElementById("hostInfos").className = "show";
				}else{
					var login = document.createElement("a");
					login.href = "site/login.php";
					login.innerHTML = "Anmelden";
					
					document.getElementById("loginfield").appendChild(login);
				}
				
				if(message != null){
					// Get the snackbar DIV
					var snackbar = document.getElementById("snackbar")
					
					// Set the message
					snackbar.innerHTML = message;
					
					// Add the "show" class to DIV
					snackbar.className = "show";

					// After 3 seconds, remove the show class from DIV
					setTimeout(function(){ snackbar.className = snackbar.className.replace("show", ""); }, 5500);
				}
				
			}
		</script>
	</head>

	<body onload='init();'>

	  <header>
	   <a id="logo" href="./"><span>Do</span>-Aktiv</a> 
	  </header>
	  
	  <nav>
		<ul>
			<li class="active">Home</li>
			<li><a href="site/createEvent.php">Event erstellen</a></li>
			<li><a href="site/eventList.php">Eventliste</a></li>
			<li id= "loginfield"></li>
		</ul>
	  </nav>

	  <main role="main">


	  <section>
				Home
		</section>    
	   
		<aside>
			<!-- Sidebar -->
			
		</aside>
		
		</main>
		
		<footer>
			<!-- Footer -->
		</footer>

		<div id="snackbar"></div>
		<div id="hostInfos"></div>
	</body>
</html>
