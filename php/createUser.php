<?php
	$pdo = new PDO('mysql:host=localhost;dbname=fluegelrad', 'testuser', 'ebLBBnZ8XCHSyQTJ');
	
	//Returns a random String
	function random_string() {
		if(function_exists('random_bytes')) {
			$bytes = random_bytes(16);
			$str = bin2hex($bytes); 
		} else if(function_exists('openssl_random_pseudo_bytes')) {
			$bytes = openssl_random_pseudo_bytes(16);
			$str = bin2hex($bytes); 
		} else if(function_exists('mcrypt_create_iv')) {
			$bytes = mcrypt_create_iv(16, MCRYPT_DEV_URANDOM);
			$str = bin2hex($bytes); 
		} else {
			$str = md5(uniqid('SOdfiov389sSug94kbv', true));
		}	
		return $str;
	}
	
	//Create Token
	$token = random_string();
	$hash = password_hash($token, PASSWORD_DEFAULT);
	
	//Create expire: Expires in one year
	$expire = time();
	$expire += 30758400;
	
	//Insert data in database and get Id
	$insertUser = $pdo->prepare("INSERT INTO `user` (`id`, `token`, `expire`, `hostId`) VALUES (NULL, ?, ?, 0);");
	$insertUser->execute(array($hash,$expire));
	$id = intval($pdo->lastInsertId());
	
	//Echos Token and Id
	echo json_encode(array($id,$token,$hash));
?>