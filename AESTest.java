import org.junit.Test;


public class AESTest {

	@Test
	public void testAES() {

		String password = "secure";

		AES obj1 = new AES(password.toCharArray());
		
//		AES obj2 = new AES(password.toCharArray(), obj1.getIv(), obj1.getSalt());
		
		Message message = new Message();
		
		message.setClassroom_ID("Class1");
		message.setBody(obj1.encrypt("Hello world"));
		
		
		System.out.println(message.getBody());
		
		message.setBody(obj1.decryptObject((byte []) message.getBody()));
		
		//byte[] enc1 = (byte []) message.getBody();
		
		System.out.println(obj1.decryptObject((byte []) message.getBody()));
//		byte[] enc2 = obj2.encrypt(message);
//
//		assert(Arrays.equals(enc1, enc2));
//		
//		Message deMessage = (Message) obj1.decryptObject(enc1);
//		Message deMessage2 = (Message) obj2.decryptObject(enc1);
//
//		assertEquals("1337 H4kr", deMessage.getBody());
//		assertEquals("Class1", deMessage.getClassroom_ID());
//		
//		assertEquals("1337 H4kr", deMessage2.getBody());
//		assertEquals("Class1", deMessage2.getClassroom_ID());
	}
	
	public void testAES2() {
		
	}
}
