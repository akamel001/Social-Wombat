package test;
import hub.Hub;
import security.AES;

/*
 * Tester to isolate failures to different parts of the system
 */

/*
 * Possible tests
 * 
 * 1. test static keyword and writing stuff out to disk with static
 * aesObject encryption
 */
public class SystemTest {
	//Global aesobject
	static AES hubAESObject; 
	static AES clientAESObject;
	static Hub hub;
	
	@SuppressWarnings("unused")
	private static void aesInitialize(){
		hubAESObject = new AES("password".toCharArray());	//default
		clientAESObject = new AES("password".toCharArray());	//default
	}
	
	@SuppressWarnings("unused")
	private static void hubInitialize(){
		hub = new Hub(hubAESObject);
		hub.start();
	}
	
	@SuppressWarnings("unused")
	private static boolean testAES(){
		
		return false;
	}
	
	/*
	 * Add a user 'User' with password 'password'
	 * into the hub and test if they can log in
	 */
	@SuppressWarnings("unused")
	private static void setupUser(){
		System.out.println("Testing adding a user");
		//add the user
		if (hub.addUser("User", "password".toCharArray())){
			System.out.println("User added successfully");
		} else{
			System.out.println("User adding failed");
			System.exit(0);
		}
		//validate that the user was added
		System.out.println("Registered users");
		for (String uname : hub.getUsers()){
			System.out.print(uname);
		}
			
	}
	
	@SuppressWarnings("unused")
	private static boolean testAuthentication(){
		
		return false;
	}
	
	// immutable object passing test
	private static int addFive(int i){
		i = i+5;
		return i;
	}
	
	public static void main(String[] args){
		int i = 5;
		System.out.println(addFive(i));
		System.out.println(i);
		/*
		System.out.println("Initializing AES...");
		aesInitialize();
		System.out.println("Initializing Hub...");
		hubInitialize();
		
		setupUser();
		*/
	}
	
}
