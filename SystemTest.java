/*
 * Tester to isolate failures to different parts of the system
 */

/*
 * Possible tests
 * 
 * 1. Test static keyword and writing stuff out to disk with static
 * aesObject encryption
 */
public class SystemTest {
	//Global aesobject
	static AES aesObject; 
	static Hub hub;
	
	private static boolean testAES(){
		
		return false;
	}
	
	private static void aesInitialize(){
		aesObject = new AES("password".toCharArray());	//default
	}
	
	private static boolean testAuthentication(){
		
		return false;
	}
	
	private static void hubInitialize(){
		hub = new Hub(aesObject);
	}
	
	public static void main(String[] args){
		aesInitialize();
	}
}
