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

/*
	$barcode_array =["8905011004509","8905014259623","8905014259630","8905011003878","8905011000105","8905011000693","8905011001492","8905011004455","8905011000686","8905014259647","8905014479847","8905014479816","8905014479823","8905011004516","8905014274770","8905011004523","8905014259616","8905011000068","8905011004486","8905014274732","8905011000075","8905014479830","8905014259654","8905011004493","8905011000709","8905011004462","8905014431074","8905011066910","8905014431067","8905011068600","8905011004479","8907733753420"];

	$epc_array=["30361F84CC0070800000098B","30361F84D8195A8000000001","30361F84D8195AC000000003","30361F84CC0060C000000003","30361F84CC00028000000DBC","30361F84CC00028000000DBD","30361F84CC00114000000001","30361F84CC002540000004F4","30361F84CC006F40000008DC","30361F84CC00110000000001","30361F84D8195B0000000002","30361F84D82EDC0000000002","30361F84D82EDB4000000002","30361F84D82EDB8000000002","30361F84CC002540000004F3","30361F84D8195B0000000003","30361F84CC0070C000000703","30361F84D81AD54000000003","30361F84CC00114000000003","30361F84CC007100000002C5","30361F84D8195A4000000001","30361F84CC00110000000002","30361F84CC00018000000002","30361F84CC0070C000000704","30361F84CC00700000000318","30361F84D8195A4000000002","30361F84CC0060C000000002","30361F84D8195AC000000002","30361F84D81AD44000000002","30361F84CC0001C000000001","30361F84D82EDBC000000001","30361F84D82EDBC000000003","30361F84CC00028000000DBE","30361F84D81AD54000000002","30361F84CC0001C000000002","30361F84CC007100000002C6","30361F84D8195B4000000001","30361F84D8195A4000000003","30361F84CC0070C000000705","30361F84D82EDB4000000003","30361F84D8195A8000000003","30361F84D82EDC0000000003","30361F84D8195B0000000001","30361F84CC00704000000881","30361F84D82EDB4000000001","30361F84CC00110000000003","30361F84D82EDBC000000002","30361F84CC00704000000880","30361F84D81AD44000000001","30361F84D81AD44000000003","30361F84D82EDB8000000003","30361F84D8195B4000000002","30361F84D82EDB8000000001","30361F84D82EDC0000000001","30361F84CC006F40000008DA","30361F84CC00118000000003","30361F84CC0060C000000001","30361F84D8195AC000000001","30361F84CC00018000000003","30361F84CC006F80000009D8","30361F84D82A18C000000007","30361F84CC002540000004F2","30361F84D81AD54000000001","30361F84CC0070400000087F","30361F84D82A18C000000006","30361F84CC0688C000000003","30361F84CC006F80000009D7","30361F84CC00118000000001","30361F84CC0688C000000002","30361F84CC006F40000008DB","30361F84D8195B4000000003","30361F84D82A188000000006","30361F84CC06B300000001B7","30361F84CC00118000000002","30361F84CC00700000000319","30361F84D8195A8000000002","30361F84CC007100000002C7","30361F84CC006FC000000738","30361F84CC006FC000000737","30361F84CC006FC000000736","30361F84CC0070800000098C","30361FAF56DFC30000025452","30361F84CC00700000000317","30361F84CC0070800000098A"];


	$json_barcode = $barcode_array;
	$json_epc = $epc_array;
    $stock_date = '2021-07-02';
    $stock_time = '2021-07-02'." ".'00:00:00';
    $stock_table_name = 'dadar_stock_data';
		$product_table_name = 'dadar_product_data';
    $customer_id = '5';
    $dispatch_order = '102';
    $user_id = '10';
    $session_id = '999';
    $is_scan_finish = 1;
    $location_id = "0";
	$sub_location_id = "0";
	$store_id = '8';*/

$json_barcode = json_decode($_POST['json_barcode'], true);
	$json_epc = json_decode($_POST['json_epc'], true);
    $stock_date = $_POST['stock_date'];
    $stock_time = $_POST['stock_date']." ".$_POST['stock_time'];
    $stock_table_name = $_POST['stock_table_name'];
		$product_table_name = $_POST['product_table_name'];
    $customer_id = $_POST['customer_id'];
    $dispatch_order = $_POST['dispatch_order'];
    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];
    $is_scan_finish = 1;
    $location_id = "0";
	$sub_location_id = "0";
	$store_id = $_POST['store_id'];

	/*echo "SELECT customer_store_location.location_id, customer_store_location.sub_location_id  FROM customer_master LEFT JOIN customer_store_master ON customer_store_master.customer_id = customer_master.customer_id LEFT JOIN customer_store_location ON customer_store_location.store_id = customer_store_master.store_id  WHERE  customer_master.customer_id=".$customer_id." ORDER BY customer_store_location.csl_id ASC LIMIT 1";*/

	$result = pg_query($db,"SELECT customer_store_location.location_id, customer_store_location.sub_location_id  FROM customer_master LEFT JOIN customer_store_master ON customer_store_master.customer_id = customer_master.customer_id LEFT JOIN customer_store_location ON customer_store_location.store_id = customer_store_master.store_id  WHERE  customer_master.customer_id=".$customer_id." ORDER BY customer_store_location.csl_id ASC LIMIT 1" );

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
			$location_id = $row['location_id'];
			$sub_location_id = $row['sub_location_id'];
		}


	}

	//sequence correction query for stock tab
	pg_query($db,"SELECT SETVAL((SELECT PG_GET_SERIAL_SEQUENCE('\"".$stock_table_name."\"', 'stock_id')), (SELECT (MAX(\"stock_id\") + 1) FROM \"".$stock_table_name."\"), FALSE);");


    foreach($json_epc as $item2) 
    {
    	$epc = trim($item2);
    	$ean="";

		$result_barcode = pg_query($db,"select distinct barcode from epc_barcode_master_white_crow where epc='$epc' Limit 1");
		$rows_barcode = pg_num_rows($result_barcode);
		if($rows_barcode > 0 )			    
		{
			while($row_bar= pg_fetch_assoc($result_barcode))   
			{
				 $ean = $row_bar['barcode'];
			}

		    if (in_array($ean, $json_barcode)) //if epc is found in selected barcode array
		    {
			    $EPC= $item2;

			    $barcode = $ean;
			    $stock_id = 0;
			    $rfid_quantity="0";
			    $old_quantity="0";

			    //Get data from customer_stock_data_table_name table
				$reut= pg_query($db,"SELECT stock_id, rfid_quantity ,store_id, quantity from  ".$stock_table_name." WHERE LOWER(barcode) = LOWER('$barcode') AND store_id='$store_id'  LIMIT 1 " );
				if(pg_num_rows($reut) > 0)
				{
					while($row= pg_fetch_assoc($reut))
					{

						$rfid_quantity = $row['rfid_quantity'];
						$stock_id = $row['stock_id'];
						$old_quantity = $row['quantity'];

					}
				}

				if($stock_id == 0)
				{
					$currentDate =date("Y-m-d");
					//insert in stock table
					$result17 = pg_query($db,"INSERT INTO  ".$stock_table_name." (stock_id, store_id, customer_id, barcode, location_id, sub_location_id, quantity, rfid_quantity, uploaded_date, minimum_stocking_quantity)  VALUES ( DEFAULT, '$store_id', '$customer_id', '$barcode', '$location_id', '$sub_location_id','0','0','$currentDate','1' )"  );

					// get last inserted $stock_id
					$result18 = pg_query($db,"SELECT  stock_id  from  ".$stock_table_name." WHERE LOWER(barcode) = LOWER('$barcode') AND store_id='$store_id'  LIMIT 1 " );
					$stock_id_new ='0';

					if(pg_num_rows($result18) > 0)
					{
						while($row18= pg_fetch_assoc($result18))
						{
							$stock_id = $row18['stock_id'];
							$rfid_quantity = '0';
							$old_quantity = '0';
						}
					}
				}

				//check if epc is present in tagsmart_stock_details table
				$result6= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );

				if(pg_num_rows($result6) == 0)
				{
					// make entry in tagsmart_stock_details table

					$result2 = pg_query($db,"INSERT INTO  tagsmart_stock_details (tagsmart_id, stock_id, customer_id, epc) VALUES(DEFAULT,'$stock_id','$customer_id','$EPC')"  );

					if($result2)
					{
						$result3= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );
						if(pg_num_rows($result3) > 0)
						{
							while($row3= pg_fetch_assoc($result3))
							{
								$tagsmart_id = $row3['tagsmart_id'];
								$currentDate =date("Y-m-d");

								$result4 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id,task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1' ,'$user_id' ,'$session_id','$is_scan_finish','$customer_id','$store_id','2' )"   );

								if($result4)
								{

									//update ui_update table rfid value
									$reut9= pg_query($db,"SELECT ui_id, rfid_quantity  from   ui_update WHERE barcode = '$barcode' AND session_id='$session_id' AND customer_id='$customer_id'  LIMIT 1 " );
									if(pg_num_rows($reut9) > 0)
									{
										while($row9= pg_fetch_assoc($reut9))
										{

											$rfid_quantity = $row9['rfid_quantity'];
											$ui_id = $row9['ui_id'];

											$new_rfid_quantity  = (int)$rfid_quantity+1;

											//update customer_stock_data_table_name table
											$result7= pg_query($db,"UPDATE  ui_update SET rfid_quantity='$new_rfid_quantity'  WHERE barcode = '$barcode' AND session_id='$session_id' AND customer_id='$customer_id' " );

											//update inward_data table
											$result8= pg_query($db,"UPDATE  inward_data SET  rfid_quantity='$new_rfid_quantity'  WHERE barcode = '$barcode' AND dispatch_order_id='$dispatch_order' " );

											//update quantity in stock table
											$new_quantity = (int)$old_quantity+1;

											//update customer_stock_data_table_name table
											$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET uploaded_date='".$stock_date."' , quantity='$new_quantity' , rfid_quantity='$new_rfid_quantity' WHERE stock_id = '$stock_id' " );

										}
									}

								}

							}
						}

					}

				}
				else
				{

						$tagsmart_id =0;
						$insert_record_flag=0;
						while($row6= pg_fetch_assoc($result6))
						{

							$tagsmart_id = $row6['tagsmart_id'];

						}
						//if found in tagsmart table then check the location and sublocation in epc_location_date_time table.
						$result17= pg_query($db,"SELECT epc_location_date_time.location_id, epc_location_date_time.sub_location_id FROM public.epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id where epc='$item2' order by sldt_id desc limit 1 " );
						if(pg_num_rows($result17) > 0)
						{
							while($row17= pg_fetch_assoc($result17))
							{

								$old_location_id = $row['location_id'];
								$old_sub_location_id = $row['sub_location_id'];
								if($old_location_id != $location_id && $old_sub_location_id != $sub_location_id)
								{
									$insert_record_flag=1;
								}

							}
						}
						else
						{
							$insert_record_flag=1;
						}

						if($insert_record_flag== 1)
						{
							$currentDate =date("Y-m-d");

							$result4 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id,task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1' ,'$user_id' ,'$session_id','$is_scan_finish','$customer_id','$store_id','2' )"   );

							if($result4)
							{


								//update ui_update table rfid value
								$reut9= pg_query($db,"SELECT ui_id, rfid_quantity  from   ui_update WHERE barcode = '$barcode' AND session_id='$session_id' AND customer_id='$customer_id'  LIMIT 1 " );
								if(pg_num_rows($reut9) > 0)
								{
									while($row9= pg_fetch_assoc($reut9))
									{

										$rfid_quantity = $row9['rfid_quantity'];
										$ui_id = $row9['ui_id'];

										$new_rfid_quantity  = (int)$rfid_quantity+1;

										//update customer_stock_data_table_name table
										$result7= pg_query($db,"UPDATE  ui_update SET rfid_quantity='$new_rfid_quantity'  WHERE barcode = '$barcode' AND session_id='$session_id' AND customer_id='$customer_id' " );

										//update inward_data table
										$result8= pg_query($db,"UPDATE  inward_data SET  rfid_quantity='$new_rfid_quantity'  WHERE barcode = '$barcode' AND dispatch_order_id='$dispatch_order' " );

										//update quantity in stock table
										$new_quantity = (int)$old_quantity+1;

										//update customer_stock_data_table_name table
										$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET uploaded_date='".$stock_date."' , quantity='$new_quantity' , rfid_quantity='$new_rfid_quantity' WHERE stock_id = '$stock_id' " );

									}
								}

							}
						}
				}


			}
			else //stray table
			{
				/******** Old logic commented by Bharat on 27july2021, upon Gautam suggestion
				//check in entire stock table
				$result9= pg_query($db,"Select barcode from ".$stock_table_name." WHERE barcode='".$ean ."'");
			    if(pg_num_rows($result9) <= 0)
				{
						//check in entire product table
						$result11= pg_query($db,"Select barcode from ".$product_table_name." WHERE barcode='".$ean ."'");
				    if(pg_num_rows($result11) <= 0) // not present in product table tto, add it in stray table
						{
							//Add to stray table
							 $result2 = pg_query($db,"INSERT INTO  stray_epc (stray_id, epc, barcode, stray_date, stray_time, task_id) VALUES(DEFAULT,'$item2','$ean','$stock_date','$stock_time','2' )"  );
						}
						else
						{
							$currentDate =date("Y-m-d");
							//insert in stock table
							$result15 = pg_query($db,"INSERT INTO  ".$stock_table_name." (stock_id, store_id, customer_id, barcode, location_id, sub_location_id, quantity, rfid_quantity, uploaded_date, minimum_stocking_quantity) VALUES(DEFAULT,'$store_id','$customer_id','$ean','$location_id','$sub_location_id','1','1','$currentDate','1' )"  );

							// get last inserted $stock_id
							$result16 = pg_query($db,"SELECT  stock_id  from  ".$stock_table_name." WHERE LOWER(barcode) = LOWER('$ean')   LIMIT 1 " );
							$stock_id_new ='0';

							if(pg_num_rows($result16) > 0)
							{
								while($row16= pg_fetch_assoc($result16))
								{
									$stock_id_new = $row16['stock_id'];
								}
							}

							// make entry in tagsmart_stock_details table

							$result12 = pg_query($db,"INSERT INTO  tagsmart_stock_details (tagsmart_id, stock_id, customer_id, epc) VALUES(DEFAULT,'$stock_id_new','$customer_id','$item2')"  );

							if($result12)
							{
								$result13= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$item2')   LIMIT 1 " );
								if(pg_num_rows($result13) > 0)
								{
									while($row13= pg_fetch_assoc($result13))
									{
										$tagsmart_id = $row13['tagsmart_id'];


										$result14 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id,task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1' ,'$user_id' ,'$session_id','$is_scan_finish','$customer_id','$store_id','2' )"   );

									}
								}

							}
						}
				}
				//check if the EPC is not a part of the EPCs in the store, and it is not matching the barcodes in the inward table, but it still matches the barcode in the product / stock catalogue
				else if(pg_num_rows($result9) > 0)
				{

					$EPC= $item2;

				    $barcode = $ean;
				    $stock_id = 0;
				    $rfid_quantity="0";
				    $old_quantity="0";

				    //Get data from customer_stock_data_table_name table
					$reut= pg_query($db,"SELECT stock_id, rfid_quantity ,store_id, quantity from  ".$stock_table_name." WHERE barcode = '$barcode' AND store_id='$store_id'  LIMIT 1 " );
					if(pg_num_rows($reut) > 0)
					{
						while($row= pg_fetch_assoc($reut))
						{

							$rfid_quantity = $row['rfid_quantity'];
							$stock_id = $row['stock_id'];
							$old_quantity = $row['quantity'];

						}
					}

					//check if epc is present in tagsmart_stock_details table
					$result6= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );

					if(pg_num_rows($result6) == 0)
					{

						// make entry in tagsmart_stock_details table

						$result2 = pg_query($db,"INSERT INTO  tagsmart_stock_details (tagsmart_id, stock_id, customer_id, epc) VALUES(DEFAULT,'$stock_id','$customer_id','$EPC')"  );

						if($result2)
						{
							$result3= pg_query($db,"SELECT  tagsmart_id  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );
							if(pg_num_rows($result3) > 0)
							{
								while($row3= pg_fetch_assoc($result3))
								{
									$tagsmart_id = $row3['tagsmart_id'];
									$currentDate =date("Y-m-d");

									$result4 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id,task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1' ,'$user_id' ,'$session_id','$is_scan_finish','$customer_id','$store_id','2' )"   );



								}
							}

						}

					}

				}*/

				/**********New Logic suggested by Gautam  on 27th July2021************************************/

				//check in entire stock table
					$result9= pg_query($db,"Select barcode from ".$stock_table_name." WHERE barcode='".$ean ."' AND customer_id='$customer_id' AND store_id='$store_id'");
				    if(pg_num_rows($result9) <= 0) // not present
					{
						//check in entire product table
						$result11= pg_query($db,"Select barcode from ".$product_table_name." WHERE barcode='".$ean ."'");

						if(pg_num_rows($result11) <= 0) // not present in product table tto, add it in stray table
						{
							//Add to stray table
							 $result2 = pg_query($db,"INSERT INTO  stray_epc (stray_id, epc, barcode, stray_date, stray_time, task_id) VALUES(DEFAULT,'$item2','$ean','$stock_date','$stock_time','2' )"  );
						}
						else
						{
							//Add to drift_data table
							$result19 = pg_query($db,"INSERT INTO  drift_data (drift_id,barcode, epc, location_id, sub_location_id, drift_date, drift_timestamp, session_id, customer_id, store_id, task_id, comment)  VALUES(DEFAULT,'$ean','$item2','$location_id','$sub_location_id','$stock_date','$stock_time','$session_id','$customer_id','$store_id','2','Inward, present in product and stock table but barcode not selected')"  );
						}
						 
					}
					else
					{					

						//Add to stray table
						$result2 = pg_query($db,"INSERT INTO  stray_epc (stray_id, epc, barcode, stray_date, stray_time, task_id) VALUES(DEFAULT,'$item2','$ean','$stock_date','$stock_time','2' )"  );
					}

				/****************************************************************************/
			}
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
