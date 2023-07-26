<?php
	ini_set('max_execution_time', 0);
	ini_set('memory_limit', '1024M');

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	
	
	$customer_product_data_name = $_POST['product_table_name'];
	$customer_stock_data_name = $_POST['stock_table_name'];

	$customer_id = $_POST['customer_id'];
	$store_id = $_POST['store_id'];
	$category_responses = array(); 
	$product_responses = array(); 
	$tagsmart_responses = array();
	$responses = array();

	/*$customer_id = "3";
	$store_id = "3";
	$customer_stock_data_name = "test_fbb_stock_data";
	$customer_product_data_name = "test_fbb_product_data";*/

	//Get category table data
	$result_product_data = pg_query($db,"SELECT  * FROM customer_category_master WHERE customer_id = $customer_id" );

    
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			$category_responses[] = $row;
		}
		
				
	}


	//Get Product, stock table data
	$result_product_data = pg_query($db,"SELECT  $customer_product_data_name.*, $customer_stock_data_name.*  FROM $customer_stock_data_name  INNER JOIN $customer_product_data_name ON $customer_product_data_name.barcode = $customer_stock_data_name.barcode    WHERE $customer_stock_data_name.store_id  ='$store_id' AND $customer_stock_data_name.customer_id = $customer_id ORDER BY $customer_stock_data_name.stock_id ASC" );

    
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			$product_responses[] = $row;
		}
		
				
	}

	//Get tagsmart_stock_details, epc_location_date_time table data
	$result_product_data = pg_query($db,"SELECT  $customer_stock_data_name.barcode, tagsmart_stock_details.*, epc_location_date_time.* , tagsmart_stock_details.tagsmart_id FROM $customer_stock_data_name  INNER JOIN tagsmart_stock_details ON tagsmart_stock_details.stock_id = $customer_stock_data_name.stock_id LEFT JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id    WHERE $customer_stock_data_name.store_id  ='$store_id' AND $customer_stock_data_name.customer_id = $customer_id" );

    
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			$tagsmart_responses[] = $row;
		}
		
				
	}

	
	$responses['category_data'] =$category_responses;
	$responses['product_stock_data'] =$product_responses;
	$responses['tagsmart_epc_data'] =$tagsmart_responses;


	echo json_encode($responses);
	
	
	
?>