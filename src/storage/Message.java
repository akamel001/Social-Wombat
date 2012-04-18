package storage;
import java.io.Serializable;
import java.net.InetAddress;

import security.CheckSum;


public final class Message implements Serializable{

	private static final long serialVersionUID = 8227312404679677099L;
	private InetAddress sender;
	private InetAddress recipient;
	private Object body;
	private MessageType type;
	private String checksum;
	private long nonce;
	private byte[] salt;
	private byte[] iv;
	
	private String userName;
	private int code;
	private String classroom_id;

	
	public enum MessageType {
	    // Client -> Hub only
		Client_LogIn, 
		Client_Logout,
	    Client_GetClassEnrollment,
	    Client_GetUserEnrollment,
	    Client_SetPermissions,	//of users in a class
	    Client_DeleteSelf,
	    Client_RequestEnrollment,
	    Client_ListClassroomRequests,
	    Client_CloseSocket,
	    Client_ChangePassword,
	    Client_GetLastLogin,
	    
	    // Client -> Hub -> Server
	    Client_CreateClassroom,
	    /*
		 * CreatePost requires Post Name, and Post Body.
		 * Since only a msg body is available for storage of both,
		 * they will be stored as a 2 element arraylist with 
		 * Array[0] = Post_Name
		 * Array[1] = Post_body 	
		 */
	    Client_CreateThread,
	    /*
		 * Create client expects postID and comment to be stored as index
		 * [0] and [1] respectively in the msg.body as an ArrayList.
		 */
	    Client_CreateComment,
	    Client_GoToClassroom,
	    Client_GoToThread,	
	    Client_DeleteClassroom,
	    Client_DeleteThread,	// also known as remove post
	    Client_DeleteComment,	//also known as remove comment

	    // Hub -> Server
	    Hub_AuthServer,
	    Hub_Shutdown
	}

	public String getClassroom_ID() {
		return classroom_id; 
	}
	
	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public String generateCheckSum(){
		//return CheckSum.getChecksum(body);
		return CheckSum.getMD5Checksum(body);
	}
	
	public void setClassroom_ID(String classroom_id) {
		this.classroom_id = classroom_id;
	}

	public InetAddress getSender() {
		return sender;
	}

	public void setSender(InetAddress sender) {
		this.sender = sender;
	}

	public InetAddress getRecipient() {
		return recipient;
	}
	
	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}
	public void setRecipient(InetAddress recipient) {
		this.recipient = recipient;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	public byte[] getSalt() {
		return salt;
	}

	public String getUserName() {
		return userName;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}