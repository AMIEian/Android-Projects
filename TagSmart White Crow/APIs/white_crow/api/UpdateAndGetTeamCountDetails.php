<?php
	ini_set('max_execution_time', 0);
	error_reporting(-1);
ini_set('display_errors', 'On');

	ob_start();
	session_start();
	header('Cache-control: private');
	include("connect.php");


	$responses = array();
	$json_barcode = json_decode($_POST['json_barcode'], true);
	$stock_table_name =$_POST['stock_table_name'];
	$product_table_name =$_POST['product_table_name'];
	$json_epc = json_decode($_POST['json_epc'], true);
    $customer_id = $_POST['customer_id'];
    $store_id = $_POST['store_id'];
    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];
		$location_id = $_POST['location_id'];
    $sub_location_id = $_POST['sub_location_id'];

    //test
   /* $json_epc = json_decode('["303A667B83B4FC4DF84C3AFC","30361F84CC001C80000FA855","3014000000000000000FA861","30361F84CC000380000FA858","303A667B83B4FC4DF84C3AFD","3014000000000000000FA862","30361F84CC0000C0000FA842","3014000000000000000FA854"]', true);

	 $stock_table_name ='dadar_stock_data';
	 $product_table_name ='dadar_product_data';
	 $customer_id = 5;
	 $store_id =8;
	 $user_id = 10;
	 $session_id = 302;*/


	foreach($json_epc as $item2) {
		$epc = trim($item2);

		if(strlen($epc) == 24)
		{
			 
			$ean="";

			$result_barcode = pg_query($db,"select distinct barcode from epc_barcode_master_white_crow where epc='$epc' Limit 1");
			$rows_barcode = pg_num_rows($result_barcode);
			if($rows_barcode > 0 )			    
			{
				while($row_bar= pg_fetch_assoc($result_barcode))   
				{
					$ean = $row_bar['barcode'];
				}

				if (in_array($ean, $json_barcode)) //if epc is found in selected barcode array
				{
					$result_epc_data = pg_query($db,"select epc  from team_counts_details where customer_id ='$customer_id' AND store_id='$store_id' AND session_id = '$session_id' AND epc='$epc'" );

				    $rows = pg_num_rows($result_epc_data);

				    if($rows == 0 )
				    {
				    	$sub_location_id_old = '0';

				    	//check for drift EPC
						$res14= pg_query($db,"SELECT  epc_location_date_time.location_id, epc_location_date_time.sub_location_id from epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id WHERE epc='$epc' ORDER BY sldt_id DESC LIMIT 1" ); 

						while($row14 = pg_fetch_assoc($res14))   
						{
							$sub_location_id_old = $row14['sub_location_id'];
						}

						
						if($sub_location_id_old == '0') // if new epc, make entry
						{
							$result = pg_query($db,"INSERT INTO  team_counts_details (tc_id, store_id, customer_id, session_id, epc, user_id, location_id, sub_location_id) VALUES(DEFAULT,'$store_id','$customer_id','$session_id','$epc','$user_id','$location_id', '$sub_location_id')" );
						}
						else if($sub_location_id_old == $sub_location_id ) //if current sublocation of EPC matches with the selected EPC
						{
				    		$result = pg_query($db,"INSERT INTO  team_counts_details (tc_id, store_id, customer_id, session_id, epc, user_id, location_id, sub_location_id) VALUES(DEFAULT,'$store_id','$customer_id','$session_id','$epc','$user_id','$location_id', '$sub_location_id')" );
				    	}
				    }
				}
			}
		}
	}

	$reut9= pg_query($db,"select count(tc_id) as team_count, count(DISTINCT user_id) as total_user from team_counts_details where customer_id ='$customer_id' AND store_id='$store_id' AND session_id = '$session_id'" );
	$team_count = '0';
	$total_user ='0' ;
	if(pg_num_rows($reut9) > 0)
	{
		while($row9= pg_fetch_assoc($reut9))
		{

			$team_count = $row9['team_count'];
			$total_user = $row9['total_user'];
		}
	}

	//Get the location team_counts_details
	$result = pg_query($db,"SELECT customer_store_location.*, location_master.location, sub_location_master.sublocation FROM customer_master Inner JOIN customer_store_master ON customer_store_master.customer_id = customer_master.customer_id Inner JOIN customer_store_location ON customer_store_location.store_id = customer_store_master.store_id  Inner JOIN location_master ON location_master.location_id = customer_store_location.location_id  Inner JOIN sub_location_master ON sub_location_master.sub_location_id = customer_store_location.sub_location_id  WHERE  customer_master.customer_id=".$customer_id." AND customer_store_location.store_id='$store_id'" );

	$rows = pg_num_rows($result);
	$location = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))
		{
			$location[] = $row;
		}
	}

	//Get the total team_counts_details  by location and sublocation
	$result = pg_query($db,"select a.location_id,  a.sub_location_id, COUNT(a.tagsmart_id) as qty  from epc_location_date_time a  join tagsmart_stock_details b on a.tagsmart_id=b.tagsmart_id  LEFT JOIN missing_items_details on missing_items_details.epc =  b.epc AND missing_items_details.is_lost ='0'  join ".$stock_table_name." stk on stk.stock_id=b.stock_id  JOIN ".$product_table_name." prd on prd.barcode=stk.barcode  where a.sldt_id in  (  SELECT MAX(sldt_id) as sid from epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id  Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id  Inner JOIN ".$product_table_name." ON ".$product_table_name.".barcode = ".$stock_table_name.".barcode  WHERE epc_location_date_time.customer_id='$customer_id' AND  epc_location_date_time.store_id='$store_id'  AND epc_location_date_time.is_sold = '0'
	Group BY epc_location_date_time.tagsmart_id  )  Group BY a.location_id,  a.sub_location_id" );

	$rows = pg_num_rows($result);
	$totalCountsArray = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))
		{
			$totalCountsArray[] = $row;
		}
	}


	//Get the  team_counts_details by session id , location and sublocation
	$result = pg_query($db,"SELECT location_id, sub_location_id , count(team_counts_details.tc_id) as qty  FROM public.team_counts_details where session_id='$session_id' group by location_id, sub_location_id" );

	$rows = pg_num_rows($result);
	$sessionTotalCountsArray = array();
	if(pg_num_rows($result) > 0)
	{
		while($row= pg_fetch_assoc($result))
		{
			$sessionTotalCountsArray[] = $row;
		}
	}

	$summary_array = array();
	$counter=0;
	for($g=0; $g< sizeof($totalCountsArray); $g++)
	{
		$flag=0;
		for($s=0; $s< sizeof($sessionTotalCountsArray); $s++)
		{
				if($totalCountsArray[$g]['location_id'] == $sessionTotalCountsArray[$s]['location_id'] && $totalCountsArray[$g]['sub_location_id'] == $sessionTotalCountsArray[$s]['sub_location_id'])
				{
					$summary_array[$counter]['location_id'] = $sessionTotalCountsArray[$s]['location_id'];
					$summary_array[$counter]['sub_location_id'] = $sessionTotalCountsArray[$s]['sub_location_id'];
					$summary_array[$counter]['total_qty'] = $totalCountsArray[$g]['qty'] ;
					$summary_array[$counter]['qty'] = $sessionTotalCountsArray[$s]['qty'];

					$flag=1;
					break;
				}
		}
		if($flag == 0)
		{
			$summary_array[$counter]['location_id'] = $totalCountsArray[$g]['location_id'];
			$summary_array[$counter]['sub_location_id'] = $totalCountsArray[$g]['sub_location_id'];
			$summary_array[$counter]['total_qty'] = $totalCountsArray[$g]['qty'] ;
			$summary_array[$counter]['qty'] ='0';
		}

		$counter++;
	}

	//adding location and subloaction title
	$counter=0;
	for($g=0; $g< sizeof($summary_array); $g++)
	{
		$flag=0;
		for($s=0; $s< sizeof($location); $s++)
		{
				if($summary_array[$g]['location_id'] == $location[$s]['location_id'] && $summary_array[$g]['sub_location_id'] == $location[$s]['sub_location_id'])
				{
					$summary_array[$counter]['location'] = $location[$s]['location'];
					$summary_array[$counter]['sub_location'] = $location[$s]['sublocation'];
					$flag=1;
					break;
				}
		}
		if($flag == 0)
		{
			$summary_array[$counter]['location'] = "unknow";
			$summary_array[$counter]['sub_location'] = "unknow";
		}

		$counter++;
	}

	$responses['team_count'] = $team_count;
	$responses['total_user'] = $total_user;
	$responses['summary_team_count'] = $summary_array;


	echo json_encode($responses);

	/*************** reponse of API
	 *
	 *  {
	"team_count": "8",
	"total_user":"2",
	"summary_team_count": [{
		"location_id": "3",
		"sub_location_id": "3",
		"total_qty": "2897",
		"qty": "2833",
		"location": "Shop",
		"sub_location": "Back Store"
	}, {
		"location_id": "3",
		"sub_location_id": "4",
		"total_qty": "14760",
		"qty": "0",
		"location": "Shop",
		"sub_location": "Shop Floor"
	}, {
		"location_id": "3",
		"sub_location_id": "8",
		"total_qty": "15",
		"qty": "0",
		"location": "unknow",
		"sub_location": "unknow"
	}]

}
	 *
	 *
	 * ****************************/



?>
