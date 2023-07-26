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
	$testStr ='["8907515624917","8907733453115","8907733452941","8907733453177","8907733453153","8907733453146","8907515619142","8907733453122","8907515620582","8907733452989","8907515617919","8907515619159","8907733453139","8907733453023","8907733452996","8907515619166","8907733452958","8907733452910","8907733452934","8907733452965","8905011724667","8905011617778","8905011724834","8905011968528","8905011723585","8907403408933","8905011724643","8907515740242","8907733044368","8905011251712","8905011251743","8907733044344","8905011827399","8907733044412","8907733046676","8905011617891","8905011723509","8905011620181","8905011724827","8907515740228","8907403408704","8905011620228","8905011724773","8905011617754","8905011724803","8905011618881","8905011108917","8905011724933","8907733044429","8907733044399","8905011723516","8905011827351","8907403408827","8905011724780","8905011620280","8905011251699","8905011724629","8907403408681","8905011620204","8907515740181","8905011728221","8905011724940","8907515740280","8907515159129","8905011723561","8905011620174","8905011619819","8907733083121","8905011620235","8907733052721","8905011617907","8905011617846","8905011724735","8905011251576","8907733044313","8907515740167","8907515740297","8907733052691","8905011251552","8905011108924","8907733251650","8905011619826","8907733044382","8907733046683","8907515740174","8905011723189","8905011723219","8905011251781","8907733044405","8905011251729","8905011620211","8907733044191","8905011618508","8907733090785","8907515740266","8905011617709","8905011724711","8905011251583","8905011619802","8905011723165","8905011724957","8905011725015","8905011251569","8907733046669","8907403408841","8905011724698","8905011724858","8905011724766","8905011251767","8905011620303","8907515624870","8905011619758","8905011617457","8905011725022","8907733046645","8905011827382","8907733052653","8905011617761","8905011617921","8905011724681","8905011618898","8905011728276","8905011499886","8907733039098","8905011618874","8905011723226","8905011251774","8907515740198","8905011617747","8905011620198","8905011617808","8907733046652","8905011724841","8905011724797","8905011620297","8907733044436","8907403408674","8905011617839","8905011617914","8905011617730","8905011617822","8905011724612","8907403408964","8905011968511","8907733044290","8907515740273","8907733255498","8905011618515","8907733251926","8905011251736","8905011499596","8905011724926","8907733255504","8905011725046","8905011617693","8907515624894","8905011827375","8907733090730","8907733092277","8905011251705","8907515740235","8907403408711","8905011968504","8905011617570","8907515740204","8905011619840","8905011619772","8907733720538","8907733720330","8905011633815","8905011610441","8905011633563","8905011594505","8907733765188","8905011310136","8905011614258","8905011637431","8905011633457","8905011616719","8907733762545","8907733720354","8905011616672","8905011981114","8905011616528","8905011637455","8907733720682","8905011616559","8905011633808","8905011990017","8905011955825","8905011955795","8907733721054","8905011610304","8905011637462","8905011616542","8905011633853","8905011633785","8907733734146","8905011616511","8905011310099","8905011612742","8905011616641","8905011950479","8905011633877","8905011634126","8907733762507","8905011981398","8905011950462","8905011637509","8905011637424","8905011633822","8905011950233","8905011594284","8905011616726","8905011637493","8907733720378","8905011773665","8905011950486","8905011955788","8905011292326","8905011633587","8907733720491","8905011981091","8905011305057","8905011616740","8905011634119","8905011292357","8905011633839","8905011594253","8905011981404","8905011637417","8905011633549","8905011853329","8905011731931","8905011796015","8905011969631","8905011859833","8905011250395","8905011732488","8905011744146","8905011967569","8905011769071","8905011731634","8905011734208","8905011852544","8905011730842","8905011852537","8905011970286","8905011796305","89050117963"]'; 

	$json_barcode = json_decode($testStr, true); 
    $stock_table_name = 'dadar_stock_data';
    $customer_id = 5;
    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];*/
	
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


   // echo "select c.barcode, COALESCE(COUNT(epc_location_date_time.tagsmart_id),0)  as rfid_quantity  from   dadar_stock_data c  join ( values ".$selectedBarcodes.") as x (barcode, ordering) on c.barcode = x.barcode LEFT JOIN tagsmart_stock_details ON c.stock_id = tagsmart_stock_details. stock_id LEFT JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id  WHERE c.barcode IN (".$selectedBarcodes2 .") GROUP BY c.barcode, x.ordering order by x.ordering"; 


	$reut= pg_query($db,"select c.barcode, c.rfid_quantity   from   ui_update c join ( values ".$selectedBarcodes.") as x (barcode, ordering) on c.barcode = x.barcode WHERE session_id='$session_id' AND customer_id='$customer_id' order by x.ordering " );
	$rfid_qty = 0;
	//$reut= pg_query($db,"SELECT barcode, rfid_quantity   from  ".$stock_table_name);
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