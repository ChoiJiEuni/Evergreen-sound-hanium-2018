<?php
	$user_name = $_POST['userName'];
	$user_pass = $_POST['userPass'];
	$db_name = $_POST['databaseName'];
	$conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);

    //$conn = mysqli_connect("127.0.0.1", "B_tester", "1111", "B_db");
	//php랑 mysql랑 인코딩 방법이 달라서, 바꿔줘야지 한글 나옴.
	mysqli_query($conn,"set session character_set_connection=utf8");
	mysqli_query($conn,"set session character_set_results=utf8");
	mysqli_query($conn,"set session character_set_client=utf8");


	$img_path = $_POST['img_path'];
	$ficial_proportion = $_POST['ficial_proportion'];
	$face = $_POST['face'];

//	$img_path = "/storage/emulated/0/evergreen/evergreen_1580536368338058941.jpg";
//	$face = "1번째 인물이 왼쪽화면에서 벗어남.";

    $insert_query = "INSERT INTO out_of_range_tb(img_path, face) VALUES('".$img_path."','".$face."')";
    mysqli_query($conn, $insert_query);
    
    mysqli_close($conn);



?>