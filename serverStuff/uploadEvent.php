<?php

	// Very usefull function
	function getPost($id,$required){
		if(isset($_POST[$id])){
			return $_POST[$id];
		} else {
			if($required){
				exit("Error: ".$id." could not be found");
			}else{
				return false;
			}
		}
	}
	
	// Uploads an image
	function uploadImage($name){
		$upload_folder = 'upload/files/'; //Directory
		$filename = pathinfo($_FILES[$name]['name'], PATHINFO_FILENAME);
		$extension = strtolower(pathinfo($_FILES[$name]['name'], PATHINFO_EXTENSION));


		//Testing type
		$allowed_extensions = array('png', 'jpg', 'jpeg', 'gif');
		if(!in_array($extension, $allowed_extensions)) {
			exit("Error: Invalid image type. Only png, jpg and gif are allowed");
		}
	 
		//Testing size
		$max_size = 500*1024; //500 KB
		if($_FILES[$name]['size'] > $max_size) {
			exit("Error: Image has to be smaller then or equal to 500 kb");
		}
	 
		//Searching image for errors
		if(function_exists('exif_imagetype')) { //Die exif_imagetype-Funktion erfordert die exif-Erweiterung auf dem Server
			$allowed_types = array(IMAGETYPE_PNG, IMAGETYPE_JPEG, IMAGETYPE_GIF);
			$detected_type = exif_imagetype($_FILES[$name]['tmp_name']);
			if(!in_array($detected_type, $allowed_types)) {
				exit("Error: Only Images can be uploaded");
			}
		}
	 
		//Path for upload
		$new_path = $upload_folder.$filename.'.'.$extension;
	 
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
		return $new_path;
	}
	
	//load spamprotector & pdo
	$type=1;
	require('spamProtector.php');
	
	
	// ---- AGE ----
	$ageMin = getPost('ageMin',true);
	$ageMax = getPost('ageMax',true);
	if($ageMin > $ageMax){ //assure that ageMin is smaller then ageMax
		$ageMin = $ageMax;
		$ageMax = getPost('ageMin',true);
	}
	
	
	// ---- LOCATION ----
	$locId;
	
	if(getPost('knowLoc',true) == 1){ //Old Location
		$locId = getPost('location',true);
	}else{ //New Location
	
		//Get attributes for new location
		$nLon = getPost('longitude',true);
		$nLat = getPost('latitude',true);
		$nAdd = getPost('newAddress',true);
		
		//Insert location into database and retrieve Id
		$statement = $pdo->prepare("INSERT INTO `locations` (`id`, `address`, `latitude`, `longitude`) VALUES (NULL, ?, ?, ?);");
		$statement->execute(array($nAdd,$nLat,$nLon));
		$locId = intval($pdo->lastInsertId());
	}
	
	
	// ---- UPLOAD EVENT  (And retrieve some data from the form)----
	$statement = $pdo->prepare("INSERT INTO `events` (`id`, `name`, `locationId`, `price`, `maxParticipants`, `participants`, `hostId`, `dateStart`, `dateEnd`, `ageMin`, `ageMax`, `description`, `formId`)
								VALUES (NULL, :name, :locId, :price, :maxParticipants, :participants, :hostId, :dateStart, :dateEnd, :ageMin, :ageMax, :description, :formId)");
	$statement->execute(array(
		'name' => getPost('eventName',true),
		'locId' => $locId,
		'price' => getPost('price',true),
		'maxParticipants' => (getPost('participants',false) == false) ? (-1) : (getPost('participants',true)),
		'participants' => (getPost('countParticipants',false) == "on") ? (0) : (-1),
		'hostId' => 1,
		'dateStart' => getPost('dateStart',true),
		'dateEnd' => getPost('dateEnd',true),
		'ageMin' => $ageMin,
		'ageMax' => $ageMax,
		'description' => getPost('description',true),
		'formId' => 0,
	));
	$eventId = intval($pdo->lastInsertId());
	
	// ---- SPONSORS ----
	$sponsorIds = array();
	
	$maxSponsorId = getPost('maxSponsorId',true); //Retrieve when to stop
	for($i = 1; $i <= $maxSponsorId; $i++){
		if(getPost('sponsor '.$i,false) != false){ //Checkbox does only post if checked, so if getPost isn´t false, Checkbox existed and has been checked.
			$sponsorIds[] = $i;
		}
	}
	
	$newSponsors = getPost('sponsorCount',true);
	for($i = 1; $i <= $newSponsors; $i++ ){
		if(getPost('nameSponsor'.$i,false) != false){ //If getPost is false, either sponsor has been deleted or hasn´t been completed. In both situation, no need to add Sponsor.
			$imgPath = uploadImage('imageSponsor'.$i);
			$statement = $pdo->prepare("INSERT INTO `sponsors` (`id`, `imagePath`, `phone`, `mail`, `web`, `name`, `decribtion`) 
											VALUES (NULL, :imagePath, :phone, :mail, :web, :name, :description);");
			$statement->execute(array(
				'imagePath' => $imgPath,
				'phone' => getPost('phoneSponsor'.$i,false),
				'mail' => getPost('mailSponsor'.$i,false),
				'web' => getPost('webSponsor'.$i,false),
				'description' => getPost('descriptionSponsor'.$i,false),
				'name' => getPost('nameSponsor'.$i,true),
			));
			$sponsorIds[] = intval($pdo->lastInsertId());
		}
	}
	
	foreach($sponsorIds AS $sponsorId){
		$statement = $pdo->prepare("INSERT INTO `sponsoring` (`eventId`, `sponsorId`) VALUES (?,?)");
		$statement->execute(array($eventId,$sponsorId));
	}
	
	
	// ---- IMAGES ----
	$newImages = getPost('imageCount',true);
	for($i = 1; $i <= $newImages; $i++ ){
		if(getPost('descriptionImage'.$i,false) != false){ //If getPost is false, either image has been deleted or hasn´t been completed. In both situation, no need to add Image.
			$imgPath = uploadImage('imageImage'.$i);
			$statement = $pdo->prepare("INSERT INTO `imagePaths` (`id`, `path`, `eventId`, `description`)
										VALUES (NULL, :path, :eventId, :description)");
			$statement->execute(array(
				'path' => $imgPath,
				'eventId' => $eventId,
				'description' => getPost('descriptionImage'.$i,true),
			));
		}
	}
?>