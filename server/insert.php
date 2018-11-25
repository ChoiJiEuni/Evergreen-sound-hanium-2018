
<?php
   $conn = mysqli_connect("127.0.0.1", "root", "1111", "test");
    #print_r($conn);
    $insert_query = "INSERT INTO person(name, country) VALUES('".$_POST['name']."','".$_POST['country']."')";
    mysqli_query($conn, $insert_query);
    
    mysqli_close($conn);
?>