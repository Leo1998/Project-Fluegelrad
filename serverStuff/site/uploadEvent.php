<?php
	/*
		TODO
		Erst ALLE Informationen sammeln (Auch zu Sponsoren & Bildern) und erst am Ende entweder:
		1) Hochladen, wenn es keine Fehler gab
		2) Auf createEvent MIT vorher eingetragenen Infos zurückschicken
		
		Alle Informationen in $eventParams
		Fortschritt: bis Sponsors (exklusiv)
	*/

	//Spam protection, IP ban, Initalize PDO
	$hostRequired=true;
	require('../scripts/sitePrepare.php');
	
	// ---- Non-Fatal Errors ----
	$nfErrors = array();
	// ---- Fatal Errors ----
	$fErrors = array();
	// ---- Image Upload Params ----
	$imageParams = array();
	// ---- Event Params for Upload ----
	$eventParams = array();
	
	// Very usefull function
	/*	id : parameter-name
		fatal : fatal error if unset
		errMessage : error-message if parameter unset*/
	function getPost($id,$fatal,$errMessage){
		global $nfErrors;
		global $fErrors;
		if(isset($_POST[$id])){
			if(!empty($_POST[$id])){
				return $_POST[$id];
			}
		}
		if($fatal){
			$fErrors[] = $errMessage;
		}else if(!empty($errMessage)){
			$nfErrors[] = $errMessage;
		}
		return false;
	}
	
	// Prepares Image-Upload
	function prepareImage($name,$description){
		global $nfErrors;
		global $fErrors;
		$upload_folder = '../upload/files/'; //Directory
		$filename = pathinfo($_FILES[$name]['name'], PATHINFO_FILENAME);
		$extension = strtolower(pathinfo($_FILES[$name]['name'], PATHINFO_EXTENSION));


		//Testing type
		$allowed_extensions = array('png', 'jpg', 'jpeg', 'gif');
		if(!in_array($extension, $allowed_extensions)) {
			$nfErrors[] = "Ungültiges Dateiformat bei Bild mit der Beschreibung: "+$description;
			return null;
		}
	 
		//Testing size
		$max_size = 500*1024; //500 KB
		if($_FILES[$name]['size'] > $max_size) {
			$nfErrors[] = "Datei zu groß bei Bild mit der Beschreibung: "+$description;
			return null;
		}
	 
		//Searching image for errors
		if(function_exists('exif_imagetype')) { //Die exif_imagetype-Funktion erfordert die exif-Erweiterung auf dem Server
			$allowed_types = array(IMAGETYPE_PNG, IMAGETYPE_JPEG, IMAGETYPE_GIF);
			$detected_type = exif_imagetype($_FILES[$name]['tmp_name']);
			if(!in_array($detected_type, $allowed_types)) {
				$nfErrors[] = "Datei war kein Bild bei Bild mit der Beschreibung: "+$description;
				return null;
			}
		}
	 
		//Path for upload
		$new_path = $upload_folder.$filename.'.'.$extension;
		
		return array(
			'new_path' => $upload_folder.$filename.'.'.$extension,
			'name' => $name,
			'filename' => $filename,
			'upload_folder' => $upload_folder,
			'extension' => $extension
		);
		
		/*//New name if name already exists
		if(file_exists($new_path)) { //If name already exists, add number to name
			$id = 1;
			do {
				$new_path = $upload_folder.$filename.'_'.$id.'.'.$extension;
				$id++;
			} while(file_exists($new_path));
		}
	 
		//Upload file
		move_uploaded_file($_FILES[$name]['tmp_name'], $new_path);
		return str_replace("../","",$new_path);*/
	}
	
	//Uploads an image (use return from prepareImage for params)
	function uploadImage($params){
		if(isset($params['new_path'])){
			$new_path = $params['new_path'];
		}else{
			return;
		}
		
		if(isset($params['name'])){
			$name = $params['name'];
		}else{
			return;
		}
		
		if(isset($params['filename'])){
			$filename = $params['filename'];
		}else{
			return;
		}
		
		if(isset($params['upload_folder'])){
			$upload_folder = $params['upload_folder'];
		}else{
			return;
		}
		
		if(isset($params['extension'])){
			$extension = $params['extension'];
		}else{
			return;
		}
		
		//New name if name already exists
		if(file_exists($new_path)) { //If name already exists, add number to name
			$id = 1;
			do {
				$new_path = $upload_folder.$filename.'_'.$id.'.'.$extension;
				$id++;
			} while(file_exists($new_path));
		}
	 
		//Upload file
		move_uploaded_file($_FILES[$name]['tmp_name'], $new_path);
		return str_replace("../","",$new_path);
	}
	
	// ---- AGE ----
	$ageMin = getPost('ageMin',false,null);
	$ageMax = getPost('ageMax',false,null);
	if(empty($ageMin)){
		$ageMin = 0;
	}
	if(empty($ageMax)){
		$ageMax = 0;
	}
	if(empty($fErrors)){
		if($ageMin > $ageMax){ //assure that ageMin is smaller then ageMax
			$eventParams['ageMin'] = $ageMax;
			$eventParams['ageMax'] = $ageMin;
		}else{
			$eventParams['ageMax'] = $ageMax;
			$eventParams['ageMin'] = $ageMin;
		}
	}
	
	// ---- RETRIEVE PARAMS ----
	$eventParams['name'] = getPost('eventName',true,"Eventname war nicht gesetzt");
	$eventParams['price'] = (getPost('price',false,null) == false) ? (0) : (getPost('price',true,"Fehler 02.1 beim Event-Hochladen"));
	$eventParams['maxParticipants'] = (getPost('participants',false,null) == false) ? (-1) : (getPost('participants',true,"Fehler 02.2 beim Event-Hochladen"));
	$eventParams['participants'] = (getPost('countParticipants',false,null) == "on") ? (0) : (-1);
	$eventParams['hostId'] = $_SESSION['hostId'];
	$eventParams['dateStart'] = getPost('dateStart',true,"Startdatum war nicht gesetzt");
	$eventParams['dateEnd'] = getPost('dateEnd',true,"Enddatum war nicht gesetzt");
	$eventParams['description'] = getPost('description',true,"Beschreibung war nicht gesetzt");
	$eventParams['formId'] = 0;
	
	
	// ---- LOCATION ----
	
	if(getPost('knowLoc',true,"Fehler 01 beim Event-Hochladen") == 1){ //Old Location
		$eventParams['locId'] = getPost('location',true,"Addresse war nicht gesetzt");
	}else if(!empty(getPost('knowLoc',false,null))){ //New Location
	
		//Get attributes for new location
		$nLon = getPost('longitude',true,"Längengrad war nicht gesetzt");
		$nLat = getPost('latitude',true,"Breitengrad war nicht gesetzt");
		$nAdd = getPost('newAddress',true,"Addresse war nicht gesetzt");
		
		if(empty($fErrors)){
			//Insert location into database and retrieve Id
			/*$statement = $pdo->prepare("INSERT INTO `locations` (`id`, `address`, `latitude`, `longitude`) VALUES (NULL, ?, ?, ?);");
			$statement->execute(array($nAdd,$nLat,$nLon));
			intval($pdo->lastInsertId());*/
			$eventParams['newLoc'] = array(
				'address' => $nAdd,
				'lat' => $nLat,
				'lon' => $nLon);
		}
	}
	
	// ---- UPLOAD EVENT  (And retrieve some data from the form)----
	
	/*if(empty($fErrors)){
		$statement = $pdo->prepare("INSERT INTO `events` (`id`, `name`, `locationId`, `price`, `maxParticipants`, `participants`, `hostId`, `dateStart`, `dateEnd`, `ageMin`, `ageMax`, `description`, `formId`)
								VALUES (NULL, :name, :locId, :price, :maxParticipants, :participants, :hostId, :dateStart, :dateEnd, :ageMin, :ageMax, :description, :formId)");
		$statement->execute($eParams);
		$eventId = intval($pdo->lastInsertId());
	}else{
		$eventId = 0;
	}*/
	
	
	// ---- SPONSORS ----
	
	$maxSponsorId = getPost('maxSponsorId',true,"Fehler 03 beim Event-Hochladen"); //Retrieve when to stop
	for($i = 1; $i <= $maxSponsorId; $i++){
		if(getPost('sponsor'.$i,false,null) != false){ //Checkbox does only post if checked, so if getPost isn´t false, Checkbox existed and has been checked.
			$eventParams['sponsorIds'][] = $i;
		}
	}
	
	$newSponsors = getPost('sponsorCount',false,null);
	if(!empty($newSponsors)){
		for($i = 1; $i <= $newSponsors; $i++ ){
			if(getPost('nameSponsor'.$i,false,null) != false){ //If getPost is false, either sponsor has been deleted or hasn´t been completed. In both situation, no need to add Sponsor.
				$eventParams['newSponsors'][] = array(
					'image' => prepareImage('imageSponsor'.$i,getPost('nameSponsor'.$i,true,"Fehler 05 beim Event-Hochladen")),
					'phone' => getPost('phoneSponsor'.$i,false,null),
					'mail' => getPost('mailSponsor'.$i,false,null),
					'web' => getPost('webSponsor'.$i,false,null),
					'description' => getPost('descriptionSponsor'.$i,false,null),
					'name' => getPost('nameSponsor'.$i,true,"Fehler 05 beim Event-Hochladen"),
				);
				/*if(empty($fErrors)){
					$statement = $pdo->prepare("INSERT INTO `sponsors` (`id`, `imagePath`, `phone`, `mail`, `web`, `name`, `decribtion`) 
												VALUES (NULL, :imagePath, :phone, :mail, :web, :name, :description);");
					$statement->execute();
					$sponsorIds[] = intval($pdo->lastInsertId());
				}*/
			}
		}
	}
	
	/*if(empty($fErrors)){
		foreach($sponsorIds AS $sponsorId){
			$statement = $pdo->prepare("INSERT INTO `sponsoring` (`eventId`, `sponsorId`) VALUES (?,?)");
			$statement->execute(array($eventId,$sponsorId));
		}
	}*/
	
	
	// ---- IMAGES ----
	$newImages = getPost('imageCount',false,null);
	
	for($i = 1; $i <= $newImages; $i++ ){
		if(getPost('descriptionImage'.$i,false,null) != false){ //If getPost is false, either image has been deleted or hasn´t been completed. In both situation, no need to add Image.
			$eventParams['images'][] = $params = array(
				'image' => prepareImage('imageImage'.$i,getPost('descriptionImage'.$i,false,null)),
				'description' => getPost('descriptionImage'.$i,false,"Bildbeschreibung bei Bild $i wurde nicht gesetzt"),
			);
			/*if(empty($fErrors)){
				$statement = $pdo->prepare("INSERT INTO `imagePaths` (`id`, `path`, `eventId`, `description`)
										VALUES (NULL, :path, :eventId, :description)");
				$statement->execute($params);
			}*/
		}
	}
	
	if(empty($eventParams['images'])){
		$fErrors[] = 'Es wurden keine Bilder hochgeladen';
	}
	
	//------ UPLOAD STARTS HERE -------
	if(empty($fErrors)){
		if(isset($eventParams['newLoc'])){ //Upload new Location if necassary
			$statement = $pdo->prepare("INSERT INTO `locations` (`id`, `address`, `latitude`, `longitude`) VALUES (NULL, :address, :lat, :lon);");
			$statement->execute($eventParams['newLoc']);
			unset($eventParams['newLoc']);
			$eventParams['locId'] = intval($pdo->lastInsertId());
		}
		
		//Upload event
		$statement = $pdo->prepare("INSERT INTO `events` (`id`, `name`, `locationId`, `price`, `maxParticipants`, `participants`, `hostId`, `dateStart`, `dateEnd`, `ageMin`, `ageMax`, `description`, `formId`)
								VALUES (NULL, :name, :locId, :price, :maxParticipants, :participants, :hostId, :dateStart, :dateEnd, :ageMin, :ageMax, :description, :formId)");
		$statement->execute(array(
			'name' => $eventParams['name'],
			'locId' => $eventParams['locId'],
			'price' => $eventParams['price'],
			'maxParticipants' => $eventParams['maxParticipants'],
			'participants' => $eventParams['participants'],
			'hostId' => $eventParams['hostId'],
			'dateStart' => $eventParams['dateStart'],
			'dateEnd' => $eventParams['dateEnd'],
			'ageMin' => $eventParams['ageMin'],
			'ageMax' => $eventParams['ageMax'],
			'description' => $eventParams['description'],
			'formId' => $eventParams['formId']
		));
		$eventParams['eventId'] = intval($pdo->lastInsertId());
		
		//Upload Sponsors
		if(isset($eventParams['newSponsors'])){
			foreach($eventParams['newSponsors'] as $sponsor){
				$sponsor['imagePath'] = uploadImage($sponsor['image']);
				unset($sponsor['image']);
				$statement = $pdo->prepare("INSERT INTO `sponsors` (`id`, `imagePath`, `phone`, `mail`, `web`, `name`, `decribtion`) 
											VALUES (NULL, :imagePath, :phone, :mail, :web, :name, :description);");
				$statement->execute($sponsor);
				$eventParams['sponsorIds'][] = intval($pdo->lastInsertId());
			}
		}
		
		//Upload sponsoring
		foreach($eventParams['sponsorIds'] as $sponsorId){
			$statement = $pdo->prepare("INSERT INTO `sponsoring` (`eventId`, `sponsorId`) VALUES (?,?)");
			$statement->execute(array($eventParams['eventId'],$sponsorId));
		}
		
		//Upload Images
		foreach($eventParams['images'] as $image){
			$image['imagePath'] = uploadImage($image['image']);
			unset($image['image']);
			$image['eventId'] = $eventParams['eventId'];
			$statement = $pdo->prepare("INSERT INTO `imagePaths` (`id`, `path`, `eventId`, `description`)
										VALUES (NULL, :imagePath, :eventId, :description)");
			$statement->execute($image);
		}
	}
	
	
	$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"}";
	
	echo "
		<script type=\"text/javascript\">
			const fErrors = ".json_encode($fErrors,JSON_PRETTY_PRINT).";
			const nfErrors = ".json_encode($nfErrors,JSON_PRETTY_PRINT).";
			$hostStuff;
		</script>
	";
?>
<!doctype html>

<html>
	<head>
		<title>Event hochladen</title>
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
				
				if(fErrors.length < 1){
					document.getElementById("successMessage").innerHTML = "Event wurde erfolgreich hochgeladen";
					document.title = "Event erfolgreich hochgeladen";
				}else{
					document.getElementById("successMessage").innerHTML = "Das Event konnte leider nicht hochgeladen werden";
					document.title = "Fehler beim Hochladen";
					
					document.getElementById("fMessage").innerHTML = "Wegen folgenden Fehlern konnte das Event nicht hochgeladen werden:";
					
					var fErrorList = document.getElementById("fErrors");
					for (var i = 0; i < fErrors.length; i++){
						var li = document.createElement("li");
						
						li.appendChild(document.createTextNode(fErrors[i]));
						
						fErrorList.appendChild(li);
					}
				}
				
				if(nfErrors.length > 0){
					document.getElementById("nfMessage").innerHTML = "Es sind folgende Fehler aufgetreten:";
					
					var nfErrorList = document.getElementById("nfErrors");
					for (var i = 0; i < nfErrors.length; i++){
						var li = document.createElement("li");
						
						li.appendChild(document.createTextNode(nfErrors[i]));
						
						fErrorList.appendChild(li);
					}
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
				<li class="active">Event erstellen</li>
				<li><a href="eventList.php">Eventliste</a></li>
				<li id= "loginfield"></li>
			</ul>
		</nav>

		<main role="main">


		<section>
			<h2 id="successMessage"></h2>
			<h2 id="fMessage"></h2>
			<ul id="fErrors" name="fErrors" style="list-style-type: none;">
			</ul>
			<h2 id="nfMessage"></h2>
			<ul id="nfErrors" name="nfErrors" style="list-style-type: none;">
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
	
	<div id="hostInfos"></div>
</html>