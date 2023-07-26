<?php

function InsertTagSmart_StockValues($bar,$epc,$custId,$storeId,$cust_stk_data_tblname,$cust_prd_data_tblname,$loc_id,$sub_loc_id,$t_id)
{

	//ini_set('max_execution_time', 0);
	ob_start();
	header('Cache-control: private');
	include("connect.php");
	include("Error_Write_To_Log_File.php");


	$barcode = $bar;
	$EPC = $epc;
	$customer_id = $custId;
	$store_id = $storeId;
	$customer_stock_data_table_name = $cust_stk_data_tblname;
	$customer_product_data_table_name = $cust_prd_data_tblname;
	$location_id=$loc_id;
	$sub_location_id=$sub_loc_id;
	$task_id=$t_id;

 error_write("barcode:".$barcode ."      in InsertTagSmartStockValues.php");
error_write("epc:".$epc ."      in InsertTagSmartStockValues.php");
	
	$stock_id=0;
	//$location_id=0;
	//$sub_location_id=0;
	//$quantity="0";
	//$rfid_quantity="0";

	$responses = array();

	//Get data from customer_stock_data_table_name table
	$result= pg_query($db,"SELECT *   from  ".$customer_stock_data_table_name." WHERE store_id = '$store_id' AND customer_id = '$customer_id' AND barcode='$barcode' LIMIT 1 " );
   error_write("pg num:".pg_num_rows($result)."      in InsertTagSmartStockValues.php");

	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{		
			$stock_id = $row['stock_id'];
			
			//$location_id = $row['location_id'];
			//$sub_location_id = $row['sub_location_id'];
			//$quantity = $row['quantity'];
			//$rfid_quantity = $row['rfid_quantity'];
			//$new_rfid_quantity = (int)$rfid_quantity+1;

		}

		// check if value is already present. If not then 
		$result6= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );

		if(pg_num_rows($result6) == 0)
		{
				$result2 = pg_query($db,"INSERT INTO  tagsmart_stock_details (tagsmart_id, stock_id, customer_id, epc) VALUES(default,'$stock_id','$customer_id','$EPC')");
				if($result2)
				{
					$result3= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );
					if(pg_num_rows($result3) > 0)
					{
						while($row3= pg_fetch_assoc($result3))   
						{		
							$tagsmart_id = $row3['tagsmart_id'];
							$currentDate =date("Y-m-d");

							$result4 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id,task_id) VALUES(default,'$tagsmart_id','$location_id',8,'$currentDate',now(),'0','1',0 ,'0','0','$customer_id','$store_id','$task_id')"  );
							
							if($result4>0)
							{
							   $responses['EPC'] ="success";	
							}else
							{
							  $responses['EPC'] ="Transaction failed.";
							  error_write("Transaction failed "."      in InsertTagSmartStockValues.php");
							}


						}
					}
					else
					{
						$responses['EPC'] ="Transaction failed.";
		                                error_write("Transaction failed "."      in InsertTagSmartStockValues.php");
					}
				
				}
				else
				{
					$responses['EPC'] ="Tagsmart failed";
					error_write("Tagsmart failed "."    in InsertTagSmartStockValues.php");
				}

		}
		else
		{
			$responses['EPC'] ="EPC already exist. ";
			error_write("EPC already exist "."    in InsertTagSmartStockValues.php");
		}

	}
	else
	{
		$responses['EPC'] ="Barcode not valid.";
		error_write("Barcode not valid "."      in InsertTagSmartStockValues.php");
	}


return json_encode($responses);
}	
	
?>