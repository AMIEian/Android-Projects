<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");


try
{
	
	$response = array();
	$responses = array();

	/*customer_id=5&user_id=6&session_id=94&json_barcode=["8907733327270","8907733327287"]&json_epc=["303A667B83B4FC4DF84C3AFC","30361F84CC001C80000FA855","3014000000000000000FA861","30361F84CC000380000FA858","303A667B83B4FC4DF84C3AFD","3014000000000000000FA862","30361F84CC0000C0000FA842","3014000000000000000FA854"]&location_id=3&sub_location_id=3&stock_date=2021-02-02&stock_time=14:01:14&stock_table_name=dadar_stock_data&is_scan_finish=0*/
	$barcode = $_POST['barcode']; 
	$quantity = $_POST['quantity'];
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];
    $store_id = $_POST['store_id'];

    $user_id = $_POST['user_id'];
    $result3= pg_query($db,"Update picking_data SET status='1' WHERE barcode = '$barcode'" );
    $new_quantity  = 0;

    //update stock table quantity value
	$reut9= pg_query($db,"SELECT quantity  from   ".$stock_table_name." WHERE barcode = '$barcode' LIMIT 1 " );
	if(pg_num_rows($reut9) > 0)
	{
		while($row9= pg_fetch_assoc($reut9))   
		{		
			
			$stock_quantity = $row9['quantity'];
			if((int)$quantity > (int)$stock_quantity)
			{
				$new_quantity  = 0;
			}
			else
			{
				$new_quantity  = (int)$stock_quantity  - (int)$quantity;
			}

			

			//update customer_stock_data_table_name table
			$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET quantity='$new_quantity'  WHERE barcode = '$barcode'" );

		}
	}

   $response = array('status' => '1','msg' => "EPC record inserted");
	echo json_encode($response);	
	exit;
}
catch(Exception $e) {
  
  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);	
	exit;
}	


		

	
?>