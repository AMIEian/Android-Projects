<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start();
	header('Cache-control: private');
	include("connect.php");

try
{

	$missingEPCResp = array();
	$mainResp = array();
	$summaryArray = array();

  	$stock_date = $_POST['stock_date'];
    $stock_time = $_POST['stock_time'];
    $customer_id = $_POST['customer_id'];

    $user_id = $_POST['user_id'];
    $store_id = $_POST['store_id'];
		$error_logs = $_POST['error_logs'];
		$date_time = $_POST['stock_date']." ".$_POST['stock_time'];

    //Get category table data
	$result_product_data = pg_query($db,"INSERT INTO  app_logs (app_logs_id, customer_id, store_id, user_id,logs_details, date_timestamp) VALUES(DEFAULT,'$customer_id','$store_id','$user_id','$error_logs','$date_time' )"  );


	$response = array('status' => '1','msg' => "success");
	echo json_encode($response);
	exit;

}
catch(Exception $e) {

  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);
	exit;
}




?>
