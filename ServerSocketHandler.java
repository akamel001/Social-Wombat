import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.*;

<<<<<<< HEAD:ServerSocketHandler.java
public class ServerSocketHandler extends Thread{
=======
public class SocketHandler extends Thread{
>>>>>>> 160c7052142c091f921df53d8b18c40f41ac6763:SocketHandler.java
	
	Socket socket;
	Message msg;

	
	public ServerSocketHandler(Socket ser){
		this.socket = ser;
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
	
