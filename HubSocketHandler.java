import java.io.*;
import java.net.*;

public class HubSocketHandler extends Thread{
	
	Object data;
	Socket socket;

	public HubSocketHandler(Socket ser, Object data){
		this.socket = ser;
		this.data = data;
	}
	
	/*
	 * Deserialize transmission in socket and convert to a message
	 */
	private Message getMessage(Socket s){
		Message msg = null;
		try {
			InputStream obj = s.getInputStream();
			ObjectInput o = new ObjectInputStream(obj);
			msg = (Message) o.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
		return msg;
	}
	
	private void sendMessage(Socket clientSocket, Message msg){
		
	}
	
	/*
	 * When waiting for a message, only a message from a client is expected.
	 * If message is forwarded to the server, that response will only accept
	 * server messages. Authentication responses to clients accept no response.
	 */
	public void run(){
		//Read and deserialize Message from Socket
		Message msg = getMessage(this.socket);
		if (msg == null){
			System.out.println("Message was null");
		}
		//Handle the different types of client messages
		switch(msg.getType()) {
			case Client_Authentication: 
				//TODO: Do authentication
				
				//TODO: Generate a message response
				
				//TODO: Send response
				break;
			case Client_B:
				// TODO: Modify data
				
				// TODO: Create a message to forward to appropriate server
				
				// TODO: Send message and wait for server response
				break;
			default:
				//TODO: Send request denied back to the client
				break;
				
		}
		
		
		
	}
	
	
} 
	
