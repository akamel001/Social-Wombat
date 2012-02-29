import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;

public class UserList implements Serializable{
	
	private static final long serialVersionUID = 3273339989709185986L;
	private Map <String, User> userList; 
	
	/**
	 * Creates a new UserList with an empty Map of Users.
	 */
	public UserList(){
		userList = Collections.synchronizedMap(new TreeMap<String, User>());
	}

	/**
	 * Shows whether a username exists in the userlist
	 * @param id The username to be validated.
	 * @return Returns true if the username is present in the user list, false otherwise.
	 */
	public boolean validateUser(String userId){
		Set<String> s = userList.keySet();
		synchronized(userList) {
		      Iterator <String> i = s.iterator();
		      while (i.hasNext()){
		          if(i.next() == userId);
		          return true;
		      }
		  }
		return false;
	}
	
	/**
	 * Shows whether a username is contained in the userlist.
	 * @param userId The user name to be searched
	 * @return Returns true if the username is present in the user list, false otherwise.
	 */
	private boolean contains(String userId){
		Set<String> s = userList.keySet();
		synchronized(userList) {
			Iterator <String> i = s.iterator();
		    while (i.hasNext()){
		    	if(i.next() == userId);
		        	return true;
		    }
		  }
		return false;
	}

	/**
	 * Adds a new user to the userlist.<br>
	 * <br>
	 * The new username must not already be present in the user list. If it is present,
	 * the new user will not be added.
	 * @param newId The username for the new user.
	 * @return Returns true if the user was added. Returns false if the user already exists.
	 */
	public boolean addUser(String newId){
		boolean found = false;
		synchronized(userList){
			if (!contains(newId)){
				userList.put(newId, new User(newId));
				found = true;
			}
		}
		return found;
	}
	
	/**
	 * Removes a user from the userlist.
	 * @param id The username of the user to be removed.
	 * @return Returns true if the user was successfully removed. Returns false if the user was not 
	 * present in the user list. 
	 */
	public boolean removeUser(String id){
		boolean found = false;
		if (userList.remove(id)==null){
			found = true;
		}
		return found;
	}
	
	/**
	 * A container class for user info
	 * @author chris d
	 *
	 */
	public class User{
		private final String id;
		//private String password;
	
		/**
		 * Constructor for User class.
		 */
		public User(String u){
			id = u;
			//password = null;
		}
		
		/**
		 * Returns the unique identifier for the User.
		 * @return
		 */
		public String getId(){
			return id;
		}
	}
	
}