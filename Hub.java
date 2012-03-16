
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	private static volatile boolean listening = true;
	private static volatile boolean demo = true;
	
	static ClassList classList;
	static UserList userList;
	static ServerList serverList;
	static String classListName = "hub.classlist";
	static String userListName = "hub.userlist";
	static String serverListName = "hub.serverlist";
	AES hubAESObject;
	
	static HashMap<Integer,SocketPackage> serverPackages = new HashMap<Integer,SocketPackage>();
	
	static ServerSocket hubSocket = null;
	String hubIP = null;
	
	private static final boolean DEBUG = false;
	
	public Hub(AES aesObject){
		this.hubAESObject = aesObject;
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
	
	
	//////////////////////////////////////////////////////
	//					SYS-ADMIN ACTIONS				//
	//////////////////////////////////////////////////////
	
	/*
	 * "Installs" the Hub program. i.e. Lets a sys-admin
	 * generate a hub key and encrypt that with their own 
	 * 128-bit password. This encrypted hub key is what is 
	 * stored on file. In the future, the sys-admin uses their
	 * 128-bit password to decrypt the hub-key which will be used
	 * to decrypt other data. We use an encrypted hub-key so that 
	 * it won't have to change even  
	 */
	public void installHub(char[] password){
		//TODO: create a hub-key (also set to global hub-key to use during session)
		
		//encrypt hub-key with password
		
		//clear password field
		
		//store encrypted hub-key on filesystem
		
		
	}
	
	/*
	 * Handles the sys admin logging in
	 */
	public boolean sysAdminLogin(char[] password){
		//hash password
		
		//clear password field
		
		//compare passhash to newly hashed password
		
		return false;
	}
	
	public void shutDown(){
		listening = false;
	}
	
	/*
	 * Add a user to the userList.
	 */
	public boolean addUser(String username, char[] password){
		//check nulls
		if (username == null){
			return false;
		}
		//Add
		//TODO: check the output of userlist
		if(userList.addUser(username, password, hubAESObject) != -1){
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
	public boolean removeUser(String username){
		//check user exists
		if(userExists(username)){
			if( userList.removeUser(username) == 1){
				if(DEBUG) System.out.println(username + "removed.");
				return true;
			} else{
				return false;
			}
		} else{
			if(DEBUG) System.out.println(username + " was not in the user list");
			return false;
		}
	}
	
	/*
	 * Check a user exists in the userList
	 * Replaced the deprecated validate user in user list
	 */
	private boolean userExists(String username){
		char[] pass = userList.getUserPass(username, hubAESObject);
		if (pass != null){
			Arrays.fill(pass, '0');
			return true;
		}
		return false;
	}
	
	/*
	 * Get an arraylist of strings of all the users in userlist
	 */
	public ArrayList<String> getUsers(){
		//TODO: finish
		return null;
	}
	
	/**
	 * Add a server to the serverList. Doesn't activate it.
	 * Returns the server id
	 * @param serverIP
	 * @param password
	 * @return
	 */
	public int addServer(String serverIP, char[] password){
		InetAddress server = null;
		try {
			server = InetAddress.getByName(serverIP);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//Specifically written for demo to allow a startup script
		//In the real world the servers would have to be started up manually
		if (demo) password = "password".toCharArray();
		
		int r = serverList.addServer(server, SERVER_SOCKET,password,hubAESObject);
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

	
	/**
	 * Remove a server from the serverList
	 * @param serverID
	 * @return
	 */
	public boolean removeServer(int serverID){
		if(serverList.removeServer(serverID) == 1){
			// also remove from serverSockets
			serverPackages.remove(serverID);
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * Get a list of all the servers in serverList
	 */
	public Map<Integer,String> getServers(){
		//TODO: finish
		return null;
		
	}
	
	/*
	 * Populates serverSockets with the socket associated with the appropriate
	 * server number which is also the index + 1. So serverNum - 1 = index.
	 * 
	 * prints out errors, because we can't guarantee that all servers will connect
	 */
	public void connectServers() {
		int numServers = serverList.getLastServer();
		if (numServers == 0) {
			System.out.println("There are no servers to connect to. Please add some."); 
		}
		// start up a connection with all of the servers
		for (int i = 1;i<=numServers;i++){
			//Open a connection
			if (DEBUG) System.out.println("Connecting to server " + i);
			
			//Check if pre-existing
			if (serverPackages.containsKey(i)){
				//connect
				SocketPackage tempSocket = serverPackages.get(i);
				//make sure not already connected
				if(!tempSocket.isConnected()){
					authenticatedConnect(tempSocket, serverList.getServerAES(i, hubAESObject));
				}
			} else{
				//add to map and connect (happens at hub start up)
				SocketPackage newSocketPackage = 
						new SocketPackage(serverList.getAddress(i), SERVER_SOCKET);
				if(!newSocketPackage.isConnected()){
					authenticatedConnect(newSocketPackage, serverList.getServerAES(i, hubAESObject));
				}
				serverPackages.put(i, newSocketPackage);
			}
		}
	}
	
	//////////////////////////////////////////////////////
	//					SYSTEM ACTIONS					//
	//////////////////////////////////////////////////////
	/*
	 * Handles an authenticated connect and verification of the reply
	 */
	private static void authenticatedConnect(SocketPackage socketPack, AES socketAES){
		//make a network connection
		socketPack.socketConnect();
		//send the initial contact
		
		//make the message
		Message initial = new Message();
		//setters
		initial.setType(Message.MessageType.Hub_AuthServer);
		initial.setIv(socketAES.getIv());
		initial.setSalt(socketAES.getSalt());
		//create body
		ArrayList<Long> body = new ArrayList<Long>();
		//set timestamp
		body.add(0, Calendar.getInstance().getTimeInMillis());
		//set nonce
		long myNonce = new SecureRandom().nextLong();
		body.add(1,myNonce);
		
		//set and encrypt body
		initial.setBody(socketAES.encrypt(body));
		
		//send
		socketPack.sendEncrypted(socketAES.encrypt(initial));
		
		//block and wait for reply, will be type message
		byte[] eReply = socketPack.receiveEncrypted();
		
		//decrypt
		Message reply = (Message)socketAES.decryptObject(eReply);
		
		boolean verified = true;
		//verify fields
		long newChecksum = CheckSum.getChecksum(reply);
		long oldChecksum = reply.getChecksum();
		if(newChecksum != oldChecksum){
			verified = false;
		}
		//get body fields
		long serverTimestamp = ((ArrayList<Long>)reply.getBody()).get(0);
		long serverNonce = ((ArrayList<Long>)reply.getBody()).get(1);
		
		//nonce check
		if ((myNonce+1) != serverNonce){
			verified = false;
		}
		
		//Check timestamp
		long myTimestamp = Calendar.getInstance().getTimeInMillis();
		if (!(((myTimestamp - 300000) <= serverTimestamp) && (serverTimestamp <= (myTimestamp + 300000)))){
			verified = false;
		}
		
		if(!verified){
			System.out.println("Connection to socket at ip: " + socketPack.getSocketName() + " failed.");
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
	private void writeToDisk(Object o, String name){
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
	private boolean fileExists(String file){
		return new File(file).exists();
	}
	
	
	/*
	 * Initializes our data structures
	 */
	private void initializeData(){
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
			//TODO: possible error because we're making the inet address into a string now
			addServer(InetAddress.getLocalHost().getHostAddress(),"password".toCharArray());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Could not add a local host server");
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
		try {
			if (DEBUG) System.out.println("Connecting to Servers");
			connectServers();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("Servers could not be connected. Please boot up your servers.");
		}
		if (DEBUG) System.out.println("Finished connecting to servers");
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
				HubSocketHandler newRequest = new HubSocketHandler(client,classList,userList,serverList,serverPackages,hubAESObject);
				if(DEBUG) System.out.println("Accepted a connection from: "+ client.getInetAddress());
				//Starts running the new thread
				newRequest.start(); 
				
			} catch (IOException e) {
				System.out.println("Accept failed on port: " + CLIENT_SOCKET);
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
		
		if (DEBUG) System.out.println("Hub shut down.");
	}
}