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
    $stock_date = $_POST['stock_date'];
    $min_time_stamp = $_POST['min_time_stamp'];

 /* //test
   	$stock_table_name = 'ethnicity_stock_data';
    $product_table_name = 'ethnicity_product_data';
    $customer_id = 7;
    $user_id = 9;
    $stock_date = '2021-09-24';
    $min_time_stamp ='2021-09-24 17:13:27';*/
    
    $category="";

    //Get category table data
	$result_product_data = pg_query($db,"SELECT  category_column_name FROM customer_category_master WHERE customer_id = $customer_id Order BY category_level ASC Limit 1"  );

    
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	

			$category = $row['category_column_name'];
		}
		
				
	}    

	//echo "select Distinct ".$product_table_name.".".$category." as category, string_agg(".$product_table_name.".barcode, '&quot;, &quot;') AS barcode_list from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN ".$product_table_name." ON ".$stock_table_name.".barcode = ".$product_table_name.". barcode where user_id='$user_id' AND  epc_location_date_time.stock_time >='".$min_time_stamp."'  Group By ".$category."<BR>";

	//get category rfid and qty details
	$result= pg_query($db,"select Distinct ".$product_table_name.".".$category." as category, string_agg(".$product_table_name.".barcode, '&quot;, &quot;') AS barcode_list from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN ".$product_table_name." ON ".$stock_table_name.".barcode = ".$product_table_name.". barcode where user_id='$user_id' AND  epc_location_date_time.stock_time >='".$min_time_stamp."'  Group By ".$category);

	$rfid_qty = 0;
	$totalQty = 0;
	$cat = "";
	$counter=0;
	$category_rfid_qty = 0;
	if(pg_num_rows($result) > 0)
	{
		while($row2= pg_fetch_assoc($result))   
		{		
			
			$cat = $row2['category'];
			$barcode_list = $row2['barcode_list'];
			$barcode_list = "'".str_replace('&quot;', "'", $barcode_list)."'";
			$tmpArray = array();
			$category_rfid_qty = 0;
			

			//echo "select Distinct ".$stock_table_name.".barcode as barcode, ".$stock_table_name.".quantity as total_qty, Count(epc_location_date_time.tagsmart_id)  as rfid_qty from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details. stock_id  where user_id='$user_id' AND  epc_location_date_time.stock_time >='".$min_time_stamp."' AND ".$stock_table_name.".barcode IN(".$barcode_list.") Group By ".$stock_table_name.".barcode , ".$stock_table_name.".quantity<br>";

			$result2= pg_query($db,"select Distinct ".$stock_table_name.".barcode as barcode, ".$stock_table_name.".quantity as total_qty, Count(epc_location_date_time.tagsmart_id)  as rfid_qty from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details. stock_id  where user_id='$user_id' AND  epc_location_date_time.stock_time >='".$min_time_stamp."' AND ".$stock_table_name.".barcode IN(".$barcode_list.") Group By ".$stock_table_name.".barcode , ".$stock_table_name.".quantity");
			if(pg_num_rows($result2) > 0)
			{
				while($row3= pg_fetch_assoc($result2))   
				{
					$rfid_qty = $rfid_qty +(int)$row3['rfid_qty'] ;
					$totalQty = $totalQty+(int)$row3['total_qty'] ;
					$category_rfid_qty = $category_rfid_qty + (int)$row3['rfid_qty'] ;
					$tmpArray[] = $row3;
				}
				$summaryArray[$counter]['main_category'] = $cat." ( ".$category_rfid_qty." )";
				$summaryArray[$counter]['barcode_details']= $tmpArray;
			}

			$counter++;
			
		}
	}
	

	$mainResp = array();
	$mainResp['scan_total_qty'] = $totalQty;
	$mainResp['rfid_total_qty'] = $rfid_qty;
	$mainResp['scan_values'] = $summaryArray;

	echo  json_encode($mainResp);
	exit;
	
}
catch(Exception $e) {
  
  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);	
	exit;
}	

		

	
?>