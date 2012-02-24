import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.*;

public class ServerSocketHandler extends Thread{
	
	Object data;
	Socket socket;
	Message msg;

	
	public ServerSocketHandler(Socket ser, Object data){
		this.socket = ser;
		this.data = data;
	}
	
	public void run(){
		//do stuff with socket
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
	
