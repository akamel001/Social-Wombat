package security;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import storage.Message;

/**
 * A class with a single static 
 * @author chris d
 *
 */
public final class CheckSum {

	/**
	 * Constructor is private to disallow instantiation.
	 */
	private CheckSum(){}
	
	/**
	 * @deprecated
	 * Give the checksum for a passed object
	 * @param o The object to be checksumed. NOTE: This object must be completely serializable!!!!
	 * @return Returns the checksum on success, -1 if the object is not serializable.
	 */
	public static long getChecksum(Object o){
		long out = -1;
		
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
		ObjectOutputStream object_out;
		
		try {
			object_out = new ObjectOutputStream(byte_out);
			object_out.writeObject(o);
			byte[] bytes = byte_out.toByteArray();
			Checksum checksum = new CRC32();
			checksum.update(bytes,0,bytes.length);
			out = checksum.getValue();
		} catch (IOException e) {
			//e.printStackTrace();
			return -1;
		}
		return out;
	}
	
	public static String getMD5Checksum(Object o){
		return byteToHexString(getMD5Checksum_(o));
	}
	
	private static byte[] getMD5Checksum_(Object o){
		
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream();
		ObjectOutputStream object_out;
		
		try {
			object_out = new ObjectOutputStream(byte_out);
			object_out.writeObject(o);
			byte[] bytes = byte_out.toByteArray();
			
			// Create the MessageDigest object
			// The MessageDigest class provides the functionality to do hash functions on arbitrary data
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			// MessageDigest.update() is used to process the data. It can be called multiple times (eg in case the 
			// data cannot fit into a single buffer)
			md.update(bytes);
			
			// MessageDigest.update() finalizes the digest, returning the byte array result of the hash
			return md.digest();
			
		} catch (IOException e) {
			//e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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

		byte[] test = getMD5Checksum_(new String("Password"));
		System.out.println("Size: " + test.length);
		System.out.println("byteToHexString: " + CheckSum.byteToHexString(test));
		
		byte[] test1 = getMD5Checksum_(new String("Password1"));
		System.out.println("Size: " + test1.length);
		System.out.println("byteToHexString: " + CheckSum.byteToHexString(test1));
		
		byte[] test2 = getMD5Checksum_(new String("Password2"));
		System.out.println("Size: " + test2.length);
		System.out.println("byteToHexString: " + CheckSum.byteToHexString(test2));		
	}
}
