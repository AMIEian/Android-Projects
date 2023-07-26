<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start();
	header('Cache-control: private');
	include("connect.php");


	$responses = array();

    //test
		$stock_table_name ='dadar_stock_data';
		$product_table_name ='dadar_product_data';
    $customer_id = 5;
    $store_id =8;
    $user_id = 10;
    $session_id = 302;


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
		$result = pg_query($db,"select a.location_id,  a.sub_location_id, COUNT(a.tagsmart_id) as qty  from epc_location_date_time a  join tagsmart_stock_details b on a.tagsmart_id=b.tagsmart_id  join ".$stock_table_name." stk on stk.stock_id=b.stock_id  JOIN ".$product_table_name." prd on prd.barcode=stk.barcode  where a.sldt_id in  (  SELECT MAX(sldt_id) as sid from epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id  Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id  Inner JOIN ".$product_table_name." ON ".$product_table_name.".barcode = ".$stock_table_name.".barcode  WHERE epc_location_date_time.customer_id='$customer_id' AND  epc_location_date_time.store_id='$store_id'  AND epc_location_date_time.is_sold = '0'
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
		$result = pg_query($db," select a.location_id,  a.sub_location_id, COUNT(a.tagsmart_id) as qty from epc_location_date_time a  join tagsmart_stock_details b on a.tagsmart_id=b.tagsmart_id  join ".$stock_table_name." stk on stk.stock_id=b.stock_id  JOIN ".$product_table_name." prd on prd.barcode=stk.barcode  where a.sldt_id in  (  SELECT MAX(sldt_id) as sid from epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id  Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id  Inner JOIN ".$product_table_name." ON ".$product_table_name.".barcode = ".$stock_table_name.".barcode  WHERE epc_location_date_time.customer_id='$customer_id' AND  epc_location_date_time.store_id='$store_id'  AND epc_location_date_time.is_sold = '0' and  epc_location_date_time.session_id='$session_id'  Group BY epc_location_date_time.tagsmart_id  ) and a.session_id='$session_id'  Group BY a.location_id,  a.sub_location_id" );

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

		$responses['summary_team_count'] = $summary_array;


		echo json_encode($responses);

	/*************** reponse of API
	 *
	 *  {"team_count":"8",
	 {"summary_team_count":[{"location_id":"3","sub_location_id":"3","total_qty":"2897","qty":"2833","location":"Shop","sub_location":"Back Store"},{"location_id":"3","sub_location_id":"4","total_qty":"14760","qty":"0","location":"Shop","sub_location":"Shop Floor"},{"location_id":"3","sub_location_id":"8","total_qty":"15","qty":"0","location":"unknow","sub_location":"unknow"}]}}
	 *
	 *
	 * ****************************/



?>
