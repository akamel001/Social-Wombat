import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
	
	private static final boolean DEBUG = true;

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
		Message responce = handler.sendReceive(uName, Message.MessageType.Client_LogIn);
		
		return (responce.getCode() == 1)? true : false;	
	}
	//TODO ******* WORK ON JUNIT TESTS ***** 
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
		
		//TODO Set cookie with uName, Set message id with classroom name, message type
		//check message code on return
		return true;		
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
		//TODO Set cookie with uName, Set message id with classroom name, message type Client_RequestEnrollment, message body 0
		//check message code on return 
		//add debug line
		return true;		
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
		// TODO Message type createThread, cookie id username, ArrayList<string> with index 0 as threadname and 1 as postContent, message class id is class name 
		// TODO add debug logic
		return true;
	}

	/**
	 * 
	 */
	public void deleteClassroom(String classroomName, String userName) {
		// TODO Auto-generated method stub
		// TODO Set cookie uname and message type client_del classroom
		// TODO add debug log
	}

	/**
	 * 
	 */
	public void disjoinClassroom(String classroomName, String userName) {
		//TODO Set cookie with uName, Set message id with classroom name, message type Client_RequestEnrollment, message body -1
		//check message code on return 
		//add debug line
	}

	/**
	 * 
	 * @param classroomName
	 * @param userName
	 * @return Map of Thread ID -> Thread Name
	 */
	public Map<Integer, String> getThreadMapForClassroom(String classroomName, String userName) {
		// Message type is Client goto classroom, cookie uname, message classid is classroom name
		// Debug logic 
		
		return null;
	}

	public boolean createComment(String commentContent, int threadID, String classroomName, String userName) {
		// TODO cast threadID to string
		// TODO Message type CreatComment, cookie id username, ArrayList<string> with index 0 as threadID and 1 as commentContent, message class id is class name 
		// TODO add debug logic
		return true;
	}
	
	/**
	 * 
	 * @param classroomName
	 * @param userName
	 * @return Map of Names -> Permissions 
	 */
	public Map<String, Integer> getMemberMapForClassroom(String classroomName, String userName) {
		// TODO message type Client_GetClassEnrollment, cookie id username, classroomName in message class ID 
		// Add debug logic
		return null;
	}

	/**
	 * 
	 * @param memberName
	 * @param classroomName
	 * @param userName
	 */
	public void removeMember(String memberName, String classroomName, String userName) {
		// TODO cookie containing user id, classroom id in message class id, Array<String> 0th index is membername, and first index is permission to remove (-1) and array is in message body
		// todo message type cleitn set perm
		// TODO add debug logic
	}

	/**
	 * 
	 * @param currentMemberName
	 * @param userName
	 * @param classroomName
	 */
	public void changeStatus(String currentMemberName, String userName, String classroomName) {
		// TODO cookie containing user id, classroom id in message class id, Array<String> 0th index is membername, and first index is permission to remove (1) and array is in message body
		// message type client set perm
		// TODO add debug logic
	}

	/**
	 * 
	 * @param currentClassroomName
	 * @param userName
	 * @return List of all user names that have made requests to join a classroom
	 */
	public List<String> getRequestListForClassroom(String currentClassroomName, String userName) {
		// TODO Message type Client_ListClassroomRequest, userName stored in cookie, message class id contains classroom name
		// TODO Debug logic
		return null;
	}
	
	/**
	 * 
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public void confirmAsMemberOfClassroom(String pendingMember, String classroomName, String userName) {
		// TODO cookie containing user id, classroom id in message class id, Array<String> 0th index is membername, and first index is permission to remove (1) and array is in message body
		// message type client set perm
		// TODO add debug logic
	}
	
	/**
	 * 
	 * @param pendingMember
	 * @param classroomName
	 * @param userName
	 */
	public void denyMembershipToClassroom(String pendingMember, String classroomName, String userName) {
		// TODO cookie containing user id, classroom id in message class id, Array<String> 0th index is membername, and first index is permission to remove (-1) and array is in message body
		// message type client set perm
		// TODO add debug logic
		
	}

}
