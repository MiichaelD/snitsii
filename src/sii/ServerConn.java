package sii;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Scanner;

import serverComm.ServerCom;

public class ServerConn extends ServerCom{
	
	private static ServerConn instance;
	
	public static ServerConn shared(){
		if(instance == null)
			instance = new ServerConn();
		return instance;
	}
	
	private ServerConn(){
		super();
		m_mainUrl = "http://yo-t.besaba.com/solution/index.php";// hosteada en http://hostinger.co.uk/
		//m_mainUrl = "http://itmexicali.pusku.com/solution/index.php"; // hosteada en
		m_requestProperties = new HashMap<String, String>();
		m_requestProperties.put("X-Requested-With","MiichaelD");
	}
	
	public boolean isNetworkAvailable(){
		return true;
	}
	
	public static void main(String args[]){
        @SuppressWarnings("resource")
		Scanner in=new Scanner(System.in);
        System.out.print("Write a message to the server: ");
        try{
        	HashMap<String, String> query = new HashMap<String, String>();
        	query.put("snit", "MATARILE");
        	int i = 0;
        	while(in.hasNext()){
        		query.put("p"+(++i),in.next());
        	}
        	
            HttpURLConnection connection = ServerConn.shared().openConnection(query);
            System.out.println("Server's GET response: \'" +ServerConn.shared().getResponse(connection)+"\'");

            connection = ServerConn.shared().openConnection(Method.POST,ServerConn.shared().m_mainUrl,query);
            System.out.println("Server's POST response: \'"+shared().getResponse(connection)+"\'");
        }catch (Exception e){    e.printStackTrace();        System.out.println(e.getMessage());    }

    }

}
