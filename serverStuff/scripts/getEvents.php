<?php
	//check token & id
	require('checker.php');
	
	require('recieveDatabase.php');
	
	echo json_encode($emparray);
?>