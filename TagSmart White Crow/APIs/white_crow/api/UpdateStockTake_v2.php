<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	function strToBin ( $number )    {
    $result = '';
    for ( $i = 0; $i < strlen($number); $i++ ){
        $conv = base_convert($number[$i], 16, 2);
        $result .= str_pad($conv, 4, '0', STR_PAD_LEFT);
    }
    return $result;
}
	function convert_EPC_to_barcode($EPCNumber)
	{
		$biString = strToBin($EPCNumber);

		//print_r("biString ".strlen($biString)."<br>") ;
		//print_r("biString ".$biString."<br>") ;


       $actual_epc = substr($biString,14,strlen($biString)); 

		//print_r("actual_epc   ".$actual_epc."<br>") ;
        $Bar = substr($actual_epc,0,44); 

        $companyprefix = substr($Bar,0,24); 
        
        //print_r("companyprefix   ".bindec($companyprefix)."<br>") ;

        $deciCompanyPrefix = bindec($companyprefix);  

        $itemref = substr($Bar,24,44); 

        //print_r("I ".$itemref) ;

        $itemref2 = substr($itemref,0,20);  
        //System.out.println("I2 "+itemref2) ;

        $deciItemRef = bindec($itemref2);   


        if(strlen($deciItemRef ) <5)
        {
            if(strlen($deciItemRef )==4)
            {
                $deciItemRef ="0".$deciItemRef;
            }
            else
            if(strlen($deciItemRef)==3)
            {
                $deciItemRef ="00".$deciItemRef;
            }else if(strlen($deciItemRef )==2)
            {
                $deciItemRef ="000".$deciItemRef;
            }
            else if(strlen($deciItemRef)==1)
            {
                $deciItemRef ="0000".$deciItemRef;
            }
        }

        


       // $serial_number = substr($actual_epc,44,strlen($actual_epc)); 


        $barcode = $deciCompanyPrefix .$deciItemRef;

       // echo "Decoded barcode    ".$barcode.get_checksum_digit($barcode) ;

        $barcodeString = $barcode.get_checksum_digit($barcode);

        return $barcodeString;
	}

	function get_checksum_digit($barcode)
	{
		$evenSum=0;
		$oddSum = 0 ;
		
		for($y =0 ; $y< strlen($barcode); $y++)
		{
			if($y%2 == 0)
			{
				$evenSum = $evenSum+ (int)$barcode[$y];
			}
			else
			{
				$oddSum = $oddSum+ (int)$barcode[$y];
			}
		}
		
		$oddSum = 3* $oddSum;
		
		$sumValue = $evenSum+$oddSum;
		
		if($sumValue % 10 == 0)
		{
			return "0";
		}
		else
		{
			$subValue =  10 - ($sumValue % 10);
			
			return $subValue;
		}
	}

	
	$response = array();
	$responses = array();

	/*json_epc_data=[{"barcode":"8905011000150","category":"OTHER DUPATTA","epc":"30361F84CC0003C000000001","isPresent":"1","stock_id":"112"},{"barcode":"8905011000143","category":"OTHER DUPATTA","epc":"30361F84CC00038000000002","isPresent":"1","stock_id":"111"}]&stray_json_epc_data=["30361F84CC0003C000000001","30361F84D82AD94000000022","30361FAF54275D40000FAABA","30361F84CC00038000000002","303556022843A3C00000000D"]&location_id=3&sub_location_id=4&stock_date=2020-11-05&stock_time=08:09:32&stock_table_name=test_fbb_stock_data&customer_id=3*/
	
	$json_epc_data = json_decode($_POST['json_epc_data'], true); 
	$stray_json_epc_data = json_decode($_POST['stray_json_epc_data'], true); 

	$location_id = $_POST['location_id'];
    $sub_location_id = $_POST['sub_location_id'];
    $stock_date = $_POST['stock_date'];
    $stock_time = $_POST['stock_date']." ".$_POST['stock_time'];
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];

   

    //reset rfid_qty to zero in stock table 
    //Add on 20thNov2020 as per Gautam suggestion
    foreach($json_epc_data as $stock_item) {
    	$stock_id = $stock_item['stock_id']; 
    	pg_query($db,"UPDATE  ".$stock_table_name." SET  rfid_quantity='0'  WHERE stock_id = '$stock_id' " );


    }

	foreach($json_epc_data as $item) {
	    $EPC= $item['epc'];
	    
	    $barcode = $item['barcode'];
	    $stock_id = $item['stock_id']; 
	    $rfid_quantity="0";

	    //Get data from customer_stock_data_table_name table
		$reut= pg_query($db,"SELECT rfid_quantity   from  ".$stock_table_name." WHERE stock_id = '$stock_id'  LIMIT 1 " );
		if(pg_num_rows($reut) > 0)
		{
			while($row= pg_fetch_assoc($reut))   
			{		
				
				$rfid_quantity = $row['rfid_quantity'];

			}
		}

	    $result6= pg_query($db,"SELECT  *  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );

		if(pg_num_rows($result6) > 0)
		{
			while($row= pg_fetch_assoc($result6))   
			{
				$tagsmart_id= $row['tagsmart_id'];

				//echo "INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1')";

				$result= pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1')"  );

				if($result)
				{
					$new_rfid_quantity  = (int)$rfid_quantity+1;

					//update customer_stock_data_table_name table
					$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET uploaded_date='".date("Y-m-d")."' , rfid_quantity='$new_rfid_quantity'  WHERE stock_id = '$stock_id' " );
					/*if($result7)
					{
						$response = array('EPC' => $EPC);		

						$responses[] = $response;
					}*/
					
				}
			}
		}
		else
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

						$result4 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1')"  );

						if($result4)
						{
							$new_rfid_quantity  = (int)$rfid_quantity+1;

							//update customer_stock_data_table_name table
							$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET uploaded_date='".date("Y-m-d")."' , rfid_quantity='$new_rfid_quantity'  WHERE stock_id = '$stock_id' " );
							/*if($result7)
							{
								$response = array('EPC' => $EPC);		

								$responses[] = $response;
							}*/
							
						}				

					}
				}
				
			}

		}
	}

	//adding  stray barcode
	foreach($stray_json_epc_data as $item2) {
	    $EPC= $item2;
	    $ean = convert_EPC_to_barcode($EPC);

		if(strlen($ean) ==14 && strcmp("dadar_stock_data" ,$stock_table_name ) == 0) //checking only for dadar fbb store only
	    {
	    	$tx = $ean;
	    	$ean = substr($tx, 0, 13); // get first 13th character
	    }

	    $result9= pg_query($db,"Select barcode from ".$stock_table_name." WHERE barcode='".$ean ."'");
	    if(pg_num_rows($result9) <= 0)
		{
			 $result2 = pg_query($db,"INSERT INTO  stray_epc (stray_id, epc, barcode, stray_date, stray_time) VALUES(DEFAULT,'$EPC','$ean','$stock_date','$stock_time')"  );
		}
	   
	}

	$missingEPCResp = array();
 	$mainResp = array();

	$result11= pg_query($db,"SELECT  string_agg(barcode, ', ') AS barcode_list ,string_agg(tagsmart_stock_details.epc, ', ') AS EPC_list,count(epc_location_date_time.*) as epc_total, epc_location_date_time.stock_date from tagsmart_stock_details  INNER JOIN epc_location_date_time ON epc_location_date_time.tagsmart_id = tagsmart_stock_details.tagsmart_id INNER JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id Where tagsmart_stock_details.customer_id='".$customer_id ."' GROUP BY stock_date Order BY stock_date desc LIMIT 2");
    if(pg_num_rows($result11) == 2)
	{
		$epcList1 = "";
		$epcList2 = "";
		$barcodeList1 = "";
		$barcodeList2 ="";
		$epcCount1 = 0;
		$epcCount2 =0;
		$count=0;
		while($row= pg_fetch_assoc($result11))   
		{
			if($count==0)
			{
				$epcList1 = $row['epc_list'];
				$barcodeList1 = $row['barcode_list'];
				$epcCount1 = $row['epc_total'];
			}
			else if($count==1)
			{
				$epcList2 = $row['epc_list'];
				$barcodeList2 = $row['barcode_list'];
				$epcCount2 = $row['epc_total'];
			}

			$count++;
		}

		if($epcCount1 < $epcCount2 )
		{
			$currentEPCList = explode (",", $epcList1);
			$currentEPCList = array_map('trim', $currentEPCList);

			$previousEPCList = explode (",", $epcList2);
			$previousEPCList = array_map('trim', $previousEPCList);

			$currentBarcodeList = explode (",", $barcodeList1);
			$currentBarcodeList = array_map('trim', $currentBarcodeList);

			$previousBarcodeList = explode (",", $barcodeList2);
			$previousBarcodeList = array_map('trim', $previousBarcodeList);

			for($e = 0 ; $e < sizeof($previousEPCList); $e++)
			{
				if (!in_array(trim($previousEPCList[$e]),$currentEPCList))
				{
					//print_r($currentEPCList);
				//echo "<br>previous: ".$previousEPCList[$e]."<br>";

					$response= array('EPC' => $previousEPCList[$e], 'EAN' => $previousBarcodeList[$e]);
					$missingEPCResp[]= $response;
				}
			}

		}
	}

	$mainResp['epcList'] = $responses;
	$mainResp['missingEPCList'] = $missingEPCResp;
	//{"epcList":[],"missingEPCList":[]}

	echo json_encode($mainResp); 	

		

	
?>