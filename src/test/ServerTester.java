package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import security.AES;
import storage.Message;


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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//set up streams
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//create object to write
		 ArrayList<String> newArray = new ArrayList<String>();
		 newArray.add(0, "Hello World.");
		
		//send some thing
		try {
			oos.writeObject(newArray);
			oos.flush();
			oos.reset();
			System.out.println("Sent something");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//now encrypt a message and send it
		
		//create an aes object
		char[] pass = "password".toCharArray();
		AES newAES = new AES(pass);
		byte[] iv = newAES.getIv();
		byte[] salt = newAES.getSalt();
		
		//get it over to hub
		ArrayList<byte[]> newArray2 = new ArrayList<byte[]>();
		newArray2.add(0, iv);
		newArray2.add(1, salt);
		
		//send
		try {
			oos.writeObject(newArray2);
			oos.flush();
			oos.reset();
			System.out.println("Sent over encryption info");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//encrypt an object
		byte[] eMsg = newAES.encrypt(newArray);	//has the "Hello World."
		int length = eMsg.length;
		System.out.println("length of encrypted message: " + length);
		try {
			oos.writeInt(length);
			oos.write(eMsg);
			oos.flush();
			oos.reset();
			System.out.println("Sent over encrypted message");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//encrypt a message 
		Message newMsg = new Message();
		newMsg.setBody(newArray);
		//send it
		byte[] encryptedMsg = newAES.encrypt(newMsg);
		int l = encryptedMsg.length;
		System.out.println("length of encrypted message 2: " + l);
		try {
			oos.writeInt(l);
			oos.write(encryptedMsg);
			oos.flush();
			oos.reset();
			System.out.println("Sent over encrypted message 2");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
