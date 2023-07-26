<?php
	ob_start();
	//session_start(); 
	header('Cache-control: private');


function GetStoreByEpc($epc)
{
	include("connect.php");

	//$result = pg_query($db,"select distinct a.barcode from epc_barcode_master_white_crow a join ethnicity_stock_data b on a.barcode=b.barcode where a.epc='".$epc."'");
	$result = pg_query($db,"select distinct barcode from epc_barcode_master_white_crow where epc='".$epc."'");

	$rows = pg_num_rows($result);
	$responses = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{	

			/*echo $row['customer_id'];
			//echo $row['customer_name'];
			//echo $row['customer_stock_data_table_name'];	
			//echo $row['customer_product_data_table_name'];*/
			$responses[] = $row;
		}
		
				
	}else{

	
	}
	//echo json_encode($responses[0]);
	return json_encode($responses[0]);
}
	
?>