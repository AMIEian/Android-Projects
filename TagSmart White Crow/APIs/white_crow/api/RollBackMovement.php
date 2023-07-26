<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");



try
{
	
	
	$responses = array();

	/*json_epc_data=[{"barcode":"8905011000150","category":"OTHER DUPATTA","epc":"30361F84CC0003C000000001","isPresent":"1","stock_id":"112"},{"barcode":"8905011000143","category":"OTHER DUPATTA","epc":"30361F84CC00038000000002","isPresent":"1","stock_id":"111"}]&stray_json_epc_data=["30361F84CC0003C000000001","30361F84D82AD94000000022","30361FAF54275D40000FAABA","30361F84CC00038000000002","303556022843A3C00000000D"]&location_id=3&sub_location_id=4&stock_date=2020-11-05&stock_time=08:09:32&stock_table_name=test_fbb_stock_data&customer_id=3*/
	
	$session_id = $_POST['session_id']; 
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];
    $user_id = $_POST['user_id']; 
    $stock_datetime = $_POST['movement_date']." ".$_POST['movement_time'];
     

    $end_dt = strtotime($stock_datetime);
    $st_dt = $end_dt - 1800; //get half an hr back datetime

    $st_stock_datetime = date('Y-m-d H:i:s',$st_dt);
      

	$result= pg_query($db," Delete from epc_location_date_time where user_id='$user_id' AND task_id='3'  AND customer_id='$customer_id' AND stock_time BETWEEN '$st_stock_datetime' AND '$stock_datetime' " );


	if(!$result)
	{
		$response = array('status' => '0','msg' => 'Something went wrong during deleting epc records.');
		echo json_encode($response);	
		exit;											
	}
	else
	{
		$result2= pg_query($db,"UPDATE ui_update  SET rfid_quantity= 0 where user_id='$user_id' AND session_id='$session_id'  AND customer_id='$customer_id' " );

		if(!$result2)
		{
			$response = array('status' => '0','msg' => 'Something went wrong during rfid qty UI update.');
			echo json_encode($response);	
			exit;											
		}
	}



	$response = array('status' => '1','msg' => "Stock take aborted sucessfully");
	echo json_encode($response);	
	exit;
}
catch(Exception $e) {

	$response = array('status' => '0','msg' => $e);
	echo json_encode($response);	
	exit;
}	

		

	
?>