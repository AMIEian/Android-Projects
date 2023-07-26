<?php
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	
	$user_email = $_POST['user_email'];
	$password = base64_encode ($_POST['password']);
	

	$user_email = addslashes($user_email);
	$password = addslashes($password);
	
	$user_email= stripslashes($user_email);
	$password = stripslashes($password);
	
	
	$user_email = pg_escape_string($user_email);
	$password = pg_escape_string($password);
	
	$responses = array();
	$user_id =0;
	$store_id=0;
	$customer_id=0;

	$customer_name="";
	$customer_stock_data_table_name="";
	$customer_product_data_table_name="";

	$store_number="";
	$address="";
	$session_id ="0";


	//Get data from customer_login_details table
	$result= pg_query($db,"SELECT user_id, store_id, customer_id  from  customer_login_details WHERE email = '$user_email' AND password = '$password'  LIMIT 1 " );

    $rows = pg_num_rows($result);
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{		
			$user_id = $row['user_id'];
			$store_id = $row['store_id'];
			$customer_id = $row['customer_id'];
		}

		//Get data from customer_master table
		$result2= pg_query($db,"SELECT customer_name, customer_stock_data_table_name, customer_product_data_table_name  from  customer_master WHERE customer_id = '$customer_id' LIMIT 1 " );
		if(pg_num_rows($result2) > 0)
		{
			while($row2= pg_fetch_assoc($result2))   
			{		
				$customer_name = $row2['customer_name'];
				$customer_stock_data_table_name = $row2['customer_stock_data_table_name'];
				$customer_product_data_table_name = $row2['customer_product_data_table_name'];
			}
		}

		//Get data from customer_store_master table
		$result3= pg_query($db,"SELECT  store_number,address, session_id  from  customer_store_master WHERE customer_id = '$customer_id' AND store_id = '$store_id' LIMIT 1 " );
		if(pg_num_rows($result3) > 0)
		{
			while($row3= pg_fetch_assoc($result3))   
			{		
				$store_number = $row3['store_number'];
				$address = $row3['address'];
				$session_id = $row3['session_id'];
				
			}
		}		
				
	}


	$responses['user_id'] =$user_id;
	$responses['store_id'] =$store_id;

	$responses['customer_id'] =$customer_id;
	$responses['customer_name'] =$customer_name;
	$responses['customer_stock_data_table_name'] =$customer_stock_data_table_name;
	$responses['customer_product_data_table_name'] =$customer_product_data_table_name;
	$responses['store_number'] =$store_number;
	$responses['address'] =$address;
	$responses['session_id'] =$session_id;


	echo json_encode($responses);
	
	
	
?>