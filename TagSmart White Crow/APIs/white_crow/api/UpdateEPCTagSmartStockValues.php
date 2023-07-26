<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	/*$barcode = "8903035095558";
	$EPC = "30369999999999";
	$customer_id = "1";
	$store_id = "1";
	$customer_stock_data_table_name = "manyavar_stock_data";
	$customer_product_data_table_name = "manyavar_product_data";*/

	
	$wrong_EPC = $_POST['wrong_EPC'];
	$new_EPC = $_POST['new_EPC']; 

	$responses = array();

	$result6= pg_query($db,"SELECT  *  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$wrong_EPC')  LIMIT 1 " );
	if(pg_num_rows($result6) > 0)
	{
		$result= pg_query($db,"UPDATE tagsmart_stock_details SET epc = '$new_EPC' WHERE LOWER(epc) = LOWER('$wrong_EPC')" );

		if($result)
		{
			$response = array('status' => '1','msg' => 'EPC updated successfully');		

			echo json_encode($response);($responses);
			exit;
		}
		else
		{
			$response = array('status' => '0','msg' => 'Something went wrong. Try Again!');		

			echo json_encode($response);($responses);
			exit;
		}
	}
	else
	{
		$response = array('status' => '0','msg' => 'No record found');		

		echo json_encode($response);($responses);
		exit;
	}
	

	
?>