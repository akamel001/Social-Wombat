import java.io.Console;



/**
 * This is the sysadmin interface for Social Wombat.
 * 
 */
public class SysAdminInterface {
	
	static Console console;
	private static final String sREGISTRATION = UserInterfaceHelper.addFormattingAlignCenter("REGISTRATION");
	private static final String eREGISTRATION_ERROR = UserInterfaceHelper.addFormattingAlignLeft("An error occured when adding the user to the system.");
	private static final String eREGISTRATION_SUCCESS = UserInterfaceHelper.addFormattingAlignLeft("The user has been successfully added to the sytem.");
	private static final String eREGISTRATION_INSTRUCTIONS = UserInterfaceHelper.addFormattingAlignLeft("Speicfy a username and password when prompted to add a user to the system.");
	
	private static void registrationPage(String messages) {
		displayPage(sREGISTRATION, messages, null, null, eREGISTRATION_INSTRUCTIONS);	
		String userNameTemp = console.readLine("User Name? ");
		String password = console.readLine("Password? ");
		
		if (addUser(userNameTemp, password)){ // TODO: add condition checking that user/pw combo has been added successfully. Ask Chris
			registrationPage(eREGISTRATION_SUCCESS);
		} else {
			registrationPage(eREGISTRATION_ERROR);
		} 
	}

	////////////////////////////////////////////////
	//              HELPER FUNCTIONS              //
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
	
	/**
	 * Adds a user/password combo to the system.
	 * @param userNameTemp
	 * @param password
	 * @return
	 */
	private static boolean addUser(String userNameTemp, String password) {
		// TODO Auto-generated method stub
		return true;
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
		
		System.out.println("This is the sysadmin interface for Studious Wombat.");
		registrationPage(null);
	}
}