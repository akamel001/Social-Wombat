
import java.io.*;
import java.net.*;
import java.util.*;

public class HubSocketHandler extends Thread{
	//private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	private static final boolean DEBUG = false;
	
	AES aesObject = null;
	static Message msg = new Message();
	ClassList classList;
	UserList userList;
	ServerList serverList;
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	HashMap<Integer,SocketPackage> serverPackages;

	/*
	 * A handler thread that is spawned for each message sent to a socket.
	 */
	public HubSocketHandler(Socket socket, ClassList classList, UserList userList, ServerList serverList, HashMap<Integer,SocketPackage> serverPackages, AES aesObject){
		this.socket = socket;
		this.classList = classList;
		this.userList = userList;
		this.serverList = serverList;
		this.serverPackages = serverPackages;
		this.aesObject = aesObject;	// to be used for communciation with servers
		// Create datastreams
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not create input and output streams");
		}
	}
	
	private boolean authenticate(Message msg){
		//Client user id is stored as the sessionKey in the Cookie of the message
		// TODO: Possibly move this above switch statement to authenticate all requests
		if (msg.getType() == Message.MessageType.Client_LogIn){
			// extract neccessary info from msg
			String userName = msg.getUserName();
			
			//Pull out the password
			char[] password = userList.getUserPass(userName, aesObject);
			// check existence of username
			if (password == null){
				//send back error code
				msg.setCode(-1);
				if(DEBUG) System.out.println("Attempted intrusion by: " + userName);
				returnMessage(msg);
				return false;
			}
			
			//Zero out password
			Arrays.fill(password,'0');
			
			
			//look up salt
			byte[] salt = msg.getSalt();
			// TODO: lookup the password
			char[] pass = null;
			
			//Generate aes key
			aesObject = new AES(pass,salt);
			
			// TODO: timestamp
			Date date = (Date)aesObject.decryptObject((byte[])msg.getBody());
			
			//if timestamp within 5 mins of now, true.
		}
		return false;
	}
	
	/*
	 * Checks that a user is part of a classroom
	 */
	private boolean isInClassroom(String user, String classroomID){
		if(DEBUG) System.out.println("checking if user is in classroom");
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
	private void getMessage(){
		try {
			// blocking read
			msg = (Message) ois.readObject();
			//System.out.println("Just got a msg from: " + msg.getCookie().getKey());
			//System.out.println("Contents of body: " + msg.getBody());
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
	}
	
	/*
	 * Send a message back after it's been altered
	 */
	private void returnMessage(Message msg){
		//Get output stream
		try {
			oos.writeObject(msg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Return Message Failed");
		}	
	}
	
	/*
	 * Looks in the message and takes the classroom to find the correct serverID, 
	 * an int, that the message should be sent to.
	 */
	private int getServer(Message msg){
		//get classroom name from msg
		String classID = msg.getClassroom_ID();
		//table lookup of classrooms and their appropriate server
		return classList.getClassServer(classID);
	}
	
	/*
	 * Constructs the message that needs to be forwarded 
	 * and receives the response as a message
	 */
	private Message forwardToServer(Message msg){
		SocketPackage forwardSocketPackage;
		Message reply = null;

		// Open a socket connection with appropriate server
		if(DEBUG) System.out.println("Message forwarded to: " + getServer(msg));
		forwardSocketPackage = serverPackages.get(getServer(msg));

		//Only one user can be communicating with a server at one time
		synchronized(forwardSocketPackage){
			// Write out message
			forwardSocketPackage.send(msg);
			
			// Get response
			reply = forwardSocketPackage.receive();
		}
		return reply;
	}
	
	/*
	 * When waiting for a message, only a message from a client is expected.
	 * If message is forwarded to the server, that response will only accept
	 * server messages. Authentication responses to clients accept no response.
	 */
	public void run(){
		//takes over to infinitely listen for each user
		boolean listen = true;
		
		//Handling first request
		//Message will have salt, IV, username in the clear
		//in body(encrypted): calendar.getMilisec(long), and nonce(long) in ArrayList<long>. 
		
		//steps
		
		
		
		//send back
		//nonce + 1
		
		
		//all future messages
		//have checksum of all fields except checksum, store checksum in checksum field
		//message is completely encrypted
		
		//decrypt
		//check checksum
		//do stuff after
		//always create new messages
		
		//if works, create aes object with given salt and iv
		
		//TODO: change
		//First accept
		getMessage();
		//checks
		if (msg == null){
			System.out.println("Message was null");
			listen = false; // Don't waste time on bad transmissions
		} else if (msg.getUserName() == null){
			System.out.println("Session key was null");
			listen = false;
		}
		//Authenticate
		listen = authenticate(msg);
		
		while (listen){
			boolean valid = true;
			//Wait, read and deserialize Message from Socket
			getMessage();
			
			if (msg == null){
				System.out.println("Message was null");
				valid = false; // Don't waste time on bad transmissions
			} else if (msg.getUserName() == null){
				System.out.println("Session key was null");
				valid = false;
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
					
					case Client_ChangePassword:
						//TODO: do
						
						break;
				
					// Returns in body all users in a classroom
					case Client_GetClassEnrollment:
						//check permissions
						if(isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
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
						String usr = msg.getUserName();
						if(DEBUG) System.out.println(usr + " wants to see all their class enrollments");
						Map<String, Integer> userEnroll = classList.getUserEnrollment(usr);
						if (userEnroll != null){
							msg.setCode(1);
							msg.setBody(userEnroll);
						} 
						returnMessage(msg);
						break;
					case Client_ListClassroomRequests:
						//check permissions
						if(isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
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
						if(DEBUG) System.out.println(msg.getUserName() + " setting permissions");
						@SuppressWarnings("unchecked")
						ArrayList<String> a= (ArrayList<String>) msg.getBody();
						// Person
						String personToChange = a.get(0);
						// Permission to set for Person
						int per = Integer.parseInt(a.get(1));
	
						//Special case, instructor cannot be deleted
						if((isClassInstructor(personToChange,msg.getClassroom_ID())) && (per == -1)){
							//return failure
							if(DEBUG) System.out.println("Denied. Instructor cannot delete self from classroom.");
							returnMessage(msg);
							break;
						} else if(isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
							//Now changing someone else's
							// Server's return code
							if(DEBUG) System.out.println("Setting " + personToChange + "'s permissions to: " + per);
							returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per);
							// Reply
							msg.setCode(returnCode);
						} else if (isClassStudent(msg.getUserName(),msg.getClassroom_ID())){
							//Students can only delete themselves
							//Check student requestor matches up with requestee
							if (msg.getUserName().equals(personToChange)){
								// Server's return code
								returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per);
								// Reply
								msg.setCode(returnCode);
							}
						}
						returnMessage(msg);
						break;
					case Client_DeleteSelf:
						String u = msg.getUserName();
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
						String requestName = msg.getUserName();
						if(DEBUG) System.out.println(requestName + " requested to be added to classroom: " + msg.getClassroom_ID());
						if(!isInClassroom(requestName,msg.getClassroom_ID())){
							// 0 for pending enrollment, -1 for dijoining
							int p = (Integer)msg.getBody();
							if(DEBUG) System.out.println("adding into classroom...");
							returnCode = classList.setUserPermissions(requestName, msg.getClassroom_ID(), p);
							if(DEBUG) System.out.println("return code is: " + returnCode);
							msg.setCode(returnCode);
						}
						returnMessage(msg);
						break;
						
					// Client -> Hub -> Server
					// NOTE: ANYTHING SENT IN THIS PATHWAY REQUIRES THE 
					// CLASSROOM_ID TO BE SET IN THE MESSAGE ALREADY!!!!!
					case Client_CreateClassroom:
						//Add that classroom id to a server
						ClassData c = new ClassData(msg.getUserName());
						c.setClassName(msg.getClassroom_ID());
						
						//Generate some server number
						Random r = new Random();
						int maxServer = serverList.getLastServer();
						int serverNum = r.nextInt(maxServer) + 1;
						
						c.setClassServer(serverNum, SERVER_SOCKET);
						classList.addClass(c);
						
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
						if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;
					case Client_CreateComment:
						if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;
					case Client_GoToClassroom:
						if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;
					case Client_GoToThread:
						if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;
					case Client_DeleteClassroom:
						if (isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							//Check that the reply code is affirmative
							if(reply.getCode() == 1){
								classList.removeClass(reply.getClassroom_ID());
							}
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;
					case Client_DeleteThread:
						if (isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;
					case Client_DeleteComment:
						if (isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
							reply = forwardToServer(msg);
							returnMessage(reply);
						} else {
							returnMessage(msg);
						}
						break;	
					case Client_CloseSocket:
						listen = false;
						// Close everything
						try {
							oos.close();
							ois.close();
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("Couldn't close");
						}
						break;
					default:
						msg.setBody("Request denied.");
						msg.setCode(-1);
						returnMessage(msg);
						break;
				}
			}
		}
	}
} 
	
