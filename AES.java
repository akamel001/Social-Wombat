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
	 * @return Returns a byte array containing the encrypted String. Returns null on failure or 
	 * if passed a null object. 
	 */
	public byte[] encrypt(String message){
		if (message==null)
			return null;
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
			byte[] ciphertext = ecipher.doFinal(toByteArray(o));
			return ciphertext;
        } catch (javax.crypto.BadPaddingException e) {
        	e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
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
	
	public Object decryptObject(byte[] o){
		if (o==null)
			return null;
        try {
			Object obj = toObject(dcipher.doFinal(o));		
        	//String plaintext = new String(dcipher.doFinal(o), "UTF-8");			
			return obj;
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } 
        return null;
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
			String plaintext = new String(dcipher.doFinal(message), "UTF-8");			
			return plaintext;
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
	}
}
