<?php
    
    $conn = mysqli_connect("127.0.0.1", "root", "1111", "test");
 
    mysqli_set_charset($conn,"utf8");
 
    $res = mysqli_query($conn,"select * from person");  
 
    $result = array();  
       
    while($row = mysqli_fetch_array($res)){  
      array_push($result,  
        array('id' =>$row[0], 'name'=>$row[1],'country'=>$row[2] 
        ));  
    }  
       
    
    echo json_encode(array("result"=>$result));  
       
    mysqli_close($conn);  
    
?>

