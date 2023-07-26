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
    $store_id = $_POST['store_id'];

    //$customer_id = 5;
    //$store_id =8;

    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

    $selectedBarcodes = "";
    $temp = "";
    $responses = array();


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



    //Get barcode from picking_data table
    $result3= pg_query($db,"SELECT barcode from picking_data WHERE store_id='$store_id' AND status='0'" );
    $temp="";
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))
        {
            $responses[] =$row3['barcode'];
        }
    }

   echo json_encode($responses);
   /* ouput response
   ["8905011647720","8907733726455","8905011897736","8905011290575"]
   */
?>
