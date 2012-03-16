import java.io.ObjectInput;
import java.io.ObjectOutput;
import static org.junit.Assert.*;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author yilok
 *
 */
public class HubTest {

	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	static Hub testHub;
	
	
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
