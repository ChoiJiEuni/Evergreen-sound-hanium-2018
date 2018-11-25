<?php

 // 블러, 얼굴이 사진 영역 안에 있는지를 기준으로 판단하는 함수
 function get_normal_point($blur, $faceOutRate) {

     $normals = array(
       //blur high, medium, low
       array(1, 1, 1),  // $faceOutRate high
       array(1, 1, 2),
       array(1, 2, 2)   // $faceOutRate low
     );

    if ($faceOutRate <  0.2){
      $faceOut = 2;
    } else if ($faceOutRate <  0.6) {
      $faceOut = 1;
    } else {
      $faceOut = 0;
    }

    return $normals[$blur][$faceOut];
 }

 function get_plus_point($happiness) {
    if ($happiness > 0.9) {
      return 1;
    } else if ($happiness > 0.7) {
      return 1;
    } else if ($happiness > 0.3) {
      return 0;
    } else {
      return 0;
    }
 }
 
// MYSQL 접근
$user_name = $_POST['userName'];
$user_pass = $_POST['userPass'];
$db_name = $_POST['databaseName'];
$conn = mysqli_connect("127.0.0.1", $_POST['userName'], $_POST['userPass'], $_POST['databaseName']);

mysqli_set_charset($conn,"utf8");

$img_path = $_POST['imgPath']

//$brightness=$_POST['brightness']; // 실제 너무 밝거나 어두우면 인식 불가

// 결과값 단계
const BAD_CONDITION = 1;
const NORMAL_CONDITION = 2;
const GOOD_CONDITION = 3;

$query = "SELECT * FROM good_photo_conditions_tb WHERE img_path='" . img_path . "'";
$res = mysqli_query($conn, $query);
$result = array();

$blur = 0;
$faceIn = 0;
$happiness = 0; // plus point

// 사진 정보 가져옴
if($res){
    $row_count = mysqli_num_rows($res);

    if(0 != $row_count){
        $personKnow = $row_count;

        while($row=mysqli_fetch_array($resultPerson)) {
              $blur = $row["blur"];
              $faceIn = $row["faceIn"];
              $happiness = $row["happiness"];
        }
    }
}

// 사람 수 가져옴
$query2 = "SELECT num_of_people FROM picture_info_tb WHERE img_path='" . $img_path . "'";
$res = mysqli_query($conn, $query2);

$count = 0

if($res){
    $row_count = mysqli_num_rows($res);

    if(0 != $row_count){
        $personKnow = $row_count;

        while($row=mysqli_fetch_array($resultPerson)) {
              $count = $row["num_of_people"];
        }
    }
}

$faceOutRate = $faceIn / $count
$pictureCondition = get_normal_point($blur, $faceOutRate) +  function get_plus_point($happiness);

$result = array();

$query3 = "UPDATE picture_info_tb SET good_pic='" . $pictureCondition . "' WHERE img_path='" . $img_path . "'";

if ($pictureCondition == BAD_CONDITION) {
  array_push($result,
    array('message' => "촬영 안내 음성에 따라 카메라 정면을 응시하고, 웃어보세요"
    ));
} else if ($pictureCondition == NORMAL_CONDITION) {
  array_push($result,
    array('message' => "사진을 촬영할 때, 활짝 웃어보세요"
    ));
} else if ($pictureCondition == GOOD_CONDITION) {
  array_push($result,
    array('message' => "멋진 사진이네요"
    ));
}

array_push($result, $pictureCondition);
echo json_encode(array("evergreen"=>$result));
mysqli_close($conn);


  ?>
