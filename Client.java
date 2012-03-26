import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Client {

	//TODO Checksum 
	// TODO: check that any inputs are not empty/null.
	
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_OUTPUT = false;

	private static final String hub_addr = "127.0.0.1";
	private static final int HUB_PORT = 4444;
	private SocketPackage socket;
	private AES aes = null;
	/**
	 *  
	 * This method is used in the login page to verify a user name.
	 * It takes in a user name and returns true if the user name can be verified,
	 * and false otherwise.
	 * @param password 
	 * @param userName
	 * @return boolean corresponding to verification status
	 */
	
	public Client(){
		InetAddress hub;
		try {			
			//setting hub address and connecting
			hub = InetAddress.getByName(hub_addr);
			socket = new SocketPackage(hub, HUB_PORT);
			socket.socketConnect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean handleLogin(String uName, char[] password){
		
		if(DEBUG) return true; 
		
		if(password.length == 0)
			return false;
		
		aes = new AES(password);
		Calendar c = Calendar.getInstance();
		SecureRandom r = new SecureRandom();
		
		Long nonce = r.nextLong();
		
		//Zeros out password
		Arrays.fill(password, '0');

		Message message = new Message();
		message.setType(Message.MessageType.Client_LogIn);
		message.setSalt(aes.getSalt());
		message.setIv(aes.getIv());
		message.setUserName(uName);

		ArrayList<Long> list = new ArrayList<Long>();
		list.add(0, c.getTimeInMillis());
		list.add(1, nonce);
		
		if(DEBUG_OUTPUT) System.out.println("Seinding time in sec: " + list.get(0));

		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		
		if(DEBUG_OUTPUT) System.out.println("Checksum: " + message.getChecksum());
		
		message.setBody(aes.encrypt(list));
		
		if(DEBUG_OUTPUT) System.out.println("Sending authenticating message...");
		
		socket.send(message);

		
		// Get timestamp message
		Message response = socket.receive();
		if(DEBUG_OUTPUT) System.out.println("Received a response");
		
		if(response.getCode() == -1) return false;
		
		byte[] encryptedBody = (byte[])response.getBody();
		
		ArrayList<Long> return_list = (ArrayList<Long>)aes.decryptObject(encryptedBody);
		
		if (return_list==null){
			System.out.println("unable to decrypt msg body");
			return false;
		}else
			response.setBody(return_list);
		
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!\n==> Received checksum: " + response.getChecksum() + "\n==> Generated Checksum" + response.generateCheckSum());
			return false;
		}else{
			if(DEBUG_OUTPUT) System.out.println("Checksum passed!");
		}

		
		
		long now =	c.getTimeInMillis();
		long hub_time = return_list.get(0); 
		long hub_nonce = return_list.get(1);
		
		boolean allowed = false;
		
		if( now <= hub_time+300000 && hub_time-300000 <= hub_time && hub_nonce==nonce+1)
			allowed=true;
		
		if(allowed){
			if(DEBUG_OUTPUT) System.out.println("Authenticated!!");
			return true;
		}
		else{
			if(DEBUG_OUTPUT) System.out.println("Failed to Authenticate!!");
			return false;
		}
			
	}
	
	/**
	 * This methods logs out a user corresponding to currentUserName.
	 * @param currentUserName
	 * @return
	 */
	public boolean handleLogout(String currentUserName) {

		if(DEBUG)
			return true;

		Message message = new Message();
		message.setUserName(currentUserName);

		message.setType(Message.MessageType.Client_Logout);

		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		
		socket.sendEncrypted(aes.encrypt(message));
		byte[] encMessage = socket.receiveEncrypted();
		
		Message response = (Message) aes.decryptObject(encMessage);
		
		return (response.getCode() == 1)? true : false;	
	}
	
	/**
	 * This method is used to create a classroom.
	 * It takes in a classroom name and returns true if the classroom can be created,
	 * and false otherwise.
	 * @param classroomName
	 * @param uName
	 * @return boolean corresponding to classroom creation status
	 */
	public boolean createClassroom(String classroomName, String uName) {

		if(DEBUG)
			return true;

		Message message = new Message();
		message.setUserName(uName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_CreateClassroom);

		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		
		socket.sendEncrypted(aes.encrypt(message));
		byte[] encMessage = socket.receiveEncrypted();
		
		Message response = (Message) aes.decryptObject(encMessage);
		
		return (response.getCode() == 1)? true : false;	
	}

	/**
	 * This method is used in requesting to join a classroom.
	 * It takes in a classroom request name and returns true if the classroom can be requested,
	 * and false otherwise.
	 * @param classroomRequestName
	 * @param requesterUserName
	 * @return boolean corresponding to whether the classroom can be requested
	 */
	public boolean requestToJoinClassroom(String classroomRequestName, String requesterUserName) {

		if(DEBUG)
			return true;


		Message message = new Message();
		message.setUserName(requesterUserName);
		message.setType(Message.MessageType.Client_RequestEnrollment);
		message.setClassroom_ID(classroomRequestName);
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;			
	}

	/**
	 * This method is used to get a list of classrooms for a particular member.
	 * It takes in a user name and returns the list of classrooms of which the user is a member.
	 * @param userName
	 * @return Map of classroomName -> permissions 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getClassroomMapForUser(String userName) {
		
		if(DEBUG){
			Map<String, Integer> classroomList = new HashMap<String, Integer>();

			classroomList.put("CS 4820", 1);
			classroomList.put("LING 4844", 2);
			classroomList.put("HD 3260", 3);
			return classroomList;		
		}
		Message message = new Message();
		
		message.setUserName(userName);
		message.setType(Message.MessageType.Client_GetUserEnrollment);
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		byte[] enc = aes.encrypt(message);
		if(DEBUG_OUTPUT) System.out.println("Sending message with ");
		socket.sendEncrypted(enc);
	
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		
		//TODO Inform Julia of response that the hub is not responding and needs to be handled
		if(response.getType() == Message.MessageType.Hub_Shutdown){
			System.out.println("The hub is shut down, exiting for now.");
			System.exit(-1);
		}
		
		return (Map<String, Integer>) ((response.getCode() == 1)? response.getBody() : null);	
	}

	/**
	 * This method creates a new thread with threadName and postContent.
	 * @param threadName
	 * @param postContent
	 * @param currentUserName
	 * @return
	 */
	public boolean createThread(String threadName, String postContent, String currentUserName, String classroomName) {

		if(DEBUG)
			return true;

//		ClientSocketHandler handler = new ClientSocketHandler();
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, threadName);
		list.add(1, postContent);

		Message message = new Message();
		message.setUserName(currentUserName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_CreateThread);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;	
	}

	/**
	 *
	 */
	public boolean deleteClassroom(String classroomName, String userName) {
		
		if(DEBUG)
			return true;
		
		Message message = new Message();
		message.setUserName(userName);
		message.setType(Message.MessageType.Client_DeleteClassroom);
		message.setClassroom_ID(classroomName);
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;	
	}

	/**
	 * Create message type to handle this request appropriately 
	 * **** Consider bool return type to confirm class has been deleted? ***
	 */
	public boolean disjoinClassroom(String classroomName, String userName) {
		
		if(DEBUG)
			return true;

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, userName);
		list.add(1, "-1");
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_SetPermissions);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;		
	}

	/**
	 * 
	 * @param classroomName
	 * @param userName
	 * @return Map of Thread ID -> Thread Name
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> getThreadMapForClassroom(String classroomName, String userName) {

		if(DEBUG){
			Map<Integer, String> defaultMap = new HashMap<Integer, String>();
			defaultMap.put(new Integer(4), "World");
			defaultMap.put(new Integer(2), "Hello");
			defaultMap.put(new Integer(7), "What's up?");
			return defaultMap;	
		}

		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_GoToClassroom);
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		

		return (Map<Integer, String>) ((response.getCode() == 1)? response.getBody() : null);	
	}

	/**
	 * This function returns the contents of a single thread given its ID.
	 * @param threadID
	 * @param classroomName
	 * @param userName
	 * @return Map of ThreadTopic/Post/Comment IDs -> Content
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, String> getThreadGivenID(Integer threadID, String classroomName, String userName) {
		
		if(DEBUG){
			Map<Integer, String> defaultMap = new HashMap<Integer, String>();
			defaultMap.put(new Integer(4), "Initial Post");
			defaultMap.put(new Integer(2), "Topic");
			defaultMap.put(new Integer(7), "I like to comment on stuff!");
			return defaultMap;	
		}
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setBody(threadID);
		message.setType(Message.MessageType.Client_GoToThread);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return null;
		}
		return (Map<Integer, String>) ((response.getCode() == 1)? response.getBody() : null);
	}

	public boolean createComment(String commentContent, int threadID, String classroomName, String userName) {

		if(DEBUG)
			return true;


		ArrayList<String> list = new ArrayList<String>();
		list.add(0, Integer.toString(threadID));
		list.add(1, commentContent);

		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_CreateComment);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;		
	}

	/**
	 * 
	 * @param classroomName
	 * @param userName
	 * @return Map of Names -> Permissions 
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getMemberMapForClassroom(String classroomName, String userName) {

		if(DEBUG){
			Map<String, Integer> defaultMap = new HashMap<String, Integer>();
			defaultMap.put("Abdel", new Integer(1));
			defaultMap.put("Yilok", new Integer(2));
			defaultMap.put("Chris", new Integer(3));
			defaultMap.put("Nikolai", new Integer(2));
			return defaultMap;	
		}
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_GetClassEnrollment);
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);

		return (Map<String, Integer>) ((response.getCode() == 1)? response.getBody() : null);
	}

	/**
	 *  
	 * 
	 * @param memberName
	 * @param classroomName
	 * @param userName
	 */
	public boolean removeMember(String memberName, String classroomName, String userName) {

		if(DEBUG)
			return true;
		

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, memberName);
		list.add(1, "-1");
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_SetPermissions);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;	
	}

	/**
	 *  
	 * @param currentMemberName
	 * @param userName
	 * @param classroomName
	 */													
	public boolean changeStatus(String currentMemberName, int currentMemberPerm, String userName, String classroomName) {

		if(DEBUG)
			return true;


		ArrayList<String> list = new ArrayList<String>();
		list.add(0, currentMemberName);
		list.add(1, Integer.toString(currentMemberPerm));

		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_SetPermissions);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;
	}

	/**
	 * 
	 * @param currentClassroomName
	 * @param userName
	 * @return List of all user names that have made requests to join a classroom
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRequestListForClassroom(String currentClassroomName, String userName) {

		if(DEBUG){
			List<String> defaultList = Arrays.asList("Bob", "Julia", "Cornelia");
			return defaultList;	
		}
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(currentClassroomName);
		message.setType(Message.MessageType.Client_ListClassroomRequests);
		
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());	
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		
		return (List<String>) ((response.getCode() == 1)? response.getBody() : null);
	}

	/**
	 * 
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public boolean confirmAsMemberOfClassroom(String pendingMember, String classroomName, String userName) {
		
		if(DEBUG)
			return true;


		ArrayList<String> list = new ArrayList<String>();
		list.add(0, pendingMember);
		list.add(1, "1");
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_SetPermissions);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;
	}

	/**
	 * 
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public boolean denyMembershipToClassroom(String pendingMember, String classroomName, String userName) {
		
		if(DEBUG)
			return true;


		ArrayList<String> list = new ArrayList<String>();
		list.add(0, pendingMember);
		list.add(1, "-1");
		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_SetPermissions);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);

		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;
	}
	
	/**
	 * Deletes a comment.
	 * @param commentID
	 * @param currentThreadID
	 * @param currentUserName
	 * @param currentClassroomName
	 * @return
	 */
	public boolean deleteComment(Integer commentID, Integer threadID, String userName, String classroomName) {

		if(DEBUG)
			return true;

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0, threadID);
		list.add(1, commentID);

		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_DeleteComment);
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;
	}
	
	/**
	 * Deletes a thread.
	 * @param currentThreadID
	 * @param currentUserName
	 * @param currentClassroomName
	 * @return
	 */
	public boolean deleteThread(Integer threadID, String userName, String classroomName) {
	
		if(DEBUG)
			return true;

		
		Message message = new Message();
		message.setUserName(userName);
		message.setClassroom_ID(classroomName);
		message.setType(Message.MessageType.Client_DeleteThread);
		message.setBody(threadID);
		message.setChecksum(message.generateCheckSum());
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum miss match!");
			return false;
		}
		return (response.getCode() == 1)? true : false;
	}
	
	/**
	 * This a method for changing a user's password.
	 * Before changing a password, it must:
	 *   verify that userNameTemp matches currentUserName;
	 *   verify that oldPassword matches currentUserName's password;
	 *   check that newPassword1 matches newPassword2.
	 * This method deals with passwords and care must be taken to leave
	 * no trace of any passwords, new or old.
	 * @param oldPassword <- this is currentUserName's initial password
	 * @param newPassword <- this is the desired password for currentUserName
	 * @param confirmNewPassword <- again, this is the desired password for currentUserName (we ask for it again to minimize the effects of user typos)
	 * @param userNameTemp <- this is the supplied name of the current user; it must match currentUserName for a password change
	 * @param currentUserName <- this is the name of the current user
	 * @return
	 */
	
	public boolean changePassword(char[] oldPassword, char[] newPassword,
			char[] confirmNewPassword, String userNameTemp, String currentUserName) {
		// TODO Auto-generated method stub
		if(DEBUG)
			return true;
		
		Message message = new Message();
		message.setUserName(currentUserName);
		message.setType(Message.MessageType.Client_ChangePassword);
		
		ArrayList<char []> list = new ArrayList<char []>();
		list.add(0, oldPassword);
		list.add(1, newPassword);
		list.add(2, confirmNewPassword);
		list.add(3, userNameTemp.toCharArray());
		
		
		message.setBody(list);
		message.setChecksum(message.generateCheckSum());
		
		socket.sendEncrypted(aes.encrypt(message));
		
		//Clearing password arrays
		Arrays.fill(oldPassword, '0');
		Arrays.fill(newPassword, '0');
		Arrays.fill(confirmNewPassword, '0');

		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum mismatch!");
			return false;
		}
		return (response.getCode() == 1)? true : false;
	}

	/**
	 * Gets the last login info for this particular user.
	 * @return last login info as a string
	 */
	public String getLastLogin(String userName) {
		
		Message message = new Message();
		message.setType(Message.MessageType.Client_GetLastLogin);
		message.setUserName(userName);
		message.setBody(0);
		message.setChecksum(message.generateCheckSum());	
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		Message response = (Message) aes.decryptObject(encMessage);
		if(response.getChecksum() != response.generateCheckSum()){
			if(DEBUG_OUTPUT) System.out.println("Checksum mismatch!");
			return null;
		}
		String lastLogin = (String) response.getBody();
		
		return lastLogin;
	}
}
