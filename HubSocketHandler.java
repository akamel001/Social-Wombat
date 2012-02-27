import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Map;

public class HubSocketHandler extends Thread{
	//private static int CLIENT_SOCKET = 4444;
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
			// Preset to failure
			int returnCode = -1;
			Message reply = null;
			//Handle the different types of client messages
			switch(msg.getType()) {
				
				// Client -> Hub
				
				//Client user id is stored as the sessionKey in the Cookie of the message
				// TODO: Possibly move this above switch statement to authenticate all requests
				case Client_LogIn: 
					String sessionKey = msg.getCookie().getKey();
					if(userList.validateUser(sessionKey)){
						//Reply with confirmation
						msg.setCode(1);
					} else {
						//Reply with denial
						msg.setCode(-1);
					}
					returnMessage(msg);
					break;
				// Returns in body all users in a classroom
				case Client_GetClassEnrollment:
					//String = User, Integer = 
					Map<String, Integer> classEnroll = classList.getClassEnrollment(msg.getClassroom_ID());
					if (classEnroll == null){
						//error
						msg.setCode(-1);
					} else {
						msg.setCode(1);
						msg.setBody(classEnroll);
					}
					returnMessage(msg);
					break;
				// Return in body a list of the classes that a client is enrolled in
				case Client_GetUserEnrollment:
					String usr = msg.getCookie().getKey();
					Map<String, Integer> userEnroll = classList.getUserEnrollment(usr);
					if (userEnroll == null){
						//error
						msg.setCode(-1);
					} else {
						msg.setCode(1);
						msg.setBody(userEnroll);
					}
					returnMessage(msg);
					break;
				// Change the permissions for another user
				// Store user to be changed and the permissions as an arraylist
				// [0] = username, [1] = permissions
				case Client_SetPermissions:
					@SuppressWarnings("unchecked")
					ArrayList<String> a= (ArrayList<String>) msg.getBody();
					// Person
					String personToChange = a.get(0);
					// Permission to set for Person
					int per = Integer.parseInt(a.get(1));
					// Server's return code
					returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per);
					// Reply
					msg.setCode(returnCode);
					returnMessage(msg);
					break;
				case Client_DeleteSelf:
					
					break;
				// Request to be added to a class
				case Client_RequestEnrollment:
					String requestName = msg.getCookie().getKey();
					// 0 for pending enrollment, -1 for dijoining
					int p = (Integer)msg.getBody();
					returnCode = classList.setUserPermissions(requestName, msg.getClassroom_ID(), p);
					break;
					
				// Client -> Hub -> Server
				// NOTE: ANYTHING SENT IN THIS PATHWAY REQUIRES THE 
				// CLASSROOM_ID TO BE SET IN THE MESSAGE ALREADY!!!!!
				case Client_CreateClassroom:
					//Choose any server to create the classroom in
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				/*
				 * Create post requires Post Name, and Post Body.
				 * Since only a msg body is available for storage of both,
				 * they will be stored as a 2 element arraylist with 
				 * Array[0] = Post_Name
				 * Array[1] = Post_body 	
				 */
				case Client_CreateThread:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				case Client_CreateComment:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				case Client_GoToClassroom:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				case Client_GoToThread:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				case Client_DeleteClassroom:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				case Client_DeleteThread:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;
				case Client_DeleteComment:
					reply = forwardToServer(msg);
					returnMessage(reply);
					break;	
					
				default:
					msg.setBody("Request denied.");
					msg.setCode(-1);
					returnMessage(msg);
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
	
