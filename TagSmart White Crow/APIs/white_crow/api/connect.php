<?php
// db info
$host        = "host = tagsmart.cnxgzwcfzpdc.ap-south-1.rds.amazonaws.com";
   $port        = "port = 5432";
   $dbname      = "dbname = tagsmart";
   $credentials = "user = postgres password=EQ5telXUFJW8oAXcTcfY";

   $db = pg_connect( "$host $port $dbname $credentials"  );
   if(!$db) {
      die("Error : Unable to open database\n"); 
   } 

?> 