<?php
$result = json_decode(file_get_contents('http://ip-api.com/json/119.197.127.248'));
//var_dump($result);
	print $result->lat . "<br>";
    print $result->lon . "<br>";
 /*$json = file_get_contents("https://www.geoip-db.com/json");
    $data = json_decode($json);

    print $data->country_code . "<br>";
    print $data->country_name . "<br>";
    print $data->state . "<br>";
    print $data->city . "<br>";
    print $data->postal . "<br>";
    print $data->latitude . "<br>";
    print $data->longitude . "<br>";
    print $data->IPv4 . "<br>";*/
?>