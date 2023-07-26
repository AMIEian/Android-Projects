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
    $location = $_POST['location'];
    $sublocation = $_POST['sublocation'];
    $store_id = $_POST['store_id'];

	/*$customer_id = 5;
    $location = 3;
    $sublocation = 4;
    $store_id = 9;*/


    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

    $sales_master_table = "";
    $sales_master_barcode_column = "";
    $sales_master_epc_column = "";

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


    //echo "SELECT epc_location_date_time.* FROM epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id LEFT JOIN missing_items_details on missing_items_details.epc =  tagsmart_stock_details.epc AND missing_items_details.is_lost ='0' LEFT JOIN ".$sales_master_table. " ON ".$sales_master_table.".epc =  tagsmart_stock_details.epc  WHERE ".$sales_master_table.".epc IS NULL  AND sldt_id IN (SELECT MAX(sldt_id) AS sldt_id FROM epc_location_date_time WHERE  customer_id='$customer_id' AND store_id='$store_id' AND is_sold = '0' Group BY tagsmart_id )<br>";

    //Get max sldt_id ids from epc_location_date_time table with desire location and sublocation

    $result3= pg_query($db,"SELECT epc_location_date_time.* FROM epc_location_date_time  Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id LEFT JOIN missing_items_details on missing_items_details.epc =  tagsmart_stock_details.epc AND missing_items_details.is_lost ='0' LEFT JOIN ".$sales_master_table. " ON ".$sales_master_table.".epc =  tagsmart_stock_details.epc  WHERE ".$sales_master_table.".epc IS NULL  AND sldt_id IN (SELECT MAX(sldt_id) AS sldt_id FROM epc_location_date_time WHERE  customer_id='$customer_id' AND store_id='$store_id' AND is_sold = '0' Group BY tagsmart_id )" );
    $temp="";
    if(pg_num_rows($result2) > 0)
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
        /*$query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode Inner JOIN tagsmart_stock_details ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id WHERE sldt_id IN (".$selectedIDs.")  AND epc_location_date_time.store_id ='$store_id' ;";*/

        $query_str="SELECT Distinct  ".$str.", ". $customer_stock_data_table_name.".barcode, count(tagsmart_stock_details.tagsmart_id) as quantity FROM public.epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN ". $customer_product_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ". $customer_product_data_table_name.". barcode where epc_location_date_time.customer_id='$customer_id' AND epc_location_date_time.store_id='$store_id'  AND sldt_id IN (".$selectedIDs." )   Group BY tagsmart_stock_details.tagsmart_id, ". $customer_stock_data_table_name.".barcode,".$str;
    }






     $filename = __DIR__  ."/".$customer_product_data_table_name . ".csv";

     //echo $query_str;


$fp = fopen($filename, 'w');


$tablerecords = array();


    $result3= pg_query($db,$query_str);
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))
        {
            //$tablerecords[] = $row3;
            //echo $row3;
            fputcsv($fp, $row3);
        }
    }

fclose($fp);




    $command = escapeshellcmd('python2 tree_product.py '.$filename);
    $output = shell_exec($command);
    echo $output;

?>
