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


<html>
<head>
	<title>Home</title>
    <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
	<meta charset="utf-8">
	<meta name="author" content="@Firmenname" /> <!-- Hier sollte der Name des Autors, der Inhalte erstellt, rein. -->
	<meta name="Description" content="Ersellen sie ihr Event!" /> 
    <link href="site/css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" /> <!-- Hier sollte der Pfad zur CSS-datei eingetragen werden, 
           die für die Bildschirmausgabe zuständig ist. Je nachdem in welchem Verzeichnis sich diese Datei befindet muss der Pfad angepasst werden. -->
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
				logout.href = "site/logout.php";
				logout.innerHTML = "Abmelden";
				
				div.appendChild(nameLabel);
				div.appendChild(image);
				//div.appendChild(logout);
				document.getElementById("loginfield").appendChild(logout);
				//document.getElementById("header").appendChild(div);
			}else{
				var login = document.createElement("a");
				login.href = "site/login.html";
				login.innerHTML = "Anmelden";
				
				document.getElementById("loginfield").appendChild(login);
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

</body>
</html>
