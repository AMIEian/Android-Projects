<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	
	
	$customer_product_data_name = $_POST['product_table_name'];
	$customer_stock_data_name = $_POST['stock_table_name'];
	$product_responses = array();
	$stock_responses = array();
	$responses = array();

	//Get Product table metadata
	$result_product_data = pg_query($db,"select column_name, data_type, character_maximum_length  from INFORMATION_SCHEMA.COLUMNS 
    where table_name ='$customer_product_data_name'  order by ordinal_position" );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			/*echo $row['customer_id'];
			echo $row['customer_name'];
			echo $row['customer_stock_data_table_name'];	
			echo $row['customer_product_data_table_name'];*/
			$product_responses[] = $row;
		}
		
				
	}

	//Get stock table metadata
	$result_product_data = pg_query($db,"select column_name, data_type, character_maximum_length  from INFORMATION_SCHEMA.COLUMNS 
    where table_name ='$customer_stock_data_name'  order by ordinal_position" );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			/*echo $row['customer_id'];
			echo $row['customer_name'];
			echo $row['customer_stock_data_table_name'];	
			echo $row['customer_product_data_table_name'];*/
			$stock_responses[] = $row;
		}
		
				
	}

	$responses['product_metadata'] =$product_responses;
	$responses['stock_metadata'] =$stock_responses;


	echo json_encode($responses);
	
	
	
?>