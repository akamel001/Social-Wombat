import javax.crypto.SecretKey;

/**
 * This is the class used for AES encryption and decryption.
 * @author Julia
 *
 */
public class AESEncrypter {
	
	/**
	 * Constructor for password-based encryption.
	 * @param password
	 * @param nonce
	 */
	AESEncrypter(char[] password, byte[] nonce){
		
	}
	
	/**
	 * Constructor for key-based encryption.
	 * @param secretKey
	 */
	AESEncrypter(SecretKey secretKey){
		
	}
	
	/**
	 * Encypts a message and returns the encrypted version of the message.
	 * @param message
	 * @return
	 */
	public String encrypt(String message){
		return null;
		
	}
	
	/**
	 * Decrypts an encrypted message and returns the decrypted version of the message.
	 * @param message
	 * @return
	 */
	public String decrypt(String message){
		return null;
		
	}
	
}
