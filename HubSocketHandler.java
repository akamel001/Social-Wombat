
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
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
	 * Checks that a user is part of a classroom
	 */
	private boolean isInClassroom(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID) > 0);
	}
	
	/*
	 * Check that a user is the instructor of a classroom
	 */
	private boolean isClassInstructor(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID) == 3);
	}
	
	/*
	 * Check that a user is the TA or instructor of a classroom
	 */
	private boolean isClassTAorInstructor(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID) > 1);
	}
	
	/*
	 * Check that a user is the student of a classroom
	 */
	private boolean isClassStudent(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID) == 1);
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
			msg.setCode(-1);
			int returnCode = -1;
			Message reply = null;
			//Handle the different types of client messages
			
			//NOTE: ALL MESSAGES ARE PRESET TO RETURN CODE -1
			switch(msg.getType()) {	
			
				// Client -> Hub
				
				//Client user id is stored as the sessionKey in the Cookie of the message
				// TODO: Possibly move this above switch statement to authenticate all requests
				case Client_LogIn: 
					String sessionKey = msg.getCookie().getKey();
					if(userList.validateUser(sessionKey)){
						//Reply with confirmation
						msg.setCode(1);
						System.out.println("User " + msg.getCookie().getKey() + " logged in");
					} 
					returnMessage(msg);
					break;
				// Returns in body all users in a classroom
				case Client_GetClassEnrollment:
					//check permissions
					if(isClassTAorInstructor(msg.getCookie().getKey(),msg.getClassroom_ID())){
						//String = User, Integer = Permission
						Map<String, Integer> classEnroll = classList.getClassEnrolled(msg.getClassroom_ID());
						if (classEnroll != null){				
							msg.setCode(1);
							msg.setBody(classEnroll);
						} 
					} 
					returnMessage(msg);
					break;
				// Return in body a list of the classes that a client is enrolled in
				case Client_GetUserEnrollment:
					String usr = msg.getCookie().getKey();
					Map<String, Integer> userEnroll = classList.getUserEnrollment(usr);
					if (userEnroll != null){
						msg.setCode(1);
						msg.setBody(userEnroll);
					} 
					returnMessage(msg);
					break;
				case Client_ListClassroomRequests:
					//check permissions
					if(isClassTAorInstructor(msg.getCookie().getKey(),msg.getClassroom_ID())){
						//gets list of users
						List<String> requests = classList.getClassPending(msg.getClassroom_ID());
						if (requests != null){
							msg.setCode(1);
							msg.setBody(requests);
						}
					}
					returnMessage(msg);
					break;
				// Change the permissions for another user, special case for student
				// Store user to be changed and the permissions as an arraylist
				// [0] = username, [1] = permissions
				case Client_SetPermissions:
					ArrayList<String> a= (ArrayList<String>) msg.getBody();
					// Person
					String personToChange = a.get(0);
					// Permission to set for Person
					int per = Integer.parseInt(a.get(1));
					
					//Special case, instructor cannot be deleted
					if((isClassInstructor(personToChange,msg.getClassroom_ID())) && (per == -1)){
						//return failure
						returnMessage(msg);
						break;
					} else if(isClassTAorInstructor(msg.getCookie().getKey(),msg.getClassroom_ID())){
						//Now changing someone else's
						// Server's return code
						returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per);
						// Reply
						msg.setCode(returnCode);
					} else if (isClassStudent(msg.getCookie().getKey(),msg.getClassroom_ID())){
						//Students can only delete themselves
						//Check student requestor matches up with requestee
						if (msg.getCookie().getKey().equals(personToChange)){
							// Server's return code
							returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per);
							// Reply
							msg.setCode(returnCode);
						}
					}
					returnMessage(msg);
					break;
				case Client_DeleteSelf:
					String u = msg.getCookie().getKey();
					if(userList.removeUser(u)){
						//success
						//TODO: remove the users from classList and if prof, remove serverlist
						msg.setCode(1);
					} else{
						//fail
						msg.setCode(-1);
					}
					returnMessage(msg);
					break;
				// Request to be added to a class
				case Client_RequestEnrollment:
					//check, cannot be already in class
					String requestName = msg.getCookie().getKey();
					if(!isInClassroom(requestName,msg.getClassroom_ID())){
						// 0 for pending enrollment, -1 for dijoining
						int p = (Integer)msg.getBody();
						returnCode = classList.setUserPermissions(requestName, msg.getClassroom_ID(), p);
						msg.setCode(returnCode);
					}
					returnMessage(msg);
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
					if (isInClassroom(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
					break;
				case Client_CreateComment:
					if (isInClassroom(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
					break;
				case Client_GoToClassroom:
					if (isInClassroom(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
					break;
				case Client_GoToThread:
					if (isInClassroom(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
					break;
				case Client_DeleteClassroom:
					if (isClassTAorInstructor(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
					break;
				case Client_DeleteThread:
					if (isClassTAorInstructor(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
					break;
				case Client_DeleteComment:
					if (isClassTAorInstructor(msg.getCookie().getKey(),msg.getClassroom_ID())){
						reply = forwardToServer(msg);
						returnMessage(reply);
					} else {
						returnMessage(msg);
					}
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
	
