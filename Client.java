import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	
	public static void sendReceive(String userName){
		//TODO send to server and wait for receive 
		
	}
	
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
	
	public static void register(){
		
		String uName = getUserName();
		sendReceive(uName);
	}
	
	public static void login(){
		
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
                register();
                break;
            case 2:
                login();
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
