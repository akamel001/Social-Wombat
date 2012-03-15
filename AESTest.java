import static org.junit.Assert.*;
import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.Test;


public class AESTest {

	@Test
	public void testAES() {

		String password = "secure";
		String password2 = "seucre";
		AES obj1 = new AES(password.toCharArray());
		
		AES obj2 = new AES(password.toCharArray(), obj1.getIv(), obj1.getSalt());
		
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

		Message deMessage = (Message) obj1.decryptObject(enc1);
		Message deMessage2 = (Message) obj2.decryptObject(enc1);

		System.out.println("Message encrypted: \n====> " + enc1);
		//System.out.println("Message encrypted: \n====> " + enc2);
		
		assertEquals("1337 H4kr", deMessage.getBody());
		assertEquals("1337 H4kr", deMessage2.getBody());

	}
}
