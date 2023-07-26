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

	/*//test simulator
	$testStr ='["8905014406126","8905011396666","8905011496090","8905011919537","8907515689176","8905011907886","8905011993674","8905011284925","8905011659365","8905011286455","8905011338635","8905011725787","8905011390459","8905011496847","8905011699309","8905011862833","8905011696841","8905011612391","8907403276716","8905011311324","8905011944096","8905011939368","8905011339205","8905011811060","8907733160143","8907515684485","8905011346333","8905011611707","8905011284710","8905011932161","8905011116240","8905011650515","8905011739944","8905011648062","8905011283904","8905011651512","8905011648024","8905011874478","8905011768401","8907403203927","8905011650805","8905011293781","8905011385509","8905011002895","8905011756002","8905011627593","8905011875543","8905011743231","8905011301998","8905011299318","8905011294597","8905011294153","8907733326556","8905011385493","8905011931898","8905011300205","8905014704765","8905011298274","8905011611493","8905011610687","8905011342090","8905011353553","8905011496649","8905014587696","8905011931751","8905011647584","8905011300090","8905011650423","8905011764625","8905011948469","8905011497776","8905014520761","8905011301981","8905011659624","8905014704628","8905011349143","8905011494331","8905011251132","8905011949145","8905011277040","8905011297581","8905011755968","8905011647591","8905011325734","8905011647720","8905011290575","8905011897736","8907515633872","8905011647904","8905011396659","8905011318033","8905011759652","8905011650508","8905011700982","8905011729082","8905011788300","8905011906339","8905011661696","8905011874904"]'; 

	$json_barcode = json_decode($testStr, true); 
    $stock_table_name = 'dadar_stock_data';
    $customer_id = 5;
    $user_id = 6;
    $session_id = 75;*/
	
	$json_barcode = json_decode($_POST['json_barcode'], true); 
    $stock_table_name = $_POST['stock_table_name'];
    $customer_id = $_POST['customer_id'];
    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];


    $temp="";
    $temp2="";
    $counter=1;
    foreach($json_barcode as $ean) {
        
         $temp .= "('". $ean."',".$counter."),";
         $temp2 .= "'". $ean."',";
         $counter++;
    }

    $selectedBarcodes = rtrim($temp, ",");
    $selectedBarcodes2 = rtrim($temp2, ",");


   /*echo "select c.barcode, c.rfid_quantity   from   ui_update c INNER  join ( values ".$selectedBarcodes.") as x (barcode, ordering) on c.barcode = x.barcode WHERE session_id='$session_id' AND customer_id='$customer_id' order by x.ordering "; */


	$reut= pg_query($db,"select c.barcode, c.rfid_quantity   from   ui_update c INNER join ( values ".$selectedBarcodes.") as x (barcode, ordering) on c.barcode = x.barcode WHERE session_id='$session_id' AND customer_id='$customer_id' order by x.ordering " );
	$rfid_qty = 0; 
	if(pg_num_rows($reut) > 0)
	{
		while($row= pg_fetch_assoc($reut))   
		{		
			
			$responses[] = $row;
			$rfid_qty = $rfid_qty +(int)$row['rfid_quantity'] ;

		}
	}

	$mainResp = array();
	$mainResp['scan_total_qty'] = $rfid_qty;
	$mainResp['scan_values'] = $responses;

	echo  json_encode($mainResp);
	exit;
}
catch(Exception $e) {

	echo json_encode($mainResp);	
	exit;
}	

		

	
?>