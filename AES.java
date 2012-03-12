import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is the class used for AES encryption and decryption.
 */
public class AES {
    private static Cipher ecipher;
    private static Cipher dcipher;
    private static final String ENCRYPT_ALGORITHM = "PBKDF2WithHmacSHA1" ;
    private static final int iterationCount = 100;
    private static final int keyLength = 128;
    
    /**
     * Constructor for password-based encryption.
     * 
     * @param password
     * @param nonce
     * @param salt
     */
    
	AES(char[] password, byte[] salt){

		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(ENCRYPT_ALGORITHM);
			KeySpec spec = new PBEKeySpec(password, salt, iterationCount, keyLength);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
			
			/* Encrypt the message. */
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			ecipher.init(Cipher.ENCRYPT_MODE, secret);
			
			AlgorithmParameters params = ecipher.getParameters();
			byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
			System.out.println(iv);
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Encypts a message and returns the encrypted version of the message.
	 * @param message
	 * @return
	 */
	public byte[] encrypt(String message){
        try {
			byte[] ciphertext = ecipher.doFinal(message.getBytes("UTF-8"));
			return ciphertext;
        } catch (javax.crypto.BadPaddingException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
        } 
        return null;
	}
	
	public byte[] encrypt(Object o){
        try {
			byte[] ciphertext = ecipher.doFinal((byte []) o);
			return ciphertext;
        } catch (javax.crypto.BadPaddingException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
        }  
        return null;
	}
	
	/**
	 * Decrypts an encrypted message and returns the decrypted version of the message.
	 * @param message
	 * @return
	 */
	public String decrypt(byte[] message){
        try {
			String plaintext = new String(dcipher.doFinal(message), "UTF-8");			
			return plaintext;
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
		
        return null;
	}
	
	 public static void main(String args[]) 
	 { 
		    byte[] salt = new byte[8];
		    SecureRandom random = new SecureRandom();
			random.nextBytes(salt);
			
			String password = "pwned";
			String message = "lolz";
			
			AES obj1 = new AES(password.toCharArray(), salt);
			AES obj2 = new AES(password.toCharArray(), salt);

			byte[] enc = obj1.encrypt(message);
			//System.out.println("Message encrypted: \n====> " + enc);
			
			String tmp = obj2.decrypt(enc);
			System.out.println("Message decrypted: \n====> " + tmp);
			
	 }
	
}
