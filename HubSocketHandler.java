import java.io.*;
import java.net.*;

public class HubSocketHandler extends Thread{
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	ClassList classList;
	UserList userList;
	Socket socket;

	/*
	 * A handler thread that is spawned for each message sent to a socket.
	 */
	public HubSocketHandler(Socket ser, ClassList classList, UserList userList){
		this.socket = ser;
		this.classList = classList;
		this.userList = userList;
	}
	
	/*
	 * Deserialize transmission in socket and convert to a message
	 */
	private Message getMessage(Socket s){
		Message msg = null;
		try {
			InputStream obj = s.getInputStream();
			ObjectInput ois = new ObjectInputStream(obj);
			msg = (Message) ois.readObject();
			//TODO: check if the close should be here or later.
			ois.close();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
		return msg;
	}
	
	private void returnMessage(Message msg, Socket s){
		//Get output stream
		try {
			OutputStream obj = s.getOutputStream();
			ObjectOutput oos = new ObjectOutputStream(obj);
			oos.writeObject(msg);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("returnMessage failed");
		}
		
	}
	
	/*
	 * Looks in the message and takes the classroom to find the correct server 
	 * that the message should be sent to.
	 */
	private InetAddress getServer(Message msg, Object data){
		//get classroom name from msg
		String classID = msg.getClassroom_ID();
		//table lookup of classrooms and their appropriate server
		int server = classList.getClassServer(classID);
		//return server
		return null;
		
	}
	
	/*
	 * Constructs the message that needs to be forwarded 
	 * and receives the response
	 */
	private void forwardToServer(Message msg){
		//Open a socket connection with appropriate server
	}
	
	/*
	 * When waiting for a message, only a message from a client is expected.
	 * If message is forwarded to the server, that response will only accept
	 * server messages. Authentication responses to clients accept no response.
	 */
	public void run(){
		boolean valid = true;
		//Read and deserialize Message from Socket
		Message msg = getMessage(this.socket);
		if (msg == null){
			System.out.println("Message was null");
			valid = false; // Don't waste time on bad transmissions
		}
		if (valid){
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
					
					// TODO: Server response is forwarded back to this.socket
					break;
				default:
					//TODO: Send request denied back to the client
					break;
					
			}
		}
	}
	
	
} 
	
