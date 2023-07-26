<?php
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	
	
	$result = pg_query($db,"SELECT * FROM public.customer_master ORDER BY customer_id ASC ");

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