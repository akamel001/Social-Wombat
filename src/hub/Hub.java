package hub;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.DataObject;
import security.AES;
import security.CheckSum;
import storage.ClassList;
import storage.Message;
import storage.ServerList;
import storage.UserList;
import util.SocketPackage;


//Hub class. Handles communication between data servers and clients
// Hub does authentication and forwards messages to the servers. 
public final class Hub extends Thread {
	
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	static volatile boolean listening = true;
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

		if (DEBUG) {if(userList == null) System.out.println("userlist is null");}
		
		//Add
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
	public int connectServers() {
		if (DEBUG) System.out.println("Connecting to the servers");
		int numServers = serverList.getLastServer();
		if (DEBUG) System.out.println("Needs to connect with " + numServers + " servers");
		if (numServers == 0) {
			System.out.println("There are no servers to connect to. Please add some."); 
		}
		// start up a connection with all of the servers
		int numConnected = 0;
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
					if (authenticatedConnect(tempSocket, servAES)){
						//add to connected servers
						if (DEBUG) System.out.println("Server: " + i + " connected successfully.");
						numConnected++;
					} else {
						if(DEBUG) System.out.println("Server: " + i + " failed to connect at address: " + tempSocket.getSocketName());
					}
				}
			} else{
				//add to map and connect (happens at hub start up)
				SocketPackage newSocketPackage = 
						new SocketPackage(serverList.getAddress(i), SERVER_SOCKET);
				if(!newSocketPackage.isConnected()){
					
					//regenerate serverAES object
					//grab pass
					char[] servPass = serverList.getServerPass(i, hubAESObject);
					//grab iv(0), salt(1)
					List<byte[]> serveIvSalt = serverList.getIvSalt(i, hubAESObject);
					AES servAES = new AES(servPass,serveIvSalt.get(0),serveIvSalt.get(1));
					//zero out password
					Arrays.fill(servPass, '0');
					if (authenticatedConnect(newSocketPackage, servAES)){
						//add to connected servers
						if (DEBUG) System.out.println("Server: " + i + " connected successfully.");
						numConnected++;
					} else {
						if(DEBUG) System.out.println("Server: " + i + " failed to connect at address: " + newSocketPackage.getSocketName());
					}
				}
				serverPackages.put(i, newSocketPackage);
			}
		}
		return numConnected;
	}
	
	//////////////////////////////////////////////////////
	//					SYSTEM ACTIONS					//
	//////////////////////////////////////////////////////
	/*
	 * Handles an authenticated connect and verification of the reply
	 */
	@SuppressWarnings("unchecked")
	private static boolean authenticatedConnect(SocketPackage socketPack, AES socketAES){
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
		initial.setChecksum(CheckSum.getMD5Checksum(body));
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
			return false;
		}
		
		//decrypt body
		byte[] encryptedBody = (byte[])reply.getBody();
		ArrayList<Long> replyBody = (ArrayList<Long>)socketAES.decryptObject(encryptedBody);
		if (replyBody == null){
			System.out.println("unable to decrypt msg body");
		}
		//reset the body to the decrypted body
		reply.setBody(replyBody);
		//verify fields
		String newChecksum = reply.generateCheckSum();
		String oldChecksum = reply.getChecksum();
		
		if(!newChecksum.equals(oldChecksum)){
			if(DEBUG) System.out.println("HUB: checksums don't match."); 
			if(DEBUG) System.out.println("New checksum: " + newChecksum);
			if(DEBUG) System.out.println("Old checksum: " + oldChecksum);
			return false;
		}
		//get body fields
		long serverTimestamp = replyBody.get(0);
		long serverNonce = replyBody.get(1);
		
		//nonce check
		if ((myNonce+1) != serverNonce){
			if(DEBUG) System.out.print("HUB: nonce test failed.");
			return false;
		}
		
		//Check timestamp
		long myTimestamp = Calendar.getInstance().getTimeInMillis();
		if (!(((myTimestamp - 300000) <= serverTimestamp) && (serverTimestamp <= (myTimestamp + 300000)))){
			if(DEBUG) System.out.print("HUB: timestamp test failed.");
			return false;
		}
		
		//everything passes
		
		//store nonce
		socketPack.setNonce(serverNonce);
		return true;
		
	}
	
	/* 
	 * Reads a given object from the filesystem
	 */
	private static Object readFromDisk(String name){
		DataObject o = null;
		
		try {
		    FileInputStream fin = new FileInputStream(name);
		    ObjectInputStream ois = new ObjectInputStream(fin);
		    
		    
		    o = (DataObject) ois.readObject();
		    
		    String diskChecksum = o.getChecksum();
		    String expectedChecksum = CheckSum.getMD5Checksum(o.getData());
		    
		    if(!diskChecksum.equals(expectedChecksum)){
		    	ois.close();
		    	if(DEBUG) System.out.println("Woops! bad checksum when reading data from disk\n Expected " 
		    			+ expectedChecksum + "\n Got " + diskChecksum);
		    	
		    	return null;
		    }
		    ois.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return o.getData();
	}
	
	/*
	 * Writes a given object to the filesystem
	 */
	private void writeToDisk(Object write, String name){
		DataObject o = new DataObject(write, CheckSum.getMD5Checksum(write));
		
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
			
			if(DEBUG) System.out.println("Loaded classlist from disk");
			
			if(classList == null){
				if(DEBUG) System.out.println("Files on disk where not read, possibly tampered with");
				classList = new ClassList();
			}
		} else {
			//create new ClassList
			classList = new ClassList();
		}
		
		if (fileExists(userListName)){
			userList = (UserList) readFromDisk(userListName);
			if(DEBUG) System.out.println("Loaded userList from disk ");

			if(userList == null){
				if(DEBUG) System.out.println("Files on disk where not read, possibly tampered with");
				userList = new UserList();
			}
		} else {
			//create new Userlist
			userList = new UserList();
		}
		if (fileExists(serverListName)){
			serverList = (ServerList) readFromDisk(serverListName);
			if(DEBUG) System.out.println("Loaded serverList from disk");

			if(serverList == null){
				if(DEBUG) System.out.println("Files on disk where not read, possibly tampered with");
				serverList = new ServerList();
			}
		} else {
			//create new ServerList
			serverList = new ServerList();
		}
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
					//Handle spam
					try {
						//Spawn new ServerSocketHandler thread
						HubSocketHandler newRequest = 
								new HubSocketHandler(
										client,
										classList,
										userList,
										serverList,
										serverPackages,
										hubAESObject,
										currentUsers);
						if(DEBUG) System.out.println("Accepted a connection from: "+ client.getInetAddress());
						//Starts running the new thread
						newRequest.start(); 
					} catch (Exception e){
						if (DEBUG) System.out.println("Bad message received, ignoring");
					}
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
		if (DEBUG) System.out.println("listening volatile variable should be false, it is currently: " + listening);
	}
}