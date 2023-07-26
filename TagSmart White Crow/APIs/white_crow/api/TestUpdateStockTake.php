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

	$result7= pg_query($db,"Select epc from tagsmart_stock_details INNER JOIN  epc_location_date_time on epc_location_date_time.tagsmart_id= tagsmart_stock_details.tagsmart_id  where stock_date='2021-01-12' " );
	if($result7)
	{
				
		while($row7= pg_fetch_assoc($result7))   
		{
			$response = array('EPC' => $row7['epc']);
			$responses[] = $response;
		}
		
	}

	

	$missingEPCResp = array();
 	$mainResp = array();

	$result11= pg_query($db,"SELECT  string_agg(barcode, ', ') AS barcode_list ,string_agg(tagsmart_stock_details.epc, ', ') AS EPC_list,count(epc_location_date_time.*) as epc_total, epc_location_date_time.stock_date from tagsmart_stock_details  INNER JOIN epc_location_date_time ON epc_location_date_time.tagsmart_id = tagsmart_stock_details.tagsmart_id INNER JOIN dadar_stock_data ON dadar_stock_data.stock_id = tagsmart_stock_details.stock_id Where tagsmart_stock_details.customer_id='5' GROUP BY stock_date Order BY stock_date desc LIMIT 2");
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