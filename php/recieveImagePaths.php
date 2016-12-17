<?php
	//check token & id
	require('checker.php');
	
	//Initalize Array
	$emparray = array();
	
	//SQL Statement
	$pathsGet = $pdo->prepare("SELECT * from `imagePaths`");
	$pathsGet->execute();
	
	//Iterate, put rows in emparray
	while($row = $pathsGet->fetch()) {
		$emparray[] = $row;
	}
	
	//Echo json with rows
	echo json_encode($emparray);
?>