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
	$responses = array();

	
	$json_epc_data = json_decode($_POST['json_epc_data'], true); 

	foreach($json['tagsmart_epc_data'] as $item) {
	    $EPC= $item['epc'];
	    $location_id = $item['location_id'];
	    $sub_location_id = $item['sub_location_id'];
	    $stock_date = $item['stock_date'];
	    $stock_time = $item['stock_time'];
	    $is_sold = $item['is_sold'];
	    $is_sse = $item['is_sse'];

	    $result6= pg_query($db,"SELECT  *  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );

		if(pg_num_rows($result6) > 0)
		{
			while($row= pg_fetch_assoc($result6))   
			{
				$tagsmart_id= $row['tagsmart_id'];
				$result= pg_query($db,"UPDATE epc_location_date_time SET location_id = '$location_id', sub_location_id= '$sub_location_id', stock_date ='$stock_date', stock_time = '$stock_time', is_sold ='$is_sold, is_sse='$is_sse'  WHERE tagsmart_id = '$tagsmart_id'" );

				if(!$result)				
				{
					$responses = array('EPC' => '$EPC');		

					
				}
			}
		}
	}

	echo json_encode($response); 	

	
?>