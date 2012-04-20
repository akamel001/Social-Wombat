package test;
import hub.Hub;
import security.AES;


/**
 * @author yilok
 *
 */
public class HubTest {

	//private static int CLIENT_SOCKET = 4444;
	//private static int SERVER_SOCKET = 5050;
	
	static Hub testHub;
	
	
	@SuppressWarnings("unused")
	private static void testAddUsers(){
		
	}
	
	public static void main(String[] args){
		//generate a new aes key to use
		char[] pass = "password".toCharArray();
		AES aesKey = new AES(pass);
		
		//create hub
		testHub = new Hub(aesKey);
		
		//run tests
		
	}
}
