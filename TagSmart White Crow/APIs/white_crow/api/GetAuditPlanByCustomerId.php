<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	$customer_id = $_POST['customer_id'];
	$store_id = $_POST['store_id'];
	$customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";
	//$customer_id =3;


	//Get category table data
    $result_product_data = pg_query($db,"SELECT  * FROM customer_category_master WHERE customer_id = $customer_id  " );
    
    $maxlevel = pg_num_rows($result_product_data) +1; // 1 is added for barcode

    //Get session id
	$result_product_data = pg_query($db,"select session_id  from customer_store_master where customer_id ='$customer_id' " );

    $rows = pg_num_rows($result_product_data);
	if(pg_num_rows($result_product_data) > 0)
	{
		while($row= pg_fetch_assoc($result_product_data))   
		{	
			
			$session_id = $row['session_id'];
		}
				
	}

	$new_session_id = (int)$session_id+1;

	//Update  session id
	$result_product_data = pg_query($db,"UPDATE customer_store_master SET session_id = '$new_session_id' where customer_id ='$customer_id'" );
    
    

	//Get data from customer_master table
    $result2= pg_query($db,"SELECT customer_name, customer_stock_data_table_name, customer_product_data_table_name  from  customer_master WHERE customer_id = '$customer_id' LIMIT 1  " );
    if(pg_num_rows($result2) > 0)
    {
        while($row2= pg_fetch_assoc($result2))   
        {       
            $customer_name = $row2['customer_name'];
            $customer_stock_data_table_name = $row2['customer_stock_data_table_name'];
            $customer_product_data_table_name = $row2['customer_product_data_table_name'];
        }
    }

	$query="SELECT audit_plan_name,  array_to_string(array_agg(audit_plan_details.icode), ',') as icode, array_to_string(array_agg(barcode), ',') as barcodes  FROM audit_plan_details INNER JOIN ".$customer_product_data_table_name." ON ".$customer_product_data_table_name .".icode = audit_plan_details.icode WHERE   activation_date = (SELECT MAX(activation_date) FROM audit_plan_details WHERE customer_id=".$customer_id ."  AND store_id='$store_id' ) group by audit_plan_name;";
	
	$result = pg_query($db,$query);

	$rows = pg_num_rows($result);
	$responses = array();
	$mainArray = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))   
		{	

			/*echo $row['customer_id'];
			echo $row['customer_name'];
			echo $row['customer_stock_data_table_name'];	
			echo $row['customer_product_data_table_name'];*/
			$responses[] = $row;
		}
		
				
	}
	$mainArray['details'] = $responses;
	$mainArray['max_level'] = $maxlevel;
	$mainArray['session_id'] = $new_session_id;
	echo json_encode($mainArray);
	
	
	
?>