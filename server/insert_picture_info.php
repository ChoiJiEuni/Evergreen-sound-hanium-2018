<?php
    $user_name = $_POST['userName'];
	$user_pass = $_POST['userPass'];
	$db_name = $_POST['databaseName'];
	$conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);

   // $conn = mysqli_connect("127.0.0.1", "B_tester", "1111", "B_db");

	//php랑 mysql랑 인코딩 방법이 달라서, 바꿔줘야지 한글 나옴.
	mysqli_query($conn,"set session character_set_connection=utf8");
	mysqli_query($conn,"set session character_set_results=utf8");
	mysqli_query($conn,"set session character_set_client=utf8");

	$img_path = $_POST['img_path'];
	$location = $_POST['location'];
	$create_date = $_POST['create_date'];
	$num_of_people = $_POST['num_of_people'];
	$record_path = $_POST['record_path'];
	
	//picture_info_tb 값 삽입.
    $insert_query = "INSERT INTO picture_info_tb(img_path, location, create_date, num_of_people,record_path) VALUES('".$img_path."','".$location."','".$create_date."','".$num_of_people."','".$record_path."')";
    mysqli_query($conn, $insert_query);

    mysqli_close($conn);
?>