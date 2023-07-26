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

	
	$response = array();
	$responses = array();
	
	$barcode = $_POST['barcode']; 
	$epc = $_POST['epc']; 

	$location_id = $_POST['location_id'];
    $sub_location_id = $_POST['sub_location_id'];
    $stock_date = $_POST['stock_date'];
    $stock_time = $_POST['stock_date']." ".$_POST['stock_time'];
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];

    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];

   

    

	 
		$store_id = 0;
	    $stock_id = "0"; 
	    $rfid_quantity="0";

	    //Get data from customer_stock_data_table_name table
		$reut= pg_query($db,"SELECT rfid_quantity, stock_id ,store_id  from  ".$stock_table_name." WHERE barcode = '$barcode'  LIMIT 1 " );
		if(pg_num_rows($reut) > 0)
		{
			while($row= pg_fetch_assoc($reut))   
			{		
				
				$rfid_quantity = $row['rfid_quantity'];
				$stock_id = $row['stock_id'];
				$store_id = $row['store_id'];

			}
		}

	    $result6= pg_query($db,"SELECT  *  from   tagsmart_stock_details WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 " );

		if(pg_num_rows($result6) > 0)
		{
			while($row= pg_fetch_assoc($result6))   
			{
				$tagsmart_id= $row['tagsmart_id'];

				//echo "INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1')";
				

				$result= pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1' ,'$user_id' ,'$session_id','1','$customer_id','$store_id' )"  );

				if($result)
				{
					$new_rfid_quantity  = (int)$rfid_quantity+1;

					//update customer_stock_data_table_name table
					$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET uploaded_date='".date("Y-m-d")."' , rfid_quantity='$new_rfid_quantity'  WHERE stock_id = '$stock_id' " );
					if($result7)
					{
						$response = array('status' => "1");
					}
					else
					{
						$response = array('status' => "0");
					}
					
				}
				else
				{
					$response = array('status' => "0");
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

						$result4= pg_query($db,"INSERT INTO  epc_location_date_time (sldt_id, tagsmart_id, location_id, sub_location_id,stock_date, stock_time, is_sold, is_sse, user_id, session_id, is_scan_finished, customer_id, store_id,task_id) VALUES(DEFAULT,'$tagsmart_id','$location_id','$sub_location_id','$stock_date','$stock_time','0','1' ,'$user_id' ,'$session_id','1','$customer_id','$store_id','1' )"  );

						if($result4)
						{
							$new_rfid_quantity  = (int)$rfid_quantity+1;

							//update customer_stock_data_table_name table
							$result7= pg_query($db,"UPDATE  ".$stock_table_name." SET uploaded_date='".date("Y-m-d")."' , rfid_quantity='$new_rfid_quantity'  WHERE stock_id = '$stock_id' " );
							if($result7)
							{
								$response = array('status' => "1");
							}
							else
							{
								$response = array('status' => "0");
							}
							
						}
						else
						{
							$response = array('status' => "0");
						}				

					}
				}
				else
				{
					$response = array('status' => "0");
				}
				
			}
			else
			{
				$response = array('status' => "0");
			}

		}
	

	//{"epcList":[],"missingEPCList":[]}

	echo json_encode($response); 	

	
?>