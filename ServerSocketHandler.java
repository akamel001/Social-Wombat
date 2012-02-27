
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class ServerSocketHandler extends Thread{
	//private static int SERVER_SOCKET = 5050;
	
	ClassDB classDB;
	Socket socket;
	ObjectOutput oos;
	ObjectInput ois;

	/*
	 * A handler thread that is spawned for each message sent to server
	 */
	public ServerSocketHandler(Socket ser, ClassDB classDB){
		this.socket = ser;
		this.classDB = classDB;
		// Create datastreams
		try {
			this.oos = new ObjectOutputStream(this.socket.getOutputStream());
			this.ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not create input and output streams");
		}
	}
	
	/*
	 * Deserialize transmission in socket and convert to a message
	 */
	private Message getMessage(){
		Message msg = null;
		try {
			msg = (Message) ois.readObject();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
		return msg;
	}
	
	/*
	 * Send a message back after it's been altered
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
	
	public void run(){
		boolean valid = true;
		//Read and deserialize Message from Socket
		Message msg = getMessage();
		
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
					returnCode = classDB.addClassRoom(msg.getClassroom_ID());
					msg.setCode(returnCode);
					returnMessage(msg);
					break;
				/*
				 * Create post requires Post Name, and Post Body.
				 * Since only a msg body is available for storage of both,
				 * they will be stored as a 2 element arraylist with 
				 * Array[0] = Post_Name
				 * Array[1] = Post_body 	
				 */
				case Client_CreateThread:
					@SuppressWarnings("unchecked")
					ArrayList<String> post = (ArrayList<String>) msg.getBody();
					String postName = (String)post.get(0);
					String postBody = (String)post.get(1);
					returnCode = classDB.addPost(msg.getClassroom_ID(), postName, postBody);
					msg.setCode(returnCode);
					returnMessage(msg);
					break;
				/*
				 * Create client expects postID and comment to be stored as index
				 * [0] and [1] respectively in the msg.body as an ArrayList.
				 */
				case Client_CreateComment:
					@SuppressWarnings("unchecked")
					ArrayList<String> com = (ArrayList<String>)msg.getBody();
					String postId = com.get(0);
					String comment = com.get(1);
					returnCode = classDB.addComment(msg.getClassroom_ID(), postId, comment);
					msg.setCode(returnCode);
					returnMessage(msg);
					break;
				case Client_GoToClassroom:
					// TODO: Looks like the function isn't implemented in ClassDB yet?
					break;
				case Client_GoToThread:
					// TODO: Same
					break;
				case Client_DeleteClassroom:
					returnCode = classDB.removeClassRoom(msg.getClassroom_ID());
					msg.setCode(returnCode);
					returnMessage(msg);
					break;
				case Client_DeleteThread:
					int p = (Integer)msg.getBody();
					returnCode = classDB.removePost(msg.getClassroom_ID(), p);
					msg.setCode(returnCode);
					returnMessage(msg);
					break;
				/*
				 * Requires postId and commentID which will be stored in the 
				 * msg.body() as an ArrayList<Integer>
				 */
				case Client_DeleteComment:
					@SuppressWarnings("unchecked")
					ArrayList<Integer> commentParams = (ArrayList<Integer>)msg.getBody();
					int postID = commentParams.get(0);
					int commentID = commentParams.get(1);
					returnCode = classDB.removeComment(msg.getClassroom_ID(), postID, commentID);
					msg.setCode(returnCode);
					returnMessage(msg);
					break;	
				default:
					//For illegal requests
					msg.setBody("Illegal Request Sent");
					msg.setCode(-1);
					returnMessage(msg);
					break;
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
	
