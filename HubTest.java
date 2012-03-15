import static org.junit.Assert.*;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */

/**
 * @author yilok
 *
 */
public class HubTest {

	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	Hub testHub;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//Set up a fake client to send stuff
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		AES newAES = new AES("password".toCharArray());
		testHub = new Hub(newAES);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link Hub#Hub()}.
	 */
	@Test
	public void testHub() {
		// Test to see if ip address is right
		String expectedAddr = "";
		try {
			expectedAddr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			fail("Could not get ip address");
		}
		assertEquals(expectedAddr,testHub.hubIP);
		
	}

	/**
	 * Test method for {@link Hub#addUser(java.lang.String)}.
	 */
	@Test
	public void testAddUser() {
		//Users are added in main
		
		//userList will be null until main() is run
		assertNull(testHub.userList);
	}

	/**
	 * Test method for {@link Hub#addServer(java.net.InetAddress)}.
	 */
	@Test
	public void testAddServer() {
		//Servers are added in main
		//serverList will be null until main()
		
		assertNull(testHub.serverList);
	}

	/**
	 * Test method for {@link Hub#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		//should create a serverList and userList
		//testHub.main(null);
		
		//HOWEVER, MAIN JUST WILL SPIN WITHOUT ANOTHER THREAD TO CHECK
		
		//assertNotNull(testHub.userList);
		
		//assertNotNull(testHub.serverList);
	}

}
