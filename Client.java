import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author 
 *
 */
public class Client {

	private static final boolean DEBUG = false;
	private Cookie cookie = new Cookie("");

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
		handler.getMessageSending().setBody(0);
		Message response = handler.sendReceive();

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

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setType(Message.MessageType.Client_GetUserEnrollment);

		Message responce = handler.sendReceive();

		return (Map<String, Integer>) ((responce.getCode() == 1)? responce.getBody() : null);	
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
	 *
	 */
	public boolean deleteClassroom(String classroomName, String userName) {
		
		if(DEBUG)
			return true;

		ClientSocketHandler handler = new ClientSocketHandler();
		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_DeleteClassroom);

		Message response = handler.sendReceive();

		return (response.getCode() == 1)? true : false;	
	}

	/**
	 * Create message type to handle this request appropriately 
	 * **** Consider bool return type to confirm class has been deleted? ***
	 */
	public boolean disjoinClassroom(String classroomName, String userName) {
		
		if(DEBUG)
			return true;

		ClientSocketHandler handler = new ClientSocketHandler();
		ArrayList<String> list = new ArrayList<String>();
		list.add(0, userName);
		list.add(1, "-1");

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setBody(list);
		handler.getMessageSending().setType(Message.MessageType.Client_SetPermissions);
		handler.getMessageSending().setBody(-1);
		
		Message response = handler.sendReceive();

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
	@SuppressWarnings("unchecked")
	public Map<Integer, String> getThreadGivenID(Integer threadID, String classroomName, String userName) {
		
		if(DEBUG){
			Map<Integer, String> defaultMap = new HashMap<Integer, String>();
			defaultMap.put(new Integer(4), "Initial Post");
			defaultMap.put(new Integer(2), "Topic");
			defaultMap.put(new Integer(7), "I like to comment on stuff!");
			return defaultMap;	
		}

		ClientSocketHandler handler = new ClientSocketHandler();
		
		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		
		if(threadID == Integer.valueOf(null)){
			System.out.println("ThreadID is null!");
			System.exit(-1);
		}	
		
		handler.getMessageSending().setBody(threadID);
		handler.getMessageSending().setType(Message.MessageType.Client_GoToThread);

		Message response = handler.sendReceive();

		return (Map<Integer, String>) ((response.getCode() == 1)? response.getBody() : null);
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

		if(DEBUG){
			Map<String, Integer> defaultMap = new HashMap<String, Integer>();
			defaultMap.put("Abdel", new Integer(1));
			defaultMap.put("Yilok", new Integer(2));
			defaultMap.put("Chris", new Integer(3));
			defaultMap.put("Nikolai", new Integer(2));
			return defaultMap;	
		}

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_GetClassEnrollment);

		Message response = handler.sendReceive();

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

		return (response.getCode() == 1)? true : false;	
	}

	/**
	 *  
	 * @param currentMemberName
	 * @param userName
	 * @param classroomName
	 */													
	public boolean changeStatus(String currentMemberName, String userName, String classroomName) {

		if(DEBUG)
			return true;

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

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);
		handler.getMessageSending().setClassroom_ID(currentClassroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_ListClassroomRequests);

		Message responce = handler.sendReceive();

		return (List<String>) ((responce.getCode() == 1)? responce.getBody() : null);
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

		ClientSocketHandler handler = new ClientSocketHandler();

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0, threadID);
		list.add(1, commentID);

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_DeleteComment);
		handler.getMessageSending().setBody(list);

		Message response = handler.sendReceive();

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

		ClientSocketHandler handler = new ClientSocketHandler();

		cookie.setKey(userName);
		handler.getMessageSending().setCookie(cookie);

		handler.getMessageSending().setClassroom_ID(classroomName);
		handler.getMessageSending().setType(Message.MessageType.Client_DeleteThread);
		handler.getMessageSending().setBody(threadID);

		Message response = handler.sendReceive();

		return (response.getCode() == 1)? true : false;
	}

}
