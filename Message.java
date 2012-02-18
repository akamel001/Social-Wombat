import java.io.*;
import java.net.*;
import java.util.*;

public class Message{
	private InetAddress sender;
	private InetAddress recipient;
	private String body;
	private MessageType type;
	
	public enum MessageType {
	    A, B 
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
	
	public void setSender(InetAddress s){
		sender = s;
	}
	
	/*
	public InetAddress getRecipient(){
		return recipient;
	}
	
	public MessageType getType(){
		return type;
	}
	
	public String getBody(){
		return body;
	}
	*/
	
}