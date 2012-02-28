import java.io.Console;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// TODO: action/error messages at top of each page
// TODO: pull out commonalities for cleaner code
// TODO: name of page, info (name, classroom), messages, options, question
// TODO: different menus based on privileges
// TODO: make sure the current... are updated as we go
// TODO: currentPendingMember

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
	private static final String sNEWLINE = 				System.getProperty("line.separator");
	private static final String sBIGDIVIDER = 			generateBigDivider();
	private static final String sSMALLDIVIDER =			generateSmallDivider();
	private static final String sLOGIN = 				addFormattingAlignCenter("LOG IN");
	private static final String sHOMEPAGE = 			addFormattingAlignCenter("HOME"); 
	private static final String sCLASSROOMLISTPAGE = 	addFormattingAlignCenter("CLASSROOM LIST"); 
	private static final String sCLASSROOMPAGE = 		addFormattingAlignCenter("CLASSROOM");
	private static final String sTHREADLISTPAGE = 		addFormattingAlignCenter("THREAD LIST");
	private static final String sTHREADPAGE = 			addFormattingAlignCenter("THREAD");
	private static final String sMEMBERLISTPAGE = 		addFormattingAlignCenter("MEMBER LIST");
	private static final String sMEMBERPAGE = 			addFormattingAlignCenter("MEMBER");
	private static final String sREQUESTLISTPAGE = 		addFormattingAlignCenter("REQUEST LIST");
	private static final String sREQUESTPAGE = 			addFormattingAlignCenter("REQUEST");
	
	// TODO: errors and messages
	private static final String eLOGINERROR = 			addFormattingAlignLeft("There was an error in logging in with the provided username.");
	private static final String eCLASSROOMCREATIONERROR=addFormattingAlignLeft("An error occured when creating the classroom.");
	private static final String eNONVALIDSELECTION = 	addFormattingAlignLeft("That is not a valid selection.");
	private static final String eCLASSROOMREQUESTERROR = addFormattingAlignLeft("An error occured when requesting to join a classroom.");
	
	private static final String mCLASSROOMCREATIONSUCCESS = addFormattingAlignLeft("You have successfully created a classroom!");
	private static final String mCLASSROOMREQUESTSUCCESS = 	addFormattingAlignLeft("You have successfully requested to join a classroom!");
	
	private static final String sHOMEPAGEOPTIONS =		addFormattingAlignLeft("1. View your classrooms.") +
														addFormattingAlignLeft("2. Create a classroom.") +
														addFormattingAlignLeft("3. Request to join a classroom.") +
														addFormattingAlignLeft("4. Log out.");
	
	private static final String sCLASSROOMPAGEOPTIONS = addFormattingAlignLeft("1. View discussion board.") +
														addFormattingAlignLeft("2. Create a thread.") +
														addFormattingAlignLeft("3. View members of this classroom.") +
														addFormattingAlignLeft("4. View requests to join this classroom.") +
														addFormattingAlignLeft("5. Delete this classroom./Disjoin this classroom.") +
														addFormattingAlignLeft("6. Go back home.");

	private static final String sTHREADPAGEOPTIONS = 	addFormattingAlignLeft("1. Comment on this thread.") +
														addFormattingAlignLeft("2. Delete a comment.") +
														addFormattingAlignLeft("3. Delete this entire thread") +
														addFormattingAlignLeft("4. Go back to this classroom's main page.");
	
	private static final String sMEMBERPAGEOPTIONS = 	addFormattingAlignLeft("1. Remove this member from this classroom.") +
														addFormattingAlignLeft("2. Change this member's status.") +
														addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	private static final String sREQUESTPAGEOPTIONS = 	addFormattingAlignLeft("1. Confirm as a member.") +
														addFormattingAlignLeft("2. Deny membership.") +
														addFormattingAlignLeft("3. Go back to this classroom's main page.");
	
	/**
	 * This is the login page. The login page requests a user name. If the user name is valid, 
	 * it goes to the home page. Otherwise, it loops back to the login page and displays an 
	 * error message.
	 * @param messages TODO
	 */
	private static void loginPage(String messages) {
		displayPage(sLOGIN, messages, null, null, null);	
		String userNameTemp = console.readLine("User Name? ");
		
		if (client.handleLogin(userNameTemp)){
			currentUserName = userNameTemp;
			homePage(addFormattingAlignLeft("Welcome, " + currentUserName + "!"));
		} else {
			loginPage(eLOGINERROR);
		}
	}
	
	/**
	 * This is the home page. It displays four options:
	 * 1. View your classrooms.
	 * 2. Create a classroom.
	 * 3. Request to join a classroom.
	 * 4. Log out.
	 * @param messages TODO
	 */
	private static void homePage(String messages) {	
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		displayPage(sHOMEPAGE, messages, info, null, sHOMEPAGEOPTIONS);
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
				classroomPage(mCLASSROOMCREATIONSUCCESS);
			} else {
				homePage(eCLASSROOMCREATIONERROR);
			}
	        break;
	    // Request to join a classroom.
	    case 3:
	    	String classroomRequestName = console.readLine("Please specify the name of the classroom you'd like to join: ");
	    	if (client.requestToJoinClassroom(classroomRequestName, currentUserName)){
				homePage(mCLASSROOMREQUESTSUCCESS); // TODO: bookmark
			} else {
				homePage(eCLASSROOMREQUESTERROR);
			}
	        break;
	    // Log out.
	    case 4:
	    	currentUserName = null;
	        loginPage(null);
	        break;
	    default:
	        break;
		}
		
	}
	
	/**
	 * @param messages TODO
	 * 
	 */
	private static void classroomListPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		Map<String, Integer> classroomMap = client.getClassroomMapForUser(currentUserName);		
		displayPage(sCLASSROOMLISTPAGE, messages, info, null, mapToUIString(classroomMap));

		int selection = getValidSelectionFromUser(classroomMap.size());
		
		//currentClassroomName = classroomMap.get(selection - 1); TODO		
	    classroomPage(null);		
	}

	/**
	 * @param messages TODO
	 * 
	 */
	private static void classroomPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		displayPage(sCLASSROOMPAGE, messages, info, null, sCLASSROOMPAGEOPTIONS);
		
		int selection = getValidSelectionFromUser(6);
		
		switch (selection) {
		// select thread
	    case 1:
	        threadListPage(null);
	        break;
	    // create thread
	    case 2:
	    	String threadNameTemp = console.readLine("Please specify a thread topic: ");
	    	String postContent = console.readLine("Please write your post's content: ");
	    	if (client.createThread(threadNameTemp, postContent, currentUserName, currentClassroomName)){
	    		currentThreadName = threadNameTemp;
				threadPage(null);
			} else {
				classroomPage(messages);
			}
	        break;
	    // select member
	    case 3:
	        memberListPage(null);
	        break;
	    // view requests
	    case 4:
	        requestListPage(null);
	        break;
	    // delete classroom/remove self
	    case 5:
	    	client.deleteClassroom(currentClassroomName, currentUserName);
	    	// TODO: client.disjoinClassroom(currentClassroomName, currentUserName);
	    	homePage(null);
	        break;
	    // go back to homepage
	    case 6:
	        homePage(null);
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}

	
	/**
	 * @param messages TODO
	 * 
	 */
	private static void threadListPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		Map<Integer, String> threadMap = client.getThreadListForClassroom(currentClassroomName, currentUserName);		
		displayPage(sTHREADLISTPAGE, messages, info, null, mapToUIString2(threadMap));
		
		int selection = getValidSelectionFromUser(threadMap.size());
		
		currentThreadName = threadMap.get(selection - 1); // TODO: only works if thread names are unique.
	    threadPage(null);		
	}

	/**
	 * @param messages TODO
	 * 
	 */
	private static void threadPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Current thread: " + currentThreadName + "."));
		displayPage(sTHREADPAGE, messages, info, null, sTHREADPAGEOPTIONS); // TODO: get thread content
		
		int selection = getValidSelectionFromUser(4);
		
		switch (selection) {
		// create new comment
	    case 1:
	    	String commentContent = console.readLine("Please write your comment: ");
	    	if (client.createComment(commentContent, currentThreadID, currentClassroomName, currentUserName)){
				threadPage(messages);
			} else {
				classroomPage(null);
			}
	        break;
	    // delete comment
	    case 2:
	        threadPage(messages);
	        break;
	    // delete thread
	    case 3:
	        classroomPage(null);
	        break;
	    // go back to classroom
	    case 4:
	    	currentThreadName = null;
	    	classroomPage(null);
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	/**
	 * @param messages TODO
	 * 
	 */
	private static void memberListPage(String messages) {		
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		Map<String, Integer> memberMap = client.getMemberListForClassroom(currentClassroomName, currentUserName);	
		displayPage(sMEMBERLISTPAGE, messages, info, null, mapToUIString(memberMap));

		int selection = getValidSelectionFromUser(memberMap.size());
		
		//currentMemberName = memberMap.get(key); TODO
	    memberPage(null);
	}
	
	/**
	 * @param messages TODO
	 * 
	 */
	private static void memberPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Currently viewing member: " + currentMemberName + "."));
		displayPage(sMEMBERPAGE, messages, info, null, sMEMBERPAGEOPTIONS); //TODO: member content such as status
		
		int selection = getValidSelectionFromUser(3);
		
		switch (selection) {
		// remove member
	    case 1:
	    	client.removeMember(currentMemberName, currentClassroomName, currentUserName);
	        memberPage(messages);
	        break;
	    // change status
	    case 2:
	    	client.changeStatus(currentMemberName, currentUserName, currentClassroomName);
	    	memberPage(messages);
	        break;
	    // go back to classroom
	    case 3:
	    	currentMemberName = null;
	        classroomPage(null);
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	/**
	 * 
	 * @param messages
	 */
	private static void requestListPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		List<String> requestList = client.getRequestListForClassroom(currentClassroomName, currentUserName);
		displayPage(sREQUESTLISTPAGE, messages, info, null, listToUIString(requestList));
		
		int selection = getValidSelectionFromUser(requestList.size());
		
		currentMemberName = requestList.get(selection - 1);
	    requestPage(null);
		
	}
	
	/**
	 * @param messages TODO
	 * 
	 */
	private static void requestPage(String messages) {
		String info = addFormattingAlignLeft("Logged in as " + currentUserName + ".");
		info = info.concat(addFormattingAlignLeft("Current classroom: " + currentClassroomName + "."));
		info = info.concat(addFormattingAlignLeft("Currently viewing request from member: " + currentMemberName + "."));
		displayPage(sREQUESTPAGE, messages, info, null, sREQUESTPAGEOPTIONS);		

		int selection = getValidSelectionFromUser(3);
		
		switch (selection) {
		// confirm as a member
	    case 1:
	    	client.confirmAsMemberOfClassroom(currentMemberName, currentClassroomName, currentUserName);
	    	currentMemberName = null;
	        requestListPage(null);
	        break;
	    // deny membership
	    case 2:
	    	client.denyMembershipToClassroom(currentMemberName, currentClassroomName, currentUserName);
	    	currentMemberName = null;
	    	requestListPage(null);
	        break;
	    // go back to classroom
	    case 3:
	    	currentMemberName = null;
	        classroomPage(null);
	        break;
	    default:
	        // TODO
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
					console.printf(eNONVALIDSELECTION);
				}
			} catch (NumberFormatException e) {
				console.printf(eNONVALIDSELECTION);
				validSelection = false;
			}
		}
		return selection;
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
	
	/**
	 * 
	 * @param map
	 * @return uiString
	 */
	private static String mapToUIString(Map<String, Integer> map) {
		// TODO depends on sorting
		int i = 1;
		String uiString = "";
		String uiStringTemp;
		for (String key : map.keySet()){
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(Integer.toString(i) + ". ");
			i++;
			uiStringTemp = uiStringTemp.concat(key);
			uiString = uiString.concat(addFormattingAlignLeft(uiStringTemp));
		}
		return uiString;
	}
	
	/**
	 * 
	 * @param threadList
	 * @return
	 */
	private static String mapToUIString2(Map<Integer, String> map) {
		// TODO Auto-generated method stub
		int i = 1;
		String uiString = "";
		String uiStringTemp;
		for (Entry<Integer, String> entry : map.entrySet()){
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(Integer.toString(i) + ". ");
			i++;
			uiStringTemp = uiStringTemp.concat(entry.getValue());
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
		formattedString = formattedString.concat("|" + sNEWLINE);
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
		formattedString = formattedString.concat("|" + sNEWLINE);
		return formattedString;		
	}
	
	public static String generateBigDivider() {
		String bigDivider = "+";
		for (int i = 0; i < iWINDOWWIDTH; i++){
			bigDivider = bigDivider.concat("=");
		}
		return bigDivider.concat("+" + sNEWLINE);		
	}
	
	public static String generateSmallDivider() {
		String smallDivider = "+";
		for (int i = 0; i < iWINDOWWIDTH; i++){
			smallDivider = smallDivider.concat("-");
		}
		return smallDivider.concat("+" + sNEWLINE);		
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
		console.printf(sBIGDIVIDER + pageName);
		if (messages != null) {
			console.printf(sSMALLDIVIDER + messages);
		}
		if (info != null) {
			console.printf(sSMALLDIVIDER + info);
		}
		if (content != null) {
			console.printf(sSMALLDIVIDER + content);
		}
		if (options != null) {
			console.printf(sSMALLDIVIDER + options);
		}
		console.printf(sBIGDIVIDER);
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