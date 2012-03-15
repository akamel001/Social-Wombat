import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
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

	
	private byte[] iv= null;
    public int numIterations = 1024;
    private SecretKey secretKey = null;
    private Cipher ecipher = null;
    private Cipher dcipher = null;
    private SecureRandom random =  new SecureRandom();
    
    //private byte[] salt = new byte[8];
    
	AES(char[] password, byte[] salt){
		try {		
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        KeySpec spec = new PBEKeySpec(password, salt, 1024, 128);
	        secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        init();	
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	AES(char[] password, byte[] iv, byte[] salt){
		try {
	        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	        KeySpec spec = new PBEKeySpec(password, salt, 1024, 128);
	        secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        init(iv);		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	private void init(){
		try {
			ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
	    	iv = ecipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
        	dcipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidParameterSpecException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}
	
	private void init(byte[] IV){
		try {
			ecipher.init(Cipher.ENCRYPT_MODE, secretKey);
        	iv = IV;
			dcipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Encypts a message and returns the encrypted version of the message.
	 * @param message
	 * @return Returns a byte array containing the encrypted String. Returns null on failure or 
	 * if passed a null object. 
	 */
	public byte[] encrypt(String message){
		if (message==null)
			return null;
        try {
        	return ecipher.doFinal(message.getBytes("UTF-8"));
        } catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("Bad padding Exception. Probably bad IV");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
        
        return null;
	}
	
	/**
	 * 
	 * @param o
	 * @return Returns a byte array containing the encrypted Object. Returns null on failure or 
	 * if passed a null object.
	 */
	public byte[] encrypt(Object o){
		if (o==null)
			return null;
        try {
			byte[] cipheredObject = ecipher.doFinal(toByteArray(o));
			return cipheredObject;
        } catch (javax.crypto.BadPaddingException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
        }  
        return null;
	}
	
	public Object decryptObject(byte[] o){
		if (o==null)
			return null;
        try {
			Object obj = toObject(dcipher.doFinal(o));		
			return obj;
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } 
        return null;
	}
	
	public Object toObject (byte[] bytes)
	{
	  Object obj = null;
	  try {
	    ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
	    ObjectInputStream ois = new ObjectInputStream (bis);
	    obj = ois.readObject();
	  }
	  catch (IOException ex) {
	    //TODO: Handle the exception
	  }
	  catch (ClassNotFoundException ex) {
	    //TODO: Handle the exception
	  }
	  return obj;
	}
	
	public byte[] toByteArray (Object obj)
	{
	  byte[] bytes = null;
	  ByteArrayOutputStream bos = new ByteArrayOutputStream();
	  try {
	    ObjectOutputStream oos = new ObjectOutputStream(bos); 
	    oos.writeObject(obj);
	    oos.flush(); 
	    oos.close(); 
	    bos.close();
	    bytes = bos.toByteArray ();
	  }
	  catch (IOException ex) {
	    //TODO: Handle the exception
	  }
	  return bytes;
	}

	
	/**
	 * Decrypts an encrypted message and returns the decrypted version of the message.
	 * @param message
	 * @return Returns the decrypted String. Returns null on error or if passed byte array equals null.
	 */
	public String decrypt(byte[] message){
		if (message==null)
			return null;
        try {
        	return new String(dcipher.doFinal(message), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	System.out.println("Unsupported Encodeing.");
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("Bad padding Exception. Probably bad IV");
		} 
        return null;
	}
	
	public static void main(String[] args) {
	    byte[] salt = new byte[8];
	    byte[] salt2 = new byte[8];
	    
	    SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		random.nextBytes(salt2);

		String password = "secure";
		String password2 = "seucre";
		AES obj1 = new AES(password.toCharArray(), salt);
		
		AES obj2 = new AES(password.toCharArray(), obj1.getIv(), salt);
		
		Message message = new Message();
		
		message.setClassroom_ID("Class1");
		message.setBody("1337 H4kr");
		
		//String message = "I am 1337";
		//String message2 = "lolcatz";
		
		byte[] enc1 = obj1.encrypt(message);
		byte[] enc2 = obj2.encrypt(message);
		
		if(Arrays.equals(enc1, enc2))
			System.out.println("EQUALLL!!!");
		//byte[] enc2 = obj1.encrypt(message2);
		//byte[] enc2 = obj2.encrypt(message);

		//obj2.setIv(obj1.getIv());
		Message deMessage = (Message) obj1.decryptObject(enc1);
		Message deMessage2 = (Message) obj1.decryptObject(enc2);

		System.out.println("Message encrypted: \n====> " + enc1);
		//System.out.println("Message encrypted: \n====> " + enc2);

		System.out.println(deMessage.getBody());
		System.out.println(deMessage2.getBody());

		//System.out.println(obj2.decrypt(enc1));
    }
	
	public byte[] getIv() {
		return iv;
	}
}
