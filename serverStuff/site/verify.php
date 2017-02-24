<?php
	session_start();

	//Get Hostname(name)
	if(isset($_POST['name'])) {
		$name = $_POST['name'];
	} else {
		header("Location: ../index.php?m=10");
		exit();
	}
	
	//Get password(pass)
	if(isset($_POST['pass'])) {
		$pass = $_POST['pass'];
	} else {
		header("Location: ../index.php?m=10");
		exit();
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
	while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
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
			
			$statement2 = $pdo->prepare("SELECT `events`.`id` FROM `events` WHERE `events`.`hostId` = ? AND `events`.`dateEnd` < ?");
			$time = date("Y-m-d H:i:s", time());
			$statement2->execute(array($hId,$time));
			
			$expiredEvents = $statement2->fetchAll(PDO::FETCH_ASSOC);
			unset($statement2);
			
			if(array_key_exists(0,$expiredEvents)){
				$expiredEvents = json_encode($expiredEvents);
				header("Location: ../index.php?m=0");
			}else{
				header("Location: ../index.php?m=1");
			}
			exit();
		}else{
			header("Location: ../index.php?m=2");
			exit();
		}
	}
	
	//exit if database does not contain Host
	if(!$knownHost){
		header("Location: ../index.php?m=2");
		exit();
	}
?>