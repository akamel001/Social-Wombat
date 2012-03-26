/**
 * A class for checking username and password string legality.
 * @author Julia
 */
public class StringLegalityChecker {
	
	// Username Requirements Constant
	static String sUSERNAME_REQUIREMENTS = "[a-zA-Z0-9_]{3,30}"; // only alphanumeric and underscore characters are allowed; must be at least 3 and at most 30 characters long.
	// Password Requirements Constant
	static String sPASSWORD_REQUIREMENTS = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])[0-9a-zA-Z@#$%^&+=]{8,25})"; // can only contain alphanumerics and the listed special characters; 
																														   // must be at least 8 and at most 25 characters long;
																										   				   // must contain at least one uppercase letter;
																										   				   // must contain at least one lowercase letter; 
																										   				   // must contain at least one number;
																										   				   // must contain at least one special character.
																										
	/**
	 * Checks that a given string is a legal username string.
	 * @param str
	 * @return true if the username legality requirements are satisfied; false otherwise
	 */
	public static boolean checkIfUsernameStringIsLegal(String str){
		return (str.matches(sUSERNAME_REQUIREMENTS));
	}

	
	/**
	 * Checks that a given string is a legal password string.
	 * @param str
	 * @return true if the password legality requirements are satisfied; false otherwise
	 */
	public static boolean checkIfPasswordStringIsLegal(char [] pw){
		if (pw.length < 8 || pw.length > 25) { // Check that the password's length is at least 8 and at most 25 characters.
			return false;
		}
		boolean foundUppercase = false;
		boolean foundLowercase = false;
		boolean foundNumber = false;
		boolean foundSpecial = false;
		for (int i = 0; i < pw.length; i++) {
			int asciiVal = (int)pw[i];
			if (asciiVal >= 65 && asciiVal <= 90){ // checks for uppercase
				foundUppercase = foundUppercase || true;
			} else if (asciiVal >= 97 && asciiVal <= 122){ // checks for lowercase
				foundLowercase = foundLowercase || true;
			} else if (asciiVal >= 48 && asciiVal <= 57){ // checks for number
				foundNumber = foundNumber || true;
			} else if (pw[i] == '!' || pw[i] == '@' || pw[i] == '#' || pw[i] == '$' || pw[i] == '%' || pw[i] == '^' || pw[i] == '&' || pw[i] == '*' || pw[i] == '_' || pw[i] == '+' || pw[i] == '-' || pw[i] == '='){ // checks for special character
				foundSpecial = foundSpecial || true;
			} else {
				return false;
			}
		}
		
		if ((foundUppercase && foundLowercase && foundNumber && foundSpecial) == false) { // Check that the password contains 3 of the four types of characters.
			return false;
		}
		return true;
	}

}
