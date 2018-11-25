<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$user_name = $_POST['userName'];
$user_pass = $_POST['userPass'];
$db_name = $_POST['databaseName'];
$link = mysqli_connect("127.0.0.1", $_POST['userName'], $_POST['userPass'], $_POST['databaseName']);

//$link = mysqli_connect("127.0.0.1", "root", "1111", "a181112259011372_db");

if (!$link)
{
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();
}

mysqli_set_charset($link,"utf8");

$imgPath=isset($_POST['img_path']) ? $_POST['img_path'] : '';

if($imgPath != ''){
    // 사진 정보 쿼리
    $queryData = "SELECT num_of_people, face, happiness, Blur
                  FROM picture_info_tb PIC, good_photo_conditions_tb GOOD
                  WHERE PIC.img_path LIKE '" . $imgPath . "' AND GOOD.img_path LIKE '" . $imgPath . "'";
    $resultData = mysqli_query($link, $queryData);

    if ($resultData) {
        $row_count = mysqli_num_rows($resultData);

        if($row_count != 0) {
            while($row=mysqli_fetch_array($resultData)) {
                $face = $row["face"];
                $happiness = $row["happiness"];
                $blur = $row["Blur"];
                $count = $row["num_of_people"];
            }

            $faceOutRate = $face/$count;

          // 사진이 흔들렸는지
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

            // 결과 메시지 설정
            $result = "초기 메시지";
            if ($normals[$blur][$faceOut] == 1) {
                $result = "사진이 흔들렸거나 얼굴이 화면 밖에 있네요! 촬영 시 알림음에 따라 카메라 방향을 응시하시면 더 좋은 사진을 찍을 수 있어요.";
            } else {
                if ($happiness > 0.7) {
                    $result = "멋진 사진이네요!";
                } else {
                    $result = "조금 더 활짝 웃어보세요!";
                }
            }
            // echo $result;

            echo json_encode(array("evergreen"=>$result), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
            mysqli_close($link);
        } else {
          echo "존재하지 않는 사진입니다";
        }
    }
}

?>





<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){ // html 테스트용 코드
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         사진경로: <input type = "text" name = "img_path" />
         <input type = "submit" />
      </form>

   </body>
</html>

<?php
}
 ?>
