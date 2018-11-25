<?php
    
    $user_name = $_POST['userName'];
    $user_pass = $_POST['userPass'];
    $db_name = $_POST['databaseName'];
    $conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);
 
    mysqli_set_charset($conn,"utf8");
    $img_date=isset($_POST['img_date']) ? $_POST['img_date'] : '';
    $img_location=isset($_POST['img_location']) ? $_POST['img_location'] : '';
    $img_person=isset($_POST['img_person']) ? $_POST['img_person'] : '';

    
    //날짜 검색
    if ($img_date != ""){
        $query = "SELECT img_path FROM picture_info_tb WHERE create_date LIKE '". "%"."$img_date". "%"."'";
        $query = $query . " ORDER BY create_date DESC"; 
    } 
    if($img_location != ""){
            $temp =   "%".$img_location . "%";
            $query = "SELECT img_path FROM picture_info_tb WHERE location LIKE '$temp'";
            $query = $query . " ORDER BY create_date DESC"; 
    }
    if ($img_person != ""){ 
            //어짜피 인물이름가지고 경로명만 구하면되는데 굳이 서브 쿼리 써야 하나 싶어서 없앰.
           /* $temp =  "%".$img_person . "%";
            $query = "SELECT img_path FROM recognition_tb WHERE name LIKE '$temp'";   */
            $temp =  "%".$img_person . "%";
            $query = "SELECT img_path FROM picture_info_tb WHERE img_path in (select img_path from recognition_tb where name like '".$temp."')"; 
            $query = $query . " ORDER BY create_date DESC"; 

    }
        
    

    $res = mysqli_query($conn, $query);
    $result = array();  
       
    while($row = mysqli_fetch_array($res)){  
      array_push($result,  
        array('path' =>$row[0]
        ));  
    }  
       
    echo json_encode(array("evergreen"=>$result));  
    mysqli_close($conn);  
    
?>

