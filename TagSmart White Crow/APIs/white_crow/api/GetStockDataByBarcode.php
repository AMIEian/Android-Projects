<?php
    ini_set('max_execution_time', 0);
    ob_start();
    session_start(); 
    header('Cache-control: private');
    include("connect.php");

    $responses = array();

    if(!isset($_POST['customer_id']))
    {
        echo json_encode($responses);
        exit;
    }

    $customer_id = $_POST['customer_id'];
    $json_barcode = json_decode($_POST['json_barcode'], true);
    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

    $selectedBarcodes = "";
    $temp = "";

    foreach($json_barcode as $ean) {
        
         $temp .= "'". $ean."',";

    }
    $selectedBarcodes = rtrim($temp, ",");
    
    //Get category table data
    $result_product_data = pg_query($db,"SELECT  * FROM customer_category_master WHERE customer_id = $customer_id Order BY category_level ASC" );
    $count=0;
    $str="";
    $datasize = pg_num_rows($result_product_data);
    $cat_level=0;
    
    if(pg_num_rows($result_product_data) > 0)
    {
        while($row= pg_fetch_assoc($result_product_data))   
        {   
            if(($datasize -1 ) == $count)
            {
                $str.= $row['category_column_name'];
                $cat_level  = $cat_level+1;
            }
            else
            {
                $str.=$row['category_column_name'].",";
                $cat_level  = $cat_level+1;
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

    $stock_responses = array();
    $total_qty=0;

    $query_str="select  stock_id, barcode,  quantity FROM ". $customer_stock_data_table_name." WHERE ".$customer_stock_data_table_name.".barcode IN (".$selectedBarcodes.") ;";
    $result_product_data = pg_query($db,$query_str );

     $rows = pg_num_rows($result_product_data);
    if(pg_num_rows($result_product_data) > 0)
    {
        while($row= pg_fetch_assoc($result_product_data))   
        {              
            $stock_responses[] = $row;
            $total_qty= $total_qty + (int)$row['quantity'];
        }
        
                
    }

    $responses['total_qty'] = $total_qty;
    $responses['total_category'] = $cat_level;
    $responses['stock_details'] = $stock_responses;

    echo json_encode($responses);

?>