import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocketHandler {

	private static final int SERVER_PORT = 4444;



	public static Message constructMessage(String uName, Message.MessageType type, Object body){


		Message message = new Message();
		Cookie cookie = new Cookie(uName);
		
		message.setCookie(cookie);
		message.setType(type);
		message.setBody(body);

		try {
			//TODO change to static variables uses localhost for now
			message.setRecipient(InetAddress.getLocalHost());
			message.setSender(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			System.out.println(e);
		}

		return message;
	}
	
	public Message sendReceive(String uName, Message.MessageType type){

		Message messageSending = constructMessage(uName, type, null);

		//TODO construct the received message from the hub
		Message messageReceived = null; 
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

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
			//System.out.print(messageReceived.getBody());
			
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
	

	public Message sendReceive(String uName, Message.MessageType type, Object body){

		Message messageSending = constructMessage(uName, type, body);

		//TODO construct the received message from the hub
		Message messageReceived = null; 
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;

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
			System.out.print(messageReceived.getBody());
			
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
}
