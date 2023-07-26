<?php
	ob_start();
	session_start(); 
	header('Cache-control: private');
	//include("GetStoreByCustomerId.php");
	include("GetBarcodeByEpc.php");

$epcStr=$_REQUEST["epc"];
//$responses = array();

$obkStock=json_decode(GetStoreByEpc($epcStr));
$barcode=$obkStock->barcode;
//$responses['barcode'] =$barcode;	
echo $barcode;
//echo  json_encode($responses);
?>
