<?php
	ini_set('max_execution_time', 0);
	ob_start();
	header('Cache-control: private');
	include("connect.php");

	
	$customer_id = $_POST['customer_id'];
    $user_id = $_POST['user_id'];
    $store_id = $_POST['store_id'];
	$Barcode =$_POST['barcode'];
	$EPC =$_POST['epc'];

	/* //Testing
	$customer_id = 7;
	$store_id = 7;
	$Barcode ='VDC39525';
	$EPC  = 'E28011702000015EDF8209EF';
	*************************/



	$currentDate =date("Y-m-d H:i:s");
	$responses = array();
      
     


        
	// check if value is already present. If not then 
	$result6= pg_query($db,"SELECT epc from  epc_barcode_master_white_crow WHERE LOWER(epc) = LOWER('$EPC') AND barcode = '$Barcode'  LIMIT 1 ");
	if(pg_num_rows($result6) > 0)
	{
		$insert_result =pg_query($db,"update epc_barcode_master_white_crow set isverified='YES' where customer_id='$customer_id' and store_id='$store_id' and barcode='$Barcode' and epc='$EPC' ");
		$cmdtuples = pg_affected_rows($insert_result);
		if($cmdtuples > 0)
		{
			$response = array('status' => '1','msg' => 'Updated successfully');
			echo json_encode($response);	
			exit;	
		}
		else
		{
			$response = array('status' => '0','msg' => 'Fail to updated the record');
			echo json_encode($response);	
			exit;	
		}

	}
	else
	{
		$response = array('status' => '2','msg' => 'Please contact project in charge for help.');
		echo json_encode($response);	
		exit;	
	}

?>