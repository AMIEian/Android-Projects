<?php
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	$customer_id = $_POST['customer_id'];
	$store_id = $_POST['store_id'];
	$location_id = $_POST['location_id'];
	
	$result = pg_query($db,"SELECT location_master.location_id, location_master.location, sub_location_master.sub_location_id,sub_location_master.sublocation FROM customer_master Inner JOIN customer_store_master ON customer_store_master.customer_id = customer_master.customer_id Inner JOIN customer_store_location ON customer_store_location.store_id = customer_store_master.store_id  Inner JOIN location_master ON location_master.location_id = customer_store_location.location_id  Inner JOIN sub_location_master ON sub_location_master.sub_location_id = customer_store_location.sub_location_id WHERE  customer_master.customer_id=".$customer_id." AND customer_store_location.store_id= ".$store_id." AND customer_store_location.location_id=".$location_id);

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
			$responses[] = $row;
		}
		
				
	}
	echo json_encode($responses);
	
	
	
?>