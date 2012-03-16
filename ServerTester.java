import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class ServerTester {
static Socket socket;
static ObjectOutputStream oos;
static ObjectInputStream ois;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create a socket that binds to local host
		try {
			socket = new Socket(InetAddress.getLocalHost(),5000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//set up streams
		try {
			oos = (ObjectOutputStream) socket.getOutputStream();
			ois = (ObjectInputStream) socket.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//create object to write
		 ArrayList<String> newArray = new ArrayList<String>();
		 newArray.add(0, "Hello World.");
		
		//send some thing
		try {
			oos.writeObject(newArray);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
