<?php
    ini_set('max_execution_time', 0);
    ini_set('memory_limit', '8192M');
    ob_start();
    session_start();
    header('Cache-control: private');
    include("api/connect.php");

    if(!isset($_POST['customer_id']))
    {
        print_r("Customer id missing :(");
        exit;
    }

    $customer_id = $_POST['customer_id'];
    $json_barcode = json_decode($_POST['json_barcode'], true);
    $is_new_session = $_POST['is_new_session'];
    $session_id = $_POST['session_id'];
    $user_id = $_POST['user_id'];
    $store_id = $_POST['store_id'];

    $location = $_POST['location'];
    $sublocation = $_POST['sublocation'];

    /*//Test
    $testStr ='["8905014259647","8905014259630","8905014259623","8905014259654","8905014259616","8905014479823","8905014479816","8905014479830","8905014479847","8905011001492","8905014274732","8905014274770","8905014430978","8905014431074","8905014431067","8905014431081","8905014431098","8905011004455","8905011004462","8905011004479","8905011004486","8905011004493","8905011004509","8905011004516","8905011004523","8905011068624","8905011068617","8905011068600","8905011000105","8905011003878","8905011003885","8905011066910","8905011000020","8905011000044","8905011000075"]';
    $user_id =11;
    $customer_id = 5;
    $store_id =9;
    $json_barcode = json_decode($testStr, true);
    $session_id = 0;
    $is_new_session = 1;
    $location = 3;
    $sublocation = 4;*/


    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

    $sales_master_table = "";
    $sales_master_barcode_column = "";
    $sales_master_epc_column = "";

    $selectedBarcodes = "";
    $temp = "";


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

    //Get data from customer_sales_details table
    $result_sales= pg_query($db,"SELECT sales_master_table, sales_master_barcode_column, sales_master_epc_column  from  customer_sales_details WHERE customer_id = '$customer_id' AND store_id='$store_id'  LIMIT 1  " );
    if(pg_num_rows($result_sales) > 0)
    {
        while($row_sales= pg_fetch_assoc($result_sales))
        {
            $sales_master_table = $row_sales['sales_master_table'];
            $sales_master_barcode_column = $row_sales['sales_master_barcode_column'];
            $sales_master_epc_column = $row_sales['sales_master_epc_column'];
        }
    }

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

    $temp2="";
    $temp="";
    foreach($json_barcode as $ean) {

         $temp2 .= "(DEFAULT,'". $ean."',0,".$customer_id.",".$user_id.", ".$session_id."),";
         $temp .= "'". $ean."',";

    }
    $uiBarcodes = rtrim($temp2, ",");
    $selectedBarcodes = rtrim($temp, ",");

    $result4= pg_query($db,"SELECT * from ui_update WHERE  customer_id='$customer_id' AND user_id='$user_id' AND session_id='$session_id' LIMIT 1 " );
    if(pg_num_rows($result4) == 0)
    {

        $ui_query="INSERT INTO ui_update (ui_id,barcode, rfid_quantity, customer_id, user_id, session_id) VALUES ".$uiBarcodes;

        //make ui_update table insert
        $result6= pg_query($db,$ui_query);


        //make  barcode and respective qty insert in session_table table data
        $barcode_string = "'" . implode ( "', '", $json_barcode ) . "'";
        $result_st_data = pg_query($db,"SELECT  string_agg(barcode, ', ') AS barcode_list , string_agg(quantity, ', ') AS qty_list FROM ".$customer_stock_data_table_name." WHERE  store_id ='$store_id' AND barcode IN (".$barcode_string .")"  );

        $barcode_list='';
        $qty_list='';

        if(pg_num_rows($result_st_data) > 0)
        {
            while($row_st= pg_fetch_assoc($result_st_data))
            {
                $barcode_list=$row_st['barcode_list'];
                $qty_list=$row_st['qty_list'];
            }

            $time_stamp = date("Y-m-d h:i:s");

            $sesResp = pg_query($db,"INSERT INTO  session_details (sd_id, session_id, barcodes, barcodes_quantities, customer_id, session_timestamp, store_id, task_id, is_live,user_id ) VALUES(DEFAULT,'$session_id','$barcode_list','$qty_list','$customer_id','$time_stamp','$store_id','1','1','$user_id')"  );


        }
    }

    /********** Bharat Query with sales master table added 21stoct2021 ***********************************************************/
    //$result3= pg_query($db,"SELECT epc_location_date_time.* FROM epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id LEFT JOIN missing_items_details on missing_items_details.epc =  tagsmart_stock_details.epc AND missing_items_details.is_lost ='0' LEFT JOIN ".$sales_master_table. " ON ".$sales_master_table.".epc =  tagsmart_stock_details.epc  WHERE ".$sales_master_table.".epc IS NULL  AND sldt_id IN (SELECT MAX(sldt_id) AS sldt_id FROM epc_location_date_time WHERE  customer_id='$customer_id' AND store_id='$store_id' AND is_sold = '0' Group BY tagsmart_id )" );

    /********** Bharat Query WITHOUT sales master table added 21stoct2021 *****************************************************/
    $result3= pg_query($db,"SELECT epc_location_date_time.* FROM epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id LEFT JOIN missing_items_details on missing_items_details.epc =  tagsmart_stock_details.epc AND missing_items_details.is_lost ='0'  WHERE  sldt_id IN (SELECT MAX(sldt_id) AS sldt_id FROM epc_location_date_time WHERE  customer_id='$customer_id' AND store_id='$store_id' AND is_sold = '0' Group BY tagsmart_id )" );
    $temp="";
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))
        {
            $loc = $row3['location_id'];
            $sub = $row3['sub_location_id'];
            if($location == $loc && $sublocation  == $sub)
            {
                $temp .= "'". $row3['sldt_id']."',";
            }

        }
    }
    $selectedIDs = rtrim($temp, ",");


    $query_str="";
    if(strlen($selectedIDs) == 0)
    {
       $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode  WHERE location_id='$location' AND sub_location_id='$sublocation' AND store_id ='$store_id';";
    }
    else
    {
        /*previous query: - $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode Inner JOIN tagsmart_stock_details ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id WHERE sldt_id IN (".$selectedIDs.")  AND epc_location_date_time.store_id ='$store_id' ;";*/

        /********** Bharat Query with sales master table added 21stoct2021 ***********************************************************/
        //$query_str="SELECT Distinct  ".$str.", ". $customer_stock_data_table_name.".barcode, count(tagsmart_stock_details.stock_id)   as quantity FROM public.epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN ". $customer_product_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ". $customer_product_data_table_name.". barcode LEFT JOIN ".$sales_master_table." ON ".$customer_stock_data_table_name.".barcode = ".$sales_master_table.". ".$sales_master_barcode_column." where epc_location_date_time.customer_id='$customer_id' AND epc_location_date_time.store_id='$store_id'  AND sldt_id IN (".$selectedIDs." )   Group BY  ". $customer_stock_data_table_name.".barcode,".$str;

        /********** Bharat Query WITHOUT sales master table added 21stoct2021 *****************************************************/
         $query_str="SELECT Distinct  ".$str.", ". $customer_stock_data_table_name.".barcode, count(tagsmart_stock_details.stock_id)   as quantity FROM public.epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN ". $customer_product_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ". $customer_product_data_table_name.". barcode where epc_location_date_time.customer_id='$customer_id' AND epc_location_date_time.store_id='$store_id'  AND sldt_id IN (".$selectedIDs." )   Group BY  ". $customer_stock_data_table_name.".barcode,".$str;
    }

     $filename = __DIR__  ."/".$customer_product_data_table_name ."_".time(). ".csv";
     //echo $query_str;


$fp = fopen($filename, 'w');


$tablerecords = array();
$final_qty = 0;

    $result3= pg_query($db,$query_str);
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))
        {
            //$tablerecords[] = $row3;
            //echo $row3;
            $ean = $row3['barcode'];
            if( in_array($ean, $json_barcode) )
            {
                fputcsv($fp, $row3);
                $final_qty = (int)$row3['quantity']+$final_qty ;
            }
        }
    }

fclose($fp);

/* Code added on Oct 24 2021********************************************/

    $currentDate =date("Y-m-d");

    $result4 = pg_query($db,"INSERT INTO  stock_take_session_expected_dtls (stsed_id, session_id, tag_expected_qty, session_date, location_id, sub_location_id, customer_id, store) VALUES(DEFAULT,'$session_id','$final_qty','$currentDate','$location','$sublocation','$customer_id' , '$store_id')"   );

/********************************************************************/




    $command = escapeshellcmd('python2 tree_product.py '.$filename);
    $output = shell_exec($command);
    echo $output;

?>
