<?php
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

	convert_EPC_to_barcode("30361F84CC00040000000002");

	
?>