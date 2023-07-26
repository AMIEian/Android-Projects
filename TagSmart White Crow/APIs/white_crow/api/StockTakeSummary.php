<?php
	ini_set('max_execution_time', 0);
	ob_start();
	session_start();
	header('Cache-control: private');
	include("connect.php");

try
{

	$missingEPCResp = array();
	$mainResp = array();
	$summaryArray = array();

   	$stock_table_name = $_POST['stock_table_name'];
    $product_table_name = $_POST['product_table_name'];
    $customer_id = $_POST['customer_id'];

    $user_id = $_POST['user_id'];
    $session_id = $_POST['session_id'];

  /*  $stock_table_name = "dadar_stock_data";
    $product_table_name = "dadar_product_data";
    $customer_id = 5;

    $user_id = 10;
    $session_id = 159;*/


    $category="barcode";

		//update Session details update
		$result_session = pg_query($db,"Update  session_details Set is_live='0'  WHERE session_id = '$session_id'"  );

	//Get category table data
	$result_product_data = pg_query($db,"SELECT  category_column_name FROM customer_category_master WHERE customer_id = $customer_id Order BY category_level ASC"  );
	$str="";
	$count=0;

    $datasize = pg_num_rows($result_product_data);

    $category_responses = array();

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

    //Get last epc record w.r.t session id and user
	$reut= pg_query($db,"Select sldt_id from epc_location_date_time where  user_id='$user_id' AND session_id='$session_id' order by sldt_id DESC limit 1 " );
	if(pg_num_rows($reut) > 0)
	{
		while($row= pg_fetch_assoc($reut))
		{

			$sldt_id = $row['sldt_id'];
			//update is_scan_finish to 1 as complete stock take
			pg_query($db,"UPDATE epc_location_date_time SET is_scan_finished='1' where  sldt_id='$sldt_id' " );

		}
	}

	$filename = __DIR__  ."/".$product_table_name ."_stock_take_".time(). ".csv";
     //echo $query_str;


	$fp = fopen($filename, 'w');

	//get category rfid and qty details
	$result= pg_query($db,"select DISTINCT ".$str.", ".$product_table_name.".barcode  , ui_update.rfid_quantity as scan_qty from ui_update  Inner JOIN ".$stock_table_name." ON ".$stock_table_name.".barcode = ui_update. barcode Inner JOIN ".$product_table_name." ON ".$stock_table_name.".barcode = ".$product_table_name.". barcode where user_id='$user_id' AND session_id='$session_id' AND ui_update.rfid_quantity !=0  " );
	if(pg_num_rows($result) > 0)
	{
		while($row2= pg_fetch_assoc($result))
		{

			//$summaryArray[] = $row2;
			fputcsv($fp, $row2);

		}
	}
	fclose($fp);




    $command = escapeshellcmd('python2 ../tree_product.py '.$filename);
    $summary_output = shell_exec($command);

	//Get missing EPC
	$result11= pg_query($db,"SELECT  string_agg(barcode, ', ') AS barcode_list ,string_agg(tagsmart_stock_details.epc, ', ') AS epc_list,count(epc_location_date_time.*) as epc_total from tagsmart_stock_details  INNER JOIN epc_location_date_time ON epc_location_date_time.tagsmart_id = tagsmart_stock_details.tagsmart_id INNER JOIN ".$stock_table_name." ON ".$stock_table_name.".stock_id = tagsmart_stock_details.stock_id Where tagsmart_stock_details.customer_id='".$customer_id ."' GROUP BY session_id Order BY session_id desc LIMIT 2");
    if(pg_num_rows($result11) == 2)
	{
		$epcList1 = "";
		$epcList2 = "";
		$barcodeList1 = "";
		$barcodeList2 ="";
		$epcCount1 = 0;
		$epcCount2 =0;
		$count=0;
		while($row= pg_fetch_assoc($result11))
		{
			if($count==0)
			{
				$epcList1 = $row['epc_list'];
				$barcodeList1 = $row['barcode_list'];
				$epcCount1 = $row['epc_total'];
			}
			else if($count==1)
			{
				$epcList2 = $row['epc_list'];
				$barcodeList2 = $row['barcode_list'];
				$epcCount2 = $row['epc_total'];
			}

			$count++;
		}

		if($epcCount1 < $epcCount2 )
		{
			$currentEPCList = explode (",", $epcList1);
			$currentEPCList = array_map('trim', $currentEPCList);

			$previousEPCList = explode (",", $epcList2);
			$previousEPCList = array_map('trim', $previousEPCList);

			$currentBarcodeList = explode (",", $barcodeList1);
			$currentBarcodeList = array_map('trim', $currentBarcodeList);

			$previousBarcodeList = explode (",", $barcodeList2);
			$previousBarcodeList = array_map('trim', $previousBarcodeList);

			for($e = 0 ; $e < sizeof($previousEPCList); $e++)
			{
				if (!in_array(trim($previousEPCList[$e]),$currentEPCList))
				{
					//print_r($currentEPCList);
				//echo "<br>previous: ".$previousEPCList[$e]."<br>";

					$response= array('EPC' => $previousEPCList[$e], 'EAN' => $previousBarcodeList[$e]);
					$missingEPCResp[]= $response;
				}
			}

		}
	}

	$mainResp['epcList'] = json_decode($summary_output);
	$mainResp['missingEPCList'] = $missingEPCResp;
	//{"epcList":[],"missingEPCList":[]}

	echo json_encode($mainResp);

	/******************** ouput response


	{"epcList":[{"name":"KNIGHTHOOD","children":[{"name":"MENS FORMAL EASY CARE SHIRTS","children":[{"name":"KHSH-4201-EC-FILAFIL-A-FS-SD","children":[{"name":"MUSTARD","children":[{"name":"42-SF","children":[{"name":"8905011004554","children":[{"name":"3"}]}]},{"name":"40-SF","children":[{"name":"8905011004547","children":[{"name":"3"}]}]},{"name":"44-SF","children":[{"name":"8905011004561","children":[{"name":"3"}]}]},{"name":"39-SF","children":[{"name":"8905011004530","children":[{"name":"3"}]}]}]},{"name":"LAVENDER","children":[{"name":"42-SF","children":[{"name":"8905011004479","children":[{"name":"3"}]}]},{"name":"40-SF","children":[{"name":"8905011004462","children":[{"name":"3"}]}]},{"name":"44-SF","children":[{"name":"8905011004486","children":[{"name":"3"}]}]},{"name":"39-SF","children":[{"name":"8905011004455","children":[{"name":"3"}]}]}]},{"name":"LIME","children":[{"name":"42-SF","children":[{"name":"8905011004516","children":[{"name":"3"}]}]},{"name":"40-SF","children":[{"name":"8905011004509","children":[{"name":"3"}]}]},{"name":"44-SF","children":[{"name":"8905011004523","children":[{"name":"3"}]}]},{"name":"39-SF","children":[{"name":"8905011004493","children":[{"name":"3"}]}]}]}]}]}]},{"name":"CHAMPION","children":[{"name":"MENS SWEATSHIRTS","children":[{"name":"CHGW-3191-MCASW-02-B-FS-RF","children":[{"name":"NAVY","children":[{"name":"M","children":[{"name":"8905014430961","children":[{"name":"3"}]}]},{"name":"L","children":[{"name":"8905014430978","children":[{"name":"3"}]}]},{"name":"XL","children":[{"name":"8905014430985","children":[{"name":"3"}]}]},{"name":"XXL","children":[{"name":"8905014430992","children":[{"name":"3"}]}]}]},{"name":"Olive","children":[{"name":"M","children":[{"name":"8905014431012","children":[{"name":"3"}]}]},{"name":"L","children":[{"name":"8905014431029","children":[{"name":"3"}]}]},{"name":"XL","children":[{"name":"8905014431036","children":[{"name":"3"}]}]},{"name":"XXL","children":[{"name":"8905014431043","children":[{"name":"3"}]}]}]},{"name":"RED","children":[{"name":"M","children":[{"name":"8905014431067","children":[{"name":"3"}]}]},{"name":"L","children":[{"name":"8905014431074","children":[{"name":"3"}]}]},{"name":"XL","children":[{"name":"8905014431081","children":[{"name":"3"}]}]},{"name":"XXL","children":[{"name":"8905014431098","children":[{"name":"3"}]}]}]}]}]},{"name":"MENS SUMMER POLO","children":[{"name":"CHGW-4191-CST-03-B-HS-RF-CP","children":[{"name":"CORAL","children":[{"name":"S","children":[{"name":"8905014274732","children":[{"name":"3"}]}]},{"name":"XXL","children":[{"name":"8905014274770","children":[{"name":"3"}]}]}]}]}]}]},{"name":"SRISHTI","children":[{"name":"OTHER DUPATTA","children":[{"name":"LED-3191-10019-B--SF-SF-SG","children":[{"name":"PINK","children":[{"name":"FS","children":[{"name":"8905011000044","children":[{"name":"3"}]}]}]}]},{"name":"LED-3191-10018-B--SF-SF-LK","children":[{"name":"MUSTARD","children":[{"name":"FS","children":[{"name":"8905011000037","children":[{"name":"3"}]}]}]}]},{"name":"LED-3191-10020-B--SF-SF-LK","children":[{"name":"PEACH","children":[{"name":"FS","children":[{"name":"8905011000051","children":[{"name":"3"}]}]}]}]},{"name":"LED-3191-10017-B--SF-SF-UI","children":[{"name":"INDIGO","children":[{"name":"FS","children":[{"name":"8905011000020","children":[{"name":"3"}]}]}]}]},{"name":"LED-3191-10022-B--SF-SF-AS","children":[{"name":"GREY","children":[{"name":"FS","children":[{"name":"8905011000075","children":[{"name":"3"}]}]}]}]},{"name":"LED-3191-10021-B--SF-SF-SG","children":[{"name":"BLACK","children":[{"name":"FS","children":[{"name":"8905011000068","children":[{"name":"3"}]}]}]}]}]}]},{"name":"SHYLA","children":[{"name":"TANK TOPS","children":[{"name":"LNS-3191-RF-545-TNK","children":[{"name":"GREY MELANGE","children":[{"name":"XS","children":[{"name":"8905011066910","children":[{"name":"3"}]}]}]}]}]},{"name":"ESSENTIAL FULL LENGTH PYJAMA","children":[{"name":"LPY-4201-SI-831-PY","children":[{"name":"BLUE","children":[{"name":"XS","children":[{"name":"8905011000105","children":[{"name":"3"}]}]}]}]}]},{"name":"ANKLE SLEEPGOWNS","children":[{"name":"ANG-4201-MA-909","children":[{"name":"PINK","children":[{"name":"XS","children":[{"name":"8905011068600","children":[{"name":"3"}]}]},{"name":"S","children":[{"name":"8905011068617","children":[{"name":"3"}]}]},{"name":"M","children":[{"name":"8905011068624","children":[{"name":"3"}]}]}]}]}]},{"name":"SCARF","children":[{"name":"SCF-0010-A1950-JQ-PL-ASM","children":[{"name":"BLUE","children":[{"name":"70 X 180CMS","children":[{"name":"8905011000686","children":[{"name":"3"}]}]}]},{"name":"BLACK","children":[{"name":"70 X 180CMS","children":[{"name":"8905011000709","children":[{"name":"3"}]}]}]},{"name":"FUSCHIA","children":[{"name":"70 X 180CMS","children":[{"name":"8905011000693","children":[{"name":"3"}]}]}]}]},{"name":"SCF-3191-A1943-JQ-PL-SWM","children":[{"name":"BLACK","children":[{"name":"70 X 180CMS","children":[{"name":"8905011003878","children":[{"name":"3"}]}]}]},{"name":"PEACH","children":[{"name":"70 X 180CMS","children":[{"name":"8905011003885","children":[{"name":"3"}]}]}]}]}]}]},{"name":"ATEESA","children":[{"name":"KURTA","children":[{"name":"ATTW-4191-PN102-A-HL-DL-AV","children":[{"name":"BLUE","children":[{"name":"XS","children":[{"name":"8905014259616","children":[{"name":"3"}]}]},{"name":"S","children":[{"name":"8905014259623","children":[{"name":"3"}]}]},{"name":"M","children":[{"name":"8905014259630","children":[{"name":"3"}]}]},{"name":"L","children":[{"name":"8905014259647","children":[{"name":"3"}]}]},{"name":"XL","children":[{"name":"8905014259654","children":[{"name":"3"}]}]}]}]}]}]},{"name":"BARE","children":[{"name":"SHORTS - 3-4TH","children":[{"name":"CH-PRINTED-BOXER-DSG-02","children":[{"name":"BLUE","children":[{"name":"M","children":[{"name":"8905014479816","children":[{"name":"3"}]}]},{"name":"L","children":[{"name":"8905014479823","children":[{"name":"3"}]}]},{"name":"XL","children":[{"name":"8905014479830","children":[{"name":"3"}]}]},{"name":"XXL","children":[{"name":"8905014479847","children":[{"name":"3"}]}]}]}]}]}]},{"name":"BUFFALO","children":[{"name":"MENS HS ESSENTIAL TEES","children":[{"name":"BFTS-0010-HT-A-HS-RF-CT","children":[{"name":"MUSTARD","children":[{"name":"XL","children":[{"name":"8905011001591","children":[{"name":"3"}]}]},{"name":"M","children":[{"name":"8905011000914","children":[{"name":"3"}]}]},{"name":"L","children":[{"name":"8905011001492","children":[{"name":"3"}]}]},{"name":"S","children":[{"name":"8905011000198","children":[{"name":"3"}]}]},{"name":"XS","children":[{"name":"8905011000136","children":[{"name":"3"}]}]},{"name":"XXL","children":[{"name":"8905011002369","children":[{"name":"3"}]}]}]}]}]}]}],"missingEPCList":[{"EPC":"30361F84CC3BB880000000B9","EAN":"8905011611547"},{"EPC":"30361F84CC3BA1C00000047E","EAN":"8905011610632"},{"EPC":"30361F84CC1D3FC0000003E2","EAN":"8905011299516"},{"EPC":"30361F84CC364F80000F91C6","EAN":"8905011556145"},{"EPC":"30361F84CC3BBC400000035E","EAN":"8905011611691"},{"EPC":"30361F84CC3BCC00000000AA","EAN":"8905011612322"},{"EPC":"30361FAF540FB180000001E3","EAN":"8907733160709"},{"EPC":"30361F84CC364C80000F921C","EAN":"8905011556022"},{"EPC":"30361FAF541FF1C0000002C1","EAN":"8907733327119"},{"EPC":"30361F84CC304A00000002A3","EAN":"8905011494485"},{"EPC":"30361F84CC3F3BC000000374","EAN":"8905011647515"},{"EPC":"30361F84CC365180000F9231","EAN":"8905011556220"},{"EPC":"30361F84CC1D6C00000000BC","EAN":"8905011301288"},{"EPC":"30361F84CC1DB100000F9216","EAN":"8905011304043"},{"EPC":"30361F84CC5CAB000000038D","EAN":"8905011948926"},{"EPC":"30361F84CC3BBEC0000000BE","EAN":"8905011611790"},{"EPC":"30361FAF541FF1800000016A","EAN":"8907733327102"},{"EPC":"30361F84CC3BBC8000000279","EAN":"8905011611707"},{"EPC":"30361F84CC3F8B00000001AD","EAN":"8905011650683"},{"EPC":"30361F84CC406C0000000ED0","EAN":"8905011659686"},{"EPC":"30361F84CC364CC0000F9212","EAN":"8905011556039"},{"EPC":"30361F84CC3BA34000000225","EAN":"8905011610694"},{"EPC":"30361F84CC406C0000000ECF","EAN":"8905011659686"},{"EPC":"30361FAF549C548000000366","EAN":"8907733160082"},{"EPC":"30361F84CC3080000000047E","EAN":"8905011496649"},{"EPC":"30361F84CC3F6B4000000484","EAN":"8905011649410"},{"EPC":"30361F84CC3F7DC00000011B","EAN":"8905011650157"},{"EPC":"30361F84CC193FC000000097","EAN":"8905011258551"},{"EPC":"30361F84CC3BB6C00000029C","EAN":"8905011611479"},{"EPC":"30361F84CC5AE04000000292","EAN":"8905011930570"},{"EPC":"30361F84CC3BBD40000001C1","EAN":"8905011611738"},{"EPC":"30361F84CC5CAB400000021B","EAN":"8905011948933"},{"EPC":"30361FAF5439298000000125","EAN":"8907733585342"},{"EPC":"30361FAF540FB5400000056A","EAN":"8907733160853"},{"EPC":"30161F84CE834040000001C2","EAN":"8905011658689"},{"EPC":"30361FABEC582EC0000F9243","EAN":"8907515902992"},{"EPC":"30361F84CC3BB38000000834","EAN":"8905011611349"},{"EPC":"30361FABEC582F00000F923E","EAN":"8907515903005"},{"EPC":"30361F84CC3BCC80000006E4","EAN":"8905011612346"},{"EPC":"30361F84CC3F8A00000003FF","EAN":"8905011650645"},{"EPC":"30361FABEC2D27800000016C","EAN":"8907515462380"},{"EPC":"30361FAF544EE280000F924F","EAN":"8907733807789"},{"EPC":"30361F84CC469040000005CE","EAN":"8905011722571"},{"EPC":"30361F84CC406740000F922E","EAN":"8905011659495"},{"EPC":"30361F84CC421CC000000069","EAN":"8905011676997"},{"EPC":"30361F84CC365200000F9219","EAN":"8905011556244"},{"EPC":"30361F84CC364D40000F9223","EAN":"8905011556053"},{"EPC":"30361FAF541FE3C0000F91E6","EAN":"8907733326556"},{"EPC":"30361F84CC4069C00000092C","EAN":"8905011659594"},{"EPC":"30361F84CC406D0000000614","EAN":"8905011659723"},{"EPC":"30361FABEC0C4940000F91C3","EAN":"8907515125810"},{"EPC":"30361F84CC3BB50000000110","EAN":"8905011611400"},{"EPC":"30361F84CC1906800000026A","EAN":"8905011256267"},{"EPC":"30361FAF54182600000F924A","EAN":"8907733247288"},{"EPC":"30361F84CC365240000F922F","EAN":"8905011556251"},{"EPC":"30361F84CC4067400000050E","EAN":"8905011659495"},{"EPC":"30361F84CC3BB4C0000F91FE","EAN":"8905011611394"},{"EPC":"30361F84CC40BD0000000211","EAN":"8905011662921"},{"EPC":"30361F84CC40670000000D2C","EAN":"8905011659488"},{"EPC":"30361F84CC3653C00000029C","EAN":"8905011556312"},{"EPC":"30361F84CC364F80000F91D6","EAN":"8905011556145"},{"EPC":"30361F84CC4F348000000041","EAN":"8905011811060"},{"EPC":"30361F84CC3BCB4000000020","EAN":"8905011612292"},{"EPC":"30161F84CE445180000000D7","EAN":"8905011594246"},{"EPC":"30361F84CC3D0900000004AB","EAN":"8905011625001"},{"EPC":"30361FAF540FB58000000177","EAN":"8907733160860"},{"EPC":"30361F84CC3F9FC000000250","EAN":"8905011651512"},{"EPC":"30361F84CC5AF78000000030","EAN":"8905011931508"},{"EPC":"30361F84CC4041C0000000F6","EAN":"8905011657996"},{"EPC":"30361F84CC3BA14000000609","EAN":"8905011610618"},{"EPC":"30161F84CE8343800000020D","EAN":"8905011658702"},{"EPC":"30361F84CC3BBE4000000035","EAN":"8905011611776"},{"EPC":"30361FAF540FB0C0000000AA","EAN":"8907733160679"},{"EPC":"30361F84CC364D00000F91C2","EAN":"8905011556046"},{"EPC":"30361F84CC406800000006AD","EAN":"8905011659525"},{"EPC":"30361F84CC3BB4800000008C","EAN":"8905011611387"},{"EPC":"30361F84CC40678000000525","EAN":"8905011659501"},{"EPC":"30361F84CC421C40000002C9","EAN":"8905011676973"},{"EPC":"30361F84CC3D4B00000F91D5","EAN":"8905011627647"},{"EPC":"30361F84CC3BCC8000000625","EAN":"8905011612346"},{"EPC":"30361F84CC364CC0000F91F9","EAN":"8905011556039"},{"EPC":"30361F84CC364D00000F91F4","EAN":"8905011556046"},{"EPC":"30361F84CC190680000000C1","EAN":"8905011256267"},{"EPC":"30361F84CC5AFD40000000DE","EAN":"8905011931737"},{"EPC":"30361F84CC3F1B0000000309","EAN":"8905011646204"},{"EPC":"30361F84CC4064C000000AAC","EAN":"8905011659396"},{"EPC":"30361FAF541FF1C0000001F6","EAN":"8907733327119"},{"EPC":"30161F84CE834040000004D7","EAN":"8905011658689"},{"EPC":"30361F84CC3EE3C0000000E6","EAN":"8905011643999"},{"EPC":"30361F84CC3BA2400000006D","EAN":"8905011610656"},{"EPC":"30361F84CC3BB780000007AB","EAN":"8905011611509"},{"EPC":"30361F84CC1D5100000F91FC","EAN":"8905011300205"},{"EPC":"30361F84CC3F38C000000181","EAN":"8905011647393"},{"EPC":"30361F84CC3BB5400000081A","EAN":"8905011611417"},{"EPC":"30361F84CC1A1EC0000000A4","EAN":"8905011267478"},{"EPC":"30361F84CC3BBA000000005C","EAN":"8905011611608"},{"EPC":"30161F84CE444B8000000A45","EAN":"8905011594222"},{"EPC":"30361F84CC5442000000009E","EAN":"8905011862802"},{"EPC":"30361F84CC5CAAC0000007C0","EAN":"8905011948919"},{"EPC":"30361F84CC3BA40000000273","EAN":"8905011610724"},{"EPC":"30361F84CC364FC0000F9234","EAN":"8905011556152"},{"EPC":"30361F84CC3F3C80000F922A","EAN":"8905011647546"},{"EPC":"30361F84CC3BA34000000787","EAN":"8905011610694"},{"EPC":"30361F84CC3BBE4000000125","EAN":"8905011611776"},{"EPC":"30361F84CC3FAE000000002A","EAN":"8905011652083"},{"EPC":"30361FAF540FB54000000443","EAN":"8907733160853"},{"EPC":"30361F84CC3F514000000289","EAN":"8905011648376"},{"EPC":"30361F84CC455BC0000000AE","EAN":"8905011710233"},{"EPC":"30361F84CC40AFC00000018A","EAN":"8905011662396"},{"EPC":"30361F84CC406C0000000EA1","EAN":"8905011659686"},{"EPC":"30361F84CC3F8CC0000000C3","EAN":"8905011650751"},{"EPC":"30361F84CC3F6B800000057E","EAN":"8905011649427"},{"EPC":"30361F84CC192A800000003C","EAN":"8905011257707"},{"EPC":"30361F84CC191880000000F5","EAN":"8905011256984"},{"EPC":"30361F84CC3D0A000000061A","EAN":"8905011625049"},{"EPC":"30361F84CC406500000001EC","EAN":"8905011659402"},{"EPC":"30361F84CC364F80000F9205","EAN":"8905011556145"},{"EPC":"30361F84CC3BB38000000C4A","EAN":"8905011611349"},{"EPC":"30361F84CC3F6BC000000122","EAN":"8905011649434"},{"EPC":"30361F84CC3BB780000F9203","EAN":"8905011611509"},{"EPC":"30361F84CC364FC0000F91EA","EAN":"8905011556152"},{"EPC":"30361F84CC3D0E400000030F","EAN":"8905011625216"},{"EPC":"30361F84CC365200000F91ED","EAN":"8905011556244"},{"EPC":"30361FAF540FB3C0000000B8","EAN":"8907733160792"},{"EPC":"30361F84CC3F42C000000080","EAN":"8905011647799"},{"EPC":"30361F84CC3BB9C0000002D6","EAN":"8905011611592"},{"EPC":"30361F84CC3F3C00000000D2","EAN":"8905011647522"},{"EPC":"30361F84CC406C0000000EC7","EAN":"8905011659686"},{"EPC":"30361F84CC3F418000000603","EAN":"8905011647744"},{"EPC":"30361F84CC46904000000068","EAN":"8905011722571"},{"EPC":"30361F84CC406CC000000655","EAN":"8905011659716"},{"EPC":"30361F84CC468740000000BF","EAN":"8905011722212"},{"EPC":"30361FAF540FB480000001D3","EAN":"8907733160822"},{"EPC":"30361F84CC364DC0000F91F0","EAN":"8905011556077"},{"EPC":"30361F84CC1FCD40000F91F7","EAN":"8905011325659"},{"EPC":"30361F84CC468FC000000081","EAN":"8905011722557"},{"EPC":"30361F84CC40710000000153","EAN":"8905011659884"},{"EPC":"30361F84CC3BB740000F9217","EAN":"8905011611493"},{"EPC":"30161F84CE75424000000185","EAN":"8905011644361"},{"EPC":"30361F84CC5AE5400000023F","EAN":"8905011930778"},{"EPC":"30361FAF543927C0000002EC","EAN":"8907733585274"},{"EPC":"30361F84CC40414000000151","EAN":"8905011657972"},{"EPC":"30161F84CE444B80000004B1","EAN":"8905011594222"},{"EPC":"30361F84CC3D0E800000074C","EAN":"8905011625223"},{"EPC":"30361F84CC40650000000472","EAN":"8905011659402"},{"EPC":"30361F84CC3BCE000000007E","EAN":"8905011612407"},{"EPC":"30361F84CC3BBC400000074D","EAN":"8905011611691"},{"EPC":"30361F84CC3BA140000005C0","EAN":"8905011610618"},{"EPC":"30161F84CE4449C0000002BB","EAN":"8905011594215"},{"EPC":"30161F84CE1F4F8000000729","EAN":"8905011556350"},{"EPC":"30361F84CC3BB64000000644","EAN":"8905011611455"},{"EPC":"30361F84CC18FDC0000001A5","EAN":"8905011255918"},{"EPC":"30361F84CC4F38C0000003C3","EAN":"8905011811237"},{"EPC":"30361F84CC364D00000F9242","EAN":"8905011556046"},{"EPC":"30361F84CC3F91C00000007A","EAN":"8905011650959"},{"EPC":"30161FAF549CE9C000000087","EAN":"8907733160679"},{"EPC":"30361FAF544EE2400000012B","EAN":"8907733807772"},{"EPC":"30361F84CC3FAD800000011C","EAN":"8905011652069"},{"EPC":"30361F84CC3BBC4000000373","EAN":"8905011611691"},{"EPC":"30361F84CC3BA340000002D3","EAN":"8905011610694"},{"EPC":"30361FAF542EA200000020D4","EAN":"8907733477524"},{"EPC":"30361F84CC365000000F91CA","EAN":"8905011556169"},{"EPC":"30361F84CC3BB40000000408","EAN":"8905011611363"},{"EPC":"30361F84CC3D4780000008CB","EAN":"8905011627500"},{"EPC":"30361F84CC406C80000004D7","EAN":"8905011659709"},{"EPC":"30361FAF549C548000000359","EAN":"8907733160082"},{"EPC":"30361F84CC3D0E00000004EB","EAN":"8905011625209"},{"EPC":"30361F84CC3BBE40000003CD","EAN":"8905011611776"},{"EPC":"30361F84CC3BA1C0000003A0","EAN":"8905011610632"},{"EPC":"30361F84CC455D0000000229","EAN":"8905011710288"},{"EPC":"30361F84CC406D4000000414","EAN":"8905011659730"},{"EPC":"30361F84CC3BA380000007D9","EAN":"8905011610700"},{"EPC":"30361FAF541C59800000003E","EAN":"8907733290307"},{"EPC":"30361F84CC3BA38000000310","EAN":"8905011610700"},{"EPC":"30361F84CC3BB480000003D5","EAN":"8905011611387"},{"EPC":"30361F84CC1BF4C000000041","EAN":"8905011286271"},{"EPC":"30361F84CC3F398000000156","EAN":"8905011647423"},{"EPC":"30361F84CC5441800000008E","EAN":"8905011862789"},{"EPC":"30361F84CC3BBE40000F91DF","EAN":"8905011611776"},{"EPC":"30361F84CC32E640000000B0","EAN":"8905011521211"},{"EPC":"30361F84CC404200000001DE","EAN":"8905011658009"},{"EPC":"30361F84CC3BA30000000001","EAN":"8905011610687"},{"EPC":"30361F84CC1D36000000027A","EAN":"8905011299127"},{"EPC":"30361FAF540FB58000000386","EAN":"8907733160860"},{"EPC":"30361F84CC364D40000F920F","EAN":"8905011556053"},{"EPC":"30361F84CC3BBA000000012B","EAN":"8905011611608"},{"EPC":"30361F84CC3BB38000000838","EAN":"8905011611349"},{"EPC":"30361F84CC4F34C0000003E0","EAN":"8905011811077"},{"EPC":"30361F84CC3BBC0000000035","EAN":"8905011611684"},{"EPC":"30361F84CC4042C00000017A","EAN":"8905011658030"},{"EPC":"30361F84CC3F7BC0000001B1","EAN":"8905011650072"},{"EPC":"30361F84CC3F86C000000040","EAN":"8905011650515"},{"EPC":"30361F84CC5DD540000007D8","EAN":"8905011960850"},{"EPC":"30361F84CC3F95400000016D","EAN":"8905011651093"},{"EPC":"30361F84CC3F3A00000005E5","EAN":"8905011647447"},{"EPC":"30361F84CC3BBD800000011C","EAN":"8905011611745"},{"EPC":"30361F84CC60D040000000E5","EAN":"8905011991373"},{"EPC":"30361FAF540FB540000F9215","EAN":"8907733160853"},{"EPC":"30361F84CC18FDC0000001C1","EAN":"8905011255918"},{"EPC":"30361F84CC455CC0000000EB","EAN":"8905011710271"},{"EPC":"30361F84CC1D7E0000000275","EAN":"8905011302001"},{"EPC":"30161FAF549C45000000032D","EAN":"8907733160020"},{"EPC":"30361FAF549C69C00000017D","EAN":"8907733160167"},{"EPC":"30361F84CC40670000000089","EAN":"8905011659488"},{"EPC":"30361F84CC3BA340000002BB","EAN":"8905011610694"},{"EPC":"30361F84CC5CAAC00000085C","EAN":"8905011948919"},{"EPC":"30361FAF541C5A800000045A","EAN":"8907733290345"},{"EPC":"30361F84CC1A36C0000000A4","EAN":"8905011268437"},{"EPC":"30361F84CC3BA340000001B9","EAN":"8905011610694"},{"EPC":"30361F84CC1D518000000169","EAN":"8905011300229"},{"EPC":"30361FABEC0C4B40000F91D1","EAN":"8907515125896"},{"EPC":"30361F84CC1929C00000016B","EAN":"8905011257677"},{"EPC":"30361FABEC0C4940000F9233","EAN":"8907515125810"},{"EPC":"30361F84CC364F80000F9227","EAN":"8905011556145"},{"EPC":"30361F84CC3BB700000F91CF","EAN":"8905011611486"},{"EPC":"30361F84CC3BB88000000018","EAN":"8905011611547"},{"EPC":"30361FABEC2D27000000011E","EAN":"8907515462366"},{"EPC":"30361F84CC1DB1000000028F","EAN":"8905011304043"},{"EPC":"30361F84CC3BBC400000071C","EAN":"8905011611691"},{"EPC":"30361F84CC3F868000000305","EAN":"8905011650508"},{"EPC":"30361F84CC5AFE000000006E","EAN":"8905011931768"},{"EPC":"30361F84CC191800000000C1","EAN":"8905011256960"},{"EPC":"30361F84CC3BBE4000000159","EAN":"8905011611776"},{"EPC":"30361F84CC3BB8C00000020D","EAN":"8905011611554"},{"EPC":"30361F84CC3BA2C0000005B3","EAN":"8905011610670"},{"EPC":"30361F84CC3BA38000000334","EAN":"8905011610700"},{"EPC":"30361F84CC3F040000000287","EAN":"8905011645283"},{"EPC":"30361FAF545D1A80000F91EC","EAN":"8907733953387"},{"EPC":"30361FAF541C5A0000000507","EAN":"8907733290321"},{"EPC":"30361F84CC1E7C40000006F4","EAN":"8905011312178"},{"EPC":"30361F84CC3BA380000007CF","EAN":"8905011610700"},{"EPC":"30361F84CC3BA38000000338","EAN":"8905011610700"},{"EPC":"30361F84CC1918400000003F","EAN":"8905011256977"},{"EPC":"30361F84CC1CB08000000480","EAN":"8905011293781"},{"EPC":"30361F84CC3BB70000000150","EAN":"8905011611486"},{"EPC":"30161F84CE834200000000AE","EAN":"8905011658696"},{"EPC":"30361F84CC3F71C000000204","EAN":"8905011649670"},{"EPC":"30361F84CC40654000000692","EAN":"8905011659419"},{"EPC":"30361F84CC364FC0000F924B","EAN":"8905011556152"},{"EPC":"30361F84CC3F8A40000001E1","EAN":"8905011650652"},{"EPC":"30361F84CE792040000000B3","EAN":"8905011648321"},{"EPC":"30361F84CC5AE9C00000003D","EAN":"8905011930952"},{"EPC":"30361F84CC409D8000000181","EAN":"8905011661665"},{"EPC":"30361F84CC1D3AC000000264","EAN":"8905011299318"},{"EPC":"30361F84CC3BB84000000534","EAN":"8905011611530"},{"EPC":"30361F84CC3BB68000000723","EAN":"8905011611462"},{"EPC":"30361F84CC406C0000000ECD","EAN":"8905011659686"},{"EPC":"30361F84CC192A0000000253","EAN":"8905011257684"},{"EPC":"30361F84CC1BFA00000004EC","EAN":"8905011286486"},{"EPC":"30361FAF54E0FD40000001E7","EAN":"8907733230389"},{"EPC":"30361F84CC19188000000226","EAN":"8905011256984"},{"EPC":"30361F84CC3BB74000000227","EAN":"8905011611493"},{"EPC":"30361F84CC3BB840000003A2","EAN":"8905011611530"},{"EPC":"30361F84CC3BB380000002A1","EAN":"8905011611349"},{"EPC":"30361F84CC406A8000000638","EAN":"8905011659624"},{"EPC":"30361FAF544EEBC000000356","EAN":"8907733808151"},{"EPC":"30361F84CC3BB380000006B7","EAN":"8905011611349"},{"EPC":"30361F84CC1D99C000000200","EAN":"8905011303114"},{"EPC":"30361F84CC409D4000000042","EAN":"8905011661658"},{"EPC":"30361F84CC3BB34000000666","EAN":"8905011611332"},{"EPC":"30361F84CC364CC0000F9232","EAN":"8905011556039"},{"EPC":"30361F84CC3F6B0000000139","EAN":"8905011649403"},{"EPC":"30361FAF541FF18000000266","EAN":"8907733327102"},{"EPC":"30361F84CC1CB180000000D9","EAN":"8905011293828"},{"EPC":"30361F84CC365200000F91F5","EAN":"8905011556244"},{"EPC":"30361F84CC1A29C000000075","EAN":"8905011267911"},{"EPC":"30361F84CC468FC0000F91FD","EAN":"8905011722557"},{"EPC":"30361F84CC3D4B4000000096","EAN":"8905011627654"},{"EPC":"30361FAF540FB0C000000740","EAN":"8907733160679"},{"EPC":"30361F84CC4932C0000F91DA","EAN":"8905011749554"},{"EPC":"30361F84CC3F044000000075","EAN":"8905011645290"},{"EPC":"30361FABEC3DAA4000000466","EAN":"8907515631458"},{"EPC":"30361F84CC3BBAC00000026F","EAN":"8905011611639"},{"EPC":"30361F84CC3D4A80000F9204","EAN":"8905011627623"},{"EPC":"30361FAF54188400000F91DE","EAN":"8907733251049"},{"EPC":"30361F84CC3D49C00000085A","EAN":"8905011627593"},{"EPC":"30361FABEC0C4CC0000F9209","EAN":"8907515125957"},{"EPC":"30361F84CC3F3C80000F9214","EAN":"8905011647546"},{"EPC":"30361F84CC364C80000F923C","EAN":"8905011556022"},{"EPC":"30361F84CC3BA38000000353","EAN":"8905011610700"},{"EPC":"30361F84CE78E740000001B4","EAN":"8905011648093"},{"EPC":"30361F84CC406780000004E4","EAN":"8905011659501"},{"EPC":"30361F84CC406580000006F9","EAN":"8905011659426"},{"EPC":"30361F84CC19070000000220","EAN":"8905011256281"},{"EPC":"30361F84CC1DB300000F91D7","EAN":"8905011304128"},{"EPC":"30361F84CC3F3EC000000288","EAN":"8905011647638"},{"EPC":"30361F84CC46E08000000137","EAN":"8905011725787"},{"EPC":"30361F84CC3F79C000000181","EAN":"8905011649991"},{"EPC":"30161F84CE7516C0000001CE","EAN":"8905011644187"},{"EPC":"30361F84CC1D8A80000000F3","EAN":"8905011302506"},{"EPC":"30361F84CC3BA2C000000333","EAN":"8905011610670"},{"EPC":"30361F84CC1D4B40000003C6","EAN":"8905011299974"},{"EPC":"30361F84CC3BBAC0000000D5","EAN":"8905011611639"},{"EPC":"30161F84CE83498000000355","EAN":"8905011658726"},{"EPC":"30361FAF54392980000006D0","EAN":"8907733585342"},{"EPC":"30361F84CC468FC0000003FA","EAN":"8905011722557"},{"EPC":"30361F84CC18FE40000000BA","EAN":"8905011255932"},{"EPC":"30161F84CE83420000000063","EAN":"8905011658696"},{"EPC":"30361F84CC5AFDC00000007B","EAN":"8905011931751"},{"EPC":"30361F84CE7918C000000375","EAN":"8905011648291"},{"EPC":"30361F84CC364CC0000F91FB","EAN":"8905011556039"},{"EPC":"30361F84CC3BA340000003BC","EAN":"8905011610694"},{"EPC":"30361F84CC3F3C40000002D3","EAN":"8905011647539"},{"EPC":"30361F84CC3F0BC0000001F2","EAN":"8905011645597"},{"EPC":"30361F84CC42D940000001D2","EAN":"8905011684534"},{"EPC":"30361FAF549C63C0000001E2","EAN":"8907733160143"},{"EPC":"30361F84CC3BB380000F921D","EAN":"8905011611349"},{"EPC":"30361F84CC5B014000000242","EAN":"8905011931898"},{"EPC":"30361F84CC3BBD4000000208","EAN":"8905011611738"},{"EPC":"30361F84CC5AE2C000000021","EAN":"8905011930679"},{"EPC":"30361F84CC3F7C0000000264","EAN":"8905011650089"},{"EPC":"30361F84CC46908000000179","EAN":"8905011722588"},{"EPC":"30361FABEC3DA6C0000F91DC","EAN":"8907515631311"},{"EPC":"30361FAF54188400000F920C","EAN":"8907733251049"},{"EPC":"30361F84CC3F15C0000003A2","EAN":"8905011645993"},{"EPC":"30361F84CC42A6C0000F9220","EAN":"8905011682516"},{"EPC":"30361F84CC3BB38000000246","EAN":"8905011611349"},{"EPC":"30361F84CC364C80000F91F8","EAN":"8905011556022"},{"EPC":"30361F84CC191800000000EA","EAN":"8905011256960"},{"EPC":"30361F84CC3F938000000006","EAN":"8905011651024"},{"EPC":"30361F84CC364FC0000F9206","EAN":"8905011556152"},{"EPC":"30361F84CC3BBAC000000392","EAN":"8905011611639"},{"EPC":"30361F84CC3D49C00000083F","EAN":"8905011627593"},{"EPC":"30361F84CC4067C000000811","EAN":"8905011659518"},{"EPC":"30361F84CC54414000000401","EAN":"8905011862772"},{"EPC":"30361F84CC19FBC00000008C","EAN":"8905011266075"},{"EPC":"30361F84CC3EF6800000007D","EAN":"8905011644743"},{"EPC":"30361F84CC3BBC4000000701","EAN":"8905011611691"},{"EPC":"30361F84CC1FCC0000000080","EAN":"8905011325604"},{"EPC":"30361F84CC3BA18000000009","EAN":"8905011610625"},{"EPC":"30361F84CC1D51800000013C","EAN":"8905011300229"},{"EPC":"30361F84CC3BA380000007DB","EAN":"8905011610700"},{"EPC":"30361F84CC5441C0000001AC","EAN":"8905011862796"},{"EPC":"30361F84CC3F4080000F91D9","EAN":"8905011647706"},{"EPC":"30361F84CC3BA3400000067B","EAN":"8905011610694"},{"EPC":"30361F84CC4F340000000236","EAN":"8905011811046"},{"EPC":"30361F84CC364C80000F9210","EAN":"8905011556022"},{"EPC":"30361F84CC40670000000DA4","EAN":"8905011659488"},{"EPC":"30361F84CC3BA34000000312","EAN":"8905011610694"},{"EPC":"30361F84CC5AE300000001BF","EAN":"8905011930686"},{"EPC":"30361F84CC3BA3C000000010","EAN":"8905011610717"},{"EPC":"30361F84CC4069C000000AAB","EAN":"8905011659594"},{"EPC":"30161F84CE834D00000000E8","EAN":"8905011658740"},{"EPC":"30361F84CC5AF840000F91FA","EAN":"8905011931539"},{"EPC":"30361F84CC3BBD4000000002","EAN":"8905011611738"},{"EPC":"30361F84CC1D6940000F9222","EAN":"8905011301172"}]}


	********************************************************/

}
catch(Exception $e) {

  $response = array('status' => '0','msg' => $e);
	echo json_encode($response);
	exit;
}




?>