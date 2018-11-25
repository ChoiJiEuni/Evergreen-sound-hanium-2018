<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
$user_name = $_POST['userName'];
$user_pass = $_POST['userPass'];
$db_name = $_POST['databaseName'];
//
$link = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);
if (!$link)
{
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();
}

mysqli_set_charset($link,"utf8");


//POST 값을 읽어온다.
$path=isset($_POST['img_path']) ? $_POST['img_path'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

/*path에 값이 들어간 경우 쿼리 및 작업 수행*/
if ($path !="" ){
    /*먼저, 사진에 인식된 인물들이 있는지 확인한다.*/
    $queryPerson="SELECT * FROM recognition_tb WHERE img_path='$path'"; // (사진에 인식된)인물정보 쿼리
    $resultPerson=mysqli_query($link, $queryPerson);

    $personKnow = 0; // 인식된 인물 수를 저장하는 문자열
    $dataPerson = ""; // 인식된 인물 이름을 저장하는 문자열

    if($resultPerson){
        $row_count = mysqli_num_rows($resultPerson);

        // 사진에 등록된 인물이 인식되면 결과값 반환
        if(0 != $row_count){
            $personKnow = $row_count;

            while($row=mysqli_fetch_array($resultPerson)){
                  $dataPerson = $dataPerson . $row["name"] . " ";
                  // 인물 이름의 경우: "권소영 임소희 최지은" 식으로 띄어쓰기를 구분기준으로 <하나의 문자열>로 받아옴
                  // 아래에서 사진 정보를 가져온 후 하나의 json 배열로 결과값을 내보내기 위함
            }
        }

        mysqli_free_result($resultPerson);

    } else {
        echo "SQL문 처리중 에러 발생 : ";
        echo mysqli_error($link);
    }

    /*사진 정보를 가져오고, 위에서 수행한 <인물 인식 정보를 포함>한 json 배열을 결과값으로 내보냄*/
    $queryInfo="SELECT * FROM picture_info_tb WHERE img_path='$path'"; // 사진정보 쿼리
    $result=mysqli_query($link, $queryInfo);

    $data = array(); // 결과값을 저장할 배열

    if($result){

        $row_count = mysqli_num_rows($result);

        if ( 0 == $row_count ){ // 해당 경로명의 사진에 해당하는 정보가 없는 경우

            array_push($data,
              array('location'=>'N',
              'date'=>'N',
              'happiness'=>'-1',
              'personCount'=>'-1',
              'personKnowCount'=>'-1',
              'personName'=>'N',
              'record'=>'N'
            ));

            if (!$android) { //php 웹 출력

                echo "'";
                echo $path;
                echo "'은 찾을 수 없습니다.";

            }else { //안드로이드 출력

                header('Content-Type: application/json; charset=utf8');
                $json = json_encode(array("evergreen"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                echo $json;

            }

        } else { // <사진 정보> 데이터를 정상적으로 받아 온 경우

            /*결과값을 (위치, 날짜, 행복도, 인물수(전체), 인물수(인식된), 인물이름(String)) json 배열로 내보내기*/
            while($row=mysqli_fetch_array($result)){
                if($row["record_path"] == null){
                    $record = "";
                } else {
                    $record = $row["record_path"];
                }
                array_push($data,
                  array('location'=>$row["location"],
                  'date'=>$row["create_date"],
                  //'happiness'=>$row["happiness"],
                  'personCount'=>$row["num_of_people"],
                  'personKnowCount'=>$personKnow,
                  'personName'=>$dataPerson,
                  'record'=>$record
                ));
            }

            if (!$android) { //php 웹 출력

                echo "<pre>";
                print_r($data);
                echo '</pre>';

            } else { //안드로이드 출력

                header('Content-Type: application/json; charset=utf8');
                $json = json_encode(array("evergreen"=>$data), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                echo $json;
            }
        }

        mysqli_free_result($result);

    }
    else{
        echo "SQL문 처리중 에러 발생 : ";
        echo mysqli_error($link);
    }
}
else {
    echo "조회할 사진의 경로명을 입력하세요.";
}

mysqli_close($link);

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         사진 경로: <input type = "text" name = "img_path" />
         <input type = "submit" />
      </form>

   </body>
</html>

<?php
}
 ?>
