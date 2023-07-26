<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start();
	header('Cache-control: private');
	include("connect.php");

try
{

	$missingEPCResp = array();
	$mainResp = array();
	$summaryArray = array();

  $stock_table_name = $_POST['stock_table_name'];
    $product_table_name = $_POST['product_table_name'];
    $customer_id = $_POST['customer_id'];

    $user_id = $_POST['user_id'];
    $dispatch_order = $_POST['dispatch_order'];
    $store_id = $_POST['store_id'];

  	/*$stock_table_name = 'dadar_stock_data';
    $product_table_name = 'dadar_product_data';
    $customer_id = 5;

    $user_id = 6;
    $dispatch_order = 102;
    $store_id = 8;*/


    $category="barcode";

		//update Session details update
		$result_session = pg_query($db,"Update  session_details Set is_live='0'  WHERE user_id = '$user_id' AND task_id='2'"  );

    //Get category table data
	$result_product_data = pg_query($db,"SELECT  category_column_name FROM customer_category_master WHERE customer_id = $customer_id Order BY category_level ASC"  );
	$str="";
	$count=0;

    $datasize = pg_num_rows($result_product_data);

	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))
		{

			 if(($datasize -1 ) == $count)
            {
                $str.= $row['category_column_name'];
            }
            else
            {
                $str.=$row['category_column_name'].",";
            }
            $category_responses[] = $row;
            $count++;
		}


	}

	/*echo "select DISTINCT ".$str.", ".$product_table_name.".barcode , CONCAT  (inward_data.quantity , ';', inward_data.rfid_quantity) AS total_qty_scan_qty  from inward_data Inner JOIN ".$product_table_name." ON inward_data.barcode = ".$product_table_name.". barcode where dispatch_order_id='$dispatch_order' AND store_id='$store_id'  <br>" ;*/

	$filename = __DIR__  ."/".$product_table_name ."_inward_".time(). ".csv";
     //echo $query_str;


	$fp = fopen($filename, 'w');

	//echo "select DISTINCT ".$str.", ".$product_table_name.".barcode , CONCAT  (inward_data.quantity , ';', inward_data.rfid_quantity) AS total_qty_scan_qty  from inward_data Inner JOIN ".$product_table_name." ON inward_data.barcode = ".$product_table_name.". barcode where dispatch_order_id='$dispatch_order' AND store_id='$store_id' ";

	//get category rfid and qty details
	$result= pg_query($db,"select DISTINCT ".$str.", ".$product_table_name.".barcode , CONCAT  (inward_data.quantity , ';', inward_data.rfid_quantity) AS total_qty_scan_qty  from inward_data Inner JOIN ".$product_table_name." ON inward_data.barcode = ".$product_table_name.". barcode where dispatch_order_id='$dispatch_order' AND store_id='$store_id' " );
	if(pg_num_rows($result) > 0)
	{
		while($row2= pg_fetch_assoc($result))
		{

			 fputcsv($fp, $row2);

		}
	}
	fclose($fp);




    $command = escapeshellcmd('python2 ../tree_product.py '.$filename);
    $output = shell_exec($command);

	echo $output;


}
catch(Exception $e) {

  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);
	exit;
}




?>
