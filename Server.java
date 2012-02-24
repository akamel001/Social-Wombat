import java.io.*;
import java.net.*;

public class Server {
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	private static boolean listening = true;
	// TODO: data for now is any relevant data that needs to be passed to a
	// handler thread
	private static Object datastructures;
	
	// Constructor
	public Server() {
		//TODO: Any start up constructor code here
		
	}
	
	private static void initializeData(){
		//TODO: Initialize any data structures we may need
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
			serverSocket = new ServerSocket(SERVER_SOCKET);
		} catch (IOException e) {
			System.out.println("Could not listen on port: " + SERVER_SOCKET);
			System.exit(-1);
		}
		
		// Spin until a new message is received and then spawn a 
		// ServerSocketHandler thread
		while(listening){
			try {
				Socket hub = serverSocket.accept();
				//Spawn new ServerSocketHandler thread, we assume that the
				//hub has directed this message to the correct Server
				ServerSocketHandler newRequest = new ServerSocketHandler(hub,datastructures);
				//Starts running the new thread
				newRequest.start(); 
			} 
			catch (IOException e) {
				System.out.println("Accept failed on port: " + SERVER_SOCKET);
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
