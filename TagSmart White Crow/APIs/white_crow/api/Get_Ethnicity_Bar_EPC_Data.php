<?php
	ob_start();
	//session_start(); 
	header('Cache-control: private');
	include("connect.php");


	$json_barcode = json_decode($_POST['json_barcode'], true);
 
    $temp="";
    foreach($json_barcode as $ean) {
          
         $temp .= "'". $ean."',";

    } 

    $selectedBarcodes = rtrim($temp, ",");

	$result = pg_query($db,"select  barcode,epc from epc_barcode_master_white_crow WHERE barcode  IN ( ".$selectedBarcodes.")");

	$rows = pg_num_rows($result);
	$responses = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{	

			
			$responses[] = $row;
		}
		
				
	}
	echo json_encode($responses);
	
?>