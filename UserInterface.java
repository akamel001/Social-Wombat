import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is the user interface for Social Wombat.
 * 
 * This class is for anything dealing with what the user sees and the
 * work flow of the social network. However, it does not deal with the
 * behind-the-scenes logic for any of the actions, which is done in 
 * the client class.
 * 
 */
public class UserInterface {
	
	static Console console;
	static Client client;
	private static String currentUserName;
	private static String currentClassroomName;
	private static String currentPermissions;
	private static String currentThreadName;
	private static Integer currentThreadID;
	private static String currentMemberName;
	private static String currentPendingMemberName;
	
	static int iWINDOWWIDTH = 81;
	
	// Below is a bunch of strings used in the text-based user interface.
	private static final String sNEW_LINE = 			System.getProperty("line.separator");
	private static final String sBIG_DIVIDER = 			generateBigDivider();
	private static final String sSMALL_DIVIDER =		generateSmallDivider();
	private static final String sLOG_IN = 				addFormattingAlignCenter("LOG IN");
	private static final String sHOME_PAGE = 			addFormattingAlignCenter("HOME"); 
	private static final String sCLASSROOM_LIST_PAGE = 	addFormattingAlignCenter("CLASSROOM LIST"); 
	private static final String sCLASSROOM_PAGE = 		addFormattingAlignCenter("CLASSROOM");
	private static final String sTHREAD_LIST_PAGE = 	addFormattingAlignCenter("THREAD LIST");
	private static final String sTHREAD_PAGE = 			addFormattingAlignCenter("THREAD");
	private static final String sMEMBER_LIST_PAGE = 	addFormattingAlignCenter("MEMBER LIST");
	private static final String sMEMBER_PAGE = 			addFormattingAlignCenter("MEMBER");
	private static final String sREQUEST_LIST_PAGE = 	addFormattingAlignCenter("REQUEST LIST");
	private static final String sREQUEST_PAGE = 		addFormattingAlignCenter("REQUEST");
	
	private static final String sINSTRUCTOR =			"Instructor";
	private static final String sTEACHING_ASSISTANT = 	"Teaching Assistant";
	private static final String sSTUDENT = 				"Student";
	
	private static final String eGENERAL_ERROR = 			addFormattingAlignLeft("An error has occurred.");
	private static final String eLOG_IN_ERROR = 			addFormattingAlignLeft("There was an error in logging in with the provided username.");		
	private static final String eCLASSROOM_CREATION_ERROR=	addFormattingAlignLeft("An error occured when creating the classroom.");
	private static final String eCLASSROOM_REQUEST_ERROR =	addFormattingAlignLeft("An error occured when requesting to join a classroom.");
	private static final String eNON_VALID_SELECTION = 		addFormattingAlignLeft("That is not a valid selection.");
	private static final String eTHREAD_CREATION_ERROR = 	addFormattingAlignLeft("An error occured when creating your thread.");
	
	private static final String mCLASSROOM_CREATION_SUCCESS =	addFormattingAlignLeft("You have successfully created a classroom!");
	private static final String mCLASSROOM_REQUEST_SUCCESS =	addFormattingAlignLeft("You have successfully requested to join a classroom!");
	private static final String mLOG_OUT_SUCCESS = 				addFormattingAlignLeft("You have successfully logged out.");
	private static final String mTHREAD_CREATION_SUCCESS = 		addFormattingAlignLeft("You have successfully posted your new thread to the discussion board.");	
	private static final String mDISJOIN_CLASSROOM_SUCCESS = 	addFormattingAlignLeft("You have successfully disjoined the classroom.");
	private static final String mDELETE_CLASSROOM_SUCCESS =  	addFormattingAlignLeft("You have successfully deleted your classroom.");
	private static final String mREMOVE_MEMBER_SUCCESS = 		addFormattingAlignLeft("You have successfully removed a member from this classroom.");
	private static final String mCHANGE_STATUS_SUCCESS =		addFormattingAlignLeft("You have successfully changed the status of a member.");
	private static final String mCONFIRM_AS_MEMBER_SUCCESS = 	 addFormattingAlignLeft("You have successfully added a member to this classroom.");
	private static final String mDENY_MEMBERSHIP_SUCCESS =   	addFormattingAlignLeft("You have successfully denied a user member to this classroom.");
	
	private static final String sHOME_PAGE_OPTIONS =		addFormattingAlignLeft("1. View your classrooms.") +
															addFormattingAlignLeft("2. Create a classroom.") +
															addFormattingAlignLeft("3. Request to join a classroom.") +
															addFormattingAlignLeft("4. Log out.");
	
	private static final String sCLASSROOM_PAGE_OPTIONS_INSTRUCTOR = addFormattingAlignLeft("1. View discussion board.") +
															addFormattingAlignLeft("2. Create a thread.") +
															addFormattingAlignLeft("3. View members of this classroom.") +
															addFormattingAlignLeft("4. View requests to join this classroom.") +
															addFormattingAlignLeft("5. Delete this classroom.") +
															addFormattingAlignLeft("6. Go back home.");
	
	private static final String sCLASSROOM_PAGE_OPTIONS_TEACHING_ASSISTANT =	addFormattingAlignLeft("1. View discussion board.") +
															addFormattingAlignLeft("2. Create a thread.") +
															addFormattingAlignLeft("3. View members of this classroom.") +
															addFormattingAlignLeft("4. View requests to join this classroom.") +
															addFormattingAlignLeft("5. Disjoin this classroom.") +
															addFormattingAlignLeft("6. Go back home.");
	
	private static final String sCLASSROOM_PAGE_OPTIONS_STUDENT = addFormattingAlignLeft("1. View discussion board.") +
															addFormattingAlignLeft("2. Create a thread.") +
															addFormattingAlignLeft("3. Disjoin this classroom.") +
															addFormattingAlignLeft("4. Go back home.");

	private static final String sTHREAD_PAGE_OPTIONS_INSTRUCTOR_AND_TEACHING_ASSISTANT = addFormattingAlignLeft("1. Comment on this thread.") +
															addFormattingAlignLeft("2. Delete a comment.") +
															addFormattingAlignLeft("3. Delete this entire thread") +
															addFormattingAlignLeft("4. Go back to this classroom's main page.");
	
	private static final String sTHREAD_PAGE_OPTIONS_STUDENT = addFormattingAlignLeft("1. Comment on this thread.") +
															addFormattingAlignLeft("2. Go back to this classroom's main page.");
	
	private static final String sMEMBER_PAGE_OPTIONS_INSTRUCTOR = addFormattingAlignLeft("1. Remove this member from this classroom.") +
															addFormattingAlignLeft("2. Change this member's status.") +
															addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	private static final String sMEMBER_PAGE_OPTIONS_TEACHING_ASSISTANT = addFormattingAlignLeft("1. Remove this member from this classroom.") +
															addFormattingAlignLeft("2. Go back to this classroom's main page.");
	
	private static final String sREQUEST_PAGE_OPTIONS = 	addFormattingAlignLeft("1. Confirm as a member.") +
															addFormattingAlignLeft("2. Deny membership.") +
															addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	/**
	 * This is the login page. The login page requests a user name. If the user name is valid, 
	 * it goes to the home page. Otherwise, it loops back to the login page and displays an 
	 * error message.
	 * @param messages
	 */
	private static void loginPage(String messages) {
		displayPage(sLOG_IN, messages, null, null, null);	
		String userNameTemp = console.readLine("User Name? ");
		
		if (client.handleLogin(userNameTemp)){
			currentUserName = userNameTemp;
			homePage(addFormattingAlignLeft("Welcome, " + currentUserName + "!"));
		} else {
			loginPage(eLOG_IN_ERROR);
		}
	}
	
	/**
	 * This is the home page. It displays four options:
	 * 1. View your classrooms.
	 * 2. Create a classroom.
	 * 3. Request to join a classroom.
	 * 4. Log out.
	 * @param messages
	 */
	private static void homePage(String messages) {	
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		displayPage(sHOME_PAGE, messages, info, null, sHOME_PAGE_OPTIONS);
		int selection = getValidSelectionFromUser(4);
		
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
	    // Log out.
	    case 4:
	    	currentUserName = null;
	        loginPage(mLOG_OUT_SUCCESS);
	        break;
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}
		
	}
	
	/**
	 * This is the classroom list page. It displays the list of classrooms
	 * of which a particular user is a member.
	 * @param messages 
	 */
	private static void classroomListPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		Map<String, Integer> classroomMap = client.getClassroomMapForUser(currentUserName);	
		List<String> classroomList = mapStringKeysToList(classroomMap);
		displayPage(sCLASSROOM_LIST_PAGE, messages, info, null, listToUIString(classroomList));

		int selection = getValidSelectionFromUser(classroomMap.size());
		String classroomSelection = classroomList.get(selection - 1);
		
		currentClassroomName = classroomSelection;	
		currentPermissions = convertIntToStringPermissions(classroomMap.get(classroomSelection));
	    classroomPage(null);		
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
	 * 
	 */
	private static void classroomPage(String messages) {
		int maxSelection = 0;
		
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
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
				threadPage(mTHREAD_CREATION_SUCCESS);
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
	    	client.disjoinClassroom(currentClassroomName, currentUserName);
	    	homePage(mDISJOIN_CLASSROOM_SUCCESS);
	        break;
	    // Go back home.
	    case 6:
	    	currentClassroomName = null;
	    	currentPermissions = null;
	        homePage(null);
	        break;
	    // Delete this classroom.
	    case 7:
	    	client.deleteClassroom(currentClassroomName, currentUserName);
	    	homePage(mDELETE_CLASSROOM_SUCCESS);
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
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
		
		Map<Integer, String> threadMap = client.getThreadMapForClassroom(currentClassroomName, currentUserName);		
		List<Integer> threadIDList = mapIntegerKeysToList(threadMap);
		List<String> threadTopicsList = mapValuesToList(threadMap);
		
		displayPage(sTHREAD_LIST_PAGE, messages, info, null, listToUIString(threadTopicsList));
		
		int selection = getValidSelectionFromUser(threadMap.size());				
		Integer threadSelection = threadIDList.get(selection - 1);
		
		currentThreadID = threadSelection;
		currentThreadName = threadMap.get(threadSelection);
	    threadPage(null);
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
		
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
		info = info.concat(addFormattingAlignLeft("Current thread: " + currentThreadName + "."));
		
		String threadContent = null; //TODO: get thread content
		
		if (currentPermissions == sTEACHING_ASSISTANT || currentPermissions == sINSTRUCTOR) {
			displayPage(sTHREAD_PAGE, messages, info, threadContent, sTHREAD_PAGE_OPTIONS_INSTRUCTOR_AND_TEACHING_ASSISTANT);
			maxSelection = 4;
		}
		else if (currentPermissions == sSTUDENT) {
			displayPage(sTHREAD_PAGE, messages, info, threadContent, sTHREAD_PAGE_OPTIONS_STUDENT);
			maxSelection = 2;
		}
		
		int selection = getValidSelectionFromUser(maxSelection);
		selection = selectionConverterThreadPage(selection, currentPermissions);
		
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
	    	// TODO: needs to be implemented
	        threadPage(null);
	        break;
	    // Delete this entire thread.
	    case 3:
	    	// TODO: needs to be implemented
	        classroomPage(null);
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
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
		
		Map<String, Integer> memberMap = client.getMemberMapForClassroom(currentClassroomName, currentUserName);
		List<String> memberList = mapStringKeysToList(memberMap); // TODO not stable
		displayPage(sMEMBER_LIST_PAGE, messages, info, null, mapToUIString3(memberMap)); // TODO not stable

		int selection = getValidSelectionFromUser(memberMap.size());
		
		currentMemberName = memberList.get(selection);
	    memberPage(null);
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
		
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
		info = info.concat(addFormattingAlignLeft("Currently viewing member: " + currentMemberName + "."));
		
		String memberContent = null; //TODO: member content such as status?
		
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
	    	client.removeMember(currentMemberName, currentClassroomName, currentUserName);
	    	currentMemberName = null;
	        memberListPage(mREMOVE_MEMBER_SUCCESS);
	        break;
	    // Change this member's status.
	    case 2:
	    	client.changeStatus(currentMemberName, currentUserName, currentClassroomName);
	    	memberPage(mCHANGE_STATUS_SUCCESS);
	        break;
	    // Go back to this classroom's main page.
	    case 3:
	    	currentMemberName = null;
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
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
		
		List<String> requestList = client.getRequestListForClassroom(currentClassroomName, currentUserName);
		displayPage(sREQUEST_LIST_PAGE, messages, info, null, listToUIString(requestList));
		
		int selection = getValidSelectionFromUser(requestList.size());
		
		currentPendingMemberName = requestList.get(selection - 1);
	    requestPage(null);		
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
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Status for this classroom: " + currentPermissions + "."));
		info = info.concat(addFormattingAlignLeft("Currently viewing request from member: " + currentMemberName + "."));
		
		displayPage(sREQUEST_PAGE, messages, info, null, sREQUEST_PAGE_OPTIONS);		

		int selection = getValidSelectionFromUser(3);
		
		switch (selection) {
		// Confirm as a member.
	    case 1:
	    	client.confirmAsMemberOfClassroom(currentPendingMemberName, currentClassroomName, currentUserName);
	    	currentPendingMemberName = null;
	        requestListPage(mCONFIRM_AS_MEMBER_SUCCESS);
	        break;
	    // Deny membership.
	    case 2:
	    	client.denyMembershipToClassroom(currentPendingMemberName, currentClassroomName, currentUserName);
	    	currentPendingMemberName = null;
	    	requestListPage(mDENY_MEMBERSHIP_SUCCESS);
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
	
	// Helper functions.
		
	/**
	 *  Get valid selection from the user.
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
	
	private static int selectionConverterClassroomPage(int selection, String permissions) {
		if (currentPermissions == sSTUDENT) {
			if (selection == 3)
				selection = 5;
			if (selection == 4)
				selection = 6;
		}
		else if (currentPermissions == sINSTRUCTOR) {
			if (selection == 5)
				selection = 7;
		}
		return selection;
	}
	
	private static int selectionConverterMemberPage(int selection, String permissions) {
		if (currentPermissions == sINSTRUCTOR) {
			if (selection == 2)
				selection = 3;
		}
		return selection;
	}
	
	private static int selectionConverterThreadPage(int selection, String currentPermissions2) {
		if (currentPermissions == sSTUDENT) {
			if (selection == 2)
				selection = 4;
		}
		return selection;
	}
	
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
		else if (num == 1){
			permissions = sTEACHING_ASSISTANT;
		}
		else if (num == 1){
			permissions = sINSTRUCTOR;
		}
		return permissions;
	}
	
	// Various map to list functions.	
	public static List<String> mapStringKeysToList(Map<String, Integer> map){
		List<String> outputList = new ArrayList<String>();
		for (String key : map.keySet()){
			outputList.add(key);
		}
		return outputList;
	}
	
	private static List<Integer> mapIntegerKeysToList(Map<Integer, String> map) {
		List<Integer> outputList = new ArrayList<Integer>();
		for (Integer key : map.keySet()){
			outputList.add(key);
		}
		return outputList;
	}
	
	private static List<String> mapValuesToList(Map<Integer, String> map) {
		List<String> outputList = new ArrayList<String>();
		for (String value : map.values()){
			outputList.add(value);
		}
		return outputList;
	}
	
	// UI formatting.
	
	/**
	 * Clears the the top of the screen depending on the size of your window.
	 */
	public static void clearScreen() {
		System.out.println(((char) 27)+"[2J");
	}
	
	/**
	 * Takes in a list and turns it into a TUI-renderable string
	 */
	public static String listToUIString(List<String> list) {
		String uiString = "";
		String uiStringTemp;
		for (int i = 0; i < list.size(); i++){
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(Integer.toString(i + 1) + ". ");
			uiStringTemp = uiStringTemp.concat(list.get(i));
			uiString = uiString.concat(addFormattingAlignLeft(uiStringTemp));
		}		
		return uiString;		
	}
	
//	/**
//	 * 
//	 * @param map
//	 * @return uiString
//	 */
//	private static String mapToUIString(Map<String, Integer> map) {
//		// TODO depends on sorting
//		int i = 1;
//		String uiString = "";
//		String uiStringTemp;
//		for (String key : map.keySet()){
//			uiStringTemp = "";
//			uiStringTemp = uiStringTemp.concat(Integer.toString(i) + ". ");
//			i++;
//			uiStringTemp = uiStringTemp.concat(key);
//			uiString = uiString.concat(addFormattingAlignLeft(uiStringTemp));
//		}
//		return uiString;
//	}
//	
//	/**
//	 * 
//	 * @param threadList
//	 * @return
//	 */
//	private static String mapToUIString2(Map<Integer, String> map) {
//		int i = 1;
//		String uiString = "";
//		String uiStringTemp;
//		for (Entry<Integer, String> entry : map.entrySet()){
//			uiStringTemp = "";
//			uiStringTemp = uiStringTemp.concat(Integer.toString(i) + ". ");
//			i++;
//			uiStringTemp = uiStringTemp.concat(entry.getValue());
//			uiString = uiString.concat(addFormattingAlignLeft(uiStringTemp));
//		}
//		return uiString;
//	}
	
	/**
	 * 
	 * @param threadList
	 * @return
	 */
	private static String mapToUIString3(Map<String, Integer> map) {
		int i = 1;
		String uiString = "";
		String uiStringTemp;
		for (Entry<String, Integer> entry : map.entrySet()){
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(Integer.toString(i) + ". ");
			i++;
			uiStringTemp = uiStringTemp.concat(entry.getKey() + " (" + entry.getValue().toString() + ")");
			uiString = uiString.concat(addFormattingAlignLeft(uiStringTemp));
		}
		return uiString;
	}
	
	/**
	 * Adds borders and appropriate amount of white space depending on the length of the input string.
	 * Uses left text alignment.
	 * @param string
	 * @return formattedString
	 */
	public static String addFormattingAlignLeft(String string) {
		String formattedString = "| ";
		formattedString = formattedString.concat(string);
		for (int i = 0; i < iWINDOWWIDTH - string.length() - 1; i++){
			formattedString = formattedString.concat(" ");
		}
		formattedString = formattedString.concat("|" + sNEW_LINE);
		return formattedString;
	}
	
	/**
	 * Adds borders and appropriate amount of white space depending on the length of the input string.
	 * Uses center text alignment.
	 * @param string
	 * @return formattedString
	 */
	public static String addFormattingAlignCenter(String string) {
		int leftWidth = (iWINDOWWIDTH - string.length())/2;
		int rightWidth = iWINDOWWIDTH - string.length() - leftWidth;
		
		String formattedString = "|";
		for (int i = 0; i < leftWidth; i++){
			formattedString = formattedString.concat(" ");
		}
		formattedString = formattedString.concat(string);
		for (int j = 0; j < rightWidth; j++){
			formattedString = formattedString.concat(" ");
		}
		formattedString = formattedString.concat("|" + sNEW_LINE);
		return formattedString;		
	}
	
	public static String generateBigDivider() {
		String bigDivider = "+";
		for (int i = 0; i < iWINDOWWIDTH; i++){
			bigDivider = bigDivider.concat("=");
		}
		return bigDivider.concat("+" + sNEW_LINE);		
	}
	
	public static String generateSmallDivider() {
		String smallDivider = "+";
		for (int i = 0; i < iWINDOWWIDTH; i++){
			smallDivider = smallDivider.concat("-");
		}
		return smallDivider.concat("+" + sNEW_LINE);		
	}	

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
		clearScreen();
		console.printf(sBIG_DIVIDER + pageName);
		if (messages != null) {
			console.printf(sSMALL_DIVIDER + messages);
		}
		if (info != null) {
			console.printf(sSMALL_DIVIDER + info);
		}
		if (content != null) {
			console.printf(sSMALL_DIVIDER + content);
		}
		if (options != null) {
			console.printf(sSMALL_DIVIDER + options);
		}
		console.printf(sBIG_DIVIDER);
	}
	
	
	
	
	
	// Main.

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