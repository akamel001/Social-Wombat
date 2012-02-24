import java.io.*;
import java.net.*;

public class HubSocketHandler extends Thread{
	
	Object data;
	Socket socket;
	Message msg;

	public HubSocketHandler(Socket ser, Object data){
		this.socket = ser;
		this.data = data;
	}
	
	public void run(){
		//Read and deserialize Message from Socket
		
		//Figure out whether it is a message from Client or Server
		
		//If from Client: Authenticate and forward to appropriate Server
		
		//If from Server: Forward to appropriate Client
		
		
		try {
			InputStream obj = this.socket.getInputStream();
			ObjectInput o = new ObjectInputStream(obj);
			this.msg = (Message) o.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
	}
	
	
} 
	
