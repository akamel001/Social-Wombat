import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;


public class ClientSocketHandler {
	
	private static final int SERVER_PORT = 4444;


	
	public static Message constructMessage(String uName, Message.MessageType type){
		

		Message message = new Message();
		message.setType(type);
		message.setBody(uName);
		message.setCode(0); // <- decide what codes are...?
		
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
		
		Message messageSending = constructMessage(uName, type);
		
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
		     
//		     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//		     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 
			 System.out.println("sent waiting for hub to respond...");
		     //oos.writeObject(messageSending);
			 oos.writeObject(new Date());
			 oos.flush();
			 
		       // read an object from the server
		        Date date = (Date) ois.readObject();
		        System.out.print("The date is: " + date);
		        oos.close();
		        ois.close();
		        
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
		   } finally {
			   
		   }
			
		return messageReceived;
	}
}
