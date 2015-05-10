<?php
/* define ('HOST','db4free.net');
define ('USERNAME','private');
define ('PASSWORD','private');
define ('DBNAME','private'); */

define ('HOST','mysql.hostinger.co.uk');
define ('USERNAME','private');
define ('PASSWORD','private');
define ('DBNAME','private');

$db;

function connectDb(){
     global $db;
     $db = mysql_connect(HOST, USERNAME, PASSWORD);
     if (mysqli_connect_errno($db )){
       echo "Failed to connect to MySQL: " . mysqli_connect_error();
     }

     if( $db ){
		mysql_select_db(DBNAME);
		//echo '<h1>connectado <br /></h1>';
		return $db;
     } else{
		//echo 'false <br />';
		return false;
     }
}



function runQuery($query){
	global $db;
	$result = mysql_query($query,$db);
	while($row=(mysql_fetch_assoc($result))){
		print_r ($row);
	}
	
}

function getInstitutoNIP($noInst){
	global $db;
	$query = "SELECT * FROM instituto WHERE id = '{$noInst}'";
	$result = mysql_query ($query, $db);
	//print_r(mysql_fetch_array($result));
	
	while($row=(mysql_fetch_assoc($result))){
		return $row['url_sii'];
	}
	return "";
}

function getAlumnoNip($noControl){
	global $db;
	$query = "SELECT * FROM alumno WHERE noControl = '{$noControl}'";
	$result = mysql_query ($query, $db);
	//print_r(mysql_fetch_array($result));
	
	while($row=(mysql_fetch_assoc($result))){
		return $row['nip'];
	}
	return "";
}

function saveNIP($noControl, $nip, $InstitutoID){
	global $db;
	$query = "insert into alumno values('{$noControl}','{$nip}',NULL,NULL,{$InstitutoID}) on duplicate key update nip='{$nip}'; ";
	$result = mysql_query($query,$db);
	return $result;
}

function saveUser($p1,$p2,$p3,$p4,$p5){
	global $db;
	$query = "REPLACE into usuario values('{$p1}','{$p2}','{$p3}','{$p4}','{$p5}');";
	//echo 'SAVED: '.$p1.' '.$p2.' '.$p3.' '.$p4.' '.$p5;
	$result = mysql_query($query,$db);
	return $result;
}

function saveUserQuery($p1, $p2){
	global $db;
	$query = "insert into queries values(NULL,'{$p1}','{$p2}',now());";
	$result = mysql_query($query,$db);
	return $result;
}


function getProducts($category = 0){
	global $db;
	
	if($category == 0){
		$query 	= "select * from products order by name";	
	}
	else{
		$query 	= "select * from products where category = {$category} order by name";	
	}
		
	$result = mysql_query ($query, $db);
	$output = '';
	
	while($row=(mysql_fetch_assoc($result))){
			$output .= '<li>';
			$output .= '<div class="productimg">';
			$output .= '<img src="http://media01.gameloft.com/products/' .$row['id'] .'/default/wap/android/artwork/'.$row['id'].'.jpg" />';
			$output .= '</div>';
			
			$output .= '<div class="productdata">';
			$output .= '<a href="#"> ' .$row['name'] .' </a>';
			$output .= '</div>';
			$output .= '<div class="clear"> </div>';
			$output .= '</li>';
	}
	return $output;
}
//init MySQL Connection
connectDb();
?>