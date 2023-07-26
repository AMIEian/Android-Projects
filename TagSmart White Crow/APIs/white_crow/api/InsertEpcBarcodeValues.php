<?php
	ini_set('max_execution_time', 0);
	ob_start();
	header('Cache-control: private');
	include("connect.php");

	
	$customer_id = $_REQUEST['customer_id'];
        $user_id = $_REQUEST['user_id'];
        $store_id = $_REQUEST['store_id'];
	$Barcode =$_REQUEST['barcode'];
	$EPC =$_REQUEST['epc'];
	$currentDate =date("Y-m-d h:i:s");
	$responses = array();
      
        /*
	$customer_id = 13;
        $user_id = 13;
        $store_id = 13;
	$Barcode ='1234545';
	$EPC ='erecre';
	$currentDate =date("Y-m-d H:i:s");
        */


        
	// check if value is already present. If not then 
	$result6= pg_query($db,"SELECT  epc  from  epc_barcode_master_white_crow WHERE LOWER(epc) = LOWER('$EPC')   LIMIT 1 ");
	if(pg_num_rows($result6) == 0)
	{
		$insert_result =pg_query($db,"INSERT INTO  epc_barcode_master_white_crow(ebid,barcode,epc,date_printed,customer_id,store_id,user_id) VALUES(DEFAULT,'$Barcode','$EPC','$currentDate','$customer_id','$store_id','$user_id')");
		if($insert_result>0)
		{
		$responses['output'] =1;	
		}

	}else{
		$responses['output'] =2;	
	}


	//return json_encode($responses);
	echo  json_encode($responses);
?>