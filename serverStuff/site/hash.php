<?php
	if(isset($_GET['s'])){
		$s = $_GET['s'];
	}else{
		exit("Error: ?s is not set");
	}
	
	echo password_hash($s, PASSWORD_DEFAULT);
?>