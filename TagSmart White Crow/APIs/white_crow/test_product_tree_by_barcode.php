<?php
    ini_set('max_execution_time', 0);
    ob_start();
    session_start(); 
    header('Cache-control: private');
    include("api/connect.php");

    /*f(!isset($_POST['customer_id']))
    {
        print_r("Customer id missing :(");
        exit;
    }*/

    $customer_id = '5';
    $json_barcode = json_decode('["8907515729155"]', true);
    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

    $selectedBarcodes = "";
    $temp = "";

    foreach($json_barcode as $ean) {
       // echo $ean;
        
        $temp .= "'". $ean."',";

    }
    $selectedBarcodes = rtrim($temp, ",");
    
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

    $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity FROM ". $customer_product_data_table_name." LEFT JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode WHERE ".$customer_product_data_table_name.".barcode IN (".$selectedBarcodes.") ;";

     $filename = __DIR__  ."/".$customer_product_data_table_name ."_".time(). ".csv";
     //echo $query_str;
   

$fp = fopen($filename, 'w');


$tablerecords = array();


    $result3= pg_query($db,$query_str);
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))   
        {       
            $tablerecords[] = $row3;
            //echo $row3;
            fputcsv($fp, $row3);
        }
    }

fclose($fp);




    $command = escapeshellcmd('python2 tree_product.py '.$filename);
    $output = shell_exec($command);
    echo $output;

?>