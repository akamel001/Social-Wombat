import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


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
		
		try{
		     Socket socket = new Socket(messageSending.getRecipient(), SERVER_PORT);

			 ObjectOutputStream oos;
			 oos = new ObjectOutputStream(socket.getOutputStream());

//		     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//		     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			 
			 System.out.println("sent waiting for hub to respond...");
		     oos.writeObject(messageSending);
//		     oos.flush();
				
		    
		     ObjectInputStream ois;
		     ois = new ObjectInputStream(socket.getInputStream());
		     
		     messageReceived = (Message) ois.readObject();
		     
		     System.out.println(messageReceived.getBody());
		     
		     ois.close();
		     oos.close();
		     socket.close();
		     //in buffer is ready
		   } catch (UnknownHostException e) {
			   System.out.println("Unknown host: " + messageSending.getRecipient());
		     	System.exit(1);
		   } catch  (IOException e) {
			   System.out.println("No I/O");
			   System.exit(1);
		   } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			   e.printStackTrace();
		   } finally {
			   
		   }
			
		return messageReceived;
	}
}
