import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * This is the user interface for Social Wombat.
 * 
 * This class is for anything dealing with what the user sees and the
 * work flow of the social network. However, it does not deal with the
 * behind-the-scenes logic for any of the actions, which is done in 
 * the client class.
 * 
 */
public final class UserInterface {
	
	static Console console;
	static Client client;
	private static String currentUserName;
	private static String currentClassroomName;
	private static String currentPermissions;
	private static String currentThreadName;
	private static Integer currentThreadID;
	private static String currentMemberName;
	private static String currentMemberPermissions;
	private static String currentPendingMemberName;
	
	// Below is a bunch of strings used in the text-based user interface.
	private static final String sLOG_IN = 				UserInterfaceHelper.addFormattingAlignCenter("LOG IN");
	private static final String sHOME_PAGE = 			UserInterfaceHelper.addFormattingAlignCenter("HOME"); 
	private static final String sCLASSROOM_LIST_PAGE = 	UserInterfaceHelper.addFormattingAlignCenter("CLASSROOM LIST"); 
	private static final String sCLASSROOM_PAGE = 		UserInterfaceHelper.addFormattingAlignCenter("CLASSROOM");
	private static final String sTHREAD_LIST_PAGE = 	UserInterfaceHelper.addFormattingAlignCenter("THREAD LIST");
	private static final String sTHREAD_PAGE = 			UserInterfaceHelper.addFormattingAlignCenter("THREAD");
	private static final String sMEMBER_LIST_PAGE = 	UserInterfaceHelper.addFormattingAlignCenter("MEMBER LIST");
	private static final String sMEMBER_PAGE = 			UserInterfaceHelper.addFormattingAlignCenter("MEMBER");
	private static final String sREQUEST_LIST_PAGE = 	UserInterfaceHelper.addFormattingAlignCenter("REQUEST LIST");
	private static final String sREQUEST_PAGE = 		UserInterfaceHelper.addFormattingAlignCenter("REQUEST");
	private static final String sCHANGE_PASSWORD_PAGE = UserInterfaceHelper.addFormattingAlignCenter("CHANGE PASSWORD");
	
	private static final String sINSTRUCTOR =			"Instructor";
	private static final String sTEACHING_ASSISTANT = 	"Teaching Assistant";
	private static final String sSTUDENT = 				"Student";
	
	private static final String eNON_VALID_SELECTION = 		"That is not a valid selection." + UserInterfaceHelper.sNEW_LINE;
	
	private static final String eGENERAL_ERROR = 			UserInterfaceHelper.addFormattingAlignLeft("An error has occurred.");
	private static final String eLOG_IN_ERROR = 			UserInterfaceHelper.addFormattingAlignLeft("There was an error in logging in with the provided username.");		
	private static final String eCLASSROOM_CREATION_ERROR =	UserInterfaceHelper.addFormattingAlignLeft("An error occured when creating the classroom.");
	private static final String eCLASSROOM_REQUEST_ERROR =	UserInterfaceHelper.addFormattingAlignLeft("An error occured when requesting to join a classroom.");	
	private static final String eTHREAD_CREATION_ERROR = 	UserInterfaceHelper.addFormattingAlignLeft("An error occured when creating your thread.");	
	private static final String eCOMMENT_DELETION_ERROR =	UserInterfaceHelper.addFormattingAlignLeft("An error occured when deleting the comment.");	
	private static final String eTHREAD_DELETION_ERROR = 	UserInterfaceHelper.addFormattingAlignLeft("An error occured when deleting the thread.");
	private static final String eDENY_MEMBERSHIP_ERROR = 	UserInterfaceHelper.addFormattingAlignLeft("An error occured when denying this membership request.");
	private static final String eCONFIRM_AS_MEMBER_ERROR = 	UserInterfaceHelper.addFormattingAlignLeft("An error occured when confirming this membership request.");
	private static final String eCHANGE_STATUS_ERROR = 		UserInterfaceHelper.addFormattingAlignLeft("An error occured when changing this member's status.");
	private static final String eREMOVE_MEMBER_ERROR = 		UserInterfaceHelper.addFormattingAlignLeft("An error occured when removing this member from the classroom.");
	private static final String eDELETE_CLASSROOM_ERROR = 	UserInterfaceHelper.addFormattingAlignLeft("An error occured when deleting your classroom.");
	private static final String eDISJOIN_CLASSROOM_ERROR = 	UserInterfaceHelper.addFormattingAlignLeft("An error occured when disjoining this classroom.");
	private static final String ePASSWORD_CHANGE_ERROR =	UserInterfaceHelper.addFormattingAlignLeft("An error occured when changing your password.");
	private static final String eLOG_OUT_ERROR = 			UserInterfaceHelper.addFormattingAlignLeft("An error occured when attempting to log out.");
	
	private static final String mCLASSROOM_CREATION_SUCCESS =	UserInterfaceHelper.addFormattingAlignLeft("You have successfully created a classroom!");
	private static final String mCLASSROOM_REQUEST_SUCCESS =	UserInterfaceHelper.addFormattingAlignLeft("You have successfully requested to join a classroom!");
	private static final String mLOG_OUT_SUCCESS = 				UserInterfaceHelper.addFormattingAlignLeft("You have successfully logged out.");
	private static final String mTHREAD_CREATION_SUCCESS = 		UserInterfaceHelper.addFormattingAlignLeft("You have successfully posted your new thread to the discussion board.");	
	private static final String mDISJOIN_CLASSROOM_SUCCESS = 	UserInterfaceHelper.addFormattingAlignLeft("You have successfully disjoined the classroom.");
	private static final String mDELETE_CLASSROOM_SUCCESS =  	UserInterfaceHelper.addFormattingAlignLeft("You have successfully deleted your classroom.");
	private static final String mREMOVE_MEMBER_SUCCESS = 		UserInterfaceHelper.addFormattingAlignLeft("You have successfully removed a member from this classroom.");
	private static final String mCHANGE_STATUS_SUCCESS =		UserInterfaceHelper.addFormattingAlignLeft("You have successfully changed the status of a member.");
	private static final String mCONFIRM_AS_MEMBER_SUCCESS = 	UserInterfaceHelper.addFormattingAlignLeft("You have successfully added a member to this classroom.");
	private static final String mDENY_MEMBERSHIP_SUCCESS =   	UserInterfaceHelper.addFormattingAlignLeft("You have successfully denied a user member to this classroom.");
	private static final String mCOMMENT_DELETION_SUCCESS = 	UserInterfaceHelper.addFormattingAlignLeft("You have successfully deleted the comment.");		
	private static final String mTHREAD_DELETION_SUCCESS = 		UserInterfaceHelper.addFormattingAlignLeft("You have successfully deleted the thread.");
	private static final String mPASSWORD_CHANGE_SUCCESS = 		UserInterfaceHelper.addFormattingAlignLeft("You have successfully changed your password.");
	
	private static final String cNO_REQUESTS = 					UserInterfaceHelper.addFormattingAlignLeft("This classroom has no requests to enroll.");
	private static final String cNO_MEMBERS = 					UserInterfaceHelper.addFormattingAlignLeft("This classroom has no enrolled members.");
	private static final String cNO_THREADS = 					UserInterfaceHelper.addFormattingAlignLeft("This discussions board has no threads.");
	private static final String cNO_CLASSROOMS = 				UserInterfaceHelper.addFormattingAlignLeft("You are not enrolled in any classrooms.");
	
	private static final String sCHANGE_PASSWORD_INSTRUCTIONS = UserInterfaceHelper.addFormattingAlignLeft("Specify your username and old/new passwords when prompted to by the system.");
	
	private static final String sDEFAULT_OPTIONS = 			UserInterfaceHelper.addFormattingAlignLeft("1. Go back home.");
	
	private static final String sHOME_PAGE_OPTIONS =		UserInterfaceHelper.addFormattingAlignLeft("1. View your classrooms.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Create a classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. Request to join a classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("4. Change your password.") +
															UserInterfaceHelper.addFormattingAlignLeft("5. Log out.");
	
	private static final String sCLASSROOM_PAGE_OPTIONS_INSTRUCTOR = UserInterfaceHelper.addFormattingAlignLeft("1. View discussion board.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Create a thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. View members of this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("4. View requests to join this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("5. Delete this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("6. Go back home.");
	
	private static final String sCLASSROOM_PAGE_OPTIONS_TEACHING_ASSISTANT =	UserInterfaceHelper.addFormattingAlignLeft("1. View discussion board.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Create a thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. View members of this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("4. View requests to join this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("5. Disjoin this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("6. Go back home.");
	
	private static final String sCLASSROOM_PAGE_OPTIONS_STUDENT = UserInterfaceHelper.addFormattingAlignLeft("1. View discussion board.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Create a thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. Disjoin this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("4. Go back home.");

	private static final String sTHREAD_PAGE_OPTIONS_INSTRUCTOR_AND_TEACHING_ASSISTANT = UserInterfaceHelper.addFormattingAlignLeft("1. Comment on this thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Delete a comment.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. Delete this entire thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("4. Go back to this classroom's main page.");
	
	private static final String sTHREAD_PAGE_OPTIONS_INSTRUCTOR_AND_TEACHING_ASSISTANT_NO_COMMENTS = UserInterfaceHelper.addFormattingAlignLeft("1. Comment on this thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Delete this entire thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	private static final String sTHREAD_PAGE_OPTIONS_STUDENT = UserInterfaceHelper.addFormattingAlignLeft("1. Comment on this thread.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Go back to this classroom's main page.");
	
	private static final String sMEMBER_PAGE_OPTIONS_INSTRUCTOR = UserInterfaceHelper.addFormattingAlignLeft("1. Remove this member from this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Change this member's status.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	private static final String sMEMBER_PAGE_OPTIONS_TEACHING_ASSISTANT = UserInterfaceHelper.addFormattingAlignLeft("1. Remove this member from this classroom.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Go back to this classroom's main page.");
	
	private static final String sREQUEST_PAGE_OPTIONS = 	UserInterfaceHelper.addFormattingAlignLeft("1. Confirm as a member.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Deny membership.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	/**
	 * This is the login page. The login page requests a user name. If the user name is valid, 
	 * it goes to the home page. Otherwise, it loops back to the login page and displays an 
	 * error message.
	 * @param messages
	 */
	private static void loginPage(String messages) {
		displayPage(sLOG_IN, messages, null, null, null);	
		String userNameTemp = console.readLine("User Name? ");
		char[] password = console.readPassword("Password? ");
		
		if (client.handleLogin(userNameTemp, password)){
			currentUserName = userNameTemp;
			homePage(UserInterfaceHelper.addFormattingAlignLeft("Welcome, " + currentUserName + "!") + UserInterfaceHelper.addFormattingAlignLeft("Last login info: " + client.getLastLogin(currentUserName)));
		} else {
			loginPage(eLOG_IN_ERROR);
		}
	}
	
	/**
	 * This is the home page. It displays four options:
	 * 1. View your classrooms.
	 * 2. Create a classroom.
	 * 3. Request to join a classroom.
	 * 4. Change your password.
	 * 5. Log out.
	 * @param messages
	 */
	private static void homePage(String messages) {	
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		displayPage(sHOME_PAGE, messages, info, null, sHOME_PAGE_OPTIONS);
		int selection = getValidSelectionFromUser(5);
		
		switch (selection) {
		// View your classrooms.
	    case 1: 
	    	classroomListPage(null);
	        break;
	    // Create a classroom.
	    case 2: 
	    	String classroomNameTemp = console.readLine("Please specify a classroom name: ");
	    	if (client.createClassroom(classroomNameTemp, currentUserName)){
	    		currentClassroomName = classroomNameTemp;
	    		currentPermissions = sINSTRUCTOR;
				classroomPage(mCLASSROOM_CREATION_SUCCESS);
			} else {
				homePage(eCLASSROOM_CREATION_ERROR);
			}
	        break;
	    // Request to join a classroom.
	    case 3:
	    	String classroomRequestName = console.readLine("Please specify the name of the classroom you'd like to join: ");
	    	if (client.requestToJoinClassroom(classroomRequestName, currentUserName)){
				homePage(mCLASSROOM_REQUEST_SUCCESS);
			} else {
				homePage(eCLASSROOM_REQUEST_ERROR);
			}
	        break;
	    // Change your password.
	    case 4:
	    	changePasswordPage(null);
	        break;
	    // Log out.
	    case 5:
	    	if (client.handleLogout(currentUserName)){
	    		currentUserName = null;
				loginPage(mLOG_OUT_SUCCESS);
			} else {
				homePage(eLOG_OUT_ERROR);
			}
	        break;
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}		
	}
	
	/**
	 * This is the change password page. It allows a user to change his/ her password.
	 * @param messages 
	 */
	private static void changePasswordPage(String messages) {
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		String content = UserInterfaceHelper.addFormattingAlignLeft("Password requirements:") +
						UserInterfaceHelper.addFormattingAlignLeft("* may only contain alphanumerics and the special characters !@#$%%^&*-=_+") + 
						UserInterfaceHelper.addFormattingAlignLeft("* must be at least 8 and at most 25 characters long") +
						UserInterfaceHelper.addFormattingAlignLeft("* must contain at least one uppercase letter") +
						UserInterfaceHelper.addFormattingAlignLeft("* must contain at least one lowercase letter") + 
						UserInterfaceHelper.addFormattingAlignLeft("* must contain at least one number") +
						UserInterfaceHelper.addFormattingAlignLeft("* must contain at least one special character."); // informs the user of the password requirements 
		displayPage(sCHANGE_PASSWORD_PAGE, messages, info, content, sCHANGE_PASSWORD_INSTRUCTIONS);
		
		String userNameTemp = console.readLine("User Name? ");
    	char[] oldPassword = console.readPassword("Old Password? ");
    	char[] newPassword = console.readPassword("New Password? ");
    	char[] confirmNewPassword = console.readPassword("Confirm New Password? ");
    	if (client.changePassword(oldPassword, newPassword, confirmNewPassword, userNameTemp, currentUserName)){
			homePage(mPASSWORD_CHANGE_SUCCESS);
		} else {
			homePage(ePASSWORD_CHANGE_ERROR);
		}
	}
	
	/**
	 * This is the classroom list page. It displays the list of classrooms
	 * of which a particular user is a member.
	 * @param messages 
	 */
	private static void classroomListPage(String messages) {
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		
		Map<String, Integer> classroomMap = client.getClassroomMapForUser(currentUserName);	
		List<String> classroomList = mapStringKeysToList(classroomMap);
		
		if (classroomList == null || classroomList.isEmpty()){
			String content = cNO_CLASSROOMS;
			displayPage(sCLASSROOM_LIST_PAGE, messages, info, content, sDEFAULT_OPTIONS);
			goHomeDefaultMenu();
		} else {		
			
			displayPage(sCLASSROOM_LIST_PAGE, messages, info, null, listToUIString(classroomList));
	
			int selection = getValidSelectionFromUser(classroomList.size());
			String classroomSelection = classroomList.get(selection - 1);
			
			currentClassroomName = classroomSelection;	
			currentPermissions = convertIntToStringPermissions(classroomMap.get(classroomSelection));
		    classroomPage(null);
		}
	}

	/**
	 * This is the page for a particular classroom. It displays the following 6 options for
	 * instructors:
	 * 1. View discussion board.
	 * 2. Create a thread.
     * 3. View members of this classroom.
     * 4. View requests to join this classroom.
	 * 5. Delete this classroom.
	 * 6. Go back home.
	 * 
	 * It displays the following 6 options for teaching assistants:
	 * 1. View discussion board.
	 * 2. Create a thread.
     * 3. View members of this classroom.
     * 4. View requests to join this classroom.
	 * 5. Disjoin this classroom.
	 * 6. Go back home.
	 * 
	 * It displays the following 4 options for students:
	 * 1. View discussion board.
	 * 2. Create a thread.
	 * 3. Disjoin this classroom.
	 * 4. Go back home.
	 * @param messages
	 */
	private static void classroomPage(String messages) {
		int maxSelection = 0;
		
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		if (currentPermissions == sSTUDENT) {
			displayPage(sCLASSROOM_PAGE, messages, info, null, sCLASSROOM_PAGE_OPTIONS_STUDENT);
			maxSelection = 4;
		}
		else if (currentPermissions == sTEACHING_ASSISTANT) {
			displayPage(sCLASSROOM_PAGE, messages, info, null, sCLASSROOM_PAGE_OPTIONS_TEACHING_ASSISTANT);
			maxSelection = 6;
		}
		else if (currentPermissions == sINSTRUCTOR) {
			displayPage(sCLASSROOM_PAGE, messages, info, null, sCLASSROOM_PAGE_OPTIONS_INSTRUCTOR);
			maxSelection = 6;
		}
		
		int selection = getValidSelectionFromUser(maxSelection);
		selection = selectionConverterClassroomPage(selection, currentPermissions);
		
		switch (selection) {
		// View discussion board.
	    case 1:
	        threadListPage(null);
	        break;
	    // Create a thread.
	    case 2:
	    	String threadNameTemp = console.readLine("Please specify a thread topic: ");
	    	String postContent = console.readLine("Please write your post's content: ");
	    	if (client.createThread(threadNameTemp, postContent, currentUserName, currentClassroomName)){
	    		currentThreadName = threadNameTemp;
				classroomPage(mTHREAD_CREATION_SUCCESS);
			} else {
				classroomPage(eTHREAD_CREATION_ERROR);
			}
	        break;
	    // View members of this classroom.
	    case 3:
	        memberListPage(null);
	        break;
	    // View requests to join this classroom.
	    case 4:
	        requestListPage(null);
	        break;
	    // Disjoin this classroom.
	    case 5:
	    	if (client.disjoinClassroom(currentClassroomName, currentUserName)){
	    		homePage(mDISJOIN_CLASSROOM_SUCCESS);
	    	} else {
	    		classroomPage(eDISJOIN_CLASSROOM_ERROR);
	    	}
	        break;
	    // Go back home.
	    case 6:
	    	currentClassroomName = null;
	    	currentPermissions = null;
	        homePage(null);
	        break;
	    // Delete this classroom.
	    case 7:
	    	if (client.deleteClassroom(currentClassroomName, currentUserName)){
	    		homePage(mDELETE_CLASSROOM_SUCCESS);
	    	} else {
	    		classroomPage(eDELETE_CLASSROOM_ERROR);
	    	}	    	
	    	break;	        	
	    default:
	    	console.printf(eGENERAL_ERROR);
	    	break;
		}		
	}

	/**
	 * This is the thread list page. It displays the list of thread topics
	 * for a particular classroom.
	 * @param messages
	 */
	private static void threadListPage(String messages) {
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		
		Map<Integer, String> threadMap = client.getThreadMapForClassroom(currentClassroomName, currentUserName);
		
		if (threadMap == null || threadMap.isEmpty()){
			String content = cNO_THREADS;
			displayPage(sCLASSROOM_LIST_PAGE, messages, info, content, sDEFAULT_OPTIONS);
			goHomeDefaultMenu();
		} else {
		
			TreeMap<Integer, String> threadTreeMap = new TreeMap<Integer, String>(threadMap); // Converting to TreeMap to stabilize the order.
			List<String> threadTopicsList = mapValuesToList(threadTreeMap);
			List<Integer> threadIDList = mapIntegerKeysToList(threadTreeMap);
			
			displayPage(sTHREAD_LIST_PAGE, messages, info, null, listToUIString(threadTopicsList));
			
			int selection = getValidSelectionFromUser(threadTopicsList.size());				
			Integer threadSelection = threadIDList.get(selection - 1);
			
			currentThreadID = threadSelection;
			currentThreadName = threadMap.get(threadSelection);
		    threadPage(null);
		}
	}

	/**
	 * This is the page for a particular thread. It displays the following 4 options for
	 * instructors and teaching assistants:
	 * 1. Comment on this thread.
	 * 2. Delete a comment.
	 * 3. Delete this entire thread.
	 * 4. Go back to this classroom's main page.
	 * 
	 * It displays the following 2 options for students:
	 * 1. Comment on this thread.
	 * 2. Go back to this classroom's main page.
	 * @param messages
	 */
	private static void threadPage(String messages) {
		int maxSelection = 0;
		int commentsExist = 0; // '0' for no comments, '1' for one or more comments.
		
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current thread: " + currentThreadName));
		
		Map<Integer, String> threadContentMap = client.getThreadGivenID(currentThreadID, currentClassroomName, currentUserName);
    	TreeMap<Integer, String> threadContentTreeMap = new TreeMap<Integer, String>(threadContentMap); // Converting to TreeMap to stabilize the order.
		//List<String> threadContentList = mapValuesToList(threadContentTreeMap);
		List<Integer> postIDList = mapIntegerKeysToList(threadContentTreeMap);
		String threadContent = threadMapToString(threadContentTreeMap);
		if (postIDList.size() > 2) {
			commentsExist = 1;
		}		
		
		if (currentPermissions == sTEACHING_ASSISTANT || currentPermissions == sINSTRUCTOR) {
			if (commentsExist == 1) {
				displayPage(sTHREAD_PAGE, messages, info, threadContent, sTHREAD_PAGE_OPTIONS_INSTRUCTOR_AND_TEACHING_ASSISTANT);
				maxSelection = 4;
				
			} else {
				displayPage(sTHREAD_PAGE, messages, info, threadContent, sTHREAD_PAGE_OPTIONS_INSTRUCTOR_AND_TEACHING_ASSISTANT_NO_COMMENTS);
				maxSelection = 3;
			}
		}
		else if (currentPermissions == sSTUDENT) {
			displayPage(sTHREAD_PAGE, messages, info, threadContent, sTHREAD_PAGE_OPTIONS_STUDENT);
			maxSelection = 2;
		}
		
		int selection = getValidSelectionFromUser(maxSelection);
		selection = selectionConverterThreadPage(selection, currentPermissions, commentsExist);
		
		switch (selection) {
		// Comment on this thread.
	    case 1:
	    	String commentContent = console.readLine("Please write your comment: ");
	    	if (client.createComment(commentContent, currentThreadID, currentClassroomName, currentUserName)){
				threadPage(null);
			} else {
				classroomPage(null);
			}
	        break;
	    // Delete a comment.
	    case 2:
	    	Integer commentID = postIDList.get(Integer.parseInt(console.readLine("What's the number of the comment you'd like to delete? ")) + 1);
	    	if (client.deleteComment(commentID, currentThreadID, currentUserName, currentClassroomName)){
	    		threadPage(mCOMMENT_DELETION_SUCCESS);
		        break;
	    	} else {
	    		threadPage(eCOMMENT_DELETION_ERROR);
	    	}
	    // Delete this entire thread.
	    case 3:
	    	if (client.deleteThread(currentThreadID, currentUserName, currentClassroomName)){
	    		classroomPage(mTHREAD_DELETION_SUCCESS); 
		        break;
	    	} else {
	    		threadPage(eTHREAD_DELETION_ERROR);
	    	}
	        break;
	    // Go back to this classroom's main page.
	    case 4:
	    	currentThreadName = null;
	    	classroomPage(null);
	        break;
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}		
	}

	/**
	 * This is the member list page. It displays the list of members
	 * for a particular classroom.
	 * @param messages
	 */
	private static void memberListPage(String messages) {		
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		
		Map<String, Integer> memberMap = client.getMemberMapForClassroom(currentClassroomName, currentUserName);
		
		if (memberMap == null || memberMap.isEmpty()){
			String content = cNO_MEMBERS;
			displayPage(sCLASSROOM_LIST_PAGE, messages, info, content, sDEFAULT_OPTIONS);
			goHomeDefaultMenu();
		} else {
		
			TreeMap<String, Integer> memberTreeMap = new TreeMap<String, Integer>(memberMap); // Converting to TreeMap to stabilize the order.
			List<String> memberList = mapStringKeysToList(memberTreeMap);
	
			displayPage(sMEMBER_LIST_PAGE, messages, info, null, memberMapToUIString(memberTreeMap)); // Displays members' names along with their permissions in the current classroom.
	
			int selection = getValidSelectionFromUser(memberList.size());
			
			currentMemberName = memberList.get(selection -1);
			currentMemberPermissions = convertIntToStringPermissions(memberTreeMap.get(currentMemberName));
		    memberPage(null);
		}
	}
	
	/**
	 * This is the page for a particular member. It displays the following 3 options for
	 * instructors:
	 * 1. Remove this member from this classroom.
     * 2. Change this member's status.
	 * 3. Go back to this classroom's main page.
	 * 
	 * It displays the following 2 options for teaching assistants:
	 * 1. Remove this member from this classroom.
	 * 2. Go back to this classroom's main page.
	 * 
	 * Students do not have the permissions to navigate to this page.
	 * @param messages
	 */
	private static void memberPage(String messages) {
		int maxSelection = 0;
		
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		
		String memberContent = UserInterfaceHelper.addFormattingAlignLeft("Currently viewing member: " + currentMemberName + " (" + currentMemberPermissions + ").");
		
		if (currentPermissions == sTEACHING_ASSISTANT) {
			displayPage(sMEMBER_PAGE, messages, info, memberContent, sMEMBER_PAGE_OPTIONS_TEACHING_ASSISTANT); 
			maxSelection = 2;
		}
		else if (currentPermissions == sINSTRUCTOR) {
			displayPage(sMEMBER_PAGE, messages, info, memberContent, sMEMBER_PAGE_OPTIONS_INSTRUCTOR);
			maxSelection = 3;
		}
		
		int selection = getValidSelectionFromUser(maxSelection);
		selection = selectionConverterMemberPage(selection, currentPermissions);
		
		switch (selection) {
		// Remove this member from this classroom.
	    case 1:
	    	if (client.removeMember(currentMemberName, currentClassroomName, currentUserName)){
	    		currentMemberName = null;
	    		currentMemberPermissions = null;
	    		memberListPage(mREMOVE_MEMBER_SUCCESS);
	    	} else {
	    		memberPage(eREMOVE_MEMBER_ERROR);
	    	}
	        break;
	    // Change this member's status.
	    case 2:
	    	currentMemberPermissions = switchCurrentMemberPermissions(currentMemberPermissions);
	    	Integer currentMemberPermissionsAsInt = convertStringToIntPermissions(currentMemberPermissions);
	    	if (client.changeStatus(currentMemberName, currentMemberPermissionsAsInt, currentUserName, currentClassroomName)){
	    		memberPage(mCHANGE_STATUS_SUCCESS);
	    	} else {
	    		currentMemberPermissions = switchCurrentMemberPermissions(currentMemberPermissions);
	    		memberPage(eCHANGE_STATUS_ERROR);
	    	}
	    	
	        break;
	    // Go back to this classroom's main page.
	    case 3:
	    	currentMemberName = null;
	    	currentMemberPermissions = null;
	        classroomPage(null);
	        break;
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}		
	}

	/**
	 * This is the request list page. It displays the list of user's names who have 
	 * requested to join this particular classroom.
	 * @param messages
	 */
	private static void requestListPage(String messages) {
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		
		List<String> requestList = client.getRequestListForClassroom(currentClassroomName, currentUserName);
		
		if (requestList == null || requestList.isEmpty()){
			String content = cNO_REQUESTS;
			displayPage(sCLASSROOM_LIST_PAGE, messages, info, content, sDEFAULT_OPTIONS);
			goHomeDefaultMenu();
		} else {
		
			displayPage(sREQUEST_LIST_PAGE, messages, info, null, listToUIString(requestList));
			
			int selection = getValidSelectionFromUser(requestList.size());
			
			currentPendingMemberName = requestList.get(selection - 1);
		    requestPage(null);	
		}
	}
	
	/**
	 * This is the page for a particular request. It displays the following 3 options for
	 * instructors and teaching assistants:
	 * 1. Confirm as a member.
	 * 2. Deny membership.
	 * 3. Go back to this classroom's main page.
	 * 
	 * Students do not have permissions to navigate to this page.
	 * @param messages
	 */
	private static void requestPage(String messages) {
		String info = UserInterfaceHelper.addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Current classroom: " + currentClassroomName));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Status for this classroom: " + currentPermissions));
		info = info.concat(UserInterfaceHelper.addFormattingAlignLeft("Currently viewing request from member: " + currentPendingMemberName));
		
		displayPage(sREQUEST_PAGE, messages, info, null, sREQUEST_PAGE_OPTIONS);		

		int selection = getValidSelectionFromUser(3);
		
		switch (selection) {
		// Confirm as a member.
	    case 1:
	    	if (client.confirmAsMemberOfClassroom(currentPendingMemberName, currentClassroomName, currentUserName)) {
	    		currentPendingMemberName = null;
	    		requestListPage(mCONFIRM_AS_MEMBER_SUCCESS);
	    	} else {
	    		requestPage(eCONFIRM_AS_MEMBER_ERROR);
	    	}
	        break;
	    // Deny membership.
	    case 2:
	    	if (client.denyMembershipToClassroom(currentPendingMemberName, currentClassroomName, currentUserName)) {
	    		currentPendingMemberName = null;
		    	requestListPage(mDENY_MEMBERSHIP_SUCCESS);
	    	} else {
	    		requestPage(eDENY_MEMBERSHIP_ERROR);
	    	}    	
	        break;
	    // Go back to this classroom's main page.
	    case 3:
	    	currentPendingMemberName = null;
	        classroomPage(null);
	        break;
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}		
	}
	
	
	
	
	////////////////////////////////////////////////
	//              HELPER FUNCTIONS              //
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////
	//            SELECTION FUNCTIONS             //
	////////////////////////////////////////////////
		
	/**
	 * Get valid selection from the user.
	 * @param maxInt
	 * @return integer corresponding to the selection.
	 */
	public static int getValidSelectionFromUser(int maxInt) {
		boolean validSelection = false;
		int selection = 0;
		while (!validSelection) {
			try {
				selection = Integer.parseInt(console.readLine("Please select an option. "));
				if (selection <= maxInt && selection > 0) {
					validSelection = true;
				} else {
					console.printf(eNON_VALID_SELECTION);
				}
			} catch (NumberFormatException e) {
				console.printf(eNON_VALID_SELECTION);
				validSelection = false;
			}
		}
		return selection;
	}
	
	/**
	 * This provides a default go home choice for when the user would 
	 * otherwise be in a dead end situation (for example: when he opts 
	 * to choose a classroom, but he is not actually enrolled in any 
	 * classrooms).
	 */
	public static void goHomeDefaultMenu() {
		int selection = getValidSelectionFromUser(1);
		
		switch (selection) {
		 // Go back home.
	    case 1:
	    	currentClassroomName = null;
	    	currentPermissions = null;
	    	currentThreadName = null;
	    	currentThreadID = null;
	    	currentMemberName = null;
	    	currentMemberPermissions = null;
	    	currentPendingMemberName = null;
	        homePage(null);
	        break;
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}	        
	}
	
	/**
	 * Converts a selection in the classroom page to the appropriate numbers based on permissions.
	 * @param selection
	 * @param permissions
	 * @return
	 */
	private static int selectionConverterClassroomPage(int selection, String permissions) {
		if (currentPermissions == sSTUDENT) {
			if (selection == 3) {
				selection = 5;
			} else if (selection == 4) {
				selection = 6;
			}
		}
		else if (currentPermissions == sINSTRUCTOR) {
			if (selection == 5) {
				selection = 7;
			}
		}
		return selection;
	}
	
	/**
	 * Converts a selection in the member page to the appropriate numbers based on permissions.
	 * @param selection
	 * @param permissions
	 * @return
	 */
	private static int selectionConverterMemberPage(int selection, String permissions) {
		if (currentPermissions == sTEACHING_ASSISTANT) {
			if (selection == 2) {
				selection = 3;
			}
		}
		return selection;
	}
	
	/**
	 * Converts a selection in the thread page to the appropriate numbers based on permissions.
	 * @param selection
	 * @param permissions
	 * @param commentsExist 
	 * @return
	 */
	private static int selectionConverterThreadPage(int selection, String permissions, int commentsExist) {
		if (permissions == sSTUDENT) {
			if (selection == 2) {
				selection = 4;
			}
		}
		if (permissions == sINSTRUCTOR || permissions == sTEACHING_ASSISTANT) {
			if (commentsExist == 0) {
				if (selection == 2) {
					selection = 3;
				} else if (selection == 3) {
					selection = 4;
				}
			}
		}
		return selection;
	}
	
	////////////////////////////////////////////////
	//          PERMISSIONS FUNCTIONS             //
	////////////////////////////////////////////////
	
	/**
	 * Converts permissions in number form to string form.
	 * @param num representing the permissions
	 * @return a string name for the permissions
	 */
	public static String convertIntToStringPermissions(int num) {
		String permissions = null;
		if (num == 1){
			permissions = sSTUDENT;
		}
		else if (num == 2){
			permissions = sTEACHING_ASSISTANT;
		}
		else if (num == 3){
			permissions = sINSTRUCTOR;
		}
		return permissions;
	}
	
	/**
	 * Converts permissions in string form to int form.
	 * @param num representing the permissions
	 * @return a string name for the permissions
	 */
	public static int convertStringToIntPermissions(String num) {
		int permissions = -2;
		if (num == sSTUDENT){
			permissions = 1;
		}
		else if (num == sTEACHING_ASSISTANT){
			permissions = 2;
		}
		else if (num == sINSTRUCTOR){
			permissions = 3;
		}
		return permissions;
	}
	
	/**
	 * Switches teaching assistant permissions for student permissions and vice versa.
	 * @param memberPermissions
	 * @return
	 */
	private static String switchCurrentMemberPermissions(String memberPermissions) {
		if (memberPermissions == sSTUDENT)
			return sTEACHING_ASSISTANT;
		if (memberPermissions == sTEACHING_ASSISTANT)
			return sSTUDENT;
		return memberPermissions;			
	}
	
	////////////////////////////////////////////////
	//       VARIOUS MAP TO LIST FUNCTIONS        //
	////////////////////////////////////////////////

	/**
	 * Extracts string keys in a map and puts them into a list.
	 * @param map
	 * @return List<String>
	 */
	public static List<String> mapStringKeysToList(Map<String, Integer> map){
		List<String> outputList = new ArrayList<String>();
		if (map == null) {
			return outputList;
		}
		for (String key : map.keySet()){
			outputList.add(key);
		}
		return outputList;
	}
	
	/**
	 * Extracts integer keys in a map and puts them into a list.
	 * @param map
	 * @return List<Integer>
	 */
	private static List<Integer> mapIntegerKeysToList(Map<Integer, String> map) {
		List<Integer> outputList = new ArrayList<Integer>();
		for (Integer key : map.keySet()){
			outputList.add(key);
		}
		return outputList;
	}
	
	/**
	 * Extracts string values in a map and puts them into a list.
	 * @param map
	 * @return List<String>
	 */
	private static List<String> mapValuesToList(Map<Integer, String> map) {
		List<String> outputList = new ArrayList<String>();
		for (String value : map.values()){
			outputList.add(value);
		}
		return outputList;
	}

	
	
	
	////////////////////////////////////////////////
	//               UI FORMATTING                //
	////////////////////////////////////////////////
	
	////////////////////////////////////////////////
	//  CONTENT-SPECIFIC MAP TO STRING FUNCTIONS  //
	////////////////////////////////////////////////
	
	/**
	 * This is a function specifically for displaying threads in the UI.
	 * @param threadContentMap
	 * @return String formatted thread content extracted from the map.
	 */
	private static String threadMapToString(Map<Integer, String> threadContentMap) {
		List<String> threadContentList = mapValuesToList(threadContentMap);
		String uiString = "";
		String uiStringTemp;
		for (int i = 0; i < threadContentList.size(); i++){
			uiStringTemp = "";
			if (i == 0){
				uiStringTemp = uiStringTemp.concat("Thread Topic: ");
			}
			else if (i == 1){
				uiStringTemp = uiStringTemp.concat("Initial Post: ");
			}
			else if (i >= 2){
				uiStringTemp = uiStringTemp.concat("Comment " + Integer.toString(i - 1) + ". ");
			}			
			uiStringTemp = uiStringTemp.concat(threadContentList.get(i));
			uiString = uiString.concat(UserInterfaceHelper.addFormattingAlignLeft(uiStringTemp));
		}
		return uiString;
	}		
	
	/**
	 * This is a function specifically for displaying members in the UI.
	 * @param threadList
	 * @return String
	 */
	private static String memberMapToUIString(Map<String, Integer> map) {
		int i = 1;
		String uiString = "";
		String uiStringTemp;
		for (Entry<String, Integer> entry : map.entrySet()){
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(Integer.toString(i) + ". ");
			i++;
			uiStringTemp = uiStringTemp.concat(entry.getKey() + " (" + convertIntToStringPermissions(entry.getValue()) + ")");
			uiString = uiString.concat(UserInterfaceHelper.addFormattingAlignLeft(uiStringTemp));
		}
		return uiString;
	}
	
	/**
	 * Takes in a list and turns it into a TUI-renderable string
	 * @param list
	 * @return String
	 */
	public static String listToUIString(List<String> list) {
		String uiString = "";
		String uiStringTemp;
		for (int i = 0; i < list.size(); i++){
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(Integer.toString(i + 1) + ". ");
			uiStringTemp = uiStringTemp.concat(list.get(i));
			uiString = uiString.concat(UserInterfaceHelper.addFormattingAlignLeft(uiStringTemp));
		}		
		return uiString;		
	}
	
	////////////////////////////////////////////////
	//           UI FORMATTING FUNCTIONS          //
	////////////////////////////////////////////////
	
	/**
	 * Clears the last page and displays the new one.
	 * Only necessary parameter is pageName.
	 * If there is nothing to display for a page, use null as the parameter.
	 * @param pageName
	 * @param messages
	 * @param info
	 * @param content
	 * @param options
	 */
	public static void displayPage(String pageName, String messages, String info, String content, String options){
		UserInterfaceHelper.clearScreen();
		console.printf(UserInterfaceHelper.sBIG_DIVIDER + pageName);
		if (messages != null) {
			console.printf(UserInterfaceHelper.sSMALL_DIVIDER + messages);
		}
		if (info != null) {
			console.printf(UserInterfaceHelper.sSMALL_DIVIDER + info);
		}
		if (content != null) {
			console.printf(UserInterfaceHelper.sSMALL_DIVIDER + content);
		}
		if (options != null) {
			console.printf(UserInterfaceHelper.sSMALL_DIVIDER + options);
		}
		console.printf(UserInterfaceHelper.sBIG_DIVIDER);
	}
	
	
	
	
	////////////////////////////////////////////////
	//                    MAIN                    //
	////////////////////////////////////////////////
	
	/**
	 * Main function. Goes to the login page.
	 */
	public static void main(String[] args) {
		
		console = System.console();
        if (console == null) {
            System.err.println("No console.");
            System.exit(1);
        }
        
        client = new Client();
		
		System.out.println("Welcome to Studious Wombat!");
		loginPage(null);
	}

}