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

       // print_r("I ".$itemref."<br>") ;

        $itemref2 = substr($itemref,0,20);
        //System.out.println("I2 "+itemref2) ;

        $deciItemRef = bindec($itemref2);
        //print_r("I ".$deciItemRef."<br>") ;

        $indicator="";

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
        else if(strlen($deciItemRef ) >5)
        {
        	$str_val = strval($deciItemRef);
        	$indicator= substr($str_val,0,1);
        	$deciItemRef= substr($str_val,1,strlen($deciItemRef));
        }




       // $serial_number = substr($actual_epc,44,strlen($actual_epc));


        $barcode = $indicator.$deciCompanyPrefix .$deciItemRef;

       // echo "Decoded barcode    ".$barcode.get_checksum_digit($barcode) ;



        $barcodeString = $barcode.get_checksum_digit($barcode);

        //FBB 14 digit barcode correction
        if(strlen($barcodeString) == 14)
        {
        	$barcodeString = FBB_14_digit_barcode_correction($barcodeString);
        }

        return $barcodeString;
	}

	function FBB_14_digit_barcode_correction($barcode)
	{
		$correction_barcode = substr($barcode, 1, 7).$barcode[0].substr($barcode, 8, 4).$barcode[12];

		return $correction_barcode;
	}

	function get_checksum_digit($barcode)
	{
		$evenSum=0;
		$oddSum = 0 ;
		if(strlen($barcode) ==12)
		{
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
		else if(strlen($barcode) ==13)
		{
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

			$evenSum = 3* $evenSum;

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


	}

try
{

	$response = array();
	$responses = array();

	/*customer_id=5&user_id=6&session_id=94&json_barcode=["8907733327270","8907733327287"]&json_epc=["303A667B83B4FC4DF84C3AFC","30361F84CC001C80000FA855","3014000000000000000FA861","30361F84CC000380000FA858","303A667B83B4FC4DF84C3AFD","3014000000000000000FA862","30361F84CC0000C0000FA842","3014000000000000000FA854"]&location_id=3&sub_location_id=3&stock_date=2021-02-02&stock_time=14:01:14&stock_table_name=dadar_stock_data&is_scan_finish=0*/

	/*$json_barcode = json_decode($_POST['json_barcode'], true);
	$json_epc = json_decode($_POST['json_epc'], true);

	$location_id = $_POST['location_id'];
    $sub_location_id = $_POST['sub_location_id'];
    $stock_date = $_POST['stock_date'];
    $stock_time = $_POST['stock_date']." ".$_POST['stock_time'];
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];
    $store_id = $_POST['store_id'];

    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];
    $is_scan_finish = $_POST['is_scan_finish'];*/

		$stock_table_name = 'dadar_stock_data';
    $customer_id = '5';
    $store_id = '8';

		echo "SELECT  string_agg(barcode, ', ') AS barcode_list ,string_agg(tagsmart_stock_details.epc, ', ') AS epc_list,count(epc_location_date_time.*) as epc_total from tagsmart_stock_details  INNER JOIN epc_location_date_time ON epc_location_date_time.tagsmart_id = tagsmart_stock_details.tagsmart_id INNER JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id Where tagsmart_stock_details.customer_id='".$customer_id ."' AND epc_location_date_time.store_id='$store_id'  AND task_id='1' GROUP BY session_id Order BY session_id desc LIMIT 2<br>";
		//Get missing EPC
		$result11= pg_query($db,"SELECT  string_agg(barcode, ', ') AS barcode_list ,string_agg(tagsmart_stock_details.epc, ', ') AS epc_list,count(epc_location_date_time.*) as epc_total from tagsmart_stock_details  INNER JOIN epc_location_date_time ON epc_location_date_time.tagsmart_id = tagsmart_stock_details.tagsmart_id INNER JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id Where tagsmart_stock_details.customer_id='".$customer_id ."' AND epc_location_date_time.store_id='$store_id'  AND task_id='1' GROUP BY session_id Order BY session_id desc LIMIT 2");
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
						echo "Missing EPC in previous session stock take: ".$previousEPCList[$e]."<br>";
						/*$bar = $previousBarcodeList[$e];
						if (in_array($bar, $json_barcode)) //if epc is found in selected barcode array
						{
								$response= array('EPC' => $previousEPCList[$e], 'EAN' => $previousBarcodeList[$e]);
								$itemCode = $previousEPCList[$e];

								//Add to missing_items_details table
								 $result12 = pg_query($db,"INSERT INTO  missing_items_details (missing_item_id, store_id, customer_id, barcode, epc, date_timestamp, session_id) VALUES(DEFAULT,'$store_id','$customer_id','$bar','$itemCode', '$stock_time','$session_id')"  );
						}*/

					}
				}


		}
	exit;
}
catch(Exception $e) {

  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);
	exit;
}




?>
