<?php

    ini_set('max_execution_time', 0);
    ob_start();
    session_start(); 
    header('Cache-control: private');
    include("api/connect.php");




    $row = 1;
if (($handle = fopen("barcode.csv", "r")) !== FALSE) {
  while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
    $num = count($data);
    
    $row++;
    for ($c=0; $c < $num; $c++) {

        $barcode = $data[$c];
        $result = pg_query($db,"SELECT  icode, stock_qty_sum  FROM dhulagarh_product_data WHERE  barcode='".$barcode."'");

	$rows = pg_num_rows($result);
	$responses = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{	

			/*echo $row['customer_id'];
			echo $row['customer_name'];
			echo $row['customer_stock_data_table_name'];	
			echo $row['customer_product_data_table_name'];*/
			//$responses[] = $row;
			echo $row['icode']." ".$row['stock_qty_sum']."<br>";
		}
		
				
	}
	//echo json_encode($responses);
    }
  }
  fclose($handle);
}





?>