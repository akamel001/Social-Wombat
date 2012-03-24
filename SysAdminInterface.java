import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is the system administrator interface for Social Wombat.
 * 
 */
public class SysAdminInterface {
	
	static Console console;
	static Hub hub;
	static boolean hubIsRunning = false;
	static AES aesHub;
	static SystemLogin systemLogin;
	private static final String sLOG_IN = 					UserInterfaceHelper.addFormattingAlignCenter("SYSTEM LOG IN");
	private static final String sHOME_PAGE = 				UserInterfaceHelper.addFormattingAlignCenter("SYSTEM HOME"); 
	private static final String sSYSTEM_USER_LIST_PAGE = 	UserInterfaceHelper.addFormattingAlignCenter("SYSTEM USER LIST");
	private static final String sSYSTEM_REGISTRATION_PAGE = UserInterfaceHelper.addFormattingAlignCenter("SYSTEM REGISTRATION");
	private static final String sSYSTEM_CHANGE_PASSWORD_PAGE = UserInterfaceHelper.addFormattingAlignCenter("SYSTEM PASSWORD CHANGE");
	private static final String sADD_SERVER_PAGE = 			UserInterfaceHelper.addFormattingAlignCenter("ADD SERVER");
	
	private static final String sDEFAULT_HOME_OPTION = 		UserInterfaceHelper.addFormattingAlignLeft("1. Go back to system home.");
	private static final String sSYSTEM_HOME_PAGE_OPTIONS_HUB_RUNNING =	UserInterfaceHelper.addFormattingAlignLeft("1. View users enrolled in the system.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Register a user in the system.") +
															UserInterfaceHelper.addFormattingAlignLeft("3. View servers.") +
															UserInterfaceHelper.addFormattingAlignLeft("4. Add a server.") +
															UserInterfaceHelper.addFormattingAlignLeft("5. Connect to all servers.") +
															UserInterfaceHelper.addFormattingAlignLeft("6. Shut down the hub.") +
															UserInterfaceHelper.addFormattingAlignLeft("7. Change system password.") +
															UserInterfaceHelper.addFormattingAlignLeft("8. Log out.");	
	private static final String sSYSTEM_HOME_PAGE_OPTIONS_HUB_NOT_RUNNING = UserInterfaceHelper.addFormattingAlignLeft("1. Start the hub.") +
															UserInterfaceHelper.addFormattingAlignLeft("2. Log out.");
	private static final String sREGISTRATION_INSTRUCTIONS = UserInterfaceHelper.addFormattingAlignLeft("Specify a username and password in the prompt below.");
	private static final String sADD_SERVER_INSTRUCTIONS = 	UserInterfaceHelper.addFormattingAlignLeft("Specify the IP address of the server you would like to add below.");
	private static final String sCHANGE_PASSWORD_INSTRUCTIONS = UserInterfaceHelper.addFormattingAlignLeft("Specify your username and old/new passwords below.");
	
	private static final String sHUB_IS_RUNNING = 			UserInterfaceHelper.addFormattingAlignLeft("The hub is running.");
	private static final String sHUB_IS_NOT_RUNNING = 		UserInterfaceHelper.addFormattingAlignLeft("The hub is not running.");
	
	private static final String cNO_USERS = 				UserInterfaceHelper.addFormattingAlignLeft("There are no users enrolled in the system.");
	private static final String cNO_SERVERS = 				UserInterfaceHelper.addFormattingAlignLeft("There are no servers in the system.");
	
	private static final String eNON_VALID_SELECTION = 		"That is not a valid selection." + UserInterfaceHelper.sNEW_LINE;
	
	private static final String eGENERAL_ERROR = 			UserInterfaceHelper.addFormattingAlignLeft("An error has occurred.");
	private static final String eREGISTRATION_ERROR = 		UserInterfaceHelper.addFormattingAlignLeft("An error occured when adding the user to the system.");
	private static final String eLOG_IN_ERROR = 			UserInterfaceHelper.addFormattingAlignLeft("An error occured when logging in to the system.");
	private static final String eLOG_OUT_ERROR = 			UserInterfaceHelper.addFormattingAlignLeft("An error occured when attempting to log out.");
	private static final String ePASSWORD_CHANGE_ERROR =	UserInterfaceHelper.addFormattingAlignLeft("An error occured when changing your password.");
	private static final String eSTART_HUB_ERROR =			UserInterfaceHelper.addFormattingAlignLeft("An error occured when starting up the hub.");
	private static final String eSHUT_DOWN_HUB_ERROR = 		UserInterfaceHelper.addFormattingAlignLeft("An error occured when shutting down the hub.");
	private static final String eADD_SERVER_ERROR = 		UserInterfaceHelper.addFormattingAlignLeft("An error occured when adding the server.");
	
	private static final String mREGISTRATION_SUCCESS = 	UserInterfaceHelper.addFormattingAlignLeft("The user has been successfully added to the sytem.");
	private static final String mLOG_IN_SUCCESS = 			UserInterfaceHelper.addFormattingAlignLeft("You have successfully logged in to system.");
	private static final String mLOG_OUT_SUCCESS = 			UserInterfaceHelper.addFormattingAlignLeft("You have successfully logged out.");
	private static final String mPASSWORD_CHANGE_SUCCESS = 	UserInterfaceHelper.addFormattingAlignLeft("You have successfully changed your password.");
	private static final String mSTART_HUB_SUCCESS =		UserInterfaceHelper.addFormattingAlignLeft("You have successfully start up the hub.");
	private static final String mSHUT_DOWN_HUB_SUCCESS = 	UserInterfaceHelper.addFormattingAlignLeft("You have successfully shut down the hub.");
	private static final String mADD_SERVER_SUCCESS = 		UserInterfaceHelper.addFormattingAlignLeft("You have successfully added a server.");
	private static final String mCONNECT_SERVERS_MESSAGE = 	UserInterfaceHelper.addFormattingAlignLeft("Server connection has been attempted.");
	
	private static void systemLoginPage(String messages) {
		displayPage(sLOG_IN, messages, null, null, null);	
		char[] password = console.readPassword("Password? ");
		
		if (password == null) {
			systemHomePage(eLOG_IN_ERROR);
		}
		
		aesHub = handleSystemLogin(password);
		
		if (aesHub != null) {
			systemHomePage(mLOG_IN_SUCCESS);
		} else {
			systemLoginPage(eLOG_IN_ERROR);
		} 
	}
	
	/**
	 * This is the home page. It displays six options if the hub is running:
	 * 1. View users enrolled in the system.
	 * 2. Register a user in the system.
	 * 3. View servers.
	 * 4. Add a server.
	 * 5. Connect to all servers.
	 * 6. Shut down the hub.
	 * 7. Change system password.
	 * 8. Log out.
	 * 
	 * and two options when the hub is not running:
	 * 1. Start the hub.
	 * 2. Log out.
	 * @param messages
	 */
	private static void systemHomePage(String messages) {	
		if (hubIsRunning == true) {
			displayPage(sHOME_PAGE, messages, sHUB_IS_RUNNING, null, sSYSTEM_HOME_PAGE_OPTIONS_HUB_RUNNING);
			int selection = getValidSelectionFromUser(7);
			
			switch (selection) {
			// View users enrolled in the system.
		    case 1: 
		    	systemUserListPage(null);
		        break;
		    // Register a user in the system.
		    case 2: 
		    	systemRegistrationPage(null);
		        break;
		     // View servers.
		    case 3: 
		    	serverListPage(null);
		        break;
		    // Add a server.
		    case 4: 
		    	addServerPage(null);
		        break;
		    // Connect to all servers.
		    case 5: 
		    	hub.connectServers();
		    	systemHomePage(mCONNECT_SERVERS_MESSAGE);
		        break;
		     // Shut down the hub.
		    case 6:
		    	try {
		    		hub.shutDown();
		    		System.out.println("CALL ING SHUTDOWN");
		    	} catch (Exception e) {
		    		systemHomePage(eSHUT_DOWN_HUB_ERROR);
		    	}
		    	hubIsRunning = false;
		    	systemHomePage(mSHUT_DOWN_HUB_SUCCESS);
		        break;
		    // Change system password.
		    case 7:
		    	systemChangePasswordPage(null);
		        break;
		    // Log out.
		    case 8:
		    	if (handleSystemLogout()) {
					systemLoginPage(mLOG_OUT_SUCCESS);
				} else {
					systemHomePage(eLOG_OUT_ERROR);
				}
		        break;
		    default:
		    	console.printf(eGENERAL_ERROR);
		        break;
			}	
		} else {
			displayPage(sHOME_PAGE, messages, sHUB_IS_NOT_RUNNING, null, sSYSTEM_HOME_PAGE_OPTIONS_HUB_NOT_RUNNING);
			int selection = getValidSelectionFromUser(2);
			
			switch (selection) {
			// Start the hub.
			case 1: 
		    	try {
			        hub = new Hub(aesHub);
			        if (aesHub == null) System.out.println("AES OBJECT IN HUB IS NULL!");
			        //Arrays.fill(aesHub, '0'); // TODO: check this
			        hub.start();
		    	} catch (Exception e) {
		    		systemHomePage(eSTART_HUB_ERROR);
		    	}
		    	hubIsRunning = true;
		        systemHomePage(mSTART_HUB_SUCCESS);
		        break;
			 // Log out.
		    case 2: 
		    	if (handleSystemLogout()) {
					systemLoginPage(mLOG_OUT_SUCCESS);
				} else {
					systemHomePage(eLOG_OUT_ERROR);
				}
		        break;
			}
		}
	}

	/**
	 * Displays a list of enrolled users in the system.
	 * Gives an option to go back to the system home page.
	 * @param messages
	 */
	private static void systemUserListPage(String messages) {
		List<String> userList = getSystemUserList();
		String content = cNO_USERS;		
		if (userList != null) {
			content = listToUIString(userList);
		}
		
		displayPage(sSYSTEM_USER_LIST_PAGE, messages, null, content, sDEFAULT_HOME_OPTION);
		int selection = getValidSelectionFromUser(1);
		
		switch (selection) {
		// Go back to system home.
	    case 1:
	    	systemHomePage(null);
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}
	}

	/**
	 * Allows the system admin to enroll a user in the system by specifying 
	 * that user's username and password.
	 * @param messages
	 */
	private static void systemRegistrationPage(String messages) {
		displayPage(sSYSTEM_REGISTRATION_PAGE, messages, null, null, sREGISTRATION_INSTRUCTIONS);	
		String userNameTemp = console.readLine("User Name? ");
		char[] password = console.readPassword("Password? ");
		
		if (hub.addUser(userNameTemp, password)) {
			systemHomePage(mREGISTRATION_SUCCESS);
		} else {
			systemHomePage(eREGISTRATION_ERROR);
		} 
	}
	
	/**
	 * Displays a list of servers.
	 * Gives an option to go back to the system home page.
	 * @param messages
	 */
	private static void serverListPage(String messages) {
		List<String> serverList = getServerList();
		String content = cNO_SERVERS;		
		if (serverList != null) {
			content = listToUIString(serverList);
		}
		
		displayPage(sDEFAULT_HOME_OPTION, messages, null, content, sDEFAULT_HOME_OPTION);
		int selection = getValidSelectionFromUser(1);
		
		switch (selection) {
		// Go back to system home.
	    case 1:
	    	systemHomePage(null);
	    default:
	    	console.printf(eGENERAL_ERROR);
	        break;
		}
	}

	/**
	 * Allows the system admin the add a server by specifying that server's name.
	 * @param messages
	 */
	private static void addServerPage(String messages) {
		displayPage(sADD_SERVER_PAGE, messages, null, null, sADD_SERVER_INSTRUCTIONS);	
		String serverName = console.readLine("Server IP Address? ");
		char[] serverPassword = console.readPassword("Password? ");
		if (hub.addServer(serverName, serverPassword) != -1) {
			Arrays.fill(serverPassword, '0');
			systemHomePage(mADD_SERVER_SUCCESS);
		} else {
			Arrays.fill(serverPassword, '0');
			systemHomePage(eADD_SERVER_ERROR);
		}
	}

	/**
	 * Allows the system admin to change the password by providing the old password,
	 * the new password, and a confirmation of the new password.
	 * @param messages
	 */
	private static void systemChangePasswordPage(String messages) {
		displayPage(sSYSTEM_CHANGE_PASSWORD_PAGE, messages, null, null, sCHANGE_PASSWORD_INSTRUCTIONS);	
		char[] oldPassword = console.readPassword("Old Password? ");
    	char[] newPassword = console.readPassword("New Password? ");    	
    	char[] confirmNewPassword = console.readPassword("Confirm New Password? ");
    	
    	if (changeSystemPassword(oldPassword, newPassword, confirmNewPassword)) {
			systemHomePage(mPASSWORD_CHANGE_SUCCESS);
		} else {
			systemHomePage(ePASSWORD_CHANGE_ERROR);
		}
	}
	
	////////////////////////////////////////////////
	//               SYSTEM ACTIONS               //
	////////////////////////////////////////////////
	
	/**
	 * Logs in the system administrator.
	 * @param password
	 * @return
	 */
	private static AES handleSystemLogin(char[] password) {
		AES aes = systemLogin.handleSystemLogin(password);
		if (aes != null) {
			Arrays.fill(password, '0');
			return aes;			//TODO: cd: is this the correct aes to be handed to the hub?
		}
		Arrays.fill(password, '0');
		return null;
	}

	/**
	 * Logs out the system administrator.
	 * @return
	 */
	private static boolean handleSystemLogout() {
		return true;
	}
	
	/**
	 * This a method for changing a user's password.
	 * Before changing a password, it must:
	 *   verify that oldPassword matches the system's password;
	 *   check that newPassword1 matches newPassword2.
	 * This method deals with passwords and care must be taken to leave
	 * no trace of any passwords, new or old.
	 * @param oldPassword <- this is the system's initial password
	 * @param newPassword <- this is the desired password for the system
	 * @param confirmNewPassword <- again, this is the desired password for the system (we ask for it again to minimize the effects of user typos)
	 * @return
	 */
	private static boolean changeSystemPassword(char[] oldPassword, char[] newPassword, char[] confirmNewPassword) {
		if (systemLogin.changeSystemPassword(oldPassword, newPassword, confirmNewPassword)) {
			Arrays.fill(oldPassword, '0');
			Arrays.fill(newPassword, '0');
			Arrays.fill(confirmNewPassword, '0');
			return true;
		}
		Arrays.fill(oldPassword, '0');
		Arrays.fill(newPassword, '0');
		Arrays.fill(confirmNewPassword, '0');
		return false;
	}
	
	
	/**
	 * Retrieves the list of enrolled system users.
	 * @return the list of enrolled system users.
	 */
	private static List<String> getSystemUserList() {
		return hub.getUsers();
	}
	
	private static List<String> getServerList() {
		Map<Integer, String> serverMap = hub.getServers();
		if (serverMap != null) {
			return mapToList(serverMap);
		}
		return null;
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
	public static void displayPage(String pageName, String messages, String info, String content, String options) {
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
	 * Takes in a list and turns it into a TUI-renderable string
	 * @param list
	 * @return String
	 */
	public static String listToUIString(List<String> list) {
		String uiString = "";
		String uiStringTemp;
		for (int i = 0; i < list.size(); i++) {
			uiStringTemp = "";
			uiStringTemp = uiStringTemp.concat(list.get(i));
			uiString = uiString.concat(UserInterfaceHelper.addFormattingAlignLeft(uiStringTemp));
		}		
		return uiString;		
	}
	
	/**
	 * Extracts integer keys and string values in a map and 
	 * puts them into a list of strings.
	 * @param map
	 * @return List<String>
	 */
	private static List<String> mapToList(Map<Integer, String> map) {
		List<String> outputList = new ArrayList<String>();
		for (Entry<Integer, String> entry : map.entrySet()){
			outputList.add(entry.getKey().toString() + " " + entry.getValue());
		}
		return outputList;
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
        systemLogin = SystemLogin.systemStartup();
        if (systemLogin == null) {
            System.err.println("No system login object.");
            System.exit(1);
        }
        
		System.out.println("This is the system admin interface for Studious Wombat.");
		systemLoginPage(null);
	}
}