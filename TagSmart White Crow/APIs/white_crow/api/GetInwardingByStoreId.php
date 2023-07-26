<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	$store_id = $_POST['store_id'];
	$customer_id = $_POST['customer_id'];

	//test
	/*$store_id = 5;
	$customer_id = 5;*/

		
	$result = pg_query($db,"SELECT  inward_data.*  FROM inward_data WHERE  inward_data.store_id=".$store_id );

	$diff = 0;

	$rows = pg_num_rows($result);
	$responses = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{	

			/*echo $row['customer_id'];
			echo $row['customer_name'];
			echo $row['customer_stock_data_table_name'];	
			echo $row['customer_product_data_table_name'];*/

			$dispatch_order_id =  $row['dispatch_order_id']; 

			$diff_result = pg_query($db,"SELECT  ( sum(CAST (quantity as Integer) ) - sum(CAST(rfid_quantity as Integer) ) ) as diff  FROM inward_data WHERE  inward_data.store_id=".$store_id."  AND dispatch_order_id= '".$dispatch_order_id."'" );
			 
			if(pg_num_rows($diff_result) > 0)
			{
				while($row2 = pg_fetch_assoc($diff_result))   
				{
					$diff =  (int)$row2['diff'];
					if($diff > 0)
					{
						$responses[] = $row;
					}

				}
			}

			
			
		}
		
				
	}

	//Get category table data
    $result_product_data = pg_query($db,"SELECT  * FROM customer_category_master WHERE customer_id = $customer_id  " );
    
    $maxlevel = pg_num_rows($result_product_data) +1; // 1 is added for barcode
    //Get session id
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

	//Update  session id
	$result_product_data = pg_query($db,"UPDATE customer_store_master SET session_id = '$new_session_id' where customer_id ='$customer_id'" );

    $mainArray = array();
    $mainArray['details'] = $responses;
	$mainArray['max_level'] = $maxlevel;
	$mainArray['session_id'] = $new_session_id;

	echo json_encode($mainArray);
	
	
	
?>