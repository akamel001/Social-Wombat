package hub;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKey;

import security.AES;
import security.SecureUtils;


/**
 * This class handles a system login.
 * @author Julia
 */

public final class SystemLogin implements Serializable {
	private static final long serialVersionUID = 732364698179994154L;
	// This is the hub key encrypted with the system admin password.
	private byte[] hub_key_enc;
	private byte[] hub_init_vector;
	private byte[] hub_salt;
	
	// This is the string "system_admin" encrypted with the system admin password.
	private String system_admin_hashed_pw;
	private byte[] system_admin_init_vector;
	private byte[] system_admin_salt;
	
	private static final String allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*_+-=";
	
	/**
	 * Handles the system login.
	 * @param password
	 * @return the hub AES object.
	 */
	public AES handleSystemLogin(char[] password) {
		AES aes = validateSystemPassword(password);
		if (aes != null) {			
			try {
				SecretKey hub_key = (SecretKey)aes.decryptObject(hub_key_enc);
				if(hub_key == null){
					System.out.println("***** HUB KEY IS NULL! ***** \n Sysetm exiting");
					System.exit(-1);
				}
				return new AES(hub_key, hub_init_vector, hub_salt);
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	public boolean changeSystemPassword(char[] oldPassword, char[] newPassword, char[] confirmNewPassword) {
		AES aes = validateSystemPassword(oldPassword);
		if (aes != null) {
			if (Arrays.equals(newPassword,confirmNewPassword)) {
				char[] salt = SecureUtils.getSalt();
				
				char[] temp_pass = new char[salt.length + newPassword.length];
				System.arraycopy(salt, 0, temp_pass, 0, salt.length);
				System.arraycopy(newPassword, 0, temp_pass, salt.length, newPassword.length);
				//Arrays.fill(newPassword, '0');

				// Create hash, then zero-out temp_pass
				system_admin_hashed_pw = SecureUtils.getSHA_1Hash(temp_pass);
				Arrays.fill(temp_pass, '0');
				
				AES aesNew = new AES(newPassword);
				system_admin_init_vector = aesNew.getIv();
				system_admin_salt = aesNew.getSalt();
				
				SecretKey temp = (SecretKey)aes.decryptObject(hub_key_enc);
				hub_key_enc = aesNew.encrypt(temp);
				
				writeToDisk(this, "system_startup");
				return true;
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
	private AES validateSystemPassword(char[] password) {
		if (password==null)
			return null;
		
		char[] salt = SecureUtils.getSalt();
		
		char[] temp_pass = new char[salt.length + password.length];
		System.arraycopy(salt, 0, temp_pass, 0, salt.length);
		System.arraycopy(password, 0, temp_pass, salt.length, password.length);
		//Arrays.fill(password, '0');

		// Create hash, then zero-out temp_pass
		String tempHashedPassword = SecureUtils.getSHA_1Hash(temp_pass);
		Arrays.fill(temp_pass, '0');
		
		if (tempHashedPassword.equals(system_admin_hashed_pw)){
			return new AES(password, system_admin_init_vector, system_admin_salt);
		}
		
		return null;
	}
	
	/**
	 * Creates an array of random chars from the supplied allowedCharacters.
	 * The size of this array is specified by length.
	 * @param allowedCharacters
	 * @param length
	 * @return
	 */
	public static char[] generateString(String allowedCharacters, int length) {
		SecureRandom rng = new SecureRandom();
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++) {
	        text[i] = allowedCharacters.charAt(rng.nextInt(allowedCharacters.length()));
	        
	    }
	    return text;
	}
	
	/**
	 * Reads a given object in from the specified file.
	 * @param name
	 * @return
	 */
	private static Object readFromDisk(String name){
		Object o = null;
		try {
		    FileInputStream fin = new FileInputStream(name);
		    ObjectInputStream ois = new ObjectInputStream(fin);
		    o = (SystemLogin) ois.readObject();
		    
		    ois.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}

	/**
	 * Writes an object o to a file with the specified name.
	 * @param o
	 * @param name
	 */
	private static void writeToDisk(Object o, String name){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject((SystemLogin)o);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Checks if a file with the given name exists.
	 * @param file
	 * @return
	 */
	private static boolean fileExists(String filename){
		return new File(filename).exists();
	}

	/**
	 * Either starts the system from scratch or gets the old object.
	 */
	public static SystemLogin systemStartup() {
		SystemLogin systemStartup;
		if (!fileExists("system_startup")) {
			// System admin password
			systemStartup = new SystemLogin();
			char[] systemAdminPassword = "system admin".toCharArray();
			char[] salt = SecureUtils.getSalt();
			
			char[] temp_pass = new char[salt.length + systemAdminPassword.length];
			System.arraycopy(salt, 0, temp_pass, 0, salt.length);
			System.arraycopy(systemAdminPassword, 0, temp_pass, salt.length, systemAdminPassword.length);

			// Create hash of system, then zero-out temp_pass
			systemStartup.system_admin_hashed_pw = SecureUtils.getSHA_1Hash(temp_pass);
			Arrays.fill(temp_pass, '0');		
			
			// Encrypt Hub key with sysadmin pass
			AES systemAdminAES = new AES(systemAdminPassword);
			systemStartup.system_admin_init_vector = systemAdminAES.getIv();
			systemStartup.system_admin_salt = systemAdminAES.getSalt();

			// Hub
			char[] hubPassword = generateString(allowedChars, 64);
			AES hubAES = new AES(hubPassword);
			systemStartup.hub_key_enc = systemAdminAES.encrypt(hubAES.getSecretKey());
			systemStartup.hub_init_vector = hubAES.getIv();
			systemStartup.hub_salt = hubAES.getSalt();
			
			Arrays.fill(hubPassword, '0');
			Arrays.fill(systemAdminPassword, '0');
			
			writeToDisk(systemStartup, "system_startup");			
		} else {
			systemStartup = (SystemLogin)readFromDisk("system_startup");
		}
		return systemStartup;
	}

}
