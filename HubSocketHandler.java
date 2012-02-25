import java.io.*;
import java.net.*;

public class HubSocketHandler extends Thread{
	private static int CLIENT_SOCKET = 4444;
	private static int SERVER_SOCKET = 5050;
	
	ClassList classList;
	UserList userList;
	ServerList serverList;
	Socket socket;

	/*
	 * A handler thread that is spawned for each message sent to a socket.
	 */
	public HubSocketHandler(Socket ser, ClassList classList, UserList userList, ServerList serverList){
		this.socket = ser;
		this.classList = classList;
		this.userList = userList;
		this.serverList = serverList;
	}
	
	/*
	 * Deserialize transmission in socket and convert to a message
	 */
	private Message getMessage(Socket s){
		Message msg = null;
		try {
			InputStream obj = s.getInputStream();
			ObjectInput ois = new ObjectInputStream(obj);
			msg = (Message) ois.readObject();
			//TODO: check if the close should be here or later.
			ois.close();
		} catch (Exception e){
			System.out.println(e.getMessage());
			System.out.println("Deserializing message failed.");
		}
		return msg;
	}
	
	private void returnMessage(Message msg){
		//Get output stream
		try {
			OutputStream obj = socket.getOutputStream();
			ObjectOutput oos = new ObjectOutputStream(obj);
			oos.writeObject(msg);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Return Message Failed");
		}
		
	}
	
	/*
	 * Looks in the message and takes the classroom to find the correct server 
	 * (by InetAddress) that the message should be sent to.
	 */
	private InetAddress getServer(Message msg){
		//get classroom name from msg
		String classID = msg.getClassroom_ID();
		//table lookup of classrooms and their appropriate server
		int server = classList.getClassServer(classID);
		//return server
		return serverList.getAddress(server);
		
	}
	
	/*
	 * Constructs the message that needs to be forwarded 
	 * and receives the response as a message
	 */
	private Message forwardToServer(Message msg){
		//Open a socket connection with appropriate server
		// Server can only be found by classroomID
		InetSocketAddress newSocketAddress = new InetSocketAddress(getServer(msg),SERVER_SOCKET);
		//Create socket
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(newSocketAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: add sendMessage() method
		
		//TODO: wait for response
		
		//TODO: return response message
		
		return null;
	}
	
	/*
	 * When waiting for a message, only a message from a client is expected.
	 * If message is forwarded to the server, that response will only accept
	 * server messages. Authentication responses to clients accept no response.
	 */
	public void run(){
		boolean valid = true;
		//Read and deserialize Message from Socket
		Message msg = getMessage(this.socket);
		if (msg == null){
			System.out.println("Message was null");
			valid = false; // Don't waste time on bad transmissions
		}
		if (valid){
			//Handle the different types of client messages
			switch(msg.getType()) {
				// Client -> Hub
				
				//Client user id is stored as the sessionKey in the Cookie
				//of the message
				// TODO: Possibly move this above switch statement to authenticate all requests
				case Client_LogIn: 
					String sessionKey = msg.getCookie().getKey();
					if(userList.validateUser(sessionKey)){
						//Reply with confirmation
						msg.setBody("true");
						returnMessage(msg);
					} else {
						//Reply with denial
						msg.setBody("false");
						returnMessage(msg);
					}
					break;
				case Client_GetClassEnrollment:
					// TODO: Modify data
					
					// TODO: Create a message to forward to appropriate server
					
					// TODO: Send message and wait for server response
					
					// TODO: Server response is forwarded back to this.socket
					break;
				case Client_GetUserEnrollment:
					
					break;
				case Client_SetPermissions:

					break;
				case Client_GetEnrollmentList:

					break;
				case Client_DeleteSelf:

					break;
				case Client_RequestEnrollment:

					break;
					
				// Client -> Hub -> Server
				case Client_CreateClassroom:

					break;
				case Client_CreatePost:

					break;
				case Client_CreateComment:

					break;
				case Client_GoToClassroom:

					break;
				case Client_GoToThread:

					break;
				case Client_DeleteClassroom:

					break;
				case Client_DeleteThread:

					break;
					
				default:
					//TODO: Send request denied back to the client
					break;
					
			}
		}
	}
	
	
} 
	
