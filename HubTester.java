import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class HubTester {
	static Socket server = null;
	static ObjectOutputStream oos;
	static ObjectInputStream ois;
	
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		//begin
		ServerSocket hubSocket = null;
		//Create and listen in on a port
		try {
			hubSocket = new ServerSocket(5000);
			System.out.println("Listening");
			server = hubSocket.accept();
		} catch (IOException e) {
			System.out.println("Could not listen on port: 5000");
			System.exit(-1);
		}
		
		//set up streams
		try {
			oos = new ObjectOutputStream(server.getOutputStream());
			ois = new ObjectInputStream(server.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("1st read");
		ArrayList<String> readArray = null;
		try {
			readArray = (ArrayList<String>)ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//print out the result
		System.out.println(readArray.get(0));
		
		System.out.println("getting encryption items");
		//get iv and salt
		ArrayList<byte[]> readArray2 = null;
		try {
			readArray2 = (ArrayList<byte[]>)ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] iv = readArray2.get(0);
		byte[] salt = readArray2.get(1);
		AES newAES = new AES("password".toCharArray(),iv,salt);
		
		System.out.println("2rd read");
		//get encrypted message
		int length = 0;
		//get length
		try {
			length = ois.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		byte[] eMsg = new byte[length];
		
		try {
			ois.readFully(eMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//decrypt into what should be the array
		ArrayList<String> readArray3 = (ArrayList<String>)(newAES.decryptObject(eMsg));
		System.out.println(readArray3.get(0));
	
		//decrypt msg
		System.out.println("3rd read");
		//get encrypted message
		int l = 0;
		//get length
		try {
			l = ois.readInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		byte[] encryptedMsg = new byte[l];
		
		try {
			ois.readFully(encryptedMsg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//decrypt into what should be the array
		Message msg = (Message)(newAES.decryptObject(encryptedMsg));
		ArrayList<String> readArray4 = (ArrayList<String>)msg.getBody();
		System.out.println(readArray4.get(0));
	
	}
	
	
	
	
	
}
