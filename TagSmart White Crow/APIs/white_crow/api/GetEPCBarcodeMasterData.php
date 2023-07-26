<?php
	ob_start();
	//session_start(); 
	header('Cache-control: private');
	include("connect.php");


	$customer_id = $_POST['customer_id'];
	$store_id = $_POST['store_id'];
	$user_id = $_POST['user_id'];

	$result = pg_query($db,"select  barcode,epc from epc_barcode_master_white_crow WHERE store_id='$store_id' and customer_id ='$customer_id' ");

	$rows = pg_num_rows($result);
	$responses = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{	

			
			$responses[] = $row;
		}
		
				
	}
	echo json_encode($responses);
	
?>