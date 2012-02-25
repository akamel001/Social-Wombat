import java.io.*;
import java.net.*;
import java.util.*;

public class Message implements Serializable{
	private InetAddress sender;
	private InetAddress recipient;
	private String body;
	private MessageType type;
	private Cookie cookie;
	private int code;
	private String classroom_id;
	
	public enum MessageType {
	    Client_Authentication, Client_B, Server_A, Server_B 
	}
	
	public Message(){
		//
	}
	
	/* This is neccessary for message forwarding because the client doesn't
	 * know which server its message should be sent to. So the Hub does the job 
	 * of mapping the classroom to an appropriate server!
	 */
	public String getClassroom_ID() {
		return classroom_id; 
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

	public void setRecipient(InetAddress recipient) {
		this.recipient = recipient;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Cookie getCookie() {
		return cookie;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	
}