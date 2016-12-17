<?php
	//Initalize PDO for mysql
	try {
		$pdo = new PDO('mysql:host=localhost;dbname=fluegelrad', 'testuser', 'BLysbG6Bsa2qn6nJ',array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
		//$pdo = new PDO('mysql:host=localhost;dbname=fluegelrad', 'dbUser', 'fluegelrad',array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
	} catch(PDOException $e) {
		exit("Connection failed");
    }
	
	// Function to get the client ip address
	function getClientIp() {
		$ipaddress = '';
		foreach (array('HTTP_CLIENT_IP', 'HTTP_X_FORWARDED_FOR', 'HTTP_X_FORWARDED', 'HTTP_X_CLUSTER_CLIENT_IP', 'HTTP_FORWARDED_FOR', 'HTTP_FORWARDED', 'REMOTE_ADDR') as $key){
			if (array_key_exists($key, $_SERVER) === true){
				foreach (explode(',', $_SERVER[$key]) as $ip){
					if (filter_var($ip, FILTER_VALIDATE_IP) !== false){
						$ipaddress = $ip;
						break;
					}
				}
			}
		}
		return $ipaddress;
	}
	
	//Get IP-Address and Port of Client
	$ip = getClientIp();
	$port = $_SERVER['REMOTE_PORT'];
	
	//Get count & expire for client-ip from Database
	$selectIps = $pdo->prepare("SELECT count,expire,port FROM spamProtection WHERE ip = ? AND type = ?");
	$selectIps->execute(array($ip,$type));
	
	$knownIp = false;
	$totalCount = 0;
	$expired = false;
	
	//Get count and expire for specific port and total count for ip from database
	while($row = $selectIps->fetch()) {
		if($expire < time()){
			$expired = true;
		}else{
			$totalCount += $row['count'];
			if($port == $row['port']){
				$expire = $row['expire'];
				$count = $row['count'];
				$knownIp = true;
			}
		}
	}
	
	//Stop if totalCount to high
	if($type == 0 && $totalCount > 25){ //Allow maximum of 25 type 0 requests per Ip
		exit("Please wait ".($expire-time())." secounds before trying again");
	}else if($type == 1 && $totalCount > 30){ //Allow maximum of 30 type 1 requests per Ip
		if($knownIp){
			exit("Please wait ".($expire-time())." secounds before trying again");
		}else{
			exit("Please wait before trying again");
		}
	}else if($type != 0 && $type != 1){ //Throw Error if type unknown
		if($knownIp){
			exit("Please wait ".($expire-time())." secounds before trying again");
		}else{
			exit("Please wait before trying again");
		}
	}
	
	//Choose new expire depending on type
	$newExpire = time();
	if($type==0){ //Type 0: Expires after 10 secounds
		$newExpire += 10;
	}else if($type==1){ //Type 1: Expires after 30 minutes
		$newExpire += 1800;
	}
	
	if($knownIp){ //Do if ip exists in database
		//Delete expired IPs if ip expired
		if($expired){
			$time = time();
			$deleteIps = $pdo->prepare("DELETE FROM spamProtection WHERE expire < :time");
			$deleteIps->bindParam('time', $time, PDO::PARAM_INT);
			$deleteIps->execute();
		}else{
			//Choose for Type
			if($type = 0 && $count > 2){ //Allow 3 type 0 requests per Ip+Port
				exit("Please wait ".($expire-time())." secounds before trying again");
			}else if($type = 1 && $count > 0){ //Allow 1 type 1 request per Ip+Port
				exit("Please wait ".($expire-time())." secounds before trying again");
			}else{ //Not blocked
				$updateIp = $pdo->prepare("UPDATE spamProtection SET expire = ?,count = ? WHERE ip = ? AND type = ?");
				$updateIp->execute(array($newExpire,$count+1,$ip,$type));
			}
		}
	}else{ //If Ip unknown, add Ip to database
		$addIp = $pdo->prepare("INSERT INTO `spamProtection` (`ip`, `count`, `expire`, `type`, `port`) VALUES (?, ?, ?, ?, ?);");
		$addIp->execute(array($ip,1,$newExpire,$type,$port));
	}
?>