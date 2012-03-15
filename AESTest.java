import static org.junit.Assert.*;
import java.security.SecureRandom;
import java.util.Arrays;

import org.junit.Test;


public class AESTest {

	@Test
	public void testAES() {

		String password = "secure";

		AES obj1 = new AES(password.toCharArray());
		
		AES obj2 = new AES(password.toCharArray(), obj1.getIv(), obj1.getSalt());
		
		Message message = new Message();
		
		message.setClassroom_ID("Class1");
		message.setBody(obj1.encrypt("1337 H4kr"));
		
		byte[] enc1 = obj2.encrypt(message);
		byte[] enc2 = obj2.encrypt(message);

		if(Arrays.equals(enc1, enc2))
			System.out.println("Encrypting is equal!!");

		Message deMessage = (Message) obj1.decryptObject(enc1);
		Message deMessage2 = (Message) obj2.decryptObject(enc1);

		System.out.println(obj1.decrypt((byte [])deMessage.getBody()));

//		assertEquals("1337 H4kr", obj1.decryptObject((byte[]) deMessage.getBody()));
//		assertEquals("Class1", deMessage.getClassroom_ID());
//		
//		assertEquals("1337 H4kr", deMessage2.getBody());
//		assertEquals("Class1", deMessage2.getClassroom_ID());

		
	}
}
