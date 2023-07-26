<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");	

try
{
	
	$missingEPCResp = array();
	$mainResp = array();
	$summaryArray = array();	
	
   $stock_table_name = $_POST['stock_table_name'];
    $product_table_name = $_POST['product_table_name'];
    $customer_id = $_POST['customer_id'];

    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];

    
    $category="barcode";

    //Get category table data
	$result_product_data = pg_query($db,"SELECT  category_column_name FROM customer_category_master WHERE customer_id = $customer_id Order BY category_level ASC Limit 1"  );

    
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			$category = $row['category_column_name'];
		}
		
				
	}

    //Get last epc record w.r.t session id and user
	$reut= pg_query($db,"Select sldt_id from epc_location_date_time where  user_id='$user_id' AND session_id='$session_id' order by sldt_id DESC limit 1 " );
	if(pg_num_rows($reut) > 0)
	{
		while($row= pg_fetch_assoc($reut))   
		{		
			
			$sldt_id = $row['sldt_id'];
			//update is_scan_finish to 1 as complete stock take
			pg_query($db,"UPDATE epc_location_date_time SET is_scan_finished='1' where  sldt_id='$sldt_id' " );

		}
	}

	

	//get category rfid and qty details
	$result= pg_query($db,"select ".$product_table_name.".".$category." as category , ".$stock_table_name.".quantity as total_qty, ui_update.rfid_quantity as scan_qty from ui_update  Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".barcode = ui_update. barcode Inner JOIN ".$product_table_name." ON ".$stock_table_name.".barcode = ".$product_table_name.". barcode where user_id='$user_id' AND session_id='$session_id' AND ui_update.rfid_quantity !=0 Group By ".$product_table_name.".".$category." , ".$stock_table_name.".quantity, ui_update.rfid_quantity" );
	if(pg_num_rows($result) > 0)
	{
		while($row2= pg_fetch_assoc($result))   
		{		
			
			$summaryArray[] = $row2;
			
		}
	}
	

	$mainResp['epcList'] = $summaryArray;
	//{"epcList":[],"missingEPCList":[]}

	echo json_encode($mainResp); 
	
}
catch(Exception $e) {
  
  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);	
	exit;
}	

		

	
?>