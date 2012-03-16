import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

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

// TODO: destruct any sensitive information
public final class AES implements Serializable {

	private static final long serialVersionUID = -7942831124421437491L;
	private byte[] iv = null;
    public int numIterations = 1024;
    private SecretKey secretKey = null;
    private Cipher ecipher = null;
    private Cipher dcipher = null;
    private SecureRandom random =  new SecureRandom();
    
    private byte[] salt = new byte[8];
    
	AES(char[] password){
		try {		
    		random.nextBytes(salt);
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
	        init(iv, salt);		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	AES(SecretKey s_k, byte[] iv, byte[] salt){
		try {
			secretKey = s_k;
	        ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        init(iv, salt);		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	
	protected SecretKey getSecretKey(){
		return this.secretKey;
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

	private void init(byte[] IV, byte[] SALT){
		try {
			
        	iv = IV;
        	salt = SALT;
			dcipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
			ecipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public byte[] getSalt() {
		return salt;
	}	
	public byte[] getIv() {
		return iv;
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
	
	/**
	 * Decrypts a byte[] into an object. <\b>
	 * NOTE: Strings must be cast with "new String decryptObject()" NOT "(String)"
	 * @param o the byte[] to be decrypted
	 * @return Returns the decrypted object.
	 */
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
		  ex.printStackTrace();
	  }
	  catch (ClassNotFoundException ex) {
		  ex.printStackTrace();
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
		  ex.printStackTrace();
	  }
	  return bytes;
	}
    
	/**
	 * Decrypts an encrypted message and returns the decrypted version of the message.
	 * @param message
	 * @return Returns the decrypted byte[]. Returns null on error or if passed byte array equals null.
	 */
	public byte[] decrypt(byte[] message){
		if (message==null)
			return null;
        try {
        	return dcipher.doFinal(message);
        } catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			System.out.println("Bad padding Exception. Probably bad IV");
		} 
        return null;
	}

	// TODO: Get rid of the following. Left temporarily for testing purposes.
//	public static void main(String args[]){
//		String pass_1 = new String("Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..." +
//"There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain...");
//		String plain_text = ("TEXT");
//		
//		
//		AES aes1 = new AES(pass_1.toCharArray());
//		AES aes2 = new AES(aes1.getSecretKey(),aes1.getIv(),aes1.getSalt());
//		
//		Message m = new Message();
//		ArrayList<Integer> tmp = new ArrayList<Integer>();
//		tmp.add(0, 1);
//		tmp.add(1,2);
//		m.setBody(aes1.encrypt(tmp));
//		
//		byte[] enc1 = (byte []) m.getBody();
//		
//		ArrayList<Integer> tmp2 = new ArrayList<Integer>();
//		tmp2 = (ArrayList<Integer>) (aes1.decryptObject(enc1));
//		
//		m.setBody(tmp2);
//		
//		//m = (Message)aes1.decryptObject(cipher);
//		System.out.println(m.getBody());
//		//System.out.println(out);
//	}
	
}
