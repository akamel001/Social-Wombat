/**
 * A class for checking username and password string legality.
 * @author Julia
 */
public class StringLegalityChecker {
	
	// Username Requirements Constant
	static String sUSERNAME_REQUIREMENTS = "[a-zA-Z0-9_]{3,30}"; // only alphanumeric and underscore characters are allowed; must be at least 3 and at most 30 characters long.
	// Password Requirements Constant
	static String sPASSWORD_REQUIREMENTS = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])[0-9a-zA-Z@#$%^&+=]{8,25})"; // can only contain alphanumerics and the listed special characters; 
																														   // must be at least 8 and at most 25 characters long;
																										   				   // must contain at least one uppercase letter;
																										   				   // must contain at least one lowercase letter; 
																										   				   // must contain at least one numeral;
																										   				   // must contain at least one special character.
																										
	/**
	 * Checks that a given string is a legal username string.
	 * @param str
	 * @return true if the username legality requirements are satisfied; false otherwise
	 */
	public static boolean checkIfUsernameStringIsLegal(String str){ // TODO: test
		return (str.matches(sUSERNAME_REQUIREMENTS));
	}

	
	/**
	 * Checks that a given string is a legal password string.
	 * @param str
	 * @return true if the password legality requirements are satisfied; false otherwise
	 */
	public static boolean checkIfPasswordStringIsLegal(String str){ // TODO: test
		return (str.matches(sPASSWORD_REQUIREMENTS));
	}

}
