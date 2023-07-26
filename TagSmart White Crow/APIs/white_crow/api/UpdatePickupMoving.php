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
	$json_epc = json_decode($_POST['json_epc'], true); 

	$location_id = $_POST['location_id'];
    $sub_location_id = $_POST['sub_location_id'];
    $stock_date = $_POST['stock_date'];
    $stock_time = $_POST['stock_date']." ".$_POST['stock_time'];
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];
    $store_id = $_POST['store_id'];

    $user_id = $_POST['user_id'];
    $session_id = -1;
    $is_scan_finish = 1;
    



    foreach($json_epc as $item2) {	

	    $resx= pg_query($db,"SELECT  tagsmart_id from tagsmart_stock_details  WHERE LOWER(epc) = LOWER('".$item2."') LIMIT 1" ); 
	    $ean = convert_EPC_to_barcode($item2);
	    $barcode = $ean;
	    $stock_id = 0;
	    $rfid_quantity="0";

	    //Get data from customer_stock_data_table_name table
		$reut= pg_query($db,"SELECT stock_id, store_id  from  ".$stock_table_name." WHERE barcode = '$barcode' AND store_id='$store_id'  LIMIT 1 " );
		if(pg_num_rows($reut) > 0)
		{
			while($row= pg_fetch_assoc($reut))   
			{				
				$stock_id = $row['stock_id'];

			}

		    if(pg_num_rows($resx) == 0) {		    
			    $EPC= $item2;
			    

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

							$result4 = pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id, task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','1','1' ,'$user_id' ,'$session_id','$is_scan_finish','$customer_id','$store_id','5' )"   );

							if(!$result4)
							{
								$response = array('status' => '0','msg' => 'Error while inserting EPC table');
								echo json_encode($response);	
								exit;
								
							}				

						}
					}
					
				}
				else
				{
					$response = array('status' => '0','msg' => 'Error while inserting tagsmart table');
					echo json_encode($response);	
					exit;
				}			
			}
			else
			{
				while($row= pg_fetch_assoc($resx))   
				{
					$tagsmart_id= $row['tagsmart_id'];
					$sub_location_id_old = '0';
					$res2= pg_query($db,"SELECT  epc_location_date_time.location_id, epc_location_date_time.sub_location_id from epc_location_date_time   WHERE tagsmart_id='".$tagsmart_id."' ORDER BY sldt_id DESC LIMIT 1" ); 

					while($row2 = pg_fetch_assoc($res2))   
					{
						$sub_location_id_old = $row2['sub_location_id'];
					}
					
					if($sub_location_id_old != $sub_location_id)
					{
						$result= pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id, task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','1','1' ,'$user_id' ,'$session_id','$is_scan_finish','$customer_id','$store_id','5' )"  );

						if(!$result)
						{
							
							$response = array('status' => '0','msg' => 'Error while updating EPC table');
							echo json_encode($response);	
							exit;
							
						}
					}
					else // test clause
					{
						$result2 = pg_query($db,"INSERT INTO  stray_epc (stray_id, epc, barcode, stray_date, stray_time, task_id) VALUES(DEFAULT,'$item2','$ean','$stock_date','$stock_time','5')"  );
					}

					
				}
			}

		}
		else // test clause
		{

			$result2 = pg_query($db,"INSERT INTO  stray_epc (stray_id, epc, barcode, stray_date, stray_time, task_id) VALUES(DEFAULT,'$item2','$ean','$stock_date','$stock_time','5')"  );
			
			
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