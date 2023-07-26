<?php
    ini_set('max_execution_time', 0);
    ini_set('memory_limit', '8192M');
    ob_start();
    session_start(); 
    header('Cache-control: private');
    include("api/connect.php");

   /* if(!isset($_POST['customer_id']))
    {
        print_r("Customer id missing :(");
        exit;
    }

    $customer_id = $_POST['customer_id'];
    $store_id = $_POST['store_id'];*/

    //testing
        $customer_id = 7;
        $store_id = 7;
    //testing ends 

    $location = 3;
    $sublocation = 4;   

    $customer_name = "";
    $customer_stock_data_table_name = "";
    $customer_product_data_table_name = "";

    $selectedBarcodes = "";
    $temp = "";
    $max_level = 0;

    
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
            $max_level ++;
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

    //Get barcode from replenishment_data table 
    $result3= pg_query($db,"select CASE WHEN replenishment_data.manual_replenishment_quantity ='0' THEN replenishment_quantity::int WHEN replenishment_quantity='0' THEN manual_replenishment_quantity::int ELSE least(manual_replenishment_quantity::int,replenishment_quantity::int) END AS quantity  FROM public.replenishment_data where replenishment_id in  (select max(replenishment_id ) as rid  FROM public.replenishment_data where store_id='$store_id' AND (replenishment_quantity<>'0' or manual_replenishment_quantity<>'0') group by barcode) " );
    $total_qty=0;
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))   
        {       
            $total_qty += (int) $row3['quantity'];
        }
    } 
    
    echo json_encode($total_qty);

  

?>