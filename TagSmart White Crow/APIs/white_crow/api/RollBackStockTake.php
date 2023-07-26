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

        return $barcodeString;
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
	
	
	$responses = array();

	/*json_epc_data=[{"barcode":"8905011000150","category":"OTHER DUPATTA","epc":"30361F84CC0003C000000001","isPresent":"1","stock_id":"112"},{"barcode":"8905011000143","category":"OTHER DUPATTA","epc":"30361F84CC00038000000002","isPresent":"1","stock_id":"111"}]&stray_json_epc_data=["30361F84CC0003C000000001","30361F84D82AD94000000022","30361FAF54275D40000FAABA","30361F84CC00038000000002","303556022843A3C00000000D"]&location_id=3&sub_location_id=4&stock_date=2020-11-05&stock_time=08:09:32&stock_table_name=test_fbb_stock_data&customer_id=3*/
	
	$session_id = $_POST['session_id']; 
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];
    $user_id = $_POST['user_id'];

	$result= pg_query($db," Delete from epc_location_date_time where user_id='$user_id' AND session_id='$session_id'  AND customer_id='$customer_id' " );


	if(!$result)
	{
		$response = array('status' => '0','msg' => 'Something went wrong during deleting epc records.');
		echo json_encode($response);	
		exit;											
	}
	else
	{
		$result2= pg_query($db,"UPDATE ui_update  SET rfid_quantity= 0 where user_id='$user_id' AND session_id='$session_id'  AND customer_id='$customer_id' " );

		if(!$result2)
		{
			$response = array('status' => '0','msg' => 'Something went wrong during rfid qty UI update.');
			echo json_encode($response);	
			exit;											
		}
	}



	$response = array('status' => '1','msg' => "Stock take aborted sucessfully");
	echo json_encode($response);	
	exit;
}
catch(Exception $e) {

	$response = array('status' => '0','msg' => $e);
	echo json_encode($response);	
	exit;
}	

		

	
?>