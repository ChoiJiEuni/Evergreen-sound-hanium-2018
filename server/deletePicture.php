<?php
	$user_name = $_POST['userName'];
	$user_pass = $_POST['userPass'];
	$db_name = $_POST['databaseName'];
	$conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);

	//php-mysql 한글 인코딩
	mysqli_query($conn,"set session character_set_connection=utf8");
	mysqli_query($conn,"set session character_set_results=utf8");
	mysqli_query($conn,"set session character_set_client=utf8");

	$img_path = $_POST['img_path'];

	//모든 테이블에서 해당 사진 정보 삭제
    $delete_query1 = "DELETE FROM picture_info_tb WHERE img_path='".$img_path."'";
		$delete_query2 = "DELETE FROM ficial_proportion WHERE img_path='".$img_path."'";
		$delete_query3 = "DELETE FROM good_photo_conditions_tb WHERE img_path='".$img_path."'";
		$delete_query4 = "DELETE FROM recognition_tb WHERE img_path='".$img_path."'";

    mysqli_query($conn, $delete_query1);
		mysqli_query($conn, $delete_query2);
		mysqli_query($conn, $delete_query3);
		mysqli_query($conn, $delete_query4);

    mysqli_close($conn);
?>
