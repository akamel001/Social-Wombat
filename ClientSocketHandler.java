import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ClientSocketHandler {

	private static final int HUB_PORT = 4444;
	private static final String hub_addr = "127.0.0.1";

	private Message messageSending = new Message();
	private Message messageReceived = new Message(); 
	private static InetAddress hub;
	private static SocketAddress socketAddr;
	private static Socket socket = null;
	private static ObjectOutputStream oos = null;
	private static ObjectInputStream ois = null;

	//Variables used for graceful reconnect 
	private Boolean connected = false;
	private static final int MAX_RETRY = 16;
	private static final int TIMEOUT = 5000;
	
	public Message sendReceive(){

		if(messageSending == null){
			System.out.println("Message not set correctly (null). Exiting...");
			System.exit(-1);
		}
		
		for(int i = 1; !connected; i*=2){
			try{
				hub = InetAddress.getByName(hub_addr);
				//socket = new Socket(hub, HUB_PORT);
				socket = new Socket();
				socketAddr = new InetSocketAddress(hub, HUB_PORT);

				//attempts to connect with 5second timeout
				socket.connect(socketAddr, TIMEOUT);
				connected = true;

				// open I/O streams for objects
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());

				// sending an object to the server
				//System.out.println("Sending message to server...");
				oos.writeObject(messageSending);
				oos.flush();
				oos.reset();

				// read an object from the server
				//System.out.println("Receiving message from server...");
				messageReceived = (Message) ois.readObject();

				oos.close();
				ois.close();
				socket.close();

			} catch (SocketTimeoutException e){
				System.out.println("Client timedout while connecting to hub.");
			} catch (ConnectException e) {
				System.out.println("Connection to hub was refused, retrying in " + (long)(2000*Math.log(i))/1000 + " seconds.");
				try {
					Thread.sleep((long) (2000*Math.log(i)));
				} catch (InterruptedException e1) {
					System.out.println("Another thread interupted me while sleeping. (Client)");
					e1.printStackTrace();
					System.exit(-1);
				}
			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + messageSending.getRecipient());
				//System.exit(1);
			} catch  (IOException e) {
				System.out.println("No I/O");
				//e.printStackTrace();
				//System.exit(1);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			if(!connected && i == MAX_RETRY){
				System.out.println("Reached maximum retries, exiting client");
				System.exit(-1);
			}
		}

		return messageReceived;
	}

	public Message getMessageSending() {
		return messageSending;
	}

	public static String getHubAddr() {
		return hub_addr;
	}

	public static int getServerPort() {
		return HUB_PORT;
	}
}
