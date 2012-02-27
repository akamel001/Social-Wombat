import java.io.Console;
import java.util.List;

// TODO: action/error messages at top of each page

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
	static String currentUserName;
	static String currentClassroomName;
	static String currentThreadName;
	
	// Below is a bunch of strings used in the text-based user interface.
	private static final String sNEWLINE = System.getProperty("line.separator");
	private static final String sBIGDIVIDER =			"+==========================================+" + sNEWLINE;
	private static final String sSMALLDIVIDER =			"+------------------------------------------+" + sNEWLINE;
	private static final String sLOGIN =  				"|                 LOG IN                   |" + sNEWLINE;
	private static final String sHOMEPAGE =  			"|                  HOME                    |" + sNEWLINE;
	private static final String sCLASSROOMLISTPAGE =   	"|             CLASSROOM LIST               |" + sNEWLINE;
	private static final String sCLASSROOMPAGE =   		"|                CLASSROOM                 |" + sNEWLINE;
	private static final String sTHREADLISTPAGE =   	"|               THREAD LIST                |" + sNEWLINE;
	private static final String sTHREADPAGE =   		"|                 THREAD                   |" + sNEWLINE;
	private static final String sMEMBERLISTPAGE =   	"|               MEMBER LIST                |" + sNEWLINE;
	private static final String sMEMBERPAGE =  			"|                 MEMBER                   |" + sNEWLINE;
	private static final String sREQUESTLISTPAGE =   	"|               REQUEST LIST               |" + sNEWLINE;
	private static final String sREQUESTPAGE =   		"|                REQUEST                   |" + sNEWLINE;
	
	private static final String eUSERNAMEDOESNOTEXIST =   	"The user name provided does not exist." + sNEWLINE;
	private static final String eCLASSROOMCREATIONERROR = 	"An error occured when creating the classroom." + sNEWLINE;
	private static final String eCLASSROOMREQUSTERROR = 	"An error occured when requesting to join the classroom." + sNEWLINE;
	private static final String eNONVALIDSELECTION = 	    "That is not a valid selection." + sNEWLINE;
	
	private static final String sHOMEPAGEOPTIONS = 		"| 1. select classroom                      |" + sNEWLINE +
														"| 2. create classroom                      |" + sNEWLINE +
														"| 3. request to join classroom             |" + sNEWLINE +
														"| 4. log out                               |" + sNEWLINE;
	
	private static final String sCLASSROOMPAGEOPTIONS = "| 1. select thread                         |" + sNEWLINE +
														"| 2. create thread                         |" + sNEWLINE +
														"| 3. select member                         |" + sNEWLINE +
														"| 4. view requests                         |" + sNEWLINE +
														"| 5. delete classroom/remove self          |" + sNEWLINE +
														"| 6. go back to homepage                   |" + sNEWLINE;

	private static final String sTHREADPAGEOPTIONS = 	"| 1. create new comment                    |" + sNEWLINE +
														"| 2. delete comment                        |" + sNEWLINE +
														"| 3. delete thread                         |" + sNEWLINE +
														"| 4. go back to classroom                  |" + sNEWLINE;
	
	private static final String sMEMBERPAGEOPTIONS = 	"| 1. remove member                         |" + sNEWLINE +
														"| 2. change status                         |" + sNEWLINE +
														"| 3. go back to classroom                  |" + sNEWLINE;
	
	private static final String sREQUESTPAGEOPTIONS = 	"| 1. confirm as a member                   |" + sNEWLINE +
														"| 2. deny membership                       |" + sNEWLINE +
														"| 3. go back to classroom                  |" + sNEWLINE;
	
	/**
	 * This is the login page. The login page requests a user name. If the user name is valid, 
	 * it goes to the home page. Otherwise, it loops back to the login page and displays an 
	 * error message.
	 */
	private static void loginPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sLOGIN + sBIGDIVIDER);		
		String userNameTemp = console.readLine("User Name? ");
		
		if (client.verifyLogin(userNameTemp)){
			currentUserName = userNameTemp;
			homePage();
		} else {
			// TODO: pass in a message saying that login failed.
			currentUserName = null;
			loginPage();
		}
	}
	
	/**
	 * This is the home page. It displays four options:
	 * 1. Select classroom.
	 * 2. Create classroom.
	 * 3. Request to join classroom.
	 * 4. Log out.
	 */
	private static void homePage() {	
		clearScreen();		
		console.printf(sBIGDIVIDER + sHOMEPAGE + sSMALLDIVIDER + sHOMEPAGEOPTIONS + sBIGDIVIDER);			
		int selection = getValidSelectionFromUser(4);
		
		switch (selection) {
		// select classroom
	    case 1: 
	    	classroomListPage();
	        break;
	    // create classroom
	    case 2: 
	    	String classroomNameTemp = console.readLine("Please specify a classroom name: ");
	    	if (client.createClassroom(classroomNameTemp, currentUserName)){
	    		currentClassroomName = classroomNameTemp;
				classroomPage();
			} else {
				homePage();
			}
	        break;
	    // request to join classroom
	    case 3:
	    	String classroomRequestName = console.readLine("Please specify the name of the classroom you'd like to join: ");
	    	if (client.requestClassroom(classroomRequestName, currentUserName)){
				homePage();
			} else {
				homePage();
			}
	        break;
	    // log out
	    case 4:
	    	currentUserName = null;
	        loginPage();
	        break;
	    default:
	        break;
		}
		
	}
	
	/**
	 * 
	 */
	private static void classroomListPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sCLASSROOMLISTPAGE + sSMALLDIVIDER);
		List<String> classroomList = client.getClassroomListForUser(currentUserName);
		console.printf(listToUIString(classroomList) + sBIGDIVIDER);
		int selection = getValidSelectionFromUser(classroomList.size());
		
		currentClassroomName = classroomList.get(selection - 1);		
	    classroomPage();
		
	}
	
	/**
	 * 
	 */
	private static void classroomPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sCLASSROOMPAGE + sSMALLDIVIDER + sCLASSROOMPAGEOPTIONS + sBIGDIVIDER);
		int selection = getValidSelectionFromUser(6);
		
		switch (selection) {
		// select thread
	    case 1:
	        threadListPage();
	        break;
	    // create thread
	    case 2:
	    	String threadNameTemp = console.readLine("Please specify a thread topic: ");
	    	String postContent = console.readLine("Please write your post's content: ");
	    	if (client.createThread(threadNameTemp, postContent, currentUserName)){
	    		currentThreadName = threadNameTemp;
				threadPage();
			} else {
				classroomPage();
			}
	        break;
	    // select member
	    case 3:
	        memberListPage();
	        break;
	    // view requests
	    case 4:
	        requestListPage();
	        break;
	    // delete classroom/remove self
	    case 5:
	        // TODO
	    	homePage();
	        break;
	    // go back to homepage
	    case 6:
	        homePage();
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}

	
	/**
	 * 
	 */
	private static void threadListPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sTHREADLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to thread (temp)                   |" + sNEWLINE + sBIGDIVIDER); // temp
		int selection = getValidSelectionFromUser(1); // TODO: change the max int input
		
		switch (selection) {
	    case 1:
	        threadPage();
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	/**
	 * 
	 */
	private static void threadPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sTHREADPAGE + sSMALLDIVIDER + sTHREADPAGEOPTIONS + sBIGDIVIDER);
		int selection = getValidSelectionFromUser(4);
		
		switch (selection) {
		// create new comment
	    case 1:
	        threadPage();
	        break;
	    // delete comment
	    case 2:
	        threadPage();
	        break;
	    // delete thread
	    case 3:
	        classroomPage();
	        break;
	    // go back to classroom
	    case 4:
	    	classroomPage();
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	/**
	 * 
	 */
	private static void memberListPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sMEMBERLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to member (temp)                   |" + sNEWLINE + sBIGDIVIDER); // temp
		int selection = getValidSelectionFromUser(1); // TODO: change the max int input
		
		switch (selection) {
	    case 1:
	        memberPage();
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	/**
	 * 
	 */
	private static void memberPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sMEMBERPAGE + sSMALLDIVIDER + sMEMBERPAGEOPTIONS + sBIGDIVIDER);		
		int selection = getValidSelectionFromUser(3);
		
		switch (selection) {
		// remove member
	    case 1:
	        memberPage();
	        break;
	    // change status
	    case 2:
	    	 memberPage();
	        break;
	    // go back to classroom
	    case 3:
	        classroomPage();
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	private static void requestListPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sREQUESTLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to request (temp)                   |" + sNEWLINE + sBIGDIVIDER); // temp
		int selection = getValidSelectionFromUser(1); // TODO: change the max int input
		
		switch (selection) {
	    case 1:
	        requestPage();
	        break;
	    default:
	        // TODO
	        break;
		}
		
	}
	
	/**
	 * 
	 */
	private static void requestPage() {
		clearScreen();
		console.printf(sBIGDIVIDER + sREQUESTPAGE + sSMALLDIVIDER + sREQUESTPAGEOPTIONS + sBIGDIVIDER);
		int selection = getValidSelectionFromUser(3);
		
		switch (selection) {
		// confirm as a member
	    case 1:
	        requestListPage();
	        break;
	    // deny membership
	    case 2:
	    	requestListPage();
	        break;
	    // go back to classroom
	    case 3:
	        classroomPage();
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
	
	/**
	 * Clears the the top of the screen depending on the size of your window.
	 */
	public static void clearScreen() {
		System.out.println(((char) 27)+"[2J");
	}
	
	/**
	 * Takes in a list and turns it into a TUI-renderable string
	 */
	public static String listToUIString(List<String> list){
		String currentString;
		
		String uiString = "";		
		for (int i = 1; i <= list.size(); i++){
			uiString = uiString.concat("| " + Integer.toString(i) + ". ");
			currentString = list.get(i-1);
			uiString = uiString.concat(currentString);
			for (int j = 0; j < 38 - currentString.length(); j++){
				uiString = uiString.concat(" ");
			}
			uiString = uiString.concat("|" + sNEWLINE);
		}
		
		return uiString;		
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
		loginPage();
	}

}