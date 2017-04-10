<?php
	session_start();
	
	//Initalize PDO for mysql
	if(!isset($pdo)){
		try {
			$pdo = new PDO('mysql:host=localhost;dbname=fluegelrad', 'testuser', 'rVAEAbw9q5DSvhjp',array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
		} catch(PDOException $e) {
			exit("Error: Connection failed");
		}
	}
	
	if(isset($_SESSION['hostId'])){
		$hostStuff = "const hostStuff = {\"id\" : ".$_SESSION['hostId'].", \"name\" : \"".$_SESSION['name']."\", \"image\" : \"".$_SESSION['image']."\"};";
	}else{
		if($hostRequired){
			header("Location: ../index.php?m=3");
			exit();
		}
		$hostStuff = "const hostStuff = null;";
	}
?>