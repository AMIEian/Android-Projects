<?php
    ini_set('max_execution_time', 0);
    
    ini_set('memory_limit', '8192M');
    ob_start();
    session_start(); 
    header('Cache-control: private');
    include("api/connect.php");

   function array_recursive_search_key_map($needle, $haystack) {
    foreach($haystack as $first_level_key=>$value) {
        if ($needle === $value) {
            return array($first_level_key);
        } elseif (is_array($value)) {
            $callback = array_recursive_search_key_map($needle, $value);
            if ($callback) {
                return array_merge(array($first_level_key), $callback);
            }
        }
    }
    return false;
}

class Category{

	public $name;
	public $parent_id;
	public $quantity;
	public $rfid_quantity;
	public $level;
	public $id;

}

    $customer_id = 3;
    $location = 3;
    $sublocation = 3;
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

    $query_str="select DISTINCT ".$str.", ".$customer_stock_data_table_name.".barcode, ".$customer_stock_data_table_name.".quantity ,".$customer_stock_data_table_name.".rfid_quantity  FROM ". $customer_product_data_table_name." LEFT JOIN ".$customer_stock_data_table_name." ON ".$customer_stock_data_table_name.".barcode = ".$customer_product_data_table_name.".barcode  WHERE ".$customer_stock_data_table_name.".location_id= ".$location." AND ".$customer_stock_data_table_name.".sub_location_id= ".$sublocation." ORDER BY ".$category_responses[0]['category_column_name'].";";

     $filename = __DIR__  ."/".$customer_product_data_table_name . ".csv";
     //echo $query_str;
   

$fp = fopen($filename, 'w');


$tablerecords = array();
$keywordArray = array();


   $result3= pg_query($db,$query_str);
    if(pg_num_rows($result3) > 0)
    {
        while($row3= pg_fetch_assoc($result3))   
        {  
        	//$line .= "'". $row3."',";
        	

            fputcsv($fp, $row3);
        }
    }
   

	fclose($fp);

$myfile = fopen($filename, "r") or die("Unable to open file!");
// Output one line until end-of-file
$counter=0;
while(!feof($myfile)) 
{
  	$str = fgets($myfile) ;
	$params = explode(",", $str);
	$previousCatFlag=0;

	//since last and seond last value of each line corresponds to qty and rfid qty
	for($c=0; $c< (sizeof($params)-2); $c++)
	{
		$name= $params[$c];
		if($c == 0)
		{
			$parent_id= -1;
		}
		else
		{
			$parent_id= $counter-1;
		}
		$quantity= $params[sizeof($params)-2];
		$rfid_quantity= $params[sizeof($params)-1];
		$level= $c;
		$id= $counter;


		if(array_search($name, array_column($tablerecords, 'name')))
		{
			$index =  array_search($name, array_column($tablerecords, 'name'));
			if($c == 0)
			{				

				$qty = (int)$quantity + (int)$tablerecords[$index]['quantity'];
				$rfid = (int)$rfid_quantity + (int)$tablerecords[$index]['rfid_quantity'];

				$tablerecords[$index]['quantity']= strval($qty);
				$tablerecords[$index]['rfid_quantity']= strval($rfid);
				$previousCatFlag=1;
			}
			else if($c > 0)
			{
				//print_r("<br>previousCatFlag: ".$previousCatFlag."<br>");
				if($previousCatFlag ==1 )
				{
					$qty = (int)$quantity + (int)$tablerecords[$index]['quantity'];
					$rfid = (int)$rfid_quantity + (int)$tablerecords[$index]['rfid_quantity'];

					$tablerecords[$index]['quantity']= strval($qty);
					$tablerecords[$index]['rfid_quantity']= strval($rfid);
				}
			}

			
		}
		else
		{
			$previousCatFlag=0;
			//print_r("<br>line: ".$str."<br>");
			$arrayName = array('name' => $name,'parent_id' =>$parent_id, 'quantity'=>$quantity, 'rfid_quantity'=>$rfid_quantity, 'level'=>$level, 'id'=>$id);

			$tablerecords[] = $arrayName;

			$counter++;
		}

		
	}
}
fclose($myfile);

print_r("<br>");
print_r($tablerecords);


    /*$command = escapeshellcmd('python2 tree_product.py '.$filename);
    $output = shell_exec($command);
    echo $output;*/




   /* $result3= pg_query($db,$query_str);
    if(pg_num_rows($result3) > 0)
    {
    	$counter=0;
        while($row3= pg_fetch_assoc($result3))   
        {  
        	//$line .= "'". $row3."',";
        	$str = implode (", ", $row3);
        	$params = explode(",", $str);
			$previousCatFlag=0;
			//since last and seond last value of each line corresponds to qty and rfid qty
			for($c=0; $c< (sizeof($params)-2); $c++)
			{
				$name= $params[$c];
				if($c == 0)
				{
					$parent_id= -1;
				}
				else
				{
					$parent_id= $counter-1;
				}
				$quantity= $params[sizeof($params)-2];
				$rfid_quantity= $params[sizeof($params)-1];
				$level= $c;
				$id= $counter;


				if(array_search($name, array_column($tablerecords, 'name')))
				{
					$index =  array_search($name, array_column($tablerecords, 'name'));
					if($c == 0)
					{				

						$qty = (int)$quantity + (int)$tablerecords[$index]['quantity'];
						$rfid = (int)$rfid_quantity + (int)$tablerecords[$index]['rfid_quantity'];

						$tablerecords[$index]['quantity']= strval($qty);
						$tablerecords[$index]['rfid_quantity']= strval($rfid);
						$previousCatFlag=1;
					}
					else if($c > 0)
					{
						//print_r("<br>previousCatFlag: ".$previousCatFlag."<br>");
						if($previousCatFlag ==1 )
						{
							$qty = (int)$quantity + (int)$tablerecords[$index]['quantity'];
							$rfid = (int)$rfid_quantity + (int)$tablerecords[$index]['rfid_quantity'];

							$tablerecords[$index]['quantity']= strval($qty);
							$tablerecords[$index]['rfid_quantity']= strval($rfid);
						}
					}

					
				}
				else
				{
					$previousCatFlag=0;
					//print_r("<br>line: ".$str."<br>");
					$arrayName = array('name' => $name,'parent_id' =>$parent_id, 'quantity'=>$quantity, 'rfid_quantity'=>$rfid_quantity, 'level'=>$level, 'id'=>$id);

					$tablerecords[] = $arrayName;

					$counter++;
				}

				
			}
        }
    }
    print_r($tablerecords);*/

?>