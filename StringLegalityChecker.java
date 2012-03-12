/**
 * Rules for usernames:
 * 	* alphanumeric
 *  * 3-30 characters
 *  
 * Rules for passwords:
 *  * alphanumeric
 *  * 8-25
 */ 

/**
 * @author Julia
 *
 */
public class StringLegalityChecker {
	
	// Username Requirements Constants
	int iMIN_USERNAME_LENGTH = 3;
	int iMAX_USERNAME_LENGTH = 30;
	String sUSERNAME_CHAR_REQUIREMENTS = "[a-zA-Z]*"; // only alphanumeric characters are allowed // TODO: think about these reqs a little more
	
	// Password Requirements Constants
	int iMIN_PASSWORD_LENGTH = 8;
	int iMAX_PASSWORD_LENGTH = 25;
	String sPASSWORD_CHAR_REQUIREMENTS = "[a-zA-Z]*"; // only alphanumeric characters are allowed // TODO: think about these reqs a little more
	
	/**
	 * Checks that a given string is a legal username string.
	 * @param str
	 * @return true if the username legality requirements are satisfied; false otherwise
	 */
	public boolean checkIfUsernameStringIsLegal(String str){ // TODO: test
		if (str.length() >= iMIN_USERNAME_LENGTH && str.length() <= iMAX_USERNAME_LENGTH){
			if (str.matches(sUSERNAME_CHAR_REQUIREMENTS)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks that a given string is a legal password string.
	 * @param str
	 * @return true if the password legality requirements are satisfied; false otherwise
	 */
	public boolean checkIfPasswordStringIsLegal(String str){ // TODO: test
		if (str.length() >= iMIN_PASSWORD_LENGTH && str.length() <= iMAX_PASSWORD_LENGTH){
			if (str.matches(sPASSWORD_CHAR_REQUIREMENTS)) {
				return true;
			}
		}
		return false;
	}
	

}
