import java.util.ArrayList;
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
		//TODO Set cookie with uName, Set message id with classroom name, message type
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
		return true;
	}

	/**
	 * 
	 */
	public void deleteClassroom(String classroomName) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 */
	public void disjoinClassroom(String classroomName, String userName) {
		// TODO Auto-generated method stub
		
	}

	public List<String> getThreadListForClassroom(String classroomName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean createComment(String commentContent,
			String threadName, String userName) {
		// TODO Auto-generated method stub
		return true;
	}

	public List<String> getMemberListForClassroom(String classroomName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeMember(String memberName,
			String classroomName) {
		// TODO Auto-generated method stub
		
	}

	public void changeStatus(String currentMemberName) {
		// TODO Auto-generated method stub
		
	}

	public List<String> getRequestListForClassroom(String currentClassroomName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void confirmAsMemberOfClassroom(String memberName,
			String classroomName) {
		// TODO Auto-generated method stub
		
	}

	public void denyMembershipToClassroom(String memberName,
			String classroomName) {
		// TODO Auto-generated method stub
		
	}

}
