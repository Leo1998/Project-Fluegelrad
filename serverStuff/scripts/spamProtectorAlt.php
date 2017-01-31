<?php
	//Initalize PDO for mysql
	try {
		$pdo = new PDO('mysql:host=localhost;dbname=fluegelrad', 'dbUser', 'fluegelrad',array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
	} catch(PDOException $e) {
		exit("Error: Connection failed");
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
	
	if($type == 0 || $type == 1){ //Type 0 : Standard ; Type 1 : createUser
		//Get IP-Address of Client
		$ip = getClientIp();
		
		//Get count & expire for client-ip from Database
		$statement = $pdo->prepare("SELECT `count`,`expire` FROM `spamProtection` WHERE `ip` = ? AND `type` = ?");
		$statement->execute(array($ip,$type));
		
		$knownIp = false;
		
		//Get count and expire for specific port and total count for ip from database
		while($row = $statement->fetch(PDO::FETCH_ASSOC)) {
			$knownIp = true;
			if($row['expire'] < time()){
				$time = time();
				$statement2 = $pdo->prepare("DELETE FROM `spamProtection` WHERE `expire` < :time");
				$statement2->bindParam('time', $time, PDO::PARAM_INT);
				$statement2->execute();
				unset($statement2);
			}else{
				if($type == 0 && $row['count'] >= 20){ //Allow 20 type 0 requests per Ip
					exit("Error: Please wait ".($row['expire']-time())." seconds before trying again");
				}else if($type == 1 && $row['count'] >= 2){ //Allow 2 type 1 request per Ip
					exit("Error: Please wait ".($row['expire']-time())." seconds before trying again");
				}else{ //Not blocked
					$newExpire = time();
					if($type==0){ //Type 0: Expires after 10 seconds
						$newExpire += 10;
					}else if($type==1){ //Type 1: Expires after 1 minute
						$newExpire += 60;
					}
				
					$statement2 = $pdo->prepare("UPDATE `spamProtection` SET `expire` = ?,`count` = ? WHERE `ip` = ? AND `type` = ?");
					$statement2->execute(array($newExpire,$row['count']+1,$ip,$type));
					unset($statement2);
				}
			}
		}
		
		if(!$knownIp){
			$newExpire = time();
			if($type==0){ //Type 0: Expires after 10 seconds
				$newExpire += 10;
			}else if($type==1){ //Type 1: Expires after 1 minute
				$newExpire += 60;
			}
			
			$statement = $pdo->prepare("INSERT INTO `spamProtection` (`ip`, `count`, `expire`, `type`) VALUES (?, ?, ?, ?);");
			$statement->execute(array($ip,1,$newExpire,$type));
		}
	}
?>