
<?php
   $conn = mysqli_connect("127.0.0.1", "root", "1111", "test");
    #print_r($conn);

    $delete_query = "DELETE FROM person WHERE id = ".$_POST['id'];
    
  
    mysqli_query($conn, $delete_query);
    
    mysqli_close($conn);

?>