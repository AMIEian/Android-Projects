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

    /*//testing
        $customer_id = 7;
        $store_id = 7;
    //testing ends */

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
    $result3= pg_query($db,"select replenishment_id  FROM public.replenishment_data where replenishment_id in  (select max(replenishment_id ) as rid  FROM public.replenishment_data where store_id='$store_id' AND (replenishment_quantity<>'0' or manual_replenishment_quantity<>'0') group by barcode) " );
    $temp="";
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))   
        {       
            $temp .= "'". $row3['replenishment_id']."',";
        }
    }
    $selectedBarcodes = rtrim($temp, ",");

     
    $filename = __DIR__  ."/".$customer_product_data_table_name ."_".time(). ".csv";
        //echo $query_str;
   

    $fp = fopen($filename, 'w');

  // if item are found in front store
    if(strlen($selectedBarcodes)>0)
    {
        $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, CASE WHEN replenishment_data.manual_replenishment_quantity ='0' THEN replenishment_quantity::int WHEN replenishment_quantity='0' THEN manual_replenishment_quantity::int ELSE least(manual_replenishment_quantity::int,replenishment_quantity::int) END AS quantity  FROM ". $customer_product_data_table_name." INNER JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode INNER JOIN replenishment_data ON replenishment_data.barcode = ".$customer_product_data_table_name.".barcode WHERE replenishment_data.replenishment_id IN (".$selectedBarcodes.") ;";        


        $result3= pg_query($db,$query_str);
        if(pg_num_rows($result3) > 0)
        {
            while($row3= pg_fetch_assoc($result3))   
            {       
                fputcsv($fp, $row3);
            }
        }
        
    }
    

    fclose($fp);

    $command = escapeshellcmd('python2 tree_product.py '.$filename);
    $output = shell_exec($command);
    $max_level = $max_level +1; // added twoneo more taking into consideration   replenishment qty

    $responses = array();

    $responses['max_level'] = $max_level;
    $responses['details'] = json_decode($output);
    
    echo json_encode($responses);

  

?>