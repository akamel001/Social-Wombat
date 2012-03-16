import java.net.*;
import java.io.*;

/**
 * A custom class to hold a socket, it's input stream, it's output stream,
 * methods to send & flush, other setup/tear down methods
 */

class SocketPackage {
	private Socket socket;
	private int port;
	private InetAddress addr;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private DataOutputStream dout;
//	private DataInputStream din;
	private static final int TIMEOUT = 5000;
	private static final int MAX_RETRY = 16;
	private static final boolean DEBUG = false;

	/**
	 * Creates a socket. DOES NOT CONNECT
	 */
	public SocketPackage(InetAddress addr,int port){
		if (DEBUG) System.out.println("Creating a socket");
		socket = new Socket();
		this.port = port;
		this.addr = addr;
	}
	
	/*
	 * Get some way identification/name of the socket
	 */
	public String getSocketName(){
		return addr.getHostAddress();
	}
	
	/*
	 * Used to receive an encrypted servermessage. Only for communication with 
	 * server because the AES key can be stored in socketPackage because the 
	 * socketPackage object will reside on the hub.
	 * Server message will be encrypted. So we must use the AESObject
	 * stored from beforehand.
	 * 
	 * returns null when it can't
	 */
	public byte[] receiveEncryptedBytes(){
		//TODO: TEST!
		try {
			//read stream
			return (byte[])ois.readObject();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Connects the socket to the input inetaddress. Also creates the 
	 * output and input streams
	 */
	public void socketConnect(){
		SocketAddress socketAddr = new InetSocketAddress(addr, port);
		//attempt to connect with a 5 second timeout
		for(int i = 1; !socket.isConnected(); i*=2){
			try {
				socket.connect(socketAddr, TIMEOUT);
			}catch (SocketTimeoutException e){
				e.printStackTrace();
				System.out.println("Connection has timed out.");
			} catch (ConnectException e) {
				System.out.println("Connection was refused, retrying in " + (long)(2000*Math.log(i))/1000 + " seconds.");
				try {
					Thread.sleep((long) (2000*Math.log(i)));
				} catch (InterruptedException e1) {
					System.out.println("Another thread interupted me while sleeping.");
					e1.printStackTrace();
					System.exit(-1);
				}
			}catch (IOException e) {
				e.printStackTrace();
				System.out.println("No I/O");
			}
			try {
				if (DEBUG) System.out.println("Creating an output stream");
				oos = new ObjectOutputStream(socket.getOutputStream());
				if (DEBUG) System.out.println("Creating an input stream");
				ois = new ObjectInputStream(socket.getInputStream());
				if (DEBUG) System.out.println("Creating a byte output stream");
				dout = new DataOutputStream(socket.getOutputStream());
//				if (DEBUG) System.out.println("Creating a byte input stream");
//				din = new DataInputStream(socket.getInputStream());
			} catch (IOException e){
				e.printStackTrace();
				System.out.println("Could not get streams");
			}

			if(!socket.isConnected() && i == MAX_RETRY){
				System.out.println("Reached maximum retries, exiting client");
				System.exit(-1);
			}
		}
	}

	/**
	 * Checks if a socket package is connected
	 */
	public boolean isConnected(){
		return socket.isConnected();
	}

	/**
	 * Send a message with flushing
	 * UNENCRYPTED
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
	 * Assumes a message is encrypted before calling this function
	 */
	public void sendEncrypted(byte[] msg){
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
	 * NOT SURE WHAT THIS IS
	 */
	public void send(byte msg){
		try {
			dout.write(msg);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Send byte message failed");
		}

	}
	
	public byte[] receiveBytes(){
		//TODO need to think about this one... 
		return null;
	}
	
	/**
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
