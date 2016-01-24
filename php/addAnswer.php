<?php
	$config = require("../../config_offline.php");

	$servername = $config["servername"];
	$serverusername = $config["username"];
	$serverpassword = $config["password"];
	$database = $config["database"];

	$conn = new mysqli($servername, $serverusername, $serverpassword, $database);

	$question = strtolower(mysqli_real_escape_string($conn, $_GET["question"]));
	if(substr($question, -1) != "?") 
    {
        $question .= "?";
    }
	$answer = strtolower(mysqli_real_escape_string($conn, $_GET["answer"]));

	error_log($answer . '---' . $question);

	$query = "UPDATE Questions SET answer='$answer' WHERE question='$question'";
	if($conn->query($query) !== TRUE)
	{
		error_log("couldn't update answer");
	}

?>