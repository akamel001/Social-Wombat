import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocketHandler {

	private static final int SERVER_PORT = 4444;

	private static Message messageSending = null;
	private static Message messageReceived = null; 
	private static Socket socket = null;
	private static ObjectOutputStream oos = null;
	private static ObjectInputStream ois = null;

	public Message sendReceive(String uName, Message.MessageType type){

		if(messageSending == null){
			System.out.println("Message not set correctly (null). Exiting...");
			System.exit(-1);
		}
		
		try{
			socket = new Socket(InetAddress.getLocalHost(), SERVER_PORT);

			// open I/O streams for objects
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			// sending an object to the server
			System.out.println("Sending message to server...");
			oos.writeObject(messageSending);
			oos.flush();

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


	public static void setCookie(Cookie cookie) {
		messageSending.setCookie(cookie);
	}


	public static Message getMessageSending() {
		return messageSending;
	}


	public static void setMessageSending(Message messageSending) {
		ClientSocketHandler.messageSending = messageSending;
	}


	public static int getServerPort() {
		return SERVER_PORT;
	}
}
