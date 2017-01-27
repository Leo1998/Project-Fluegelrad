<?php
	if(isset($_GET['h'])) {
		$h = $_GET['h'];
	} else {
		exit("Error: ?h is not set");
	}
	
	echo password_hash($h, PASSWORD_DEFAULT);
?>