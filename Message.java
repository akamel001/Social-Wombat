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
	
	public InetAddress getSender(){
		return sender;
	}
	
	public InetAddress getRecipient(){
		return recipient;
	}
	
	public MessageType getType(){
		return type;
	}
	
	public String getBody(){
		return body;
	}
	
	public Cookie getCookie(){
		return cookie;
	}
	
	public int getCode(){
		return code;
	}
	
	public void setSender(InetAddress s){
		sender = s;
	}
	
	public void setRecipient(InetAddress i){
		recipient = i;
	}
	
	public void setType(MessageType t){
		type = t;
	}
	
	public void setBody(String s){
		body = s;
	}
	
	public void setCookie(Cookie c){
		cookie = c;
	}
	
	public void setCode(int c){
		code = c;
	}
}