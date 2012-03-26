import java.net.*;
import java.io.*;

/**
 * A custom class to hold a socket, it's input stream, it's output stream,
 * methods to send & flush, other setup/tear down methods
 */

public final class SocketPackage {
	private Socket socket = null;
	private int port;
	private InetAddress addr;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private static final int TIMEOUT = 5000;
	private static final int MAX_RETRY = 16;
	private static final boolean DEBUG = false;

	/**
	 * Creates a socket. DOES NOT CONNECT
	 */
	public SocketPackage(InetAddress addr,int port){
		if (DEBUG) System.out.println("Creating a socket");
		
		//TODO: I commented out this line and added it to socketConnect b/c if the connection fails, you need a new socket object.
		
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
	
	/**
	 * Connects the socket to the input inetaddress. Also creates the 
	 * output and input streams
	 */
	public void socketConnect(){
		SocketAddress socketAddr = new InetSocketAddress(addr, port);
		//attempt to connect with a 5 second timeout
		for(int i = 1; !socket.isConnected(); i*=2){
			try {
				socket = new Socket();
				socket.connect(socketAddr, TIMEOUT);
			}catch (SocketTimeoutException e){
				//e.printStackTrace();
				System.out.println("Connection has timed out.");
			} catch (ConnectException e) {
				System.out.println("Connection was refused, retrying in " + (long)(2000*Math.log(i))/1000 + " seconds.");
				try {
					Thread.sleep((long) (2000*Math.log(i)));
					continue;
				} catch (InterruptedException e1) {
					System.out.println("Another thread interupted me while sleeping.");
					e1.printStackTrace();
					System.exit(-1);
				}
			}catch (SocketException e){
				System.out.println("Socket Closed. retrying in " + (long)(2000*Math.log(i))/1000 + " seconds.");
				try {
					Thread.sleep((long) (2000*Math.log(i)));
					continue;
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
	 * Send an unencrypted message with flushing
	 */
	public void send(Message msg){
		try {
			oos.writeObject(msg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("System send failed");
			System.exit(-1);
		}

	}
	
	/**
	 * Receive an unencrypted message
	 */
	public Message receive(){
		Message msg = null;
		try {
			msg = (Message) ois.readObject();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Could not receive object");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Object received was not of type Message");
		}
		return msg;
	}
	
	
	/**
	 * Sends an encrypted (byte[]) message
	 */
	public void sendEncrypted(byte[] msg){
		try {
			//write an int of the byte[] length first
			oos.writeInt(msg.length);
			oos.flush();
			oos.reset();
			//write rest of message
			oos.write(msg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("System send encrypted failed");
			System.exit(-1);
		}
	}
	

	/**
	 * Receives an encrypted message but looks in the header for the
	 * length of the byte to read.
	 * @return
	 */
	public byte[] receiveEncrypted(){
		int length = 0;
		try {
			length = ois.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] encryptedMsg = new byte[length];
		try {
			ois.readFully(encryptedMsg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encryptedMsg;
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
