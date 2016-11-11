<?php
 
$con=mysqli_connect("pipigift.ddns.net","testuser","123456","fluegelrad");
 
if (mysqli_connect_errno()) {
  echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
 
$sql = "SELECT * FROM events";
 
if ($result = mysqli_query($con, $sql)) {
	$resultArray = array();
	$tempArray = array();
 
	while($row = $result->fetch_object()) {
		$tempArray = $row;
	    array_push($resultArray, $tempArray);
	}
 
	echo json_encode($resultArray);
}
 
mysqli_close($con);
?>