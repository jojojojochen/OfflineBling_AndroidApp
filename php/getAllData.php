<?php
	$config = require("../../config_offline.php");

	$servername = $config["servername"];
	$serverusername = $config["username"];
	$serverpassword = $config["password"];
	$database = $config["database"];

	$conn = new mysqli($servername, $serverusername, $serverpassword, $database);

	$query = "SELECT * FROM Questions";
	$queryResult = $conn->query($query);

	$array = array();
	$count = 0;

	if($queryResult->num_rows > 0)
	{
		while($row = $queryResult->fetch_assoc())
		{
			$array[$count]["question"] = str_replace(" i ", " I ", ucfirst($row["question"]));
			$array[$count]["answer"] = str_replace(" i ", " I ", ucfirst($row["answer"]));
			$array[$count++]["count"] = $row["count"];
		}	
	}

    $jsonObject = json_encode(array("questions" => $array));
    echo $jsonObject;

?>