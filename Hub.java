import java.io.IOException;
import java.net.*;

//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	private static boolean listening = true;
	
	static ClassList classList;
	static UserList userList;
	static ServerList serverList;
	
	public Hub(){
		// Constructor
	}

	/*
	 * Checks if a given file exists in the current running directory
	 */
	private static boolean fileExists(String file){
		// TODO: Implement finding a given file in the file system
		return false;
	}
	
	/*
	 * Initializes our data structures
	 */
	private static void initializeData(){
		//Create or import ClassList and UserList
		//Check if ClassList exists
		if (fileExists("Class_List")){
			//import file
		} else {
			//create new ClassList
			classList = new ClassList();
		}
		if (fileExists("User_List")){
			//import file
		} else {
			userList = new UserList();
		}
	}

	/*
	 * Main running loop for a Hub
	 */
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
		while(listening){	
			// Spin until a new message is received and then spawn a 
			// HubSocketHandler thread
			try {
				System.out.println("Listening");
				Socket client = hubSocket.accept();
				//Spawn new ServerSocketHandler thread, we assume that the
				//hub has directed this message to the correct Server
				HubSocketHandler newRequest = new HubSocketHandler(client,classList,userList,serverList);
				System.out.println("Accepted a connection from: "+ client.getInetAddress());
				//Starts running the new thread
				newRequest.start(); 
				
			} catch (IOException e) {
				System.out.println("Accept failed on port: " + CLIENT_SOCKET);
				System.exit(-1);
			} 
		}
		//Close socket after done listening
		try {
			hubSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't close");
		}
	}

}