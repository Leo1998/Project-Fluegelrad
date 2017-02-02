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
				div.appendChild(logout);
				
				document.getElementById("header").appendChild(div);
			}else{
				var login = document.createElement("a");
				login.href = "login.html";
				login.innerHTML = "Anmelden";
				
				document.getElementById("header").appendChild(login);
			}
		}
	</script>
	</head>
	
	<body onload='init();'>
		<header id="header">
			<a href="home.php"> Home</a>
			<a href="eventList.php"> Eventliste</a>
		</header>
		
		<article>
			Home
		</article>
	</body>
</html>