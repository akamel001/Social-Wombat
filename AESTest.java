import static org.junit.Assert.*;
import java.security.SecureRandom;

import org.junit.Test;


public class AESTest {

	@Test
	public void testAES() {
		
	    byte[] salt = new byte[8];
	    SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		
		String password = "secure";
		
		AES obj1 = new AES(password.toCharArray(), salt);
		AES obj2 = new AES(password.toCharArray(), salt);
		
		Message message = new Message();
		
		message.setClassroom_ID("Class1");
		message.setBody("1337 H4kr");
		
		byte[] enc = obj1.encrypt(message);
		System.out.println("Message encrypted: \n====> " + enc);
		Message decypredMessage = (Message) obj2.decryptObject(enc);
		
		
		assertEquals("1337 H4kr", decypredMessage.getBody());
		System.out.println(decypredMessage.getBody());
	}

}
