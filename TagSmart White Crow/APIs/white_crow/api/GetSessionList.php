<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start();
	header('Cache-control: private');
	include("connect.php");



	$customer_id = $_POST['customer_id'];
	$store_id = $_POST['store_id'];
	$user_id = $_POST['user_id'];


/*	$customer_id = 5;
	$store_id=8;
	$user_id=10;*/

	$product_responses = array();
	$stock_responses = array();
	$session_id="0";
	$product_table_sqlite_query="";
	$responses = array();

	$dt = date("Y-m-d");


	//Get session master copy
	$result_product_data = pg_query($db,"select session_details.session_id as id , customer_login_details.email, DATE(session_details.session_timestamp)as session_date from session_details INNER JOIN customer_login_details ON customer_login_details.user_id = session_details.user_id where session_details.customer_id ='$customer_id' AND session_details.store_id='$store_id' AND session_details.task_id='1' AND DATE(session_details.session_timestamp) ='$dt' AND sd_id IN (SELECT MAX(sd_id) AS sd_id FROM session_details WHERE   session_details.is_live='1'   Group BY user_id )" );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))
		{

			$stock_responses[] = $row;
		}

	}
	echo json_encode($stock_responses);

	/************** Response ******************************

[{"id":"256","email":"testing@tagid.co.in","session_date":"2021-06-15"}]


	***********************/



?>
