import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Client {

	private static final boolean DEBUG = false;
	private static Cookie cookie = new Cookie(null);

	/**
	 *  
	 * This method is used in the login page to verify a user name.
	 * It takes in a user name and returns true if the user name can be verified,
	 * and false otherwise.
	 * @param userName
	 * @return boolean corresponding to verification status
	 */
	public boolean handleLogin(String uName){

		if(DEBUG)
			return true; 

		ClientSocketHandler handler = new ClientSocketHandler();
		cookie.setKey(uName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setType(Message.MessageType.Client_LogIn);

		Message response = handler.sendReceive();

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

		ClientSocketHandler handler = new ClientSocketHandler();
		cookie.setKey(uName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_CreateClassroom);

		Message response = handler.sendReceive();

		//TODO Set cookie with uName, Set message id with classroom name, message type
		//check message code on return
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

		ClientSocketHandler handler = new ClientSocketHandler();
		cookie.setKey(requesterUserName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomRequestName);
		handler.getMessageSending().setType(Message.MessageType.Client_RequestEnrollment);

		Message response = handler.sendReceive();

		//TODO Set cookie with uName, Set message id with classroom name, message type
		//check message code on return
		return (response.getCode() == 1)? true : false;			
	}

	/**
	 * This method is used to get a list of classrooms for a particular member.
	 * It takes in a user name and returns the list of classrooms of which the user is a member.
	 * @param userName
	 * @return Map of classroomName -> permissions 
	 */
	public Map<String, Integer> getClassroomMapForUser(String userName) {
		//message type Client_GetUserEnrollment
		//add debug line
		//cookie with uName, 

		Map<String, Integer> classroomList = new HashMap<String, Integer>();

		// TODO: this is a temp list
		classroomList.put("CS 4820", 1);
		classroomList.put("LING 4844", 2);
		classroomList.put("HD 3260", 3);
		// end of temp list

		return classroomList;		
	}




	public static void main(String [] args) {
		/* Uncomment to preform a register / login test
		if (handleLogin("bob") == false)
			System.out.println("login with bob failed");
		if (handleRegister("bob") == true)
			System.out.println("registering bob successful!");
		if (handleLogin("bob") == true)
			System.out.println("bob was logged in!!");
		 */
	}


	// New Stuff
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

		ClientSocketHandler handler = new ClientSocketHandler();
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, threadName);
		list.add(1, postContent);

		cookie.setKey(currentUserName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_CreateThread);
		handler.getMessageSending().setBody(list);

		Message responce = handler.sendReceive();

		return (responce.getCode() == 1)? true : false;	
	}

	/**
	 * **** Consider bool return type to confirm class has been deleted?
	 */
	public void deleteClassroom(String classroomName, String userName) {

		ClientSocketHandler handler = new ClientSocketHandler();
		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_DeleteClassroom);

		Message response = handler.sendReceive();

		//return (responce.getCode() == 1)? true : false;	
		// TODO Set cookie uname and message type client_del classroom
		// TODO add debug log
	}

	/**
	 * Create message type to handle this request appropriately 
	 * **** Consider bool return type to confirm class has been deleted? ***
	 */
	public void disjoinClassroom(String classroomName, String userName) {

		ClientSocketHandler handler = new ClientSocketHandler();
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, userName);
		list.add(1, "-1");

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setBody(list);
		handler.getMessageSending().setType(Message.MessageType.Client_SetPermissions);

		Message response = handler.sendReceive();

		//return (responce.getCode() == 1)? true : false;		
	}

	/**
	 * 
	 * @param classroomName
	 * @param userName
	 * @return Map of Thread ID -> Thread Name
	 */
	public Map<Integer, String> getThreadMapForClassroom(String classroomName, String userName) {

		if(DEBUG)
			return null;

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_GoToClassroom);

		Message responce = handler.sendReceive();

		return (Map<Integer, String>) ((responce.getCode() == 1)? responce.getBody() : null);	
	}

	/**
	 * This function returns the contents of a single thread given its ID.
	 * @param threadID
	 * @param classroomName
	 * @param userName
	 * @return Map of ThreadTopic/Post/Comment IDs -> Content
	 */
	public Map<Integer, String> getThreadGivenID(Integer threadID, String classroomName, String userName) {

		return null;
	}

	public boolean createComment(String commentContent, int threadID, String classroomName, String userName) {

		if(DEBUG)
			return true;

		ClientSocketHandler handler = new ClientSocketHandler();

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, Integer.toString(threadID));
		list.add(1, commentContent);

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setBody(list);
		handler.getMessageSending().setType(Message.MessageType.Client_CreateComment);

		Message response = handler.sendReceive();

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

		if(DEBUG)
			return null;

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_GetClassEnrollment);

		Message response = handler.sendReceive();

		return (Map<String, Integer>) ((response.getCode() == 1)? response.getBody() : null);
	}

	/**
	 * *** Consider boolean return type 
	 * 
	 * @param memberName
	 * @param classroomName
	 * @param userName
	 */
	public void removeMember(String memberName, String classroomName, String userName) {

		//		if(DEBUG)
		//			return true;
		//		
		ClientSocketHandler handler = new ClientSocketHandler();

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, memberName);
		list.add(1, "-1");

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_SetPermissions);
		handler.getMessageSending().setBody(list);

		Message response = handler.sendReceive();

		//return (response.getCode() == 1)? true : false;	
	}

	/**
	 *  *** Consider boolean return type
	 * @param currentMemberName
	 * @param userName
	 * @param classroomName
	 */													
	public void changeStatus(String currentMemberName, String userName, String classroomName) {

		//		if(DEBUG)
		//			return true;

		ClientSocketHandler handler = new ClientSocketHandler();

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, currentMemberName);
		list.add(1, "1");

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_SetPermissions);
		handler.getMessageSending().setBody(list);

		Message response = handler.sendReceive();

		//return (response.getCode() == 1)? true : false;
	}

	/**
	 * 
	 * @param currentClassroomName
	 * @param userName
	 * @return List of all user names that have made requests to join a classroom
	 */
	@SuppressWarnings("unchecked")
	public List<String> getRequestListForClassroom(String currentClassroomName, String userName) {

		if(DEBUG)
			return null;

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(currentClassroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_ListClassroomRequests);

		Message responce = handler.sendReceive();

		return (List<String>) ((responce.getCode() == 1)? responce.getBody() : null);
	}

	/**
	 * Consider boolean return type
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public void confirmAsMemberOfClassroom(String pendingMember, String classroomName, String userName) {
		//if(DEBUG)
		//	return true;

		ClientSocketHandler handler = new ClientSocketHandler();

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, pendingMember);
		list.add(1, "1");

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_SetPermissions);
		handler.getMessageSending().setBody(list);

		Message response = handler.sendReceive();

		//return (response.getCode() == 1)? true : false;
	}

	/**
	 * Consider boolean return type
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public void denyMembershipToClassroom(String pendingMember, String classroomName, String userName) {
		//if(DEBUG)
		//	return true;

		ClientSocketHandler handler = new ClientSocketHandler();

		ArrayList<String> list = new ArrayList<String>();
		list.add(0, pendingMember);
		list.add(1, "-1");

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_SetPermissions);
		handler.getMessageSending().setBody(list);

		Message response = handler.sendReceive();

		//return (response.getCode() == 1)? true : false;
	}

}
