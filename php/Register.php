<?php 
    $con = mysqli_connect("localhost", "id", "password", "db");
    mysqli_query($con,'SET NAMES utf8');

    $userID = $_POST["userID"];
    $userPassword = $_POST["userPassword"];
    $userName = $_POST["userName"];
    $userProfile = $_POST["userProfile"];
    $userMbti = $_POST["userMbti"];

    $statement = mysqli_prepare($con, "INSERT INTO USER VALUES (?,?,?,?,?)");
    mysqli_stmt_bind_param($statement, "sssss", $userID, $userPassword, $userName, $userProfile, $userMbti);
    mysqli_stmt_execute($statement);


    $response = array();
    $response["success"] = true;
 
   
    echo json_encode($response);
?>