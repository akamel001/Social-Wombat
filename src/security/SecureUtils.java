package security;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import storage.Message;

/**
 * A class with a single static 
 * @author chris d
 *
 */
public final class SecureUtils {
	private static byte[] salt = new byte[]
	                          			{0x05, 0x0a, 0x04, 0x08, 
	                          			0x04, 0x01, 0x0e, 0x07, 
	                          			0x09, 0x0f,	0x04, 0x0a, 
	                          			0x0c, 0x0d, 0x0e, 0x09};
	
	/**
	 * Constructor is private to disallow instantiation.
	 */
	private SecureUtils(){}
	
	public static String getMD5Hash(Object o){
		return byteToHexString(getHash_(o, "MD5"));
	}
	
	public static byte[] getSalt() { return salt; }
	
	public static String getSHA_1Hash(Object o){
		return byteToHexString(getHash_(o, "SHA-256"));
	}
	
	private static byte[] getHash_(Object o, String algo){
		
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
		ObjectOutputStream object_out;
		
		try {
			object_out = new ObjectOutputStream(byte_out);
			object_out.writeObject(o);
			byte[] bytes = byte_out.toByteArray();
			
			// Create the MessageDigest object
			// The MessageDigest class provides the functionality to do hash functions on arbitrary data
			MessageDigest md = MessageDigest.getInstance(algo);
			
			// MessageDigest.update() is used to process the data. It can be called multiple times (eg in case the 
			// data cannot fit into a single buffer)
			md.update(bytes);
			
			// MessageDigest.update() finalizes the digest, returning the byte array result of the hash
			return md.digest();
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	

	/**
	 * 
	 * @param password
	 * @param password_hash
	 * @return Returns true if the hash of 
	 */
	@SuppressWarnings("unused")
	private static boolean validatePassword(char[] password, String password_hash){
		char[] temp = Arrays.copyOf(password, password.length+1);
		for (int i=0; i<10; i++){
			temp[password.length-1]=(char)i;
			if (password_hash.equals(getMD5Hash(temp))){
				Arrays.fill(temp, '0');
				return true;
			}
		}
		Arrays.fill(temp, '0');
		return false;
	}
	private static String byteToHexString(byte[] digest){
		String out = new String();
		for (int i=0;i<digest.length;i++) {
		    out += (Integer.toHexString(0xFF & digest[i])); //  + " ";
		    }
		return out;
	}	
	
	@Test
	public static void testChecksum(String args[]){
		Message m = new Message();
		m.setBody((Object)new String("THIS IS THE BODY YO"));
		m.setClassroom_ID("CLASSROOM ID");
		m.setCode(1234567890);
		m.setUserName("USER NAME");

		byte[] test = getHash_(new String("Password"), "MD5");
		String out = SecureUtils.byteToHexString(test);
		System.out.println("Size in bytes: " + test.length);
		System.out.println("Length of string: " + out.length());
		System.out.println("byteToHexString: " + out);
		
		byte[] test1 = getHash_(new String("Password1"), "MD5");
		String out1 = SecureUtils.byteToHexString(test1);
		System.out.println("Size: " + test1.length);
		System.out.println("Length of string: " + out1.length());
		System.out.println("byteToHexString: " + SecureUtils.byteToHexString(test1));
		
		byte[] test2 = getHash_(new String("Password2"), "MD5");
		String out2 = SecureUtils.byteToHexString(test2);
		System.out.println("Size: " + test2.length);
		System.out.println("Length of string: " + out2.length());
		System.out.println("byteToHexString: " + SecureUtils.byteToHexString(test2));		
	}
}
