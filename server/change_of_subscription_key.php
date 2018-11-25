<?php


    $result = array();



        $serviceHost = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0";
        $subscriptionKey = "b8b9ac4e982b42d4bc8735452893fdbc";
    array_push($result,
        array('item' => $serviceHost
        ));

     array_push($result,
        array('item' => $subscriptionKey
        ));

    echo json_encode(array("subscriptionKey_change"=>$result));



?>
