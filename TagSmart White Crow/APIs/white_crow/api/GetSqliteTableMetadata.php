<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	
	
	$customer_product_data_name = $_POST['product_table_name'];
	$customer_stock_data_name = $_POST['stock_table_name'];


	/*$customer_product_data_name = "fbb_product_data";
	$customer_stock_data_name = "fbb_stock_data"; */

	$product_responses = array();
	$stock_responses = array();
	$stock_table_sqlite_query="";
	$product_table_sqlite_query="";
	$responses = array();

	//Get Product table metadata
	$result_product_data = pg_query($db,"select column_name, data_type, character_maximum_length  from INFORMATION_SCHEMA.COLUMNS 
    where table_name ='$customer_product_data_name'  order by ordinal_position" );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		$product_table_sqlite_query_start_part="create table " . $customer_product_data_name . "(";
		
		 $product_table_sqlite_query_middle_part="";
		$product_table_sqlite_query_end_part=");";
		while($row= pg_fetch_assoc($result_product_data))   
		{	
			
			$product_responses[] = $row['column_name'];
		}
		//building product query
		for($j=0; $j<sizeof($product_responses); $j++)
		{
			$product_table_sqlite_query_middle_part .= $product_responses[$j] ;
			if($j < (sizeof($product_responses) -1))
			{
				$product_table_sqlite_query_middle_part .=  " TEXT, " ;
			}
			else
			{
				$product_table_sqlite_query_middle_part .=   " TEXT " ;
			}
		}
		$product_table_sqlite_query = $product_table_sqlite_query_start_part.$product_table_sqlite_query_middle_part.$product_table_sqlite_query_end_part;
				
	}

	//Get stock table metadata
	$result_product_data = pg_query($db,"select column_name, data_type, character_maximum_length  from INFORMATION_SCHEMA.COLUMNS 
    where table_name ='$customer_stock_data_name'  order by ordinal_position" );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		$stock_table_sqlite_query_start_part="create table " . $customer_stock_data_name . "(";
		
		$stock_table_sqlite_query_middle_part="";
		$stock_table_sqlite_query_end_part=");";

		while($row= pg_fetch_assoc($result_product_data))   
		{	

			$stock_responses[] = $row['column_name'];
		}
		//building stock query
		for($j=0; $j<sizeof($stock_responses); $j++)
		{
			$stock_table_sqlite_query_middle_part .= $stock_responses[$j] ;
			if($j < (sizeof($stock_responses) -1))
			{
				
				$stock_table_sqlite_query_middle_part .=  " TEXT, " ;
			}
			else
			{
				$stock_table_sqlite_query_middle_part .=  " TEXT " ;
			}
		}
		$stock_table_sqlite_query =$stock_table_sqlite_query_start_part.$stock_table_sqlite_query_middle_part.$stock_table_sqlite_query_end_part;
		
				
	}




	$responses['product_metadata'] =$product_responses;
	$responses['stock_metadata'] =$stock_responses;
	$responses['product_table_sqlite_query'] =$product_table_sqlite_query ;
	$responses['stock_table_sqlite_query'] =$stock_table_sqlite_query;
	


	echo json_encode($responses);
	
	
	
?>