import java.util.ArrayList;
import java.util.List;


public class Client {
	

	/**
	 *  --------> MIGHT BE OVERLAP WITH handleLogin() <---------
	 * This method is used in the login page to verify a user name.
	 * It takes in a user name and returns true if the user name can be verified,
	 * and false otherwise.
	 * @param userName
	 * @return boolean corresponding to verification status
	 */
	public boolean verify(String userName) {
		// TODO
		return true;		
	}
	
	/**
	 * This method is used to create a classroom.
	 * It takes in a classroom name and returns true if the classroom can be created,
	 * and false otherwise.
	 * @param classroomName
	 * @param classroomCreatorName
	 * @return boolean corresponding to classroom creation status
	 */
	public boolean createClassroom(String classroomName, String classroomCreatorName) {
		// TODO
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
	public boolean requestClassroom(String classroomRequestName, String requesterUserName) {
		// TODO
		return true;		
	}
	
	/**
	 * This method is used to get a list of classrooms for a particular member.
	 * It takes in a user name and returns the list of classrooms of which the user is a member.
	 * @param userName
	 * @return boolean corresponding to whether the classroom can be requested
	 */
	public List<String> getClassroomListForUser(String userName) {
		List<String> classroomList = new ArrayList<String>();
		// TODO: this is a temp list
		classroomList.add("CS 4820");
		classroomList.add("LING 4844");
		classroomList.add("HD 3260");
		// end of temp list
		
		return classroomList;		
	}
	
	
	public static void handleLogin(String uName){

		ClientSocketHandler handler = new ClientSocketHandler();
		Message responce = handler.sendReceive(uName, Message.MessageType.Client_LogIn);
		
		//TODO finish codes
		switch(responce.getCode()){
		case 1:
			//do something
			break;
		case 2:
			//do something
			break;
		default:
			//message compromised or bad message code
		}
	}
	
	public static void handleRegister(String uName){

		ClientSocketHandler handler = new ClientSocketHandler();
		Message responce = handler.sendReceive(uName, Message.MessageType.Client_Register);
		
		//TODO finish codes
//		switch(responce.getCode()){
//		case 1:
//			//do something
//			break;
//		case 2:
//			//do something
//			break;
//		default:
//			//message compromised or bad message code
//		}
	}
public static void main(String [] args) {
		handleRegister("bob");
	}

}
