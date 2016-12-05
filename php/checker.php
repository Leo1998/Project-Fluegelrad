<?php
	//Initalize PDO for mysql
	try {
		$pdo = new PDO('mysql:host=localhost;dbname=fluegelrad', 'dbUser', 'fluegelrad');
	} catch(PDOException $e) {
		echo "Connection failed: " . $e->getMessage();
    }
	
	//Delete expired Users
	$time = time();
	$deleteUsers = $pdo->prepare("DELETE FROM user WHERE expire < :time");
	$deleteUsers->bindParam('time', $time, PDO::PARAM_INT);
	$deleteUsers->execute();
	
	//Get UserId(u)
	if(isset($_GET['u'])) {
		$u = $_GET['u'];
	} else {
		exit("?u is not set");
	}
	
	//Get Token(t)
	if(isset($_GET['t'])) {
		$t = $_GET['t'];
	} else {
		exit("?t is not set");
	}

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
	$selectTokens = $pdo->prepare("SELECT token,id FROM user WHERE id = ?");
	$selectTokens->execute(array($u));
	//Stays false, if database does not contain id
	$idPresent = false;
	while($row = $selectTokens->fetch()) {
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
			$updateToken = $pdo->prepare("UPDATE user SET token = ? , expire = ? WHERE id = ?");
			$updateToken->execute(array($newHash,$expire,$sId));
			//Echos new token
			echo json_encode(array($newToken)) . ", ";
			break;
		}else{
			exit("Invalid Token");
		}
	}
	
	//exit if database does not contain token
	if(!$idPresent){
		exit("Unknown ID");
	}
?>