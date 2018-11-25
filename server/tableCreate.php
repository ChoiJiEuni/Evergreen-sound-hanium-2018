<?php
	$user_name = $_POST['userName'];
	$user_pass = $_POST['userPass'];
	$db_name = $_POST['databaseName'];
	$conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);

	//테이블 생성1
	$sql = "CREATE TABLE picture_info_tb(
	img_path varchar(200) not null,
	location varchar(50),
	create_date BIGINT(20),
	num_of_people int,
	record_path varchar(200),
	good_pic int default '0',
	primary key(img_path)
	)";
 	mysqli_query($conn, $sql);

	//테이블 생성2
 	$sql = "CREATE TABLE registered_person_tb(
 	name varchar(20) not null,
 	person_img_path varchar(200),
 	primary key(name)
	)";
 	mysqli_query($conn, $sql);

    //테이블 생성3
	$sql = "CREATE TABLE recognition_tb(
 	img_path varchar(200),
	name varchar(20),
	foreign key (img_path) references picture_info_tb(img_path) on update cascade on delete cascade,
	foreign key (name) references registered_person_tb(name) on update cascade on delete cascade,
	primary key(img_path,name)
	)";
 	mysqli_query($conn, $sql);

 	//테이블 생성4 (얼굴 영역 비율)
	$sql = "CREATE TABLE ficial_proportion_tb(
 	img_path varchar(200),
	ficial_proportion float(6,3),
	foreign key (img_path) references picture_info_tb(img_path) on update cascade on delete cascade,
	primary key(img_path)
	)";
 	mysqli_query($conn, $sql);

	//테이블 생성5 (사진 잘 나온 정도 저장하는)
	$sql = "CREATE TABLE good_photo_conditions_tb(
 	img_path varchar(200),
	face int,  /*벗어난 정도*/
	brightness float(7,3),
	happiness float(6,3),
	Blur int,  /*선명도*/
	foreign key (img_path) references picture_info_tb(img_path) on update cascade on delete cascade,
	primary key(img_path)
	)";
 	mysqli_query($conn, $sql);

 	mysqli_close($conn);
?>