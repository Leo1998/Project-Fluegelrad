<?php
	session_start();

	if(isset($_SESSION['hostId'])){
		$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	}else{
		$hostStuff = "const hostStuff = null;";
	}
	
	echo "
<script type=\"text/javascript\">
	$hostStuff
</script>
		";
?>
<!doctype html>
<html>
	<head>
		<title>Login</title>
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
			
			function init(){
				if(hostStuff != null){
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
			<li><a href="../index.php">Home</a></li>
			<li><a href="createEvent.php">Event erstellen</a></li>
			<li><a href="eventList.php">Eventliste</a></li>
			<li class="active">Login</li>
		</ul>
	  </nav>

	  <main role="main">


	  <section>
		<h2> Login: </h2>
			<form action="verify.php" id="login" method="post">
					<label>Name:</label>
					<input type="text" id="name" name="name" maxLength="30" placeholder="Name">
					<label>Passwort:</label>
					<input type="password" id="pass" name="pass" maxLength="30" placeholder="Passwort">
					<input type="submit" value="Anmelden" class="btnSubmit" />
				</form>
		
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