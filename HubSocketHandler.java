import java.io.*;
import java.net.*;
import java.util.Date;

public class HubSocketHandler extends Thread{
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	ClassList classList;
	UserList userList;
	ServerList serverList;
	Socket socket;
	ObjectOutput oos;
	ObjectInput ois;

	/*
	 * A handler thread that is spawned for each message sent to a socket.
	 */
	public HubSocketHandler(Socket ser, ClassList classList, UserList userList, ServerList serverList){
		this.socket = ser;
		this.classList = classList;
		this.userList = userList;
		this.serverList = serverList;
		// Create datastreams
		try {
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not create input and output streams");
		}
	}
	
	/*
	 * Deserialize transmission in socket and convert to a message
	 */
	private Message getMessage(){
		Message msg = null;
		try {
			msg = (Message) ois.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
		return msg;
	}
	
	/*
	 * Send a message back after it's been altered
	 */
	private void returnMessage(Message msg){
		//Get output stream
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Return Message Failed");
		}	
	}
	
	/*
	 * Looks in the message and takes the classroom to find the correct server 
	 * (by InetAddress) that the message should be sent to.
	 */
	private InetAddress getServer(Message msg){
		//get classroom name from msg
		String classID = msg.getClassroom_ID();
		//table lookup of classrooms and their appropriate server
		int server = classList.getClassServer(classID);
		//return server
		return serverList.getAddress(server);
	}
	
	/*
	 * Constructs the message that needs to be forwarded 
	 * and receives the response as a message
	 */
	private Message forwardToServer(Message msg){
		Socket forwardSocket;
		Message reply = null;
		try {
			// Open a socket connection with appropriate server
			forwardSocket = new Socket(getServer(msg),SERVER_SOCKET);
			
			// Create inputstream and outputstream
			ObjectOutput forwardOut = new ObjectOutputStream(forwardSocket.getOutputStream());
			ObjectInput forwardIn = new ObjectInputStream(forwardSocket.getInputStream());
			
			// Write out message
			forwardOut.writeObject(msg);
			forwardOut.flush();
			
			// Get response
			reply = (Message) forwardIn.readObject();
			
			// Close connections
			forwardOut.close();
			forwardIn.close();
			forwardSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not open a forwarding socket");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Reply is not of Message type.");
		} finally {
			// Close everything
		}
		
		return reply;
	}
	
	/*
	 * When waiting for a message, only a message from a client is expected.
	 * If message is forwarded to the server, that response will only accept
	 * server messages. Authentication responses to clients accept no response.
	 */
	public void run(){
		boolean valid = true;
		//Read and deserialize Message from Socket
		
		Message msg = getMessage();
		
		
		if (msg == null){
			System.out.println("Message was null");
			valid = false; // Don't waste time on bad transmissions
		}
		if (valid){
			//Handle the different types of client messages
			switch(msg.getType()) {
				// Client -> Hub
				
				//Client user id is stored as the sessionKey in the Cookie
				//of the message
				// TODO: Possibly move this above switch statement to authenticate all requests
				case Client_LogIn: 
					String sessionKey = msg.getCookie().getKey();
					if(userList.validateUser(sessionKey)){
						//Reply with confirmation
						msg.setBody("true");
						returnMessage(msg);
					} else {
						//Reply with denial
						msg.setBody("false");
						returnMessage(msg);
					}
					break;
				case Client_Register:
					String s = msg.getBody();
					msg.setBody("User " + s + " was created.");
					returnMessage(msg);
					break;
				case Client_GetClassEnrollment:
					// TODO: Modify data
					
					// TODO: Create a message to forward to appropriate server
					
					// TODO: Send message and wait for server response
					
					// TODO: Server response is forwarded back to this.socket
					break;
				case Client_GetUserEnrollment:
					
					break;
				case Client_SetPermissions:

					break;
				case Client_GetEnrollmentList:

					break;
				case Client_DeleteSelf:

					break;
				case Client_RequestEnrollment:

					break;
					
				// Client -> Hub -> Server
				case Client_CreateClassroom:

					break;
				case Client_CreatePost:

					break;
				case Client_CreateComment:

					break;
				case Client_GoToClassroom:

					break;
				case Client_GoToThread:

					break;
				case Client_DeleteClassroom:

					break;
				case Client_DeleteThread:

					break;
					
				default:
					//TODO: Send request denied back to the client
					break;
				
			}
		}
		// Close everything
		try {
			oos.close();
			ois.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't close");
		}

	}
	
	
} 
	
