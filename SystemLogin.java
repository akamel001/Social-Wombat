/**
 * This class handles a system login.
 * @author Julia
 */

// TODO: black out sensitive variables

public class SystemLogin {
	// This is the hub key encrypted with the system admin password.
	private static byte[] hub_key_enc;
	
	// This is the string "system_admin" encrypted with the system admin password.
	private static byte[] system_admin_enc;

	private static byte[]  initialization_vector; // TODO: set this
	
	/**
	 * Handles the system login.
	 * @param password
	 * @return the hub AES object.
	 */
	public static AES handleSystemLogin(char[] password) {
		AES aes = validateSystemPassword(password);
		if (aes != null) {
			return new AES(aes.decrypt(hub_key_enc).toCharArray()); // TODO: use different constructor
		} 
		return null;
	}
	
	/**
	 * Changes the system password from oldPassword to newPassword.
	 * Changing the password basically means the 'system admin' string
	 * and hub key get re-encrypted using the new password.
	 * @param oldPassword
	 * @param newPassword
	 * @param confirmNewPassword
	 * @return true if the password has been changed, false otherwise
	 */
	public static boolean changeSystemPassword(char[] oldPassword, char[] newPassword, char[] confirmNewPassword) {
		AES aes = validateSystemPassword(oldPassword);
		if (aes != null) {
			if (newPassword.equals(confirmNewPassword)) { // TODO: can i do this comparison on char[]? correct equals? i don't think so...
				AES aesNew = new AES(newPassword); // TODO: use different constructor
				aesNew.encrypt(aes.decrypt(system_admin_enc)); // re-encrypt the "system admin" string
				aesNew.encrypt(aes.decrypt(hub_key_enc));	   // and hub key				
			}			
		}
		return false;		
	}
	
	////////////////////////////////////////////////
	//              HELPER FUNCTIONS              //
	////////////////////////////////////////////////
	/**
	 * Validates a system password and returns the AES object 
	 * created from the supplied password.
	 * @param password
	 * @return the hub AES object
	 */
	private static AES validateSystemPassword(char[] password) {
		AES aes = new AES(password); // TODO: use different constructor
		if (aes.decrypt(system_admin_enc).equals("system_admin")) {
			return aes;
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
