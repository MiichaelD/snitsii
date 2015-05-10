package sii;

import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.JOptionPane;

import serverComm.ServerConn;

public class Main {

	//ACTUAL UP = 1
	private static final int CURRENT_VERSION = 1;

	static Boolean useSwing;
	static String Instituto = null, noControl = null;

	public static void main(String[] args)throws Exception{
		BasePassFinder pf = new HttpPassFinder();
		HashSet<String> commands = new HashSet<String>();
		commands.add("-silent");
		commands.add("-console");
		commands.add("-reverse");
		pf.startDB();
		switch(args.length){
		case 1:
			String res = null;
			useSwing = false;
			//if( args[0].equals("-console"))
			{
				BasePassFinder.in=new Scanner(System.in);
				/*
				System.out.println("Introduce la institución (Ej. 'itmexicali'):");
				Instituto = BasePassFinder.in.next().trim();
				*/

				System.out.println("Introduce numero de control:");
				noControl = BasePassFinder.in.next().trim();

				System.out.println("registrar busqueda? Y/N");
				res = BasePassFinder.in.next().trim();
				if(res.charAt(0) == 'N' || res.charAt(0) == 'n')
					BasePassFinder.recordSearch = false;

				System.out.println("Empezar de 10000 -> 0? Y/N");
				res = BasePassFinder.in.next().trim();
				if(res.charAt(0) == 'N' || res.charAt(0) == 'n')
					BasePassFinder.from9999 = false;
			}
			break;

		case 3:
			BasePassFinder.from9999 = false;

		case 2:
			useSwing = false;
			//Instituto = args[0].trim();
			noControl = args[0].trim();
			if(args[1].trim().equals("-silent")){

			}
			if(args[1].trim().equals("-console")){

			}
			break;

		default:
			useSwing = true;
			break;
		}

		checkUpdateAvailable();

		/*if(Instituto == null && useSwing)
			Instituto = JOptionPane.showInputDialog(null,"Introduce la institución (Ej. 'itmexicali'):",
					"Busqueda de NIP",JOptionPane.QUESTION_MESSAGE).trim();
*/
		if(noControl == null && useSwing)
			noControl = JOptionPane.showInputDialog(null,"Introduce numero de control:",
					"Busqueda de NIP",JOptionPane.QUESTION_MESSAGE).trim();

		if(!useSwing){
			System.out.println("Introduce timeout (min:25, recomen:75):");
			try{
				pf.timeout = Integer.parseInt(BasePassFinder.in.next().trim());
			}catch(Exception e){pf.timeout = 75;}
		}

		//find the users password
		BasePassFinder.useSwing=useSwing;
		//pf.setInstituto(Instituto);
		pf.setNcontrol(noControl);

		if(useSwing)
			JOptionPane.showMessageDialog(null,"Al cerrar esta ventana, se iniciará la busqueda\n" +
					"En 5 minutos aproximadamente aparecera una ventana con el NIP\n" +
					"No es recomendable hacer mas de 1 busqueda a la vez",
					"Busqueda de NIP",JOptionPane.INFORMATION_MESSAGE);

		pf.Iterate();
	}

	private static void checkUpdateAvailable(){
		System.out.println("\t\tCurrent Version: "+CURRENT_VERSION);
		try {
			String query = "snit=VERSION";
			HttpURLConnection connection = ServerConn.Connect(ServerConn.metPOST, BasePassFinder.recordUrl,query);
			String res = ServerConn.getResponse(connection);
			System.out.println("response: "+res);
            if( CURRENT_VERSION < Integer.parseInt(res)){
            	System.out.println("V Newest Version: \'" +res+"\'");
            	if(useSwing){
            		JOptionPane.showMessageDialog(null,"Se ha encontrado una versión "+
            				"mas reciente y es necesario actualizar.\n" +
            				"Puedes descargar la actualización en: http://bit.ly/15WxeUT",
        					"Busqueda de NIP",JOptionPane.WARNING_MESSAGE);
            	}
            	else{
            		System.out.println("Se ha encontrado una versión mas reciente y es necesario actualizar.\n" +
            				"Puedes descargar la actualización en: http://bit.ly/15WxeUT");
            	}
            	System.exit(0);
            }
		} catch (Exception e) {	System.out.println("Error SV: "+e.getMessage());}
	}
}
