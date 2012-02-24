import java.io.IOException;
import java.net.*;

//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	private static boolean listening = true;
	private static Object datastructures;
	
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
		
		ServerSocket hubSocket = null;
		//Create and listen in on a port
		try {
			hubSocket = new ServerSocket(CLIENT_SOCKET);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + CLIENT_SOCKET);
			System.exit(-1);
		}
		
		// Spin until a new message is received and then spawn a 
		// HubSocketHandler thread
		while(listening){
			try {
				Socket client = hubSocket.accept();
				//Spawn new ServerSocketHandler thread, we assume that the
				//hub has directed this message to the correct Server
				HubSocketHandler newRequest = new HubSocketHandler(client,datastructures);
				System.out.println("Accepted a connection from: "+ client.getInetAddress());
				//Starts running the new thread
				newRequest.start(); 
			} catch (IOException e) {
				System.out.println("Accept failed on port: " + CLIENT_SOCKET);
				System.exit(-1);
			}
		}
		//Close socket
		try {
			hubSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}