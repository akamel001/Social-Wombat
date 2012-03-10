import java.net.*;
import java.io.*;

/**
 * A custom class to hold a socket, it's input stream, it's output stream,
 * methods to send & flush, other setup/tear down methods
 */
class SocketPackage {
	static Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	/*
	 * Creates a socket and opens an ObjectOutputStream and ObjectInputStream
	 */
	public SocketPackage(InetAddress addr,int port){
		try {
			socket = new Socket(addr,port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not create a socket in SocketPackage");
		}
	}
	
	/*
	 * Send a message with flushing
	 */
	public void send(Message msg){
		try {
			oos.writeObject(msg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("System send failed");
		}
		
	}
	
	/*
	 * Receive a message
	 */
	public Message receive(){
		Message msg = null;
		try {
			msg = (Message) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not receive object");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Object received was not of type Message");
		}
		return msg;
	}
	
	/*
	 * Close socket and streams
	 */
	public void close(){
		try {
			oos.close();
			ois.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Closing of socket and streams failed");
		}
	}
}
