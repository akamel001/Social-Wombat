package hub;



import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import security.AES;
import security.CheckSum;
import storage.ClassData;
import storage.ClassList;
import storage.Message;
import storage.ServerList;
import storage.UserList;
import util.SocketPackage;


final class HubSocketHandler extends Thread{
	//private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	private static final boolean DEBUG = true;
	
	AES hubAESObject = null;
	AES clientAESObject = null;
	static Message msg = new Message();
	ClassList classList;
	UserList userList;
	ServerList serverList;
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	HashMap<Integer,SocketPackage> serverPackages;
	String lastLogin;
	HashMap<String,Integer> currentUsers;
	String currentUser;
	Long currentNonce = null;

	/*
	 * A handler thread that is spawned for each message sent to a socket.
	 */
	public HubSocketHandler(Socket socket, ClassList classList, UserList userList, ServerList serverList, HashMap<Integer,SocketPackage> serverPackages, AES hubAESObject, HashMap<String,Integer> currentUsers){
		this.socket = socket;
		this.classList = classList;
		this.userList = userList;
		this.serverList = serverList;
		this.serverPackages = serverPackages;
		this.hubAESObject = hubAESObject;	// to be used for communciation with servers
		this.currentUsers = currentUsers;
		// Create datastreams
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			//e.printStackTrace();
			//System.out.println("Could not create input and output streams");
			//Exceptions will throw if junk is sent to the port
		}
	}
	
	
	/*
	 * Checks that a user is part of a classroom
	 */
	private boolean isInClassroom(String user, String classroomID){
		if(DEBUG) System.out.println("checking if user is in classroom");
		return (classList.getUserPermissions(user, classroomID, hubAESObject) > 0);
	}
	
	/*
	 * Check that a user is the instructor of a classroom
	 */
	private boolean isClassInstructor(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID, hubAESObject) == 3);
	}
	
	/*
	 * Check that a user is the TA or instructor of a classroom
	 */
	private boolean isClassTAorInstructor(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID, hubAESObject) > 1);
	}
	
	/*
	 * Check that a user is the student of a classroom
	 */
	private boolean isClassStudent(String user, String classroomID){
		return (classList.getUserPermissions(user, classroomID, hubAESObject) == 1);
	}
	
	/*
	 * Authentication method to deserialize the first received message
	 * and authenticate clients
	 * Message will have salt, IV, username in the clear. In the
	 * body (encrypted): calendar.getMilisec(long), nonce(long) stored 
	 * in ArrayList<long>. 
	 * 
	 * Returns true if user is authenticated and future transmissions are 
	 * allowed. False otherwise.
	 */
	@SuppressWarnings("unchecked")
	private boolean authenticate(){
		if (DEBUG) System.out.println("Received an authentication request");

		Message firstMessage = null;
		try {
			firstMessage = (Message) ois.readObject();
			// null check
			if (firstMessage == null){
				if (DEBUG) System.out.println("First msg received was null");
				return false;
			}
			else
				msg = firstMessage; // ADDED by cd 3/21/12
		} catch (EOFException e){
			System.out.println("Unable to read from socket. Socket possibly closed.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//Check message type
		if (DEBUG) System.out.println("MESSAGE TYPE: " + msg.getType());
		if (msg.getType() != Message.MessageType.Client_LogIn){
			return false;
		}
		
		//extract fields
		String usr = firstMessage.getUserName();
		byte[] salt = firstMessage.getSalt();
		byte[] iv = firstMessage.getIv();
		
		//null check
		if ((usr==null)||(salt==null)||(iv==null)){
			if (DEBUG) System.out.println("Either username, salt, or iv received in first message was null.");
			return false;
		}
		//check if the user exists
		//Pull out the password
		char[] password = userList.getUserPass(usr, hubAESObject);
		if (DEBUG) System.out.println("PASSWORD: " + Arrays.toString(password));
		// check existence of username
		if (password == null){
			//send back error code
			Message returnMsg = new Message();
			returnMsg.setType(Message.MessageType.Client_LogIn);
			returnMsg.setCode(-1);
			returnMessage(returnMsg);
			if(DEBUG) System.out.println("Attempted intrusion by: " + usr);
			
			return false;
		}
		
		//Create client aes object
		clientAESObject = new AES(password, iv, salt);
		
		//Zero out password
		Arrays.fill(password,'0');
		
		//null check aes object
		if (clientAESObject == null){
			if (DEBUG) System.out.println("clientAESObject creation failed");
			return false;
		}
		
		//Decrypt the body, cast to ArrayList<Long>
		byte[] encryptedBody = (byte[]) firstMessage.getBody();

		ArrayList<Long> body = (ArrayList<Long>)clientAESObject.decryptObject(encryptedBody);
		if (body==null){
			if (DEBUG) System.out.println("unable to decrypt msg body or invalid authentications");
			//return an invalid login occured
			Message returnMsg = new Message();
			returnMsg.setType(Message.MessageType.Client_LogIn);
			returnMsg.setCode(-1);
			returnMessage(returnMsg);
			return false;
		}
		
		//Checksum
		long newChecksum = CheckSum.getChecksum(body);
		if (DEBUG) System.out.println("New checksum: " + newChecksum);
		
		long oldChecksum = msg.getChecksum();
		if (DEBUG) System.out.println("Old checksum: " + oldChecksum);
		
		if (newChecksum != oldChecksum){
			return false;
		}
		
		long clientTimestamp = body.get(0);
		long clientNonce = body.get(1);
		
		if (DEBUG) System.out.println("Client timestamp: " + clientTimestamp);
		if (DEBUG) System.out.println("Client nonce: " + clientNonce);
		
		boolean allowed = false;
		
		//Check timestamp
		long myTimestamp = Calendar.getInstance().getTimeInMillis();
		if (((myTimestamp - 300000) <= clientTimestamp) && (clientTimestamp <= (myTimestamp + 300000))){
			allowed = true;
		}
		
		if (DEBUG) System.out.println("Client authentication allowed. Sending back appropriate reply");
		
		currentUser = firstMessage.getUserName();
		
		//check if the user is already logged in
		if (currentUsers.containsKey(firstMessage.getUserName())){
			if (DEBUG) System.out.println("User is already logged in");
			return false;
		}
		
		// add the users to the current list
		currentUsers.put(firstMessage.getUserName(), 1);
		

		if(DEBUG) System.out.println("Set CurrentUser: " + currentUser);
		//Update the login
		lastLogin = updateLastLogin(usr,socket.getInetAddress());
		
		
		//Return the authenticated message
		if (allowed){
			//Create return message
			Message returnMsg = new Message();
			ArrayList<Long> returnBody = new ArrayList<Long>();
			//set my timestamp
			returnBody.add(0, Calendar.getInstance().getTimeInMillis());
			//set the nonce+1
			currentNonce = clientNonce+1;
			returnBody.add(1, clientNonce+1);
			//set the body 
			returnMsg.setBody(returnBody);
			//set checksum
			long checksum = returnMsg.generateCheckSum();
			
			if (DEBUG) System.out.println("Returning checksum: " + checksum);
			
			returnMsg.setChecksum(checksum);
			//encrypt
			returnMsg.setBody(clientAESObject.encrypt(returnBody));
			
			//send unencrypted message
			returnMessage(returnMsg);
			
			//put thread in a state to receive future messages
			return true;
		}
		
		SecureRandom s = new SecureRandom();
		int pause = (int)((s.nextDouble()*600)+200);
		try {
			Thread.sleep(pause);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Returns the last login and then resets it to the current time.
	 * @param user The user to be updated.
	 * @param ip The current ip from which the user has logged in.
	 * @return Returns a String containing the "ip, date" of the last login. 
	 */
	public String updateLastLogin(String user, InetAddress ip){
		// Gets the last login.
		String out = userList.getLastLogin(user, hubAESObject);
		
		//  Updates the last login to the current time and passed ip.
		userList.updateLastLogin(user, ip, new Date(), hubAESObject);
		
		// return the last login
		return out;
	}
	
	/**@deprecated
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
	
	/**
	 * Gets and decrypts a message. Also does checksumming
	 * Blocking read.
	 * Will return true if was able to decrypt message and checksum
	 * passes. Will return false otherwise.
	 */
	private boolean getAndDecryptMessage(){
		
		try {
			//length
			int length = 0;

			//read the length of the byte [] first
			try {
				length = ois.readInt();
				
			} catch (EOFException e){
				System.out.println("Attempting to read from a closed socket.");
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (Exception e){
				if (DEBUG) System.out.println("Invalid message received");
				return false;
			}
			
			//set a buffer to correct length
			byte[] encryptedMsg = new byte[length];
			//read encrypted message
			try {
				
				ois.read(encryptedMsg, 0, length);
				//ois.readFully(encryptedMsg);
			} catch (EOFException e){
				System.out.println("Unable to read from socket. Socket possibly closed.");
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			msg = (Message)clientAESObject.decryptObject(encryptedMsg);
			
			//do nonce check
			long newNonce = msg.getNonce();
			if (DEBUG) System.out.println("oldNonce: " + currentNonce + " newNonce: "+ newNonce);
			if (newNonce != currentNonce+1){
				if (DEBUG) System.out.println("Expected nonce mismatch");
				return false;
			}
			//set current nonce
			currentNonce = newNonce + 1;
			
			//do checksum
			long oldChecksum = msg.getChecksum();
			long newChecksum = msg.generateCheckSum();
			
			if (DEBUG) System.out.println("oldChecksum = " + oldChecksum);
			if (DEBUG) System.out.println("newChecksum = " + newChecksum);
			
			return (oldChecksum == newChecksum);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Possible socket closure");
			return false;
		}
		
	}
	
	/**
	 * Method to send an encrypted message to the precreated streams.
	 */
	private void sendEncryptedMessage(byte[] msg){
		try {
			//send the length along first
			oos.writeInt(msg.length);
			oos.write(msg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Sending an encrypted message failed");
		}
	}
	
	/**
	 * Send an unencryted message back after it's been altered
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
	 * Encrypt and return a message. Takes a message and will set the checksum for you.
	 * 
	 */
	private void returnAndEncryptMessage(Message msg){
		//set up nonce
		msg.setNonce(currentNonce + 1);
		currentNonce = currentNonce + 1;
		//calculate checksum
		long thisChecksum = msg.generateCheckSum();
		msg.setChecksum(thisChecksum);
		//encrypt message why client aes key
		byte[] eMsg = clientAESObject.encrypt(msg);
		//send over wire
		try {
			//write in the length first
			oos.writeInt(eMsg.length);
			//then write message
			oos.write(eMsg);
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
		return classList.getClassServer(classID, hubAESObject);
	}
	
	/*
	 * Constructs the message that needs to be forwarded 
	 * and receives the response as a message
	 */
	private Message forwardToServer(Message msg){
		SocketPackage forwardSocketPackage;
		Message reply = null;
		byte[] eReply = null;
		// Open a socket connection with appropriate server
		if(DEBUG) System.out.println("Message forwarded to: " + getServer(msg));
		int serverID = getServer(msg);
		forwardSocketPackage = serverPackages.get(serverID);

		//Set checksum
		long checksum = msg.generateCheckSum();
		msg.setChecksum(checksum);
		
		//encrypt
		List<byte[]> ivsalt = serverList.getIvSalt(serverID, hubAESObject);
		//get iv
		byte[] iv = ivsalt.get(0);
		//get salt
		byte[] salt = ivsalt.get(1);
		//get pass
		char[] pass = serverList.getServerPass(serverID, hubAESObject);
		AES aesObj = new AES(pass,iv,salt);
		//clear pass
		Arrays.fill(pass, '0');
		byte[] eMsg = aesObj.encrypt(msg);
		
		//Only one user can be communicating with a server at one time
		synchronized(forwardSocketPackage){
			// Write out message
			forwardSocketPackage.sendEncrypted(eMsg);
			
			// Get response
			eReply = forwardSocketPackage.receiveEncrypted();
		}
		
		//decrypt
		reply = (Message)aesObj.decryptObject(eReply);
		
		return reply;
	}
	
	/*
	 * When waiting for a message, only a message from a client is expected.
	 * If message is forwarded to the server, that response will only accept
	 * server messages. Authentication responses to clients accept no response.
	 */
	public void run(){
		try {
			//takes over to infinitely listen for each user
			boolean listen = false;
			boolean valid = true;
			boolean relogin = false;
			
			//Authenticate, if listen is false, the socket is problematic, close connections
			//loop to allow continuous authentication
			while(!listen){
				try {
					listen = authenticate();
				} catch (Exception e){
					//Handles junk injections to our port
					if (DEBUG) System.out.println("Junk seen on port, killing that particular thread.");
					return;
				}
			}
			//All further communications
			while ((listen && valid) || relogin){
				//for future logins
				while(!listen){
					relogin = false;
					try {
						listen = authenticate();
					} catch (Exception e){
						//Handles junk injections to our port
						if (DEBUG) System.out.println("Junk seen on port, killing that particular thread.");
						return;
					}
				}
				
				//Wait, read and deserialize Message from Socket
				if (DEBUG) System.out.println("Waiting for next msg");
				//possibility of bad message here
				valid = getAndDecryptMessage();
				if (DEBUG) System.out.println("message decryption attempted, valid: " + valid);
				if (valid) {
					//check if hub is running
					if (!Hub.listening){
						if (DEBUG) System.out.println("Hub was shutdown, closing this corresponding spawned thread.");
						//hub is shutdown
						Message shutdownMsg = new Message();
						shutdownMsg.setType(Message.MessageType.Hub_Shutdown);
						shutdownMsg.setCode(-1);
						returnAndEncryptMessage(shutdownMsg);
						listen = false;
						valid = false;
						//need to remove user since we don't reach end of thread now
						currentUsers.remove(currentUser);
						return;
					}
					
					//check that the user is still the same 
					if (!currentUser.contains(msg.getUserName())){
						if (DEBUG) System.out.println("Mismatch in the authenticated user and current user");
						valid = false;
					}
					
					if (DEBUG) System.out.println("user name is: " + msg.getUserName());
					
					if (msg == null){
						System.out.println("Message was null");
						valid = false; // Don't waste time on bad transmissions
					} else if (msg.getUserName() == null){
						System.out.println("Session key was null");
						valid = false;
					}
				}
				if (valid){
									
					// Preset to failure
					msg.setCode(-1);
					int returnCode = -1;
					Message reply = null;
					//Handle the different types of client messages
					
					if (DEBUG) System.out.println("Message type is: " + msg.getType());
					
					//NOTE: ALL MESSAGES ARE PRESET TO RETURN CODE -1
					switch(msg.getType()) {	
					
						// Client -> Hub
						case Client_Logout:
							//need to reset to authenticate
							listen = false;
							valid = true;
							relogin = true;
							//remove user
							currentUsers.remove(currentUser);
							msg = new Message();
							msg.setCode(1);
							msg.setType(Message.MessageType.Client_Logout);
							returnAndEncryptMessage(msg);
							break;
						case Client_ChangePassword:
							//body: ArrayList<char[]> 
							boolean allowed = true;
							@SuppressWarnings("unchecked")
							ArrayList<char[]> body = (ArrayList<char[]>) msg.getBody();
							//[0] = oldpass, [1] new pass, [2] confirm new pass, [3] tempusername
							
							//check temp username matches provided username
							if (!Arrays.equals(body.get(3),msg.getUserName().toCharArray())){
								allowed = false;
							}
							//check username matches our currentUsername
							if (!currentUser.equals(msg.getUserName())){
								allowed = false;
							}
							//lookup pass
							char[] oldPass = userList.getUserPass(msg.getUserName(), hubAESObject);
							//check oldpass matches with lookup pass
							if(!Arrays.equals(oldPass, body.get(0))){
								allowed = false;
							}
							//clear oldpass
							Arrays.fill(oldPass, '0');
							Arrays.fill(body.get(0), '0');
							//check newpass arrays.equals confirm new pass
							if(!Arrays.equals(body.get(1), body.get(2))){
								allowed = false;
							}
							//if all success, then change pass
							if (allowed){
								if (DEBUG) System.out.println("Client change password allowed, changing...");
								int ret = userList.changeUserPassword(currentUser, body.get(1), hubAESObject);
								if(ret==1){
									//successful system change
									if (DEBUG) System.out.println("userList password changed");
								} else{
									//not successful
									if (DEBUG) System.out.println("userList password not changed");
									allowed = false;
								}
							} 
							//clear new passwords
							Arrays.fill(body.get(1), '0');
							Arrays.fill(body.get(2),'0');
							//reset msg
							msg = new Message();
							if (allowed){
								msg.setCode(1);
							} else {
								if (DEBUG) System.out.println("Client change password wasn't allowed.");
								msg.setCode(-1);
							}
							//return
							returnAndEncryptMessage(msg);
							break;
					
						// Returns in body all users in a classroom
						case Client_GetClassEnrollment:
							//check permissions
							if(isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
								//String = User, Integer = Permission
								Map<String, Integer> classEnroll = classList.getClassEnrolled(msg.getClassroom_ID(), hubAESObject);
								if (classEnroll != null){				
									msg.setCode(1);
									msg.setBody(classEnroll);
								} 
							} 
							returnAndEncryptMessage(msg);
							break;
						// Return in body a list of the classes that a client is enrolled in
						case Client_GetUserEnrollment:
							String usr = msg.getUserName();
							if(DEBUG) System.out.println(usr + " wants to see all their class enrollments");
							Map<String, Integer> userEnroll = classList.getUserEnrollment(usr, hubAESObject);
							if (userEnroll != null){
								msg.setCode(1);
								msg.setBody(userEnroll);
							} 
							returnAndEncryptMessage(msg);
							break;
						case Client_ListClassroomRequests:
							//check permissions
							if(isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
								//gets list of users
								List<String> requests = classList.getClassPending(msg.getClassroom_ID(), hubAESObject);
								if (requests != null){
									msg.setCode(1);
									msg.setBody(requests);
								}
							}
							returnAndEncryptMessage(msg);
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
								returnAndEncryptMessage(msg);
								break;
							} else if(isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
								//Now changing someone else's
								// Server's return code
								if(DEBUG) System.out.println("Setting " + personToChange + "'s permissions to: " + per);
								returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per, hubAESObject);
								// Reply
								msg.setCode(returnCode);
							} else if (isClassStudent(msg.getUserName(),msg.getClassroom_ID())){
								//Students can only delete themselves
								//Check student requestor matches up with requestee
								if (msg.getUserName().equals(personToChange)){
									// Server's return code
									returnCode = classList.setUserPermissions(personToChange, msg.getClassroom_ID(), per, hubAESObject);
									// Reply
									msg.setCode(returnCode);
								}
							}
							returnAndEncryptMessage(msg);
							break;
						case Client_GetLastLogin:
							msg.setBody(lastLogin);
							msg.setCode(1);
							returnAndEncryptMessage(msg);
							break;
						
						case Client_DeleteSelf:
							String u = msg.getUserName();
							if(userList.removeUser(u)==1){
								//success
								//TODO: remove the users from classList and if prof, remove serverlist
								msg.setCode(1);
							} else{
								//fail
								msg.setCode(-1);
							}
							returnAndEncryptMessage(msg);
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
								returnCode = classList.setUserPermissions(requestName, msg.getClassroom_ID(), p, hubAESObject);
								if(DEBUG) System.out.println("return code is: " + returnCode);
								msg.setCode(returnCode);
							}
							returnAndEncryptMessage(msg);
							break;
							
						// Client -> Hub -> Server
						// NOTE: ANYTHING SENT IN THIS PATHWAY REQUIRES THE 
						// CLASSROOM_ID TO BE SET IN THE MESSAGE ALREADY!!!!!
						case Client_CreateClassroom:
							//Add that classroom id to a server
							ClassData c = new ClassData(msg.getUserName());
							c.setClassName(msg.getClassroom_ID());
							
							//Generate some server number
							SecureRandom r = new SecureRandom();
							int maxServer = serverList.getLastServer();
							if (DEBUG) System.out.println("Servers available: " + maxServer);
							//If no servers are available, fail
							if (maxServer == 0){
								System.out.println("No servers are available, or " +
										"they have not been connected yet. Please " +
										"connect them to process the user requests.");
								Message failReply = new Message();
								failReply.setCode(-1);
								failReply.setType(Message.MessageType.Client_CreateClassroom);
								returnAndEncryptMessage(failReply);
								break;
							}
							int serverNum = r.nextInt(maxServer) + 1;
							if (DEBUG) System.out.println("Storing new classroom in server: " + serverNum 
									+ " out of: " + maxServer + " servers.");
							
							
							c.setClassServer(serverNum, SERVER_SOCKET);
							classList.addClass(c, hubAESObject);
							
							reply = forwardToServer(msg);
							returnAndEncryptMessage(reply);
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
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
							}
							break;
						case Client_CreateComment:
							if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
								reply = forwardToServer(msg);
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
							}
							break;
						case Client_GoToClassroom:
							if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
								reply = forwardToServer(msg);
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
							}
							break;
						case Client_GoToThread:
							if (isInClassroom(msg.getUserName(),msg.getClassroom_ID())){
								reply = forwardToServer(msg);
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
							}
							break;
						case Client_DeleteClassroom:
							if (isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
								reply = forwardToServer(msg);
								//Check that the reply code is affirmative
								if(reply.getCode() == 1){
									classList.removeClass(reply.getClassroom_ID());
								}
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
							}
							break;
						case Client_DeleteThread:
							if (isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
								reply = forwardToServer(msg);
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
							}
							break;
						case Client_DeleteComment:
							if (isClassTAorInstructor(msg.getUserName(),msg.getClassroom_ID())){
								reply = forwardToServer(msg);
								returnAndEncryptMessage(reply);
							} else {
								returnAndEncryptMessage(msg);
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
							returnAndEncryptMessage(msg);
							break;
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
			//gracefully remove user
			if (DEBUG) System.out.println("Connection with: " + currentUser + " broken");
			currentUsers.remove(currentUser);
		}
		if(DEBUG) System.out.println("Connected users: \n===> " + currentUsers.toString());
		//If the thread exits, then it means that the user is no longer logged in
		if (DEBUG) System.out.println(currentUser + " session has ended, removing them from current user map.");
		currentUsers.remove(currentUser);
	}
} 
	
