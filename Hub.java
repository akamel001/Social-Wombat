import java.io.IOException;
import java.net.*;


//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	static boolean listening = true;
	static Object datastructures;
	
	public Hub(){
		//fill in constructor
	}

	private static void initializeData(){

	}

	public static void main(String[] args) {
		// Start Up the Hub
		// Initialize Data Structures
		initializeData();
		
		/* 
		 * FROM THIS POINT ON IS CODE THAT WAITS FOR AND RESPONDS TO 
		 * Client REQUESTS
		 * */
		
		ServerSocket serverSocket = null;
		//Create and listen in on a port
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.out.println("Could not listen on port: 4444");
			System.exit(-1);
		}
		
		// Spin until a new message is received and then spawn a 
		// HubSocketHandler thread
		while(listening){
			try {
				Socket clientSocket = serverSocket.accept();
				//Spawn new ServerSocketHandler thread, we assume that the
				//hub has directed this message to the correct Server
				HubSocketHandler newRequest = new HubSocketHandler(clientSocket,datastructures);
				System.out.println("Accepted a connection from: "+ clientSocket.getInetAddress());
				//Starts running the new thread
				newRequest.start(); 
			} catch (IOException e) {
				System.out.println("Accept failed on port: 4444");
				System.exit(-1);
			}
		}
		//Close socket
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}