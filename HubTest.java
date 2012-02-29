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
		testHub = new Hub();
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
		String username = "user1";
		//userList should not have a user1 at first
		assertTrue("user1 could not be added",testHub.addUser(username));
		
		//user1 should exist now
		assertTrue("user1 should have been in userList",testHub.userExists("user1"));
		
		//adding the same name again should fail
		assertFalse("adding user1 again should have failed",testHub.addUser(username));
	
		//testing null add
		assertFalse("adding a null user should have failed",testHub.addUser(null));
	}

	/**
	 * Test method for {@link Hub#addServer(java.net.InetAddress)}.
	 */
	@Test
	public void testAddServer() {
		//use localhost since we can construct our own
		InetAddress newServer = null;
		try {
			newServer = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// test first add
		assertEquals("First added server should be 1",1,testHub.addServer(newServer));
		
		// there should be an add failure whne adding again
		assertEquals("Should fail when readding an already added server",-1,testHub.addServer(newServer));
		
	}

	/**
	 * Test method for {@link Hub#main(java.lang.String[])}.
	 */
	@Test
	public void testMain() {
		fail("Not yet implemented"); // TODO
	}

}
