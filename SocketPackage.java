import java.net.*;
import java.io.*;

/**
 * A custom class to hold a socket, it's input stream, it's output stream,
 * methods to send & flush, other setup/tear down methods
 */
class SocketPackage {
	Socket socket;
	int port;
	InetAddress addr;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	
	private static final boolean DEBUG = false;
	
	/*
	 * Creates a socket. DOES NOT CONNECT
	 */
	public SocketPackage(InetAddress addr,int port){
		if (DEBUG) System.out.println("Creating a socket");
		socket = new Socket();
		this.port = port;
		this.addr = addr;
	}
	
	/*
	 * Connects the socket to the input inetaddress. Also creates the 
	 * output and input streams
	 */
	public void socketConnect(){
		SocketAddress socketAddr = new InetSocketAddress(addr, port);
		//attempt to connect with a 5 second timeout
		try {
			socket.connect(socketAddr, 5000);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not connect");
		}
		try {
			if (DEBUG) System.out.println("Creating an output stream");
			oos = new ObjectOutputStream(socket.getOutputStream());
			if (DEBUG) System.out.println("Creating an input stream");
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e){
			e.printStackTrace();
			System.out.println("Could not get streams");
		}
	}
	
	/*
	 * Checks if a socket package is connected
	 */
	public boolean isConnected(){
		return socket.isConnected();
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
