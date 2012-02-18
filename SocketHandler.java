import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.Date;


public class SocketHandler extends Thread{
	
	String str = null;
	Date d = null;
	Socket socket;
	
	public SocketHandler(Socket ser){
		this.socket = ser;
	}
	
	public void run(){
		//do stuff with socket
		try {
			InputStream o = this.socket.getInputStream();
			ObjectInput s = new ObjectInputStream(o);
			this.str = (String) s.readObject();
			this.d = (Date) s.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("This was a piece of poo");
		}
	}
} 
	
