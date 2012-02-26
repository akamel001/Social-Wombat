import java.io.*;
import java.net.*;
import java.util.*;

public class Message{
	private InetAddress sender;
	private InetAddress recipient;
	private String body;
	private MessageType type;
	private Cookie cookie;
	private int code;
	
	public enum MessageType {
	    Client_Authentication, Client_B, Server_A, Server_B 
	}
	
	public Message(){
		//
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