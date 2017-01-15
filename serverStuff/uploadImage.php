<?php
	//Get describtion & event-id
	if(isset($_POST['describtion'])){
		$describtion = $_POST['describtion'];
	} else {
		exit("Error: Describtion could not be found");
	}
	
	if(isset($_POST['eventId'])){
		$eventId = $_POST['eventId'];
	} else {
		exit("Error: Event-Id could not be found");
	}
	
	//Load PDO and Spam-Protection
	$type=1;
	require('spamProtector.php');

	$upload_folder = 'upload/files/'; //Directory
	$filename = pathinfo($_FILES['userImage']['name'], PATHINFO_FILENAME);
	$extension = strtolower(pathinfo($_FILES['userImage']['name'], PATHINFO_EXTENSION));


	//Testing type
	$allowed_extensions = array('png', 'jpg', 'jpeg', 'gif');
	if(!in_array($extension, $allowed_extensions)) {
		exit("Error: Invalid type. Only png, jpg and gif are allowed");
	}
 
	//Testing size
	$max_size = 500*1024; //500 KB
	if($_FILES['userImage']['size'] > $max_size) {
		exit("Error: Image has to be smaller then or equal to 500 kb");
	}
 
	//Searching image for errors
	if(function_exists('exif_imagetype')) { //Die exif_imagetype-Funktion erfordert die exif-Erweiterung auf dem Server
		$allowed_types = array(IMAGETYPE_PNG, IMAGETYPE_JPEG, IMAGETYPE_GIF);
		$detected_type = exif_imagetype($_FILES['userImage']['tmp_name']);
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
	move_uploaded_file($_FILES['userImage']['tmp_name'], $new_path);
	echo 'Succesfull upload: <a href="'.$new_path.'">'.$new_path.'</a>';
	
	//Push path to Database
	$pathInsert = $pdo->prepare("INSERT INTO `imagePaths` (`id`, `path`, `eventId`, `description`) VALUES (NULL, ?, ?, ?)");
	$pathInsert->execute(array($new_path,$eventId,$describtion));
?>