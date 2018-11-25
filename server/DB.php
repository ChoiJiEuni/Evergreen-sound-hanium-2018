<?php

	$user_name = $_POST['userName'];
	$user_pass = $_POST['userPass'];
	$db_name = $_POST['databaseName'];
    $conn = mysqli_connect("127.0.0.1", "root", "1111");
      if(mysqli_connect_errno()){
   	echo "mysql 연결 오류".mysqli_connect_errno();
   }

//USER 만들기
    $sql="CREATE USER '".$user_name."'@'localhost' identified by '".$user_pass."'";
	mysqli_query($conn, $sql);

//DB 만들기
   $sql="CREATE DATABASE ".$db_name." CHARACTER SET utf8 COLLATE utf8_general_ci";
	mysqli_query($conn, $sql);

 	
//USER 가 DB에 대한 권한 가지게 하기.
	$sql="grant all privileges on ".$db_name.".*to ".$user_name."@localhost";
	mysqli_query($conn, $sql);

	$sql="flush privileges";
	mysqli_query($conn, $sql);

	mysqli_close($conn);
?>