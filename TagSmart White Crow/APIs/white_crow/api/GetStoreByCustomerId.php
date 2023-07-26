<?php
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	$customer_id = $_POST['customer_id'];
	
	$result = pg_query($db,"SELECT  customer_store_master.*  FROM customer_master Inner JOIN customer_store_master ON customer_store_master.customer_id = customer_master.customer_id  WHERE  customer_master.customer_id=".$customer_id );

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