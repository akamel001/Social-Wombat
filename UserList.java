import java.io.Serializable;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public final class UserList implements Serializable{

	private static final long serialVersionUID = 3273339989709185986L;
	private Map <String, byte[]> user_list; 

	/**
	 * Creates a new UserList with an empty Map of Users.
	 */
	public UserList(){
		user_list = Collections.synchronizedMap(new TreeMap<String, byte[]>());
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
		return user_list.containsKey(userId);
	}

	/**
	 *  @deprecated I must have been drunk when I wrote this -- cd.
	 * Shows whether a username is contained in the userlist.
	 * @param userId The user name to be searched
	 * @return Returns true if the username is present in the user list, false otherwise.
	 */
	private boolean contains(String userId){
		if (userId==null)
			return false;
		Set<String> s = user_list.keySet();
		synchronized(user_list) {
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
	 * NOTE: NEEDS USERNAME AND PASSWORD PARSING/AUTHENTICATION
	 * @param newId The username for the new user.
	 * @param p A char[] containing the user's password -- cannot be length 0.
	 * @param encryptor and AES object with which to encrypt the User data.
	 * @return Returns true if the user was added. Returns false if the user already exists.
	 */
	public boolean addUser(String newId, char[] p, AES encryptor){
		if (newId==null || p.length==0){
			return false;
		}
		//TODO: parse username and password
		if (newId.length()<3 || newId.length()>30){
			return false;
		}
		boolean ok = false;
		synchronized(user_list){
			if (!user_list.containsKey(newId)){
				User new_guy = new User(newId, p);
				byte[] cipher = encryptor.encrypt(new_guy);
				user_list.put(newId, cipher);
				ok = true;
			}
		}
		return ok;
	}
	
	/**
	 * Changes a User's password.
	 * @param userId
	 * @param p
	 * @param encryptor
	 * @return
	 */
	public boolean changeUserPassword(String userId, char[] p, AES encryptor){
		if(userId==null || p==null || encryptor==null || !user_list.containsKey(userId))
			return false;
		else{
			try{
				User temp_guy = (User)encryptor.decryptObject(user_list.get(userId));
				temp_guy.setPass(p);
				byte[] cipher = encryptor.encrypt(temp_guy);
				user_list.put(userId, cipher);
				temp_guy.zeroUser();
			}catch (ClassCastException e){
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Used to update a User's last login time and ip address.
	 * @param userId The user to update.
	 * @param ip The ip to be added.
	 * @param d The new timestamp
	 * @param encryptor The AES encryption object.
	 * @return Returns true if the user has been updated. Returns false otherwise.
	 */
	public boolean updateLastLogin(String userId, InetAddress ip, Date d,  AES encryptor){
		if(userId==null || ip==null || d==null || encryptor==null || !user_list.containsKey(userId))
			return false;
		else{
			try{
				User temp_guy = (User)encryptor.decryptObject(user_list.get(userId));
				temp_guy.setAddress(ip);
				temp_guy.setTimeStamp(d);
				byte[] cipher = encryptor.encrypt(temp_guy);
				user_list.put(userId, cipher);
				temp_guy.zeroUser();
			}catch (ClassCastException e){
				return false;
			}
			return true;
		}
	} 
	
	/**
	 * Returns a String containing the last login info for a User.
	 * @param userId The User 
	 * @param decryptor An AES object
	 * @return Returns a String of the form "Last login: February 29, 2012 2:09:29 PM EST. From 192.168.1.0"
	 */
	public String getLastLogin(String userId, AES decryptor){
		if(userId==null || decryptor==null || !user_list.containsKey(userId))
			return null;
		else{
			String out = "";
			try{
				User temp_guy = (User)decryptor.decryptObject(user_list.get(userId));
				SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy 'at' HH:mm:ss a, z");
				out += "Last login: " +
					sdf.format(temp_guy.getLastTimeStamp()) +
					". From: " +
					temp_guy.getLastAddress().getHostAddress();
				temp_guy.zeroUser();
			}catch (ClassCastException e){
				return null;
			}
		}
		return null;
	}

	/**
	 * Retrieves a User from the user list.
	 * @param name The name of the User to be retrieved.
	 * @param decryptor An AES object used to decrypt the User
	 * @return Returns a  copy of the User object. NOTE: call User.zeroUser() when finished
	 *  with the returned User to zero out the user's password!!!!
	 */
	public User getUser(String name, AES decryptor){
		if (name==null || decryptor==null || !user_list.containsKey(name))
			return null;
		else{
			byte[] out = user_list.get(name);
			if (out != null){
				try{
					User the_dude = (User)decryptor.decryptObject(out); 
					return the_dude;
				} catch (ClassCastException e){
					return null;
				}
			}
		}
		return null;
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
		if (user_list.remove(userId)!=null){
			found = true;
		}
		return found;
	}

	/**
	 * A container class for user info
	 * @author chris d
	 *
	 */
	public final class User implements Serializable{

		private static final long serialVersionUID = 2971288288578571927L;
		private final String name;
		private char[] password;
		private Date last_login_time;
		private InetAddress last_login_address;

		@SuppressWarnings("unused")
		private User(){
			name = "";
		}

		/**
		 * Constructor for User class.
		 * @param u The username
		 * @param p The password. Note that a new char[] is created to store the password. The original 
		 * array should be zeroed out. Otherwise, it will stay in memory.
		 */
		public User(String u, char[] p){
			if (u==null)
				name = "void";
			else
				name = u;
			password = new char[p.length];
			last_login_time = new Date();
			last_login_address = null;
			for(int i=0; i<p.length; i++)	
				password[i]=p[i];
		}
		
		/**
		 * Zeros out a user's password.
		 */
		public void zeroUser(){
			if(password!=null){
				int l = password.length;
				for (int i=0; i<l; i++)
					password[i] = '0';
			}
		}

		/**
		 * Returns the unique identifier for the User.
		 * @return
		 */
		public String getId(){
			return name;
		}

		/**
		 * Resets a User's password.
		 * @param p The new password. NOTE: this parameter will be zeroed out after the password is set!!!
		 */
		public void setPass(char[] p){
			// Zero out the old pass.
			for(int i=0; i<password.length; i++)	
				password[i]='0';
			// Create and fill a new pass.
			password = new char[p.length];
			for(int i=0; i<p.length; i++)	
				password[i]=p[i];
			// Zero out the passed password
			for(int i=0; i<p.length; i++)	
				p[i]='0';
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

		/**
		 * Returns the InetAddress currently held in the user object
		 * @return InetAddress 
		 */
		public InetAddress getLastAddress(){
			return last_login_address;
		}

		/**
		 * Returns the date currently held in the User object.
		 * @return
		 */
		public Date getLastTimeStamp(){
			return new Date(last_login_time.getTime());
		}

		/**
		 * Sets the last_login_address field
		 * @param i The new InetAddress
		 * @return Returns 1 if successful, -1 otherwise.
		 */
		public int setAddress(InetAddress i){
			if (i==null)
				return -1;
			else{
				last_login_address = i;
				return 1;
			}
		}

		/**
		 * Sets the last_login_time field.
		 * @param d The new Date.
		 * @return Returns 1 on success, -1 otherwise.
		 */
		public int setTimeStamp(Date d){
			if(d== null)
				return -1;
			else{
				last_login_time = d;
				return 1;
			}
		}
	}

}