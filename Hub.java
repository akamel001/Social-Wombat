
import java.io.*;
import java.net.*;
import java.util.HashMap;

//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	private static volatile boolean listening = true;
	
	static ClassList classList;
	static UserList userList;
	static ServerList serverList;
	static String classListName = "hub.classlist";
	static String userListName = "hub.userlist";
	static String serverListName = "hub.serverlist";
	
	static HashMap<Integer,SocketPackage> serverPackages = new HashMap<Integer,SocketPackage>();
	
	static ServerSocket hubSocket = null;
	String hubIP = null;
	
	private static final boolean DEBUG = false;
	
	public Hub(){
		// Constructor
		try {
			InetAddress addr = InetAddress.getLocalHost();
			if(DEBUG) System.out.println("New Hub created: " + addr);
			hubIP = addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Hub could not be created");
		}
	}
	
	/*
	 * Add a user to the userList.
	 */
	public static boolean addUser(String username, char[] password){
		//check nulls
		if (username == null){
			return false;
		}
		//Add
		if(userList.addUser(username)){
			if(DEBUG) System.out.println("User " + username + " added successfully!");
			return true;
		} else {
			if(DEBUG) System.out.println("User " + username + " could not be added");
			return false;
		}
	}
	
	/*
	 * Remove a user from the userList
	 */
	public static boolean removeUser(String username){
		//check user exists
		if(userList.validateUser(username)){
			if(DEBUG) System.out.println(username + "removed.");
			return userList.removeUser(username);
		} else{
			if(DEBUG) System.out.println(username + " was not in the user list");
			return false;
		}
	}
	
	/*
	 * Check a user exists in the userList
	 */
	public static boolean userExists(String username){
		return userList.validateUser(username);
	}
	
	/*
	 * Add a server to the serverList
	 */
	public static int addServer(InetAddress server){
		int r = serverList.addServer(server, SERVER_SOCKET);
		if (r == -1){
			if(DEBUG) System.out.println("Adding server " + server + "failed. It might already exist in serverList."); 
			return r;
		} else {
			if(DEBUG) System.out.println("Server" + server + " added under server id: " + r);
			//create new socket to add
			SocketPackage newSocketPackage = new SocketPackage(server,SERVER_SOCKET);
			serverPackages.put(r, newSocketPackage);
			return r;
		}
	}
	
	/*
	 * Remove a server from the serverList
	 */
	public static boolean removeServer(int serverID){
		if(serverList.removeServer(serverID) == 1){
			// also remove from serverSockets
			serverPackages.remove(serverID);
			return true;
		} else {
			return false;
		}
	}
	
	/* 
	 * Reads a given object from the filesystem
	 */
	private static Object readFromDisk(String name){
		Object o = null;
		try {
		    FileInputStream fin = new FileInputStream(name);
		    ObjectInputStream ois = new ObjectInputStream(fin);
		    if (name.equals(classListName)){
		    	//if(DEBUG) System.out.println("We are reading in ClassList");
		    	o = (ClassList) ois.readObject();
		    } else if (name.equals(userListName)){
		    	//if(DEBUG) System.out.println("We are reading in UserList");
		    	o = (UserList) ois.readObject();
		    } else if (name.equals(serverListName)){
		    	//if(DEBUG) System.out.println("We are reading in ServerList");
		    	o = (ServerList) ois.readObject();
		    }
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
			if (o instanceof UserList){
				//if(DEBUG) System.out.println("We have have an instance of UserList");
				oos.writeObject((UserList)o);
			} else if (o instanceof ClassList){
				//if(DEBUG) System.out.println("We have have an instance of ClassList");
				oos.writeObject((ClassList)o);
			} else if (o instanceof ServerList){
				//if(DEBUG) System.out.println("We have have an instance of ServerList");
				oos.writeObject((ServerList)o);
			}
			
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
		
		//TODO: Remove for multiple computers
		try {
			addServer(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Could not add a local host server");
		}
		
	}
	
	/*
	 * Populates serverSockets with the socket associated with the appropriate
	 * server number which is also the index + 1. So serverNum - 1 = index.
	 * 
	 */
	private static void connectServers(){
		int numServers = serverList.getLastServer();
		// start up a connection wit hall of the servers
		for (int i = 1;i<=numServers;i++){
			//Open a connection
			SocketPackage newSocketPackage = new SocketPackage(serverList.getAddress(i),SERVER_SOCKET);
			serverPackages.put(i, newSocketPackage);
		}
	}
	
	/*
	 * Main running loop for a Hub
	 */
	public void run() {
		// Start Up the Hub
		// Initialize Data Structures
		initializeData();
		// Connect to Servers
		connectServers();
		//add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// Write out to disk
				writeToDisk(classList, classListName);
				writeToDisk(userList, userListName);
				writeToDisk(serverList, serverListName);
				if(DEBUG) System.out.println("Data safely written out.");
				// Disconnect from Servers
				for (SocketPackage socketPackage : serverPackages.values()){
					socketPackage.close();
				}
			}
		});
		
		/* 
		 * FROM THIS POINT ON IS CODE THAT WAITS FOR AND RESPONDS TO 
		 * Client REQUESTS
		 * */
		
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
				if(DEBUG) System.out.println("Listening");
				Socket client = hubSocket.accept();
				//Spawn new ServerSocketHandler thread, we assume that the
				//hub has directed this message to the correct Server
				HubSocketHandler newRequest = new HubSocketHandler(client,classList,userList,serverList,serverPackages);
				if(DEBUG) System.out.println("Accepted a connection from: "+ client.getInetAddress());
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
		writeToDisk(classList, classListName);
		writeToDisk(userList, userListName);
		writeToDisk(serverList, serverListName);
	}
}