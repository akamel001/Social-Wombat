import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientSocketHandler {
	
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
		     Socket socket = new Socket(messageSending.getRecipient(), 4444);
		     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		     out.write(messageSending.getBody());
		     System.out.println("sent waiting for hub to respond...");
		     while(!in.ready()) in.wait(1000);
		     
		     //in buffer is ready
		   } catch (UnknownHostException e) {
			   System.out.println("Unknown host: " + messageSending.getRecipient());
		     	System.exit(1);
		   } catch  (IOException e) {
			   System.out.println("No I/O");
			   System.exit(1);
		   } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return messageReceived;
	}
}
