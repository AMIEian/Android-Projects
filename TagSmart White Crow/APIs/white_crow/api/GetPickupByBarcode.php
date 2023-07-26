<?php
	ini_set('max_execution_time', 0);

	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	$customer_id = $_POST['customer_id'];
	$json_barcode = json_decode($_POST['json_barcode'], true);
	$store_id = $_POST['store_id'];

	

	/*$testStr ='["8905011647720"]';

    $customer_id = 5;
    $json_barcode = json_decode($testStr, true);*/

	$customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

	//$customer_id =3;

	//Get category table data
    $result_product_data = pg_query($db,"SELECT  * FROM customer_category_master WHERE customer_id = $customer_id Order BY category_level ASC" );
    $count=0;
    $str="";
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

    //Get data from customer_master table
    $result2= pg_query($db,"SELECT customer_name, customer_stock_data_table_name, customer_product_data_table_name from  customer_master WHERE customer_id = '$customer_id' LIMIT 1  " );
    if(pg_num_rows($result2) > 0)
    {
        while($row2= pg_fetch_assoc($result2))   
        {       
            $customer_name = $row2['customer_name'];
            $customer_stock_data_table_name = $row2['customer_stock_data_table_name'];
            $customer_product_data_table_name = $row2['customer_product_data_table_name'];
        }
    }

	

    $temp="";
    foreach($json_barcode as $ean) {        
         
         $temp .= "'". $ean."',";
         
    }
    $selectedBarcodes = rtrim($temp, ",");
    //Get max sldt_id ids from epc_location_date_time table with desire location and sublocation
    $result4= pg_query($db,"SELECT MAX(sldt_id) as sid from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details.stock_id WHERE epc_location_date_time.is_sold= '0' AND epc_location_date_time.customer_id='$customer_id' AND  epc_location_date_time.store_id='$store_id' AND barcode IN (".$selectedBarcodes.")  Group BY epc_location_date_time.tagsmart_id" );
    $temp="";
    if(pg_num_rows($result4) > 0)
    {
        while($row3= pg_fetch_assoc($result4))   
        {       
            $temp .= "'". $row3['sid']."',";
        }
    }
    $selectedIDs = rtrim($temp, ",");

   $query_str="select Distinct  ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity, picking_data.picking_quantity,sub_location_master.sublocation, epc_location_date_time.sub_location_id, epc FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode Inner JOIN tagsmart_stock_details ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id INNER JOIN picking_data ON picking_data.barcode = ".$customer_product_data_table_name.".barcode INNER JOIN sub_location_master ON sub_location_master.sub_location_id = epc_location_date_time.sub_location_id WHERE sldt_id IN (".$selectedIDs.")";

	 
   	$mainResponse = array();
   	$barcodeArray = array();
   	$response = array();
   	$counter=0;
    $result6= pg_query($db,$query_str);
    if(pg_num_rows($result6) > 0)
    {
        while($row6= pg_fetch_assoc($result6))   
        {       
           // print_r( $row6 );
            $ean = $row6['barcode'];
            if (in_array($ean, $barcodeArray)) // check for barcode uniqueness
			{
				//echo "Match found";
				//get index of maching barcode
				$index = array_search($ean, $barcodeArray);
				$sublocation_value = $row6['sublocation'];

				//get sub location array within main barcode array	w.r.t barcode
				$temp_array = $mainResponse[$index]['location_epc'];



				$flag_sublocation=0;
				for($s=0 ; $s<sizeof($temp_array); $s++)
				{
					//check for match sub location within main barcode array w.r.t barcode
					if (strcmp($sublocation_value,$temp_array[$s]['sublocation']) ==0)
					{
						
						$flag_sublocation=1;
						$mainResponse[$index]['location_epc'][$s]['epc'][] = $row6['epc'];
						break;
					}
					
				}
				// add new sublocation array within main barcode array
				if($flag_sublocation==0)
				{					

					$mainResponse[$index]['location_epc'][]['sublocation'] = $row6['sublocation'];
					$mainResponse[$index]['location_epc'][sizeof($temp_array)]['sub_location_id'] = $row6['sub_location_id'];

					$mainResponse[$index]['location_epc'][sizeof($temp_array)]['epc'][] = $row6['epc'];
				}

				
			}
			else //if barcode is unique in the barcode array
			{
				//echo "Match not found";
				array_push($barcodeArray,$ean);
				for($c=0; $c < sizeof($category_responses); $c++)
				{
					$cat = $category_responses[$c]['category_column_name'];
					$mainResponse[$counter][$cat] = $row6[$cat];
				}
				$mainResponse[$counter]['barcode'] = $row6['barcode'];
				$mainResponse[$counter]['quantity'] = $row6['quantity'];
				$mainResponse[$counter]['picking_quantity'] = $row6['picking_quantity'];
				$mainResponse[$counter]['location_epc'][]['sublocation'] = $row6['sublocation'];
				$mainResponse[$counter]['location_epc'][0]['sub_location_id'] = $row6['sub_location_id'];

				$mainResponse[$counter]['location_epc'][0]['epc'][] = $row6['epc'];


				$counter++;
				
			}
        }
    }
    $response['category_details'] = $category_responses;
    $response['barcode_details'] = $mainResponse;
    //print_r( $mainResponse);
   echo json_encode($response);

    /* ouput response    

{
	"category_details": [{
		"cc_id": "23",
		"customer_id": "5",
		"category_column_name": "brand_description",
		"category_level": "1"
	}, {
		"cc_id": "30",
		"customer_id": "5",
		"category_column_name": "sub_class_description",
		"category_level": "2"
	}, {
		"cc_id": "24",
		"customer_id": "5",
		"category_column_name": "sku_description",
		"category_level": "3"
	}, {
		"cc_id": "25",
		"customer_id": "5",
		"category_column_name": "option",
		"category_level": "4"
	}, {
		"cc_id": "26",
		"customer_id": "5",
		"category_column_name": "size_description",
		"category_level": "5"
	}],
	"barcode_details": [{
		"brand_description": "KNIGHTHOOD",
		"sub_class_description": "MENS FORMAL SHIRTS",
		"sku_description": "KHSH-0190-OXFORD-A-FS-SD",
		"option": "LIGHT GREEN",
		"size_description": "42-RF",
		"barcode": "8905011647720",
		"quantity": "2",
		"picking_quantity": "9",
		"location_epc": [{
			"sublocation": "Back Store",
			"sub_location_id": "3",
			"epc": ["30361f84cc3f4100000f906d", "30361f84cc3f4100000f906e", "30361f84cc3f4100000f906f", "30361f84cc3f4100000f909f", "30361f84cc3f4100000f90a4", "30361f84cc3f4100000f90ad", "30361f84cc3f4100000f90ae", "30361f84cc3f4100000f90af", "30361f84cc3f4100000f90b0", "30361f84cc3f4100000f90b5", "30361f84cc3f4100000faabb"]
		}, {
			"sublocation": "Shop Floor",
			"sub_location_id": "4",
			"epc": ["30361F84CC3F410000000074", "30361F84CC3F4100000001A0", "30361F84CC3F4100000001A1"]
		}]
	}]
}
	*/
	
	
?>