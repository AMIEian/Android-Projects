<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start(); 
	header('Cache-control: private');
	include("connect.php");

	/*$barcode = "8904214926175"; 
	$customer_id = "7";
	$store_id = "7";
	$customer_stock_data_table_name = "ethnicity_stock_data";
	$customer_product_data_table_name = "ethnicity_product_data";*/

	
	$barcode = $_POST['barcode'];
	$customer_id = $_POST['customer_id'];
	$store_id = $_POST['store_id'];
	$customer_stock_data_table_name = $_POST['customer_stock_data_table_name'];
	$customer_product_data_table_name = $_POST['customer_product_data_table_name'];
	$user_id = $_POST['user_id'];	
	

	$mainResponse = array();
   	$barcodeArray = array();
   	$response = array();
   	$optionResponse = array();
   	$counter=0;

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
    
    echo "SELECT MAX(sldt_id) as sid from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details.stock_id WHERE epc_location_date_time.customer_id='$customer_id' AND  epc_location_date_time.store_id='$store_id'   AND barcode IN ('".$barcode."')  Group BY epc_location_date_time.tagsmart_id";

    //Get max sldt_id ids from epc_location_date_time table with desire location and sublocation
    $result4= pg_query($db,"SELECT MAX(sldt_id) as sid from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details.stock_id WHERE epc_location_date_time.customer_id='$customer_id' AND  epc_location_date_time.store_id='$store_id'   AND barcode IN ('".$barcode."')  Group BY epc_location_date_time.tagsmart_id" );
    $temp="";
    if(pg_num_rows($result4) > 0)
    {
        while($row3= pg_fetch_assoc($result4))   
        {       
            $temp .= "'". $row3['sid']."',";
        }
    }
    $selectedIDs = rtrim($temp, ",");


    if(strlen($selectedIDs)>0)
   	{
   		$query_str="select Distinct  ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity, sub_location_master.sublocation, epc_location_date_time.sub_location_id, epc FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode Inner JOIN tagsmart_stock_details ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id  INNER JOIN sub_location_master ON sub_location_master.sub_location_id = epc_location_date_time.sub_location_id WHERE sldt_id IN (".$selectedIDs.")";
	 
   	
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
					$mainResponse[$counter]['location_epc'][]['sublocation'] = $row6['sublocation'];
					$mainResponse[$counter]['location_epc'][0]['sub_location_id'] = $row6['sub_location_id'];

					$mainResponse[$counter]['location_epc'][0]['epc'][] = $row6['epc'];


					$counter++;
					
				}
	        }
	    }
   	}


   	$result_product_data = pg_query($db,"SELECT  barcode_group_category FROM customer_category_barcode_group WHERE customer_id = '$customer_id' AND store_id = '$store_id'" ); 
    $str_group=""; 
    
    if(pg_num_rows($result_product_data) > 0)
    {
        while($row= pg_fetch_assoc($result_product_data))   
        {   
            $str_group = $row['barcode_group_category'];
        }

        $result_option = pg_query($db,"SELECT  option_db_column FROM customer_barcode_options_master WHERE customer_id = '$customer_id' AND store_id = '$store_id'" );
        $count=0;
	    $str_option="";
	    $datasize = pg_num_rows($result_option);
	    $option_array = array();
	    
	    if(pg_num_rows($result_option) > 0)
	    {
	        while($row7= pg_fetch_assoc($result_option))   
	        {   
	            if(($datasize -1 ) == $count)
	            {
	                $str_option.= $row7['option_db_column'];
	            }
	            else
	            {
	                $str_option.=$row7['option_db_column'].",";
	            } 
	            $option_array[] = $row7;
	            $count++;
	        }

	        $result_group_value = pg_query($db,"SELECT  ".$str_group." FROM ".$customer_product_data_table_name." WHERE   barcode ='".$barcode."' LIMIT 1" );
	        $group_name="";
	        if(pg_num_rows($result_group_value) > 0)
		    {
		        while($row8= pg_fetch_assoc($result_group_value))   
		        { 
		        	$group_name=$row8[$str_group];
		        }
		    }

			//echo "Select Distinct ".$str_option." , count(tagsmart_stock_details.tagsmart_id) as scan_qty from tagsmart_stock_details  Inner JOIN ".$customer_stock_data_table_name." ON tagsmart_stock_details.stock_id = ".$customer_stock_data_table_name.". stock_id Inner JOIN ".$customer_product_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.". barcode where ".$customer_stock_data_table_name.".customer_id='$customer_id' AND ".$customer_stock_data_table_name.".store_id='$store_id' AND ".$customer_product_data_table_name.".".$str_group."='$group_name' GROUP BY  ".$str_option;		    


	        $result_option_data = pg_query($db,"Select Distinct ".$str_option." , count(tagsmart_stock_details.tagsmart_id) as scan_qty from tagsmart_stock_details  Inner JOIN ".$customer_stock_data_table_name." ON tagsmart_stock_details.stock_id = ".$customer_stock_data_table_name.". stock_id Inner JOIN ".$customer_product_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.". barcode where ".$customer_stock_data_table_name.".customer_id='$customer_id' AND ".$customer_stock_data_table_name.".store_id='$store_id' AND ".$customer_product_data_table_name.".".$str_group."='$group_name' GROUP BY  ".$str_option);

	        if(pg_num_rows($result_option_data) > 0)
		    {
		        while($row9= pg_fetch_assoc($result_option_data))   
		        { 
		        	$optionResponse[]=$row9;
		        }
		    }
	        
	                
	    }

        
                
    }




   	$response['category_details'] = $category_responses;
    $response['barcode_details'] = $mainResponse;
    $response['options'] = 	$optionResponse;
     $response['options_columns_names'] = 	$option_array;
    //print_r( $mainResponse);
	echo json_encode($response);

/****** Response ************************

{
	"category_details": [{
		"cc_id": "23",
		"customer_id": "5",
		"category_column_name": "brand_description",
		"category_level": "1",
		"category_group": "1"
	}, {
		"cc_id": "30",
		"customer_id": "5",
		"category_column_name": "sub_class_description",
		"category_level": "2",
		"category_group": null
	}, {
		"cc_id": "24",
		"customer_id": "5",
		"category_column_name": "sku_description",
		"category_level": "3",
		"category_group": null
	}, {
		"cc_id": "25",
		"customer_id": "5",
		"category_column_name": "option",
		"category_level": "4",
		"category_group": null
	}, {
		"cc_id": "26",
		"customer_id": "5",
		"category_column_name": "size_description",
		"category_level": "5",
		"category_group": null
	}],
	"barcode_details": [{
		"brand_description": "SRISHTI",
		"sub_class_description": "OTHER DUPATTA",
		"sku_description": "LED-3191-10029-B--SF-SF-AS",
		"option": "PEACH",
		"size_description": "FS",
		"barcode": "8905011000143",
		"quantity": "2",
		"location_epc": [{
			"sublocation": "Back Store",
			"sub_location_id": "3",
			"epc": ["30361F84CC000380000F9082", "30361F84CC000380000FA858"]
		}]
	}],
	"options": [{
		"size_description": "FS",
		"option": "BEIGE",
		"scan_qty": "1"
	}, {
		"size_description": "FS",
		"option": "GREY",
		"scan_qty": "2"
	}, {
		"size_description": "FS",
		"option": "MUSTARD",
		"scan_qty": "3"
	}, {
		"size_description": "FS",
		"option": "PEACH",
		"scan_qty": "3"
	}, {
		"size_description": "FS",
		"option": "PINK",
		"scan_qty": "2"
	}, {
		"size_description": "FS",
		"option": "RED",
		"scan_qty": "1"
	}],
	"options_columns_names": [{
		"option_db_column": "size_description"
	}, {
		"option_db_column": "option"
	}]
}

*****************************************/

	
	
?>

