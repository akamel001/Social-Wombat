import java.util.Scanner;

public class Client {
	
	
	public static void register(){
		Scanner inStream = new Scanner(System.in);
		System.out.println("Enter username: ");
		String username = inStream.
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
                System.out.println("Option 2 selected");
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
