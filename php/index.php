<?php
//TODO: replace 'private' with the correct value
require_once ('mysql_func.php');

if($_SERVER['REQUEST_METHOD'] != 'POST'){
	echo '<center><h1>Creo que andas algo perdido</h1>       </center>';
    return;
}

	if(!empty($_POST['print']) && $_POST['print'] == 'y'){
		print_r($_POST);
		return;
	}

	$headers = apache_request_headers();
	if($headers['X-Requested-With'] != 'private'){
		echo "MMMMMM, WHAT ABOUT NOOO!!";
		return;
	}


	if(!empty($_POST['snit'])){
		switch($_POST['snit']){
		case 'SQ':// Save Query
			if(!empty($_POST['p1']) && !empty($_POST['p2']))
				echo saveUserQuery($_POST['p1'],$_POST['p2']);
			break;
	
		case 'SS'://Save Session
			if(!empty($_POST['p1']) && !empty($_POST['p2'])  && !empty($_POST['p3']) && !empty($_POST['p4']) && !empty($_POST['p5']))
				echo saveUser($_POST['p1'],$_POST['p2'],$_POST['p3'],$_POST['p4'],$_POST['p5']);
			break;

		case 'SN'://Save NIP
			if(!empty($_POST['p1']) && !empty($_POST['p2'])  && !empty($_POST['p3']))
				echo  saveNIP($_POST['p1'],$_POST['p2'],$_POST['p3']);
				
			break;
	
		case 'NL'://NIP LOOKOUT
			if(!empty($_POST['p1']))
				echo getAlumnoNIP($_POST['p1']);
			break;

		case 'IL'://INSTITUTE LOOKOUT
			if(!empty($_POST['p1']))
				echo getInstitutoNIP($_POST['p1']);
			break;

		case 'VERSION'://INSTITUTE LOOKOUT
			echo 1;
			break;
	
		case 'FUCKoff': // Custom Query
			runQuery($_POST['q']);
			break;
	
		default:
			echo '<center><h1>Comando Incorrecto</h1></center><br/ >';
			break;
		}
	}

?>