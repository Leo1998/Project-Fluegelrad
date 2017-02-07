<?php
	session_start();
	
	if(isset($_GET['k'])){
		$k = $_GET['k'];
	}else{
		header("Location: home.php");
		exit();
	}
	
	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	$statement = $pdo->prepare("SELECT * FROM `sponsors` WHERE `sponsors`.`id` = ?");
	$statement->execute(array($k));
	
	if($row = $statement->fetch(PDO::FETCH_ASSOC)) {
		$sponsor = $row;
	}else{
		header("Location: home.php");
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

<html>
<head>
    <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
	<meta charset="utf-8">
	<meta name="author" content="@Firmenname" /> <!-- Hier sollte der Name des Autors, der Inhalte erstellt, rein. -->
	<meta name="Description" content="Ersellen sie ihr Event!" /> 
    <link href="css/screen.css" rel="stylesheet" type="text/css" media="screen, projection" /> <!-- Hier sollte der Pfad zur CSS-datei eingetragen werden, 
           die für die Bildschirmausgabe zuständig ist. Je nachdem in welchem Verzeichnis sich diese Datei befindet muss der Pfad angepasst werden. -->
	<script>
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
				login.href = "login.html";
				login.innerHTML = "Anmelden";
				
				document.getElementById("header").appendChild(login);
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
   		<li><a href="home.php">Home</li>
   		<li><a href="createEvent.php">Event erstellen</a></li>
   		<li><a href="eventList.php">Eventliste</a></li>
		<li class= "loginfield"></li>
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

</body>
</html>
