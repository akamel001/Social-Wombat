import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocketHandler {

	private static final int HUB_PORT = 4444;

	private Message messageSending = new Message();
	private Message messageReceived = new Message(); 
	private static Socket socket = null;
	private static ObjectOutputStream oos = null;
	private static ObjectInputStream ois = null;

	public Message sendReceive(){

		if(messageSending == null){
			System.out.println("Message not set correctly (null). Exiting...");
			System.exit(-1);
		}
		
		try{
			socket = new Socket(InetAddress.getLocalHost(), HUB_PORT);

			// open I/O streams for objects
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			// sending an object to the server
			System.out.println("Sending message to server...");
			oos.writeObject(messageSending);
			oos.flush();
			oos.reset();

			// read an object from the server
			System.out.println("Receiving message from server...");
			messageReceived = (Message) ois.readObject();

			oos.close();
			ois.close();
			socket.close();

		} catch (UnknownHostException e) {
			System.out.println("Unknown host: " + messageSending.getRecipient());
			System.exit(1);
		} catch  (IOException e) {
			System.out.println("No I/O");
			e.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return messageReceived;
	}

	public Message getMessageSending() {
		return messageSending;
	}

	public static int getServerPort() {
		return HUB_PORT;
	}
}
