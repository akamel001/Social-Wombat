import java.io.Console;

/**
 * 
 */

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
	
	private static final String sNEW_LINE = System.getProperty("line.separator");
	private static final String sBIGDIVIDER =				"+==========================================+" + sNEW_LINE;
	private static final String sSMALLDIVIDER =			"+------------------------------------------+" + sNEW_LINE;
	private static final String sLOGIN =  				"|                 LOG IN                   |" + sNEW_LINE;
	private static final String sHOMEPAGE =  			"|                  HOME                    |" + sNEW_LINE;
	private static final String sCLASSROOMLISTPAGE =   	"|             CLASSROOM LIST               |" + sNEW_LINE;
	private static final String sCLASSROOMPAGE =   		"|                CLASSROOM                 |" + sNEW_LINE;
	private static final String sTHREADLISTPAGE =   	"|               THREAD LIST                |" + sNEW_LINE;
	private static final String sTHREADPAGE =   		"|                 THREAD                   |" + sNEW_LINE;
	private static final String sMEMBERLISTPAGE =   	"|               MEMBER LIST                |" + sNEW_LINE;
	private static final String sMEMBERPAGE =  			"|                 MEMBER                   |" + sNEW_LINE;
	private static final String sREQUESTLISTPAGE =   	"|               REQUEST LIST               |" + sNEW_LINE;
	private static final String sREQUESTPAGE =   		"|                REQUEST                   |" + sNEW_LINE;
	
	private static final String eUSERNAMEDOESNOTEXIST =   	"The user name provided does not exist." + sNEW_LINE;
	private static final String eCLASSROOMCREATIONERROR = 	"An error occured when creating the classroom." + sNEW_LINE;
	private static final String eCLASSROOMREQUSTERROR = 	"An error occured when requesting to join the classroom." + sNEW_LINE;
	private static final String eNONVALIDSELECTION = 	    "That is not a valid selection." + sNEW_LINE;
	
	private static final String sHOMEPAGEOPTIONS = 		"| 1. select classroom                      |" + sNEW_LINE +
														"| 2. create classroom                      |" + sNEW_LINE +
														"| 3. request to be added to a classroom    |" + sNEW_LINE +
														"| 4. log out                               |" + sNEW_LINE;
	
	private static final String sCLASSROOMPAGEOPTIONS = "| 1. select thread                         |" + sNEW_LINE +
														"| 2. create thread                         |" + sNEW_LINE +
														"| 3. select member                         |" + sNEW_LINE +
														"| 4. view requests                         |" + sNEW_LINE +
														"| 5. delete classroom/remove self          |" + sNEW_LINE +
														"| 6. go back to homepage                   |" + sNEW_LINE;

	private static final String sTHREADPAGEOPTIONS = 	"| 1. create new comment                    |" + sNEW_LINE +
														"| 2. delete comment                        |" + sNEW_LINE +
														"| 3. delete thread                         |" + sNEW_LINE +
														"| 4. go back to classroom                  |" + sNEW_LINE;
	
	private static final String sMEMBERPAGEOPTIONS = 	"| 1. remove member                         |" + sNEW_LINE +
														"| 2. change status                         |" + sNEW_LINE +
														"| 3. go back to classroom                  |" + sNEW_LINE;
	
	private static final String sREQUESTPAGEOPTIONS = 	"| 1. confirm as a member                   |" + sNEW_LINE +
														"| 2. deny membership                       |" + sNEW_LINE +
														"| 3. go back to classroom                  |" + sNEW_LINE;
	
	/**
	 * 
	 */
	private static void loginPage() {
		console.printf(sBIGDIVIDER + sLOGIN + sBIGDIVIDER);
		String userName = console.readLine("User Name? ");
		
		if (verify(userName)){
			homePage();
		} else {
			console.printf(eUSERNAMEDOESNOTEXIST);
			loginPage();
		}
	}
	
	/**
	 * 
	 */
	private static void homePage() {			
		console.printf(sBIGDIVIDER + sHOMEPAGE + sSMALLDIVIDER + sHOMEPAGEOPTIONS + sBIGDIVIDER);			
		int selection = getValidSelectionFromUser(4);
		
		switch (selection) {
		// select classroom
	    case 1: 
	    	classroomListPage();
	        break;
	    // create classroom
	    case 2: 
	    	String classroomName = console.readLine("Please specify a classroom name: ");
	    	if (createClassroom(classroomName)){
				classroomPage();
			} else {
				console.printf(eCLASSROOMCREATIONERROR);
				homePage();
			}
	        break;
	    // join a classroom
	    case 3:
	    	String classroomRequestName = console.readLine("Please specify the name of the classroom you'd like to join: ");
	    	if (requestClassroom(classroomRequestName)){
				homePage();
			} else {
				console.printf(eCLASSROOMREQUSTERROR);
				homePage();
			}
	        break;
	    // log out
	    case 4:
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
		console.printf(sBIGDIVIDER + sCLASSROOMLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to classroom (temp)                |" + sNEW_LINE + sBIGDIVIDER); // temp
		int selection = getValidSelectionFromUser(6);
		
		switch (selection) {
	    case 1:
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
	private static void classroomPage() {
		console.printf(sBIGDIVIDER + sCLASSROOMPAGE + sSMALLDIVIDER + sCLASSROOMPAGEOPTIONS + sBIGDIVIDER);
		int selection = getValidSelectionFromUser(6);
		
		switch (selection) {
		// select thread
	    case 1:
	        threadListPage();
	        break;
	    // create thread
	    case 2:
	    	//String threadTopic = console.readLine("Please specify a thread topic: ");
			threadPage();
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
		console.printf(sBIGDIVIDER + sTHREADLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to thread (temp)                   |" + sNEW_LINE + sBIGDIVIDER); // temp
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
		console.printf(sBIGDIVIDER + sMEMBERLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to member (temp)                   |" + sNEW_LINE + sBIGDIVIDER); // temp
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
		console.printf(sBIGDIVIDER + sREQUESTLISTPAGE + sSMALLDIVIDER);	
		console.printf("| 1. go to request (temp)                   |" + sNEW_LINE + sBIGDIVIDER); // temp
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
	
	
	// Get valid selection from the user.
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
	 * Main function.
	 */
	public static void main(String[] args) {
		
		console = System.console();
        if (console == null) {
            System.err.println("No console.");
            System.exit(1);
        }
		
		System.out.println("Welcome to Studious Wombat!");
		loginPage();
	}
	
	
	
	// DUMMIES
	public static boolean verify(String userName){
		if (userName.equals("false")) {
			return false;
		}
		return true;		
	}
	
	public static boolean createClassroom(String classroomName){
		if (classroomName.equals("false")) {
			return false;
		}
		return true;	
	}
		
	public static boolean requestClassroom(String classroomRequestName){
		if (classroomRequestName.equals("false")) {
			return false;
		}
		return true;		
	}	
	

}