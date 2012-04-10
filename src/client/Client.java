package client;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import security.AES;
import storage.Message;
import util.SocketPackage;


public final class Client {
	
	private static final boolean DEBUG = false;
	private static final boolean DEBUG_OUTPUT = false;

	private static final String hub_addr = "127.0.0.1";
	private static final int HUB_PORT = 4444;
	private SocketPackage socket;
	private AES aes = null;
	private Long nonce = null;
	
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
	/**
	 * A function that preforms the authentication protocal described in our documentation 
	 * @param uName
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean handleLogin(String uName, char[] password){
		
		if(DEBUG) return true; 
		
		if(password.length == 0)
			return false;
		
		aes = new AES(password);
		Calendar c = Calendar.getInstance();
		SecureRandom r = new SecureRandom();
		
		nonce= r.nextLong();
		
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
		
		if(DEBUG_OUTPUT) System.out.println("Sending time in sec: " + list.get(0));

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
		
		if( now <= hub_time+300000 && hub_time-300000 <= now && hub_nonce==nonce+1)
			allowed=true;
		
		if(allowed){
			if(DEBUG_OUTPUT) System.out.println("Authenticated!!");
			nonce = hub_nonce;
			return true;
		}
		else{
			if(DEBUG_OUTPUT) System.out.println("Failed to Authenticate!!");
			return false;
		}
			
	}

	/**
	 * A function to send secure and encrypted messages to the hub
	 * @param userName
	 * @param classroomName
	 * @param body
	 * @param mType
	 * @return
	 */
	public Message SendAndReceiveEncrypted(String userName, 
										String classroomName, 
										Object body,
										Message.MessageType mType){

		Message message = new Message();
		message.setUserName(userName);
		message.setType(mType);
		message.setClassroom_ID(classroomName);
		
		if(body instanceof Integer)
			message.setBody((Integer) body);
		else
			message.setBody(body);
		
		message.setChecksum(message.generateCheckSum());
		
		//TODO add a message param for naunce and check naunce+1 on response message 
		
		socket.sendEncrypted(aes.encrypt(message));
		
		byte[] encMessage = socket.receiveEncrypted();
		
		Message response = (Message) aes.decryptObject(encMessage);
		
		//TODO Inform Julia of response that the hub is not responding and needs to be handled
		if(response.getType() == Message.MessageType.Hub_Shutdown){
			System.out.println("The hub is shut down, try again later.");
			System.exit(-1);
		}
		
		//TODO handle bad checksum!!
		if(response.getChecksum() != response.generateCheckSum())
			System.out.println("Checksum miss match!");
		
		return response;
	}
	
	/**
	 * This methods logs out a user corresponding to currentUserName.
	 * @param currentUserName
	 * @return
	 */
	public boolean handleLogout(String currentUserName) {
		Message reply = SendAndReceiveEncrypted(currentUserName, null, 0, Message.MessageType.Client_Logout);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	
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
		Message reply = SendAndReceiveEncrypted(uName, classroomName, 0, Message.MessageType.Client_CreateClassroom);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
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
		Message reply = SendAndReceiveEncrypted(requesterUserName, classroomRequestName, 0, Message.MessageType.Client_RequestEnrollment);	
		
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
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
		Message reply = SendAndReceiveEncrypted(userName, null, 0, Message.MessageType.Client_GetUserEnrollment);
		return (Map<String, Integer>) ((reply.getCode() == 1)? reply.getBody() : null);
	}

	/**
	 * This method creates a new thread with threadName and postContent.
	 * @param threadName
	 * @param postContent
	 * @param currentUserName
	 * @return
	 */
	public boolean createThread(String threadName, String postContent, String currentUserName, String classroomName) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, threadName);
		list.add(1, postContent);
		Message reply = SendAndReceiveEncrypted(currentUserName, classroomName, list, Message.MessageType.Client_CreateThread);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Deletes a classroom from server
	 * @param classroomName
	 * @param userName
	 * @return
	 */
	public boolean deleteClassroom(String classroomName, String userName) {
		Message reply = SendAndReceiveEncrypted(userName, classroomName, 0, Message.MessageType.Client_DeleteClassroom);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Create message type to handle this request appropriately 
	 * @param classroomName
	 * @param userName
	 * @return
	 */
	public boolean disjoinClassroom(String classroomName, String userName) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, userName);
		list.add(1, "-1");
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_SetPermissions);	
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Requests from server a thread map mapping from thread id to thread name
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
		Message reply = SendAndReceiveEncrypted(userName, classroomName, 0, Message.MessageType.Client_GoToClassroom);	
		return (Map<Integer, String>) ((reply.getCode() == 1)? reply.getBody() : null);	
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
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, threadID, Message.MessageType.Client_GoToThread);	
		return (Map<Integer, String>) ((reply.getCode() == 1)? reply.getBody() : null);	
	}

	public boolean createComment(String commentContent, int threadID, String classroomName, String userName) {
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, Integer.toString(threadID));
		list.add(1, commentContent);
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_CreateComment);		
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Gets a membership map that maps the member name to their permission
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
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, 0, Message.MessageType.Client_GetClassEnrollment);		
		return (Map<String, Integer>) ((reply.getCode() == 1)? reply.getBody() : null);	
	}

	/**
	 * Removes a member from a classroom
	 * @param memberName
	 * @param classroomName
	 * @param userName
	 */
	public boolean removeMember(String memberName, String classroomName, String userName) {
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, memberName);
		list.add(1, "-1");
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_SetPermissions);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Toggles the status of a member
	 * @param currentMemberName
	 * @param userName
	 * @param classroomName
	 */													
	public boolean changeStatus(String currentMemberName, int currentMemberPerm, String userName, String classroomName) {
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, currentMemberName);
		list.add(1, Integer.toString(currentMemberPerm));

		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_SetPermissions);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Requests a list of all user names that have made a requests to join a classroom
	 * @param currentClassroomName
	 * @param userName
	 * @return List of all user names that have made requests to join a classroom
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRequestListForClassroom(String currentClassroomName, String userName) {
		if(DEBUG) return Arrays.asList("Bob", "Julia", "Cornelia");
		
		Message reply = SendAndReceiveEncrypted(userName, currentClassroomName, 0, Message.MessageType.Client_ListClassroomRequests);
		return (List<String>) ((reply.getCode() == 1)? reply.getBody() : null);
	}

	/**
	 * Confirms and approve a member of a classroom
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public boolean confirmAsMemberOfClassroom(String pendingMember, String classroomName, String userName) {
		
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, pendingMember);
		list.add(1, "1");
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_SetPermissions);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Revokes membership to a classroom 
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public boolean denyMembershipToClassroom(String pendingMember, String classroomName, String userName) {

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, pendingMember);
		list.add(1, "-1");
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_SetPermissions);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
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
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0, threadID);
		list.add(1, commentID);
		
		Message reply = SendAndReceiveEncrypted(userName, classroomName, list, Message.MessageType.Client_DeleteComment);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}
	
	/**
	 * Deletes a thread.
	 * @param currentThreadID
	 * @param currentUserName
	 * @param currentClassroomName
	 * @return
	 */
	public boolean deleteThread(Integer threadID, String userName, String classroomName) {
		Message reply = SendAndReceiveEncrypted(userName, classroomName, threadID, Message.MessageType.Client_DeleteThread);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
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
		
		ArrayList<char []> list = new ArrayList<char []>();
		list.add(0, oldPassword);
		list.add(1, newPassword);
		list.add(2, confirmNewPassword);
		list.add(3, userNameTemp.toCharArray());
		
		//Clearing password arrays
		Arrays.fill(oldPassword, '0');
		Arrays.fill(newPassword, '0');
		Arrays.fill(confirmNewPassword, '0');
		
		Message reply = SendAndReceiveEncrypted(currentUserName, null, list, Message.MessageType.Client_ChangePassword);
		return (reply.getCode() == 1 || (DEBUG))? true : false;	 
	}

	/**
	 * Gets the last login info for this particular user.
	 * @param userName User name
	 * @return last login info as a string
	 */
	public String getLastLogin(String userName) {
		Message reply = SendAndReceiveEncrypted(userName, null, 0, Message.MessageType.Client_GetLastLogin);
		return (String) reply.getBody();
	}
}
