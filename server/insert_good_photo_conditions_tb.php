<?php 
    $user_name = $_POST['userName'];
	$user_pass = $_POST['userPass'];
	$db_name = $_POST['databaseName'];
	$conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);

	//php랑 mysql랑 인코딩 방법이 달라서, 바꿔줘야지 한글 나옴.
	mysqli_query($conn,"set session character_set_connection=utf8");
	mysqli_query($conn,"set session character_set_results=utf8");
	mysqli_query($conn,"set session character_set_client=utf8");

	$img_path = $_POST['img_path'];
	$face = $_POST['face'];
	$brightness = $_POST['brightness'];
	$happiness = $_POST['happiness'];
	$Blur = $_POST['Blur'];
	
	//picture_info_tb 값 삽입.
    $insert_query = "INSERT INTO good_photo_conditions_tb(img_path, face, brightness, happiness, Blur) VALUES('".$img_path."','".$face."','".$brightness."','".$happiness."','".$Blur."')";
    mysqli_query($conn, $insert_query);

    mysqli_close($conn);

?>