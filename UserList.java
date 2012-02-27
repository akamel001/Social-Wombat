import java.io.Serializable;
import java.util.*;

public class UserList implements Serializable{
	
	private static final long serialVersionUID = 3273339989709185986L;
	private Map <String, User> userList; 
	
	/**
	 * Creates a new UserList with an empty Map of Users.
	 */
	public UserList(){
		userList = Collections.synchronizedMap(new HashMap<String, User>());
	}

	
	public boolean validateUser(String id){
		Set<String> s = userList.keySet();
		synchronized(userList) {
		      Iterator <String> i = s.iterator();
		      while (i.hasNext()){
		          if(i.next() == id);
		          return true;
		      }
		  }
		return false;
	}
	
	private boolean contains(String id){
		Set<String> s = userList.keySet();
		synchronized(userList) {
			Iterator <String> i = s.iterator();
		    while (i.hasNext()){
		    	if(i.next() == id);
		        	return true;
		    }
		  }
		return false;
	}

	public boolean addUser(String newId){
		if (!contains(newId)){
			userList.put(newId, new User(newId));
			return true;
		}
		return false;
	}
	
	public boolean removeUser(String id){
		//TODO: implement
		return false;
	}
	
	public class User{
		private final String id;
		//private String password;
	
		public User(String u){
			id = u;
			//password = null;
		}
		
		public String getId(){
			return id;
		}
	}
	
}