import java.io.*;
import java.net.*;

//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	private static int CLIENT_SOCKET = 4444;
	//private static int SERVER_SOCKET = 5050;
	private static volatile boolean listening = true;
	
	static ClassList classList;
	static UserList userList;
	static ServerList serverList;
	static String classListName = "hub.classlist";
	static String userListName = "hub.userlist";
	static String serverListName = "hub.serverlist";
	
	public Hub(){
		// Constructor
	}

	private static Object readFromDisk(String name){
		Object o = null;
		try {
		    FileInputStream fin = new FileInputStream(name);
		    ObjectInputStream ois = new ObjectInputStream(fin);
		    o = (Object) ois.readObject();
		    ois.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	/*
	 * Writes a given object to the filesystem
	 */
	private static void writeToDisk(Object o, String name){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Checks if a given file exists in the current running directory
	 */
	private static boolean fileExists(String file){
		return new File(file).exists();
	}
	
	/*
	 * Initializes our data structures
	 */
	private static void initializeData(){
		//Create or import ClassList, UserList, ServerList
		if (fileExists(classListName)){
			classList = (ClassList) readFromDisk(classListName);
		} else {
			//create new ClassList
			classList = new ClassList();
		}
		if (fileExists(userListName)){
			userList = (UserList) readFromDisk(userListName);
		} else {
			//create new Userlist
			userList = new UserList();
		}
		if (fileExists(serverListName)){
			serverList = (ServerList) readFromDisk(serverListName);
		} else {
			//create new ServerList
			serverList = new ServerList();
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
		// Write out to disk
		writeToDisk(classList, "hub.classlist");
		writeToDisk(userList, "hub.userlist");
		writeToDisk(serverList, "hub.serverlist");
	}

}