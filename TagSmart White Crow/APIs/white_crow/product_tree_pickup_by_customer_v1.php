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

   // $customer_id = 5;   

    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

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

    //Get store id
    $store_id=0;
    $result1= pg_query($db,"SELECT store_id FROM public.customer_store_master WHERE customer_id = '$customer_id' LIMIT 1" );
    if(pg_num_rows($result1) > 0)
    {
        while($row1= pg_fetch_assoc($result1))   
        {       
            $store_id = $row1['store_id'];
        }
    }



    //Get barcode from replenishment_data table 
    $result3= pg_query($db,"SELECT barcode from picking_data WHERE store_id='$store_id' AND status='0'" );
    $temp="";
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))   
        {       
            $temp .= "'". $row3['barcode']."',";
        }
    }
    $selectedBarcodes = rtrim($temp, ",");


    //echo "SELECT MAX(sldt_id) as sid from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details.stock_id WHERE  epc_location_date_time.customer_id='$customer_id' AND barcode IN (".$selectedBarcodes.")  Group BY epc_location_date_time.tagsmart_id" ;

    //Get max sldt_id ids from epc_location_date_time table with desire location and sublocation
    $result4= pg_query($db,"SELECT MAX(sldt_id) as sid from epc_location_date_time Inner JOIN tagsmart_stock_details ON tagsmart_stock_details.tagsmart_id = epc_location_date_time.tagsmart_id Inner JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details.stock_id WHERE epc_location_date_time.is_sold= '0' AND epc_location_date_time.customer_id='$customer_id' AND barcode IN (".$selectedBarcodes.")  Group BY epc_location_date_time.tagsmart_id" );
    $temp="";
    if(pg_num_rows($result4) > 0)
    {
        while($row3= pg_fetch_assoc($result4))   
        {       
            $temp .= "'". $row3['sid']."',";
        }
    }
    $selectedIDs = rtrim($temp, ",");
    

    $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode Inner JOIN tagsmart_stock_details ON ".$customer_stock_data_table_name.".stock_id = tagsmart_stock_details. stock_id Inner JOIN epc_location_date_time ON tagsmart_stock_details.tagsmart_id = epc_location_date_time. tagsmart_id INNER JOIN replenishment_data ON replenishment_data.barcode = ".$customer_product_data_table_name.".barcode WHERE sldt_id IN (".$selectedIDs.")  AND replenishment_data.replenishment_quantity > quantity;";

     $filename = __DIR__  ."/".$customer_product_data_table_name ."_".time(). ".csv";
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