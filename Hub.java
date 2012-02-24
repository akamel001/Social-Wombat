import java.io.IOException;
import java.net.*;


//Main controller class. Handles communication between data servers and clients
// The hub that does authentication and forwards other messages to the servers. 
class Hub extends Thread {

	static Object datastructures;
	
	public Hub(){
		//fill in constructor
	}

	private static void initializeData(){

	}

	public static void main(String[] args) {
		// Start Up the Server
		// Initialize Data Structures
		initializeData();
		
		/* 
		 * FROM THIS POINT ON IS CODE THAT WAITS FOR AND RESPONDS TO 
		 * HUB REQUESTS
		 * */
		
		ServerSocket serverSocket = null;
		//Create and listen in on a port
		try {
			serverSocket = new ServerSocket(8020);
		} catch (IOException e) {
			System.out.println("Could not listen on port: 8020");
			System.exit(-1);
		}
		
		// Spin until a new message is received and then spawn a 
		// ServerSocketHandler thread
		while(true){
			try {
				Socket clientSocket = serverSocket.accept();
				//Spawn new ServerSocketHandler thread, we assume that the
				//hub has directed this message to the correct Server
				ServerSocketHandler newRequest = new ServerSocketHandler(clientSocket,datastructures);
				//Starts running the new thread
				newRequest.start(); 
			} 
			catch (IOException e) {
				System.out.println("Accept failed on port: 8020");
				System.exit(-1);
			}

		}
		
	}

}