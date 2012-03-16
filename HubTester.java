import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.*;

public class HubTester {
	static Socket server = null;
	static ObjectOutputStream oos;
	static ObjectInputStream ois;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//begin
		ServerSocket hubSocket = null;
		//Create and listen in on a port
		try {
			hubSocket = new ServerSocket(5000);
			server = hubSocket.accept();
		} catch (IOException e) {
			System.out.println("Could not listen on port: 5000");
			System.exit(-1);
		}
		
		//set up streams
		try {
			oos = (ObjectOutputStream) server.getOutputStream();
			ois = (ObjectInputStream) server.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
