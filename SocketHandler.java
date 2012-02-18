import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Date;


public class SocketHandler extends Thread{
	
	Socket socket;
	Message msg;

	
	public SocketHandler(Socket ser){
		this.socket = ser;
	}
	
	public void run(){
		//do stuff with socket
		try {
			InputStream obj = this.socket.getInputStream();
			ObjectInput o = new ObjectInputStream(obj);
			this.msg = (String) o.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("This was a piece of poo");
		}
	}
} 
	
