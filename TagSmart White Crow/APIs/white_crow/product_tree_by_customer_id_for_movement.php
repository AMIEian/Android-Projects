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

    /*$customer_id = 5;
    $location = 3;
    $sublocation = 3;*/

    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";
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

    //Get max sldt_id ids from epc_location_date_time table with desire location and sublocation
    $result3= pg_query($db,"SELECT MAX(sldt_id) as sid from epc_location_date_time WHERE epc_location_date_time.location_id= '$location' AND epc_location_date_time.sub_location_id= '$sublocation' AND customer_id='$customer_id' Group BY tagsmart_id" );
    $temp="";
    if(pg_num_rows($result2) > 0)
    {
        while($row3= pg_fetch_assoc($result3))   
        {       
            $temp .= "'". $row3['sid']."',";
        }
    }
    $selectedIDs = rtrim($temp, ",");



     $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode Inner JOIN tagsmart_stock_details ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id WHERE sldt_id IN (".$selectedIDs.") ;";

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