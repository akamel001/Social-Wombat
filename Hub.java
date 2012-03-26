
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.SecureRandom;

//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
class Hub extends Thread {
	
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	private static volatile boolean listening = true;
	private static volatile boolean demo = false;
	
	static ClassList classList;
	static UserList userList;
	static ServerList serverList;
	static String classListName = "hub.classlist";
	static String userListName = "hub.userlist";
	static String serverListName = "hub.serverlist";
	AES hubAESObject;
	
	static HashMap<Integer,SocketPackage> serverPackages = new HashMap<Integer,SocketPackage>();
	
	//stores current users that are logged in to disallow multiple instances of the same user
	static volatile HashMap<String,Integer> currentUsers = new HashMap<String,Integer>(); 
	
	static ServerSocket hubSocket = null;
	String hubIP = null;
	
	private static final boolean DEBUG = true;
	
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
		// Initialize Data Structures
		if (DEBUG) System.out.println("Initializing data");
		initializeData();
		if (DEBUG) System.out.println("Done initializing data");

	}
	
	
	//////////////////////////////////////////////////////
	//					SYS-ADMIN ACTIONS				//
	//////////////////////////////////////////////////////
	
	public void shutDown(){
		// Close the socket. 
		try {
			hubSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		//TODO: debug check
		if(userList == null) System.out.println("userlist is null");
		
		//Add
		//TODO: check the output of userlist
		// UserList now returns -1 if username or pass is invalid -	cd
		if(userList.addUser(username, password, hubAESObject) != -1){
			if(DEBUG) System.out.println("User " + username + " added successfully!");
			return true;
		} else {
			if(DEBUG) System.out.println("User " + username + " could not be added");
			return false;
		}
	}
	
	/**
	 * Remove a user from the userList
	 * @param username The user to remove
	 * @return Returns true if the 
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
	public List<String> getUsers(){
		return userList.getAllUsers(hubAESObject);
		 
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
			//TODO: change to not automatically add
			//connectServers();
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
	
	/**
	 * Get a list of all the servers in serverList
	 * @return Returns a hashmap of server ids mapped to Strings containing "server_ip, port". Returns
	 *          null if there are no extant servers. 
	 */
	public Map<Integer,String> getServers(){
		return serverList.getServerList();
	}
	
	/*
	 * Populates serverSockets with the socket associated with the appropriate
	 * server number which is also the index + 1. So serverNum - 1 = index.
	 * 
	 * prints out errors, because we can't guarantee that all servers will connect
	 */
	public void connectServers() {
		if (DEBUG) System.out.println("Connecting to the servers");
		int numServers = serverList.getLastServer();
		if (DEBUG) System.out.println("Needs to connect with " + numServers + " servers");
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
				if (tempSocket.isConnected()) {
					System.out.println("Server: " + i + " is connected");
				}
				//make sure not already connected
				if(!tempSocket.isConnected()){
					//regenerate serverAES object
					//grab pass
					char[] servPass = serverList.getServerPass(i, hubAESObject);
					//grab iv(0), salt(1)
					List<byte[]> serveIvSalt = serverList.getIvSalt(i, hubAESObject);
					AES servAES = new AES(servPass,serveIvSalt.get(0),serveIvSalt.get(1));
					//zero out password
					Arrays.fill(servPass, '0');
					authenticatedConnect(tempSocket, servAES);
				}
			} else{
				//add to map and connect (happens at hub start up)
				SocketPackage newSocketPackage = 
						new SocketPackage(serverList.getAddress(i), SERVER_SOCKET);
				if(!newSocketPackage.isConnected()){
					
					/*--------DEBUG-BEGIN--------------------*/
					System.out.println("IN SERVER: " + serverList.getServerList().toString());
					System.out.println("SERVER PASS: " + Arrays.toString(serverList.getServerPass(i, hubAESObject)));
					List<byte[]> b = serverList.getIvSalt(i, hubAESObject);
					System.out.println("SERVER IV: " + Arrays.toString(b.get(0)));
					System.out.println("SERVER SALT: " + Arrays.toString(b.get(1)));
					/*--------DEBUG-END--------------------*/
					
					//regenerate serverAES object
					//grab pass
					char[] servPass = serverList.getServerPass(i, hubAESObject);
					//grab iv(0), salt(1)
					List<byte[]> serveIvSalt = serverList.getIvSalt(i, hubAESObject);
					AES servAES = new AES(servPass,serveIvSalt.get(0),serveIvSalt.get(1));
					//zero out password
					Arrays.fill(servPass, '0');
					authenticatedConnect(newSocketPackage, servAES);
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
	@SuppressWarnings("unchecked")
	private static void authenticatedConnect(SocketPackage socketPack, AES socketAES){
		//make a network connection
		socketPack.socketConnect();
		if(DEBUG) System.out.println("Socket connect successful, now authenticating");
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
		initial.setChecksum(CheckSum.getChecksum(body));
		//set and encrypt body
		initial.setBody(socketAES.encrypt(body));
		
		//send
		socketPack.send(initial);
		
		if(DEBUG) System.out.println("Authentication message sent, waiting for reply...");

		//block and wait for reply, will be type message
		Message reply = socketPack.receive();
		
		if(DEBUG) System.out.println("Authentication reply received.");
		
		//null check
		if (reply==null){
			if(DEBUG) System.out.println("Authentication reply was null, stopping authenitcated connect with " + socketPack.getSocketName());
			return;
		}
		
		//decrypt body
		byte[] encryptedBody = (byte[])reply.getBody();
		ArrayList<Long> replyBody = (ArrayList<Long>)socketAES.decryptObject(encryptedBody);
		if (replyBody == null){
			System.out.println("unable to decrypt msg body");
		}
		//reset the body to the decrypted body
		reply.setBody(replyBody);
		boolean verified = true;
		//verify fields
		long newChecksum = reply.generateCheckSum();
		long oldChecksum = reply.getChecksum();
		
		if(newChecksum != oldChecksum){
			if(DEBUG) System.out.println("HUB: checksums don't match."); // TODO: when connecting to a server, the connection seems overall successful, however, the checksums don't match
			if(DEBUG) System.out.println("New checksum: " + newChecksum);
			if(DEBUG) System.out.println("Old checksum: " + oldChecksum);
			verified = false;
		}
		//get body fields
		long serverTimestamp = replyBody.get(0);
		long serverNonce = replyBody.get(1);
		
		//nonce check
		if ((myNonce+1) != serverNonce){
			if(DEBUG) System.out.print("HUB: nonce test failed.");
			verified = false;
		}
		
		//Check timestamp
		long myTimestamp = Calendar.getInstance().getTimeInMillis();
		if (!(((myTimestamp - 300000) <= serverTimestamp) && (serverTimestamp <= (myTimestamp + 300000)))){
			if(DEBUG) System.out.print("HUB: timestamp test failed.");
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
		/*
		try {
			//TODO: possible error because we're making the inet address into a string now
			addServer(InetAddress.getLocalHost().getHostAddress(),"password".toCharArray());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.println("Could not add a local host server");
		}*/
		
	}
	
	/*
	 * Main running loop for a Hub
	 */
	public void run() {
		// Start Up the Hub
		listening = true;
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
				/*
				// Disconnect from Servers
				for (SocketPackage socketPackage : serverPackages.values()){
					socketPackage.close();
				}
				*/
			}
		});
		
		/* 
		 * FROM THIS POINT ON IS CODE THAT WAITS FOR AND RESPONDS TO 
		 * Client REQUESTS
		 * */
		
		//Create and listen in on a port
		try {
			hubSocket = new ServerSocket(CLIENT_SOCKET);
			hubSocket.setSoTimeout(1000);
		} catch (IOException e) {
			System.out.println("Could not listen on port or port already in use: " + CLIENT_SOCKET);
		}
		while(listening){	
			// Spin until a new message is received and then spawn a 
			// HubSocketHandler thread
			try {
				//if(DEBUG) System.out.println("Listening");
				Socket client = hubSocket.accept();
				// If the hubsocket is closed at this point, it means that shutDown() was called while the socket was
				// was waiting. In this case, there is no need to continue.
				if(!hubSocket.isClosed()){
					//Spawn new ServerSocketHandler thread, we assume that the
					//hub has directed this message to the correct Server
					HubSocketHandler newRequest = new HubSocketHandler(client,classList,userList,serverList,serverPackages,hubAESObject,currentUsers);
					if(DEBUG) System.out.println("Accepted a connection from: "+ client.getInetAddress());
					//Starts running the new thread
					newRequest.start(); 
				}
			} catch (SocketTimeoutException e){
				// This is expected, b/c we have set the socket timeout. Do nothing.
			} catch (SocketException e){
				// This is expected, b/c we have closed the socket while it was still in accept(). Do nothing.
			}catch (IOException e) {
				System.out.println("Accept failed on port: " + CLIENT_SOCKET + " : " + e.toString());
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