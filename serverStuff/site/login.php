<?php
	session_start();

	//Get Hostname(name)
	if(isset($_POST['name'])) {
		$name = $_POST['name'];
	} else {
		exit("Error: ?name is not set");
	}
	
	//Get password(pass)
	if(isset($_POST['pass'])) {
		$pass = $_POST['pass'];
	} else {
		exit("Error: ?pass is not set");
	}

	//Spam protection, IP ban, Initalize PDO
	$type=2;
	require('../scripts/spamProtector.php');
	
	//Delete expired Sessions
	$time = time();
	$statement = $pdo->prepare("DELETE FROM sessions WHERE expire < :time");
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
	
	//Get hashed password from database
	$statement = $pdo->prepare("SELECT `hosts`.`pass`,`hosts`.`id`,`sponsors`.`imagePath` FROM `hosts` JOIN `sponsors` ON `hosts`.`sponsorId` = `sponsors`.`id` AND `sponsors`.`name` = ?");
	$statement->execute(array($name));
	//Stays false, if database does not contain host
	$knownHost = false;
	while($row = $statement->fetch()) {
		$pHash = $row['pass'];
		$hId = $row['id'];
		//Checks password with hashed password
		if(password_verify($pass, $pHash)){
			$knownHost = true;
			//Rehash pHash if necessary
			if(password_needs_rehash($pHash, PASSWORD_DEFAULT)){
				$pHash = password_hash($pass, PASSWORD_DEFAULT);
				$statement2 = $pdo->prepare("UPDATE `hosts` SET `hosts`.`pass` = ? WHERE `hosts`.`id` = ?");
				$statement2->execute(array($pHash,$hId));
				unset($statement2);
			}
			
			if(!isset($_SESSION['hostId'])){
				$_SESSION['hostId'] = $hId;
				$_SESSION['name'] = $name;
				$_SESSION['image'] = $row['imagePath'];
			}
			break;
		}else{
			exit("Passwort falsch");
		}
	}
	
	//exit if database does not contain Host
	if(!$knownHost){
		exit("Unbekannter Host");
	}
?>