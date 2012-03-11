import java.security.SecureRandom;
import javax.crypto.*;
import java.math.*;
import java.security.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public class CInit {
	private String user_name;
	private byte[] salt;
	private byte[] initialization_vector;
	
	@SuppressWarnings("unused")
	private CInit(){}
	
	public CInit(String u){
		user_name = u;
		
		// generate the salt
		salt = new byte[8];
		SecureRandom rand = new SecureRandom();
		rand.nextBytes(salt);
		
		// generate the initialization vector
		initialization_vector = new byte[16];
		try{
		AlgorithmParameters params = Cipher.getInstance("AES/CBC/PKCS5Padding").getParameters();
		if (params == null)
			System.out.println("PARAMS IS NULL");
		IvParameterSpec ips = params.getParameterSpec(IvParameterSpec.class);			    
		initialization_vector = ips.getIV(); 
		}catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch (NoSuchPaddingException e){
			e.printStackTrace();
		}catch( InvalidParameterSpecException e){
			e.printStackTrace();
		}
	}
	
	public String getUserName(){
		return user_name;
	}
	
	public byte[] getSalt(){
		return salt;
	}
	
	public byte[] getIV(){
		return initialization_vector;
	}
}
