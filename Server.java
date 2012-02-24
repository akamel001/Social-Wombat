
import java.io.*;
import java.net.*;


public class Server {
	
	// TODO: data for now is any relevant data that needs to be passed to a
	// handler thread
	static Object datastructures;
	
	// Constructor
	public Server() {
		//Any start up constructor code here
		
	}
	
	private static void initializeData(){
		//Initialize any data structures we may need
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
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.out.println("Could not listen on port: 4444");
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
				System.out.println("Accept failed on port: 4444");
				System.exit(-1);
			}

		}
		
	}
}
