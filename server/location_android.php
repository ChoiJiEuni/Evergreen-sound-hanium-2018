<?php
    
    //$ip_path=isset($_POST['ip_path']) ? $_POST['ip_path'] : '';

    $result2 = json_decode(file_get_contents('http://ip-api.com/json/119.197.127.248'));
    print $result2->lat . "<br>";
    print $result2->lon . "<br>";
    $result = array();  
       
   
      array_push($result,  
        array('id' =>$result2->lat, 'name'=>$result2->lon
        ));  
    
       
    echo json_encode(array("result"=>$result));  
       
    mysqli_close($conn);  
    
?>

