import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;

/**
 * Holds user ids.
 * @author chris d
 *
 */
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
		if (userId==null){
			return false;
		}
		return userList.containsKey(userId);
	}
	
	/**
	 * Shows whether a username is contained in the userlist.
	 * @param userId The user name to be searched
	 * @return Returns true if the username is present in the user list, false otherwise.
	 */
	private boolean contains(String userId){
		if (userId==null)
			return false;
		Set<String> s = userList.keySet();
		synchronized(userList) {
			Iterator <String> i = s.iterator();
		    while (i.hasNext()){
		    	String st = i.next();
		    	if(st == userId);
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
	public boolean addUser(String newId, char[] p){
		if (newId==null){
			return false;
		}
		if (newId.length()<3 || newId.length()>30){
			return false;
		}
		boolean found = false;
		synchronized(userList){
			if (!userList.containsKey(newId)){
				//TODO: encrypt User!!!
				User in = new User(newId, p);
				userList.put(newId, in);
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
	public boolean removeUser(String userId){
		if (userId==null)
			return false;
		boolean found = false;
		if (userList.remove(userId)!=null){
			found = true;
		}
		return found;
	}

	/**
	 * A container class for user info
	 * @author chris d
	 *
	 */
	public class User implements Serializable{
		
		private static final long serialVersionUID = 2971288288578571927L;
		private final String name;
		private char[] password;
	
		@SuppressWarnings("unused")
		private User(){
			name = "";
		}
		
		/**
		 * Constructor for User class.
		 * @param u The username
		 * @param p The password. Note that a new char[] is created to store the password. The original 
		 * array should be zeroed out. Otherwise it will stay in memory.
		 */
		public User(String u, char[] p){
			name = u;
			password = new char[p.length];
			for(int i=0; i<p.length; i++)	
				password[i]=p[i];
		}
		
		/**
		 * Returns the unique identifier for the User.
		 * @return
		 */
		public String getId(){
			return name;
		}
		
		public void setPass(char[] p){
			// Zero out the old pass.
			for(int i=0; i<password.length; i++)	
				p[i]=0;
			// Create and fill a new pass.
			password = new char[p.length];
			for(int i=0; i<p.length; i++)	
				password[i]=p[i];
		}
		
		/**
		 * Returns a copy of the char[] array containing the password. </br>
		 * NOTE: you MUST zero out the returned array AS SOON AS you are done with it.
		 * @return Returns a copy of the char[] containing the password.
		 */
		public char[] getPass(){
			char[] out = new char[password.length];
			for(int i=0; i<password.length; i++)	
				out[i]=password[i];
			return out;
		}
	}
	
}