package test;
import hub.Hub;
import security.AES;


public class TestHub extends Thread {
	
	private static AES hubAESObject;
	@SuppressWarnings("unused")
	private static Hub hub;
	
	@SuppressWarnings("unused")
	private void initialize(){
		//AES initialize
		hubAESObject = new AES("password".toCharArray());	//default
		
		//Create hub
		hub = new Hub(hubAESObject);
		
		
	}
	
	
	/*
	 * main running loop 
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run(){
		
		
	}
}
