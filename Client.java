
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

	public static String getUserName(){
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter username: ");
		
		String userName = null;
		
		try {
			userName = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return userName;
	}
	
	public static void handleRegister(){
		String uName = getUserName();
		ClientSocketHandler handler = new ClientSocketHandler();
		Message responce = handler.sendReceive(uName, Message.MessageType.Client_Register);
		
		//TODO finish codes
		switch(responce.getCode()){
		case 1:
			//do something
			break;
		case 2:
			//do something
			break;
		default:
			//message compromised or bad message code
		}
	}
	
	public static void handleLogin(){
		String uName = getUserName();
		ClientSocketHandler handler = new ClientSocketHandler();
		Message responce = handler.sendReceive(uName, Message.MessageType.Client_LogIn);
		
		//TODO finish codes
		switch(responce.getCode()){
		case 1:
			//do something
			break;
		case 2:
			//do something
			break;
		default:
			//message compromised or bad message code
		}
	}
	
    public void run() {
		
        
    	Scanner inStream = new Scanner(System.in);
         
        int input;

        System.out.println("============================");
        System.out.println("|   MENU SELECTION         |");
        System.out.println("============================");
        System.out.println("| Options:                 |");
        System.out.println("|        1. Regsiter       |");
        System.out.println("|        2. Log-in         |");
        System.out.println("|        3. Exit           |");
        System.out.println("============================");
        
        input = inStream.nextInt();
        
     
        // Switch construct
        switch (input) {
            case 1:
                handleRegister();
                break;
            case 2:
                handleLogin();
                break;
            case 3:
                System.out.println("Exit selected");
                break;
            default:
                System.out.println("Invalid selection");
                break;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Client instance = new Client();
        instance.run();
    }
}
