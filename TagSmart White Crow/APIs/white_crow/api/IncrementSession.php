<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	
	
	$customer_id = $_POST['customer_id'];


	/*$customer_product_data_name = "fbb_product_data";
	$customer_stock_data_name = "fbb_stock_data"; */

	$product_responses = array();
	$stock_responses = array();
	$session_id="0";
	$product_table_sqlite_query="";
	$responses = array();

	//Get Product table metadata
	$result_product_data = pg_query($db,"select session_id  from customer_store_master where customer_id ='$customer_id' " );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	
			
			$session_id = $row['session_id'];
		}
				
	}

	$new_session_id = (int)$session_id+1;

	//Get stock table metadata
	$result_product_data = pg_query($db,"UPDATE customer_store_master SET session_id = '$new_session_id' where customer_id ='$customer_id'" );

    $rows = pg_num_rows($result_product_data);
	if($result_product_data)
	{
		$response = array('status' => '1','session_master' => $new_session_id);
		echo json_encode($response);	
		exit;
				
	}
	else
	{
		$response = array('status' => '0','session_master' => $session_id);
		echo json_encode($response);	
		exit;
	}


	


	echo json_encode($responses);
	
	
	
?>