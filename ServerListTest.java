import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ServerListTest {

	@Test
	public final void testAddServer() {
		ServerList sl = new ServerList();
		InetAddress ip= null;
		int t;
		boolean bool;
		
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		t = sl.addServer(ip, 1000);
		assertEquals(1, t);
		
		//try to add a server with an invalid ip
		t = sl.addServer(null, 1000);
		assertEquals(-1, t);
		
		//try to add a server with an invalid port
		t = sl.addServer(ip, -1);
		assertEquals(-1, t);
		t = sl.addServer(ip, 49151);
		assertEquals(-1, t);
		
		// try to add old ip, new port
		t = sl.addServer(ip, 999);
		bool = t>1;
		assertEquals(true, bool);
		
		//try to add duplicate ip/port combo
		t = sl.addServer(ip, 1000);
		
		assertEquals(-1, t);
				
		// try to add new ip old port
		try {
			t = sl.addServer(InetAddress.getByName("javaj.sun.com"), 1000);
			bool = t>0;
			assertEquals(true, bool);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public final void testRemoveServer() {
		ServerList sl = new ServerList();
		InetAddress ip= null;
		boolean bool;
		int t;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		t = sl.addServer(ip, 1000);
		bool = t>0;
		assertEquals(true, bool);
		t = sl.removeServer(t);
		assertEquals(true, bool);
		t = sl.addServer(ip, 1000);
		bool = t>0;
		assertEquals(true, bool);
	}


}
