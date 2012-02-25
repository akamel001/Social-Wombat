import java.util.ArrayList;
import java.util.List;

/**
 * This is the interface I would like to use in my UserInterface.java class.
 * Obviously, I can change stuff around, but this is a general guideline of what's needed.
 */

/**
 * @author Julia
 *
 */
public class Client1 {
	
	public Client1() {
		
	}
	
	/**
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
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
