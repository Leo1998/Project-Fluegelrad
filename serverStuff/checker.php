<?php
	//Get UserId(u)
	if(isset($_GET['u'])) {
		$u = $_GET['u'];
	} else {
		exit("Error: ?u is not set");
	}
	
	//Get Token(t)
	if(isset($_GET['t'])) {
		$t = $_GET['t'];
	} else {
		exit("Error: ?t is not set");
	}

	//Spam protection, IP ban, Initalize PDO
	$type=0;
	require('spamProtector.php');
	
	//Delete expired Users
	$time = time();
	$statement = $pdo->prepare("DELETE FROM users WHERE expire < :time");
	$statement->bindParam('time', $time, PDO::PARAM_INT);
	$statement->execute();

	//Create random string
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
	
	//Get hashed token from database
	$statement = $pdo->prepare("SELECT token,id FROM users WHERE id = ?");
	$statement->execute(array($u));
	//Stays false, if database does not contain id
	$idPresent = false;
	while($row = $statement->fetch()) {
		$sHash = $row['token'];
		$sId = $row['id'];
		//Checks token with hashed token
		if(password_verify($t, $sHash)){
			$idPresent = true;
			//Creates new token and updates expire if token is correct
			$newToken = random_string();
			$newHash = password_hash($newToken, PASSWORD_DEFAULT);
			$expire = time();
			$expire += 30758400;
			$statement2 = $pdo->prepare("UPDATE users SET token = ? , expire = ? WHERE id = ?");
			$statement2->execute(array($newHash,$expire,$sId));
			unset($statement2);
			//Echos new token
			echo json_encode(array($newToken)).",";
			break;
		}else{
			exit("Error: Invalid Token");
		}
	}
	
	//exit if database does not contain token
	if(!$idPresent){
		exit("Error: Unknown ID");
	}
?>