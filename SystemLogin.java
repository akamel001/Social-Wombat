/**
 * This class handles a system login.
 * @author Julia
 */
public class SystemLogin {
	// This is the hub key encrypted with the system admin password.
	private static byte[] hub_key_enc;
	
	// This is the string "system_admin" encrypted with the system admin password.
	private static byte[] system_admin_enc;
	
	private static byte[]  initialization_vector; // TODO: set this
	
	public static boolean handleSystemLogin(char[] password) {
		if (validateSystemPassword(password)){
			
		}
		return false;
	}
	
	public static boolean changeSystemPassword(char[] oldPassword, char[] newPassword, char[] confirmNewPassword) {
		if (validateSystemPassword(oldPassword)) {
			if (newPassword.equals(confirmNewPassword)) { // TODO: can i do this comparison on char[]? correct equals? i don't think so...
				
			}
			
		}
		return false;		
	}
	
	////////////////////////////////////////////////
	//              HELPER FUNCTIONS              //
	////////////////////////////////////////////////
	private static boolean validateSystemPassword(char[] password) {
//		if (decrypt(password, system_admin_enc).equals("system_admin")){
//			return true;
//		} TODO
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
