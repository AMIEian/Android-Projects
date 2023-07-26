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
    $dispatch_order = $_POST['dispatch_order'];
    $store_id = $_POST['store_id'];


   /*$testStr ='["8905014406126","8905011396666","8905011496090","8905011919537","8907515689176","8905011907886","8905011993674","8905011284925","8905011659365","8905011286455","8905011338635","8905011725787","8905011390459","8905011496847","8905011699309","8905011862833","8905011696841","8905011612391","8907403276716","8905011311324","8905011944096","8905011939368","8905011339205","8905011811060","8907733160143","8907515684485","8905011346333","8905011611707","8905011284710","8905011932161","8905011116240","8905011650515","8905011739944","8905011648062","8905011283904","8905011651512","8905011648024","8905011874478","8905011768401","8907403203927","8905011650805","8905011293781","8905011385509","8905011002895","8905011756002","8905011627593","8905011875543","8905011743231","8905011301998","8905011299318","8905011294597","8905011294153","8907733326556","8905011385493","8905011931898","8905011300205","8905014704765","8905011298274","8905011611493","8905011610687","8905011342090","8905011353553","8905011496649","8905014587696","8905011931751","8905011647584","8905011300090","8905011650423","8905011764625","8905011948469","8905011497776","8905014520761","8905011301981","8905011659624","8905014704628","8905011349143","8905011494331","8905011251132","8905011949145","8905011277040","8905011297581","8905011755968","8905011647591","8905011325734","8905011647720","8905011290575","8905011897736","8907515633872","8905011647904","8905011396659","8905011318033","8905011759652","8905011650508","8905011700982","8905011729082","8905011788300","8905011906339","8905011661696","8905011874904"]';

    $customer_id = 5;
    $json_barcode = json_decode($testStr, true);
    $dispatch_order =102;
    $session_id = -99;
    $user_id =6;*/

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
        $result_st_data = pg_query($db,"SELECT  string_agg(barcode, ', ') AS barcode_list , string_agg(quantity, ', ') AS qty_list FROM inward_data WHERE store_id='$store_id' AND dispatch_order_id='$dispatch_order' AND barcode IN (".$barcode_string .")"  );

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

            $sesResp = pg_query($db,"INSERT INTO  session_details (sd_id, session_id, barcodes, barcodes_quantities,customer_id, session_timestamp, store_id, task_id, is_live,user_id) VALUES(DEFAULT,'$session_id','$barcode_list','$qty_list','$customer_id','$time_stamp','$store_id','2','1','$user_id')"  );


        }
    }

    $query_str="select DISTINCT ".$str.", inward_data.barcode, inward_data.quantity FROM ". $customer_product_data_table_name." INNER JOIN inward_data ON inward_data.barcode = ".$customer_product_data_table_name.".barcode WHERE ".$customer_product_data_table_name.".barcode IN (".$selectedBarcodes.") AND store_id='$store_id' AND dispatch_order_id='$dispatch_order' ;";

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
