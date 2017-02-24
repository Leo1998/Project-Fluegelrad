<?php
	session_start();
	
	if(!isset($_SESSION['hostId'])){
		header("Location: home.php?m=3");
		exit();
	}
	
	if(isset($_GET['k'])) {
		
	} else {
		header("Location: home.php?m=10");
		exit();
	}
?>