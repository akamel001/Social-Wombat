
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

//TODO: check permissions on the server.

public class ServerSocketHandler {
	//private static int SERVER_SOCKET = 5050;
	
	static Message msg = new Message();
	ClassDB classDB;
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	AES serverAESObject;
	char[] password;
	
	private static final boolean DEBUG = false;

	/*
	 * A handler thread that is spawned for each message sent to server
	 */
	public ServerSocketHandler(Socket ser, ClassDB classDB, char[] password){
		this.socket = ser;
		this.classDB = classDB;
		this.password = password;
		// Create datastreams
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not create input and output streams");
		}
	}
	
	/**
	 * First method that is run, expects a message type Hub_AuthServer
	 * 
	 * @return true if the hub is authenticated, false otherwise
	 */
	private boolean authenticate(){
		Message firstMessage = null;
		try {
			//first message is unencrypted
			firstMessage = (Message) ois.readObject();
			// null check
			if (firstMessage == null){
				if (DEBUG) System.out.println("First msg received was null");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//Check message type
		if (msg.getType() != Message.MessageType.Hub_AuthServer){
			return false;
		}
		
		//extract fields
		byte[] salt = firstMessage.getSalt();
		byte[] iv = firstMessage.getIv();
		
		//Create server aes object
		serverAESObject = new AES(password, iv, salt);
		
		//Zero out password
		Arrays.fill(password,'0');
		
		//null check aes object
		if (serverAESObject == null){
			if (DEBUG) System.out.println("clientAESObject creation failed");
			return false;
		}
		
		//Decrypt the body, cast to ArrayList<Long>
		byte[] encryptedBody = (byte[]) firstMessage.getBody();
		ArrayList<Long> body = (ArrayList<Long>)serverAESObject.decryptObject(encryptedBody);
		if (body==null){
			if (DEBUG) System.out.println("unable to decrypt msg body");
			return false;
		}
		
		//checksum
		long oldchecksum = firstMessage.getChecksum();
		long newchecksum = firstMessage.generateCheckSum();
		if (oldchecksum != newchecksum){
			return false;
		}
		
		long hubTimestamp = body.get(0);
		long hubNonce = body.get(1);
		
		boolean allowed = false;
		
		//Check timestamp
		long myTimestamp = Calendar.getInstance().getTimeInMillis();
		if (((myTimestamp - 300000) <= hubTimestamp) && (hubTimestamp <= (myTimestamp + 300000))){
			allowed = true;
		}
		
		//Return the authenticated message
		if (allowed){
			//Create return message
			Message returnMsg = new Message();
			ArrayList<Long> returnBody = new ArrayList<Long>();
			//set my timestamp
			returnBody.set(0, Calendar.getInstance().getTimeInMillis());
			//set the nonce+1
			returnBody.set(1, hubNonce+1);
			//set the body
			returnMsg.setBody(returnBody);
			//set checksum
			long checksum = returnMsg.generateCheckSum();
			returnMsg.setChecksum(checksum);
			
			//encrypt
			byte[] returnMessage = serverAESObject.encrypt(returnMsg);
			
			//send back success
			sendEncryptedMessage(returnMessage);
			
			//put thread in a state to receive future messages
			return true;
		}
		
		return false;	
	}
	
	/**
	 * Method to send an encrypted message to the precreated streams.
	 */
	private void sendEncryptedMessage(byte[] msg){
		try {
			//write length first
			oos.writeInt(msg.length);
			//the write the msg
			oos.write(msg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Sending an encrypted message failed");
		}
		
	}
	
	/**@deprecated
	 * Deserialize transmission in socket and convert to a message
	 */
	private void getMessage(){
		try {
			msg = (Message) ois.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
	}
	
	/**
	 * Gets and decrypts a message. Also does checksumming
	 * Blocking read.
	 * Will return true if was able to decrypt message and checksum
	 * passes. Will return false otherwise.
	 */
	private boolean getAndDecryptMessage(){
		//length
		int length = 0;
		//read the length of the byte [] first
		try {
			length = ois.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		//set a buffer to correct length
		byte[] encryptedMsg = new byte[length];
		//read encrypted message
		try {
			ois.readFully(encryptedMsg);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		msg = (Message)serverAESObject.decryptObject(encryptedMsg);
		
		//do checksum
		long oldChecksum = msg.getChecksum();
		long newChecksum = msg.generateCheckSum();
		
		return (oldChecksum == newChecksum);
		
		
	}
	
	/**@deprecated
	 * Send an unencrypted message back after it's been altered
	 */
	private void returnMessage(Message msg){
		//Get output stream
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Return Message Failed");
		}	
	}
	
	/**
	 * Encrypt and return a message. Takes a message and will set the checksum for you.
	 * 
	 */
	private void returnAndEncryptMessage(Message msg){
		//calculate checksum
		long thisChecksum = msg.generateCheckSum();
		msg.setChecksum(thisChecksum);
		//encrypt message why client aes key
		byte[] eMsg = serverAESObject.encrypt(msg);
		//send over wire
		try {
			//write in the length first
			oos.writeInt(eMsg.length);
			//then write message
			oos.write(eMsg);
			oos.flush();
			oos.reset();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Return Message Failed");
		}
	}
	
	public void run(){
		boolean listen = false;
		
		//wait for first message and authenticate
		listen = authenticate();
		
		//servers will run indefinitely if authenticated
		while(listen){
			boolean valid = false;
			//Read and deserialize Message from Socket
			valid = getAndDecryptMessage();
			
			if (msg == null){
				System.out.println("Message was null");
				valid = false; // Don't waste time on bad transmissions
			}
			if (valid){
				// Preset return code to failure
				int returnCode = -1;
				
				//Handle the different types of client messages
				switch(msg.getType()) {
					
					// Client -> Hub -> Server
					case Client_CreateClassroom:
						
						System.out.println(msg.getUserName()+ "wants to add: " + msg.getClassroom_ID());
						
						returnCode = classDB.addClassRoom(msg.getClassroom_ID(), serverAESObject);
						if (returnCode == 1){
							System.out.println(msg.getClassroom_ID() + " added.");
						}
						msg.setCode(returnCode);
						returnAndEncryptMessage(msg);
						break;
					/*
					 * Create post requires Post Name, and Post Body.
					 * Since only a msg body is available for storage of both,
					 * they will be stored as a 2 element arraylist with 
					 * Array[0] = Post_Name
					 * Array[1] = Post_body 	
					 */
					case Client_CreateThread:
						System.out.println(msg.getUserName()+ "wants to create a thread");
						@SuppressWarnings("unchecked")
						ArrayList<String> post = (ArrayList<String>) msg.getBody();
						String postName = (String)post.get(0);
						String postBody = (String)post.get(1);
						returnCode = classDB.addPost(msg.getClassroom_ID(), postName, postBody, serverAESObject);
						msg.setCode(returnCode);
						returnAndEncryptMessage(msg);
						break;
					/*
					 * Create client expects postID and comment to be stored as index
					 * [0] and [1] respectively in the msg.body as an ArrayList.
					 */
					case Client_CreateComment:
						System.out.println(msg.getUserName()+ "posting a comment");
						@SuppressWarnings("unchecked")
						ArrayList<String> com = (ArrayList<String>)msg.getBody();
						int postId = Integer.parseInt(com.get(0));
						String comment = com.get(1);
						returnCode = classDB.addComment(msg.getClassroom_ID(), postId, comment, serverAESObject);
						msg.setCode(returnCode);
						returnAndEncryptMessage(msg);
						break;
					case Client_GoToClassroom:
						System.out.println(msg.getUserName()+ "wants to enter: " + msg.getClassroom_ID());
						Map<Integer, String> threadList = classDB.getThreadList(msg.getClassroom_ID(), serverAESObject);
						if (threadList == null){
							//return failure
							msg.setCode(-1);
						} else {
							//return map
							msg.setCode(1);
							msg.setBody(threadList);
						}
						returnAndEncryptMessage(msg);
						break;
					case Client_GoToThread:
						//null check
						if (msg.getBody() == null) {
							msg.setCode(-1);
							returnAndEncryptMessage(msg);
							break;
						}
						//Thread id embedded in body
						int threadID = (Integer)msg.getBody();
						System.out.println(msg.getUserName() + " wants to view thread #: " + threadID);
						
						Map<Integer, String> thread = classDB.getThread(msg.getClassroom_ID(), threadID, serverAESObject);
						if (thread == null){
							//return failure
							msg.setCode(-1);
						} else {
							//return map
							msg.setCode(1);
							msg.setBody(thread);
						}
						returnAndEncryptMessage(msg);
						break;
					case Client_DeleteClassroom:
						System.out.println(msg.getUserName()+ "wants to delete: " + msg.getClassroom_ID());
						returnCode = classDB.removeClassRoom(msg.getClassroom_ID());
						System.out.println("Return code: " + returnCode);
						msg.setCode(returnCode);
						returnAndEncryptMessage(msg);
						break;
					case Client_DeleteThread:
						System.out.println(msg.getUserName()+ "wants to delete a thread.");
						int p = (Integer)msg.getBody();
						returnCode = classDB.removePost(msg.getClassroom_ID(), p, serverAESObject);
						msg.setCode(returnCode);
						returnAndEncryptMessage(msg);
						break;
					/*
					 * Requires postId and commentID which will be stored in the 
					 * msg.body() as an ArrayList<Integer>
					 */
					case Client_DeleteComment:
						System.out.println(msg.getUserName() + "wants to delete a comment.");
						@SuppressWarnings("unchecked")
						ArrayList<Integer> commentParams = (ArrayList<Integer>)msg.getBody();
						int postID = commentParams.get(0);
						int commentID = commentParams.get(1);
						returnCode = classDB.removeComment(msg.getClassroom_ID(), postID, commentID, serverAESObject);
						msg.setCode(returnCode);
						returnAndEncryptMessage(msg);
						break;	
					default:
						//For illegal requests
						msg.setBody("Illegal Request Sent");
						msg.setCode(-1);
						returnAndEncryptMessage(msg);
						break;
				}
			}
		}
		// Close everything
		try {
			oos.close();
			ois.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't close");
		}
		
	}
	
	
} 
	
