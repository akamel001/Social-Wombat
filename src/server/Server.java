package server;
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
import java.net.UnknownHostException;

import security.CheckSum;
import storage.ClassDB;
import util.DataObject;


public final class Server extends Thread{
	//private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	public static boolean listening = true;
	
	//datastructure that will be passed to every spawned serversockethandler thread
	private static ClassDB classDB;
	private static char[] password;
	private static String ip;
	
	private static final boolean DEBUG = false;
	
	//default, is reset in constructor
	static String classDBName = "server.classDB";	

	// Constructor
	public Server(String name, char[] password2) {
		// Constructor
		classDBName = name + ".classDB";
		password = password2;	//needs to wait for an iv, and salt
		//get ip
		InetAddress thisIp = null;
		try {
			thisIp = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("New Server created with IP: " + thisIp.getHostAddress());	
		ip = thisIp.getHostAddress();
	}
	
	public String getIP(){
		return ip;
	}
	
	/* 
	 * Reads a ClassDB from the filesystem
	 */
	private static ClassDB readFromDisk(String name){
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
		return (ClassDB) o.getData();
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
			/*if (o instanceof UserList){
				//if(DEBUG) System.out.println("We have have an instance of UserList");
				oos.writeObject((UserList)o);
			} else if (o instanceof ClassList){
				//if(DEBUG) System.out.println("We have have an instance of ClassList");
				oos.writeObject((ClassList)o);
			} else if (o instanceof ServerList){
				//if(DEBUG) System.out.println("We have have an instance of ServerList");
				oos.writeObject((ServerList)o);
			}*/
			
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
	 * Initialize data structures
	 */
	private static void initializeData(){
		
		//Create or import ServerList
		//Check if ServerList exists
		if (fileExists(classDBName)){
			//import file
			classDB = (ClassDB) readFromDisk(classDBName);
			
			if(DEBUG) System.out.println("Loaded classlist from disk");
			
			if(classDB == null){
				if(DEBUG) System.out.println("Files on disk where not read, possibly tampered with");
				classDB = new ClassDB();
			}
						
		} else {
			//create new ServerList
			classDB = new ClassDB();
		}
	}
	
	public void run() {
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
		
		//add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(){
			public void run() {
				// Write out to disk
				writeToDisk(classDB, classDBName);
				System.out.println("Data safely written out.");
			}
		});
		
		// Spin until a new message is received and then spawn a 
		// ServerSocketHandler thread
		while(listening){
			try {
				System.out.println("Listening...");
				try {
					Socket hub = serverSocket.accept();
					//Spawn new ServerSocketHandler thread, we assume that the
					//hub has directed this message to the correct Server
					ServerSocketHandler newRequest = new ServerSocketHandler(hub,classDB,password);
					System.out.println("New socket handler created");
					//Starts running function
					newRequest.run(); 
				} 
				catch (IOException e) {
					System.out.println("Accept failed on port: " + SERVER_SOCKET);
				}
			} catch (Exception e){
				//e.printStackTrace();
				if (DEBUG) System.out.println("Encountered exception. Server will continue running.");
			}
		}
		//Close socket
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error closing server");
		}
		// Write out to disk
		writeToDisk(classDB, classDBName);
	}
}
