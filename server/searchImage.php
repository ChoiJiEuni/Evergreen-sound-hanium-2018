<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$link=mysqli_connect("127.0.0.1","B_tester","1111", "B_db" );
if (!$link)
{
    echo "MySQL 접속 에러 : ";
    echo mysqli_connect_error();
    exit();
}

mysqli_set_charset($link,"utf8");


//POST 값을 읽어온다.
$date=isset($_POST['img_date']) ? $_POST['img_date'] : '';
$location=isset($_POST['img_location']) ? $_POST['img_location'] : '';
$person=isset($_POST['img_person']) ? $_POST['img_person'] : '';
$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

$query = ""; // 쿼리를 저장할 String 변수
$personResultBool = true; // 인물검색 결과가 없으면 쿼리를 더 수행하지 못하도록 하는 변수

/*
* 인물검색을 하는 경우 -> 해당 인물이 있는 경로 중 날짜, 시간으로 재검색
* 인물검색을 하지 않는 경우 -> 날짜, 시간으로 검색
*/
if ($person != ""){ // 인물검색 O

    /*사용자가 입력한 이름을 포함하는 모든 인물 검색*/
    $temp = $person . '%';
    $queryPath = "SELECT * FROM recognition_tb WHERE name LIKE '%$temp'";
    $resultPath=mysqli_query($link, $queryPath);

    if($resultPath){
        $row_count = mysqli_num_rows($resultPath);

        if($row_count == 0){ // 인물검색 결과가 존재하지 않을 경우
            echo "입력하신 조건에 해당하는 사진이 없습니다.";
            $personResultBool = false; // 쿼리를 더 수행하지 않고 종료
        } else { // 인물이 들어간 사진들이 있을 경우
            $path = "";
            $pathArray = array();
            $first = true;

            while($row=mysqli_fetch_array($resultPath)){
                if ($first){
                  $path = "'" . $row["img_path"] . "'";
                  $first = false;
                } else {
                  $path = $path . ", ";
                  $path = $path . "'" . $row["img_path"] . "'";
                }
            } // 인물을 포함한 사진 경로들을 <'경로1', '경로2', '경로3'> 의 형태로 규격화(String)

            $query = $query . "SELECT * FROM picture_info_tb WHERE img_path IN ($path)";
        }
    }

} else { // 인물검색 X
    $query = $query . "SELECT * FROM picture_info_tb";
}

if($personResultBool){
    /*날짜, 시간검색 수행*/
    if ($date != ""){
        $query = "(" . $query . ")ABC";
        $query = "SELECT * FROM " . $query . " WHERE create_date LIKE '$date'";
    } else {
        $query = "(" . $query . ")ABC";
        $query = "SELECT * FROM " . $query;
    }

    if($location != ""){
        $temp = "%" . $location . "%";
        $query = "(" . $query . ")DEF";
        $query = "SELECT img_path FROM " . $query . " WHERE location LIKE '$temp'";
    } else {
      $query = "(" . $query . ")DEF";
      $query = "SELECT img_path FROM " . $query;
    }

    if ($date != ""){
        $query = $query . " ORDER BY happiness DESC"; // 날짜로 검색 시 행복도 순으로만
    } else {
        $query = $query . " ORDER BY create_date DESC, happiness DESC"; // 날짜로 검색하지 않은 경우 날짜순, 행복도순
    }



    /*완성된 쿼리 수행*/
    if ($query !="" ){
        echo $query;

        $result=mysqli_query($link, $query);
        $dataResult = array(); // 결과값을 저장할 배열
        $data = array(); // 결과경로를 저장할 배열

        if($result){

            $row_count = mysqli_num_rows($result);

            if ( 0 == $row_count){ // 검색 결과가 존재하지 않는 경우

                /*결과값을 받아옴*/
                array_push($dataResult, array('result'=>$row_count));

                if (!$android) { //php 웹 출력

                    echo "입력하신 조건에 해당하는 사진이 없습니다.";

                } else { //안드로이드 출력

                    header('Content-Type: application/json; charset=utf8');
                    $json = json_encode(array("evergreen"=>$dataResult), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                    echo $json;
                }

            } else { // 검색 결과에 해당하는 <사진 경로>를 정상적으로 받아 온 경우

                /*결과값을 받아옴*/
                while($row=mysqli_fetch_array($result)){
                    array_push($data, $row["img_path"]);
                }

                array_push($dataResult, array('result'=>$row_count));
                array_push($dataResult, $data);

                if (!$android) { //php 웹 출력

                    echo "<pre>";
                    print_r($dataResult);
                    echo '</pre>';

                } else { //안드로이드 출력

                    header('Content-Type: application/json; charset=utf8');
                    $json = json_encode(array("evergreen"=>$dataResult), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
                    echo $json;
                }
            }

            mysqli_free_result($result);

        } else{
            echo "SQL문 처리중 에러 발생 : ";
            echo mysqli_error($link);
        }
    } else {
        echo "사진을 검색할 정보를 입력하세요.";
    }
}

mysqli_close($link);

?>



<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){ // html 구성부
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         날짜: <input type = "text" name = "img_date" />
         위치: <input type = "text" name = "img_location" />
         인물: <input type = "text" name = "img_person" />
         <input type = "submit" />
      </form>

   </body>
</html>

<?php
}
 ?>
