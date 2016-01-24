<?php
	require('./twilio-php-master/Services/Twilio.php'); 
	$config = require('../../config_offline.php'); 

    header("content-type: text/xml");
    echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    //twilio
    $twilioAPI = $config["twilioAPI"];
    $accountSID = $config["accountSID"];
    $authToken = $config["authToken"];

    //wolfram api
    $wolframID = $config["wolframID"];

    //mysql
    $servername = $config["servername"];
    $serverusername = $config["username"];
    $serverpassword = $config["password"];
    $database = $config["database"];

    function test_input($data) {
      $data = trim($data);
      $data = stripslashes($data);
      $data = htmlspecialchars($data);
      return $data;
    }

    function clean_string($string) {
      $bad = array("SELECT","DELETE", "DROP", "FROM");
      return str_replace($bad,"",$string);
    }

    $message = clean_string(test_input($_POST["Body"]));

    //initialize vars
    $term = "";
    $plainText = "";
	$searchSuccess = true; 
	$tooLong = false;

    //search type
    $messageSplit = explode(" ", $message);
    $searchType = strtolower($messageSplit[0]);
    if($searchType == "wiki")
    {
        //search user term
        $xml=simplexml_load_file("https://en.wikipedia.org/w/api.php?action=query&format=xml&list=search&srsearch=" . substr(strstr($message, ' '), 1));
        $search=$xml->query->search;
        if(count($search->children()) == 0)
        {
            $suggestion = $xml->query->searchinfo;
            if(isset($suggestion["suggestion"]))
            {
                $xml=simplexml_load_file("https://en.wikipedia.org/w/api.php?action=query&format=xml&list=search&srsearch=" . $suggestion["suggestion"]);
                $search=$xml->query->search;
                if(count($search->children()) == 0)
                {
                    $plainText = "Sorry, your search term returned no results. Please try another term.";
                    $searchSuccess = false;
                }
                else
                {
                    $term = $search->p[0]["title"];
                }
            }
            else
            {
                $plainText = "Sorry, your search term returned no results. Please try another term.";
                $searchSuccess = false;
            }
        }
        else
        {
            $term = $search->p[0]["title"];
        }


        //parse 
        if($searchSuccess)
        {
            $xml = simplexml_load_file("https://en.wikipedia.org/w/api.php?format=xml&action=query&prop=extracts&exintro=&explaintext=&titles=" . $term);
            $plainText = $xml->query->pages->page->extract;
            if(strlen($plainText) >= 800)
            {
                $plainText = substr($plainText, 0, 797) . "...";
            }
        }
    }
    else if($searchType == "wolfram")
    { 
        $xml = simplexml_load_file("http://api.wolframalpha.com/v2/query?input=" . substr(strstr($message, ' '), 1) . "&appid=" . $wolframID);
        foreach($xml->pod as $pod)
        {
            if($pod->subpod->plaintext != "")
            {
                $plainText .= htmlspecialchars($pod["title"] . ": " . $pod->subpod->plaintext . "\n");
            }
            else
            {
                $plainText .= htmlspecialchars($pod["title"]) . ": N/A\n"; 
            }
        }
        if(strlen($plainText) >= 800)
        {
            $plainText = substr($plainText, 0, 797) . "...";
        }
    } 
    else if($searchType == "question")
    {
        // Create connection
        $conn = new mysqli($servername, $serverusername, $serverpassword, $database);
        $userQuestion = strtolower(mysqli_real_escape_string($conn, substr(strstr($message, ' '), 1)));
        if(substr($userQuestion, -1) != "?") 
        {
            $userQuestion .= "?";
        }
        $query = "SELECT answer, count FROM Questions WHERE question='$userQuestion'";
        $answerQuery = $conn->query($query);

        if($answerQuery->num_rows > 0) 
        {
            while($row = $answerQuery->fetch_assoc()) 
            {
                $plainText = $row["answer"];
                if($plainText == "")
                {
                    $plainText == "Sorry, there is currently no submitted answer for this question. Please try again later.";
                }
                $currCount = $row["count"];
                $currCount++;
                $countQuery = "UPDATE Questions SET count='$currCount' WHERE question='$userQuestion'";
                if ($conn->query($countQuery) !== TRUE) {
                    error_log("couldn't update the count using countQuery");
                }
                break;
            }
        }
        else
        {
            $plainText = "Well, that's a question we haven't heard yet. We'll submit it to the community to be answered.";
            $newQuery = "INSERT INTO Questions (question, answer, count)
    VALUES ('$userQuestion', '', '1')";
            if($conn->query($newQuery) !== TRUE)
            {
                error_log("couldn't add new question using newQuery" . $conn->error);

            }
        }
        
    }

?>
<Response>
	<?php
        echo "<Message>" . $plainText . "</Message>"; 
	?>
</Response>