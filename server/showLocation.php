<?php
    
    $user_name = $_POST['userName'];
    $user_pass = $_POST['userPass'];
    $db_name = $_POST['databaseName'];
    $conn = mysqli_connect("127.0.0.1", $user_name, $user_pass, $db_name);
 
    mysqli_set_charset($conn,"utf8");
    $img_path=isset($_POST['img_path']) ? $_POST['img_path'] : '';
    
    $query = "SELECT location FROM picture_info_tb WHERE img_path = '$img_path'";


    $res = mysqli_query($conn, $query);
    $result = array();  
       
    while($row = mysqli_fetch_array($res)){  
      array_push($result,  
        array('location' =>$row[0]
        ));  
    }  
       
    echo json_encode(array("evergreen"=>$result)); 
     
    mysqli_close($conn);  
    
?>

