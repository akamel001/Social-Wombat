import java.io.Serializable;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
	 * @deprecated
	 * @param user_name The username to be checked
	 * @param pass The password to be checked. Zero out with Arrays.fill(pass, '0') when no longer needed.
	 * @param encryptor
	 * @return True if the user password combo exist.
	 */	public boolean validateUser(String user_name, char[] pass, AES encryptor){
		if (user_name==null){
			return false;
		}
		if ( !user_list.containsKey(user_name))
			return false;
		
		// (a) get user
		User temp_guy = this.getAndDecryptUser(user_name, encryptor);
		
		// (b) test user's pass against passed pass
		if (Arrays.equals(pass, temp_guy.password)){
			// Must zero out password in temp user
			temp_guy.zeroUser();
			return true;
		}
		else{
			temp_guy.zeroUser();
			return false;
		}
	}
	
	/**
	 * Returns the user's password
	 * @param user_name The user's name
	 * @param encryptor The hub encryption
	 * @return A char[] with the user's pass. NOTE: MUST BE ZEROED> Use Arrays.fill(pass, '0')
	 */
	public char[] getUserPass(String user_name, AES encryptor){
		// (a) get user
		if(user_name==null || encryptor==null)
			return null;
		User temp_guy = this.getAndDecryptUser(user_name, encryptor);
		if (temp_guy==null)
			return null;
		return temp_guy.password;
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
	 * @param new_id The username for the new user.
	 * @param pass A char[] containing the user's password -- cannot be length 0. This password will be zeroed out once it is entered!!!!!
	 * @param encryptor and AES object with which to encrypt the User data.
	 * @return Returns true if the user was added. Returns false if the user already exists.
	 */
	public int addUser(String new_id, char[] pass, AES encryptor){
		// Sanity checks
		if (new_id==null || pass.length==0){
			return -1;
		}
		//TODO: parse username and password

		if (new_id.length()<3 || new_id.length()>30)
			return -1;
		
		synchronized(user_list){
			if (!user_list.containsKey(new_id)){
				User new_guy = new User(new_id, pass);
				int out =  this.encryptAndAddUser(new_guy, encryptor);
				zeroCharArray(pass);
				return out;
			}
			else{
				return -1;
			}
		}
	}
	
	/**
	 * Changes a User's password.
	 * @param user_name The user's id
	 * @param p A byte array containing the new password 
	 * @param encryptor An AES object to encrypt the 
	 * @return
	 */
	public int changeUserPassword(String user_name, char[] p, AES encryptor){
		if(user_name==null || p==null || encryptor==null || !user_list.containsKey(user_name))
			return -1;
		else{
			synchronized(user_list){
				// (a) get user
				User temp_guy = this.getAndDecryptUser(user_name, encryptor);
				if (temp_guy==null)
					return -1;
				
				// (b) reset pass
				temp_guy.setPass(p);
				
				// (c) reenter user into user list;
				this.encryptAndAddUser(temp_guy, encryptor);
				temp_guy.zeroUser();
			}
			return 1;
		}
	}
	
	/**
	 * Used to update a User's last login time and ip address.
	 * @param user_name The user to update.
	 * @param ip The ip to be added.
	 * @param d The new timestamp
	 * @param encryptor The AES encryption object.
	 * @return Returns true if the user has been updated. Returns false otherwise.
	 */
	public int updateLastLogin(String user_name, InetAddress ip, Date d,  AES encryptor){
		if(user_name==null || ip==null || d==null || encryptor==null || !user_list.containsKey(user_name))
			return -1;
		else{
			synchronized(user_list){
				// (a) get user
				User temp_guy = this.getAndDecryptUser(user_name, encryptor);
				if (temp_guy==null)
					return -1;
				
				// (b) reset info
				temp_guy.setAddress(ip);
				temp_guy.setTimeStamp(d);
				
				// (c) reenter user into user list;
				this.encryptAndAddUser(temp_guy, encryptor);
				temp_guy.zeroUser();
			}
			return 1;
		}
	} 
	
	/**
	 * Returns a String containing the last login info for a User.
	 * @param user_name The User 
	 * @param decryptor An AES object
	 * @return Returns a String of the form "Last login: February 29, 2012 2:09:29 PM EST. From 192.168.1.0"
	 */
	public String getLastLogin(String user_name, AES decryptor){
		if(user_name==null || decryptor==null || !user_list.containsKey(user_name))
			return null;
		else{
			// (a) get user
			User temp_guy = this.getAndDecryptUser(user_name, decryptor);
			if (temp_guy==null)
				return null;
			
			// (b) get last login
			SimpleDateFormat sdf = new SimpleDateFormat("MMMMM d, yyyy 'at' HH:mm:ss a, z");
			String out = "Last login: " +
				sdf.format(temp_guy.getLastTimeStamp()) +
				". From: " +
				temp_guy.getLastAddress().getHostAddress();
			
			temp_guy.zeroUser();
			return out;
		}
	}

	/**
	 * Retrieves a User from the user list.
	 * @param name The name of the User to be retrieved.
	 * @param decryptor An AES object used to decrypt the User
	 * @return Returns a  copy of the User object. NOTE: call User.zeroUser() when finished
	 *  with the returned User to zero out the user's password!!!!
	 */
	public User getUser(String user_name, AES decryptor){
		return this.getAndDecryptUser(user_name, decryptor);
	}
	

	/**
	 * Removes a user from the userlist.
	 * @param id The username of the user to be removed.
	 * @return Returns true if the user was successfully removed. Returns false if the user was not 
	 * present in the user list. 
	 */
	public int removeUser(String user_name){
		if (user_name==null)
			return -1;
		if (user_list.remove(user_name)!=null){
			return 1;
		}
		else
			
			return -1;
	}

	/**
	 * Zeros out the passed char array
	 * @param c
	 */
	public static void zeroCharArray(char[] c){
		if (c.length>0){
			for(int i=0; i<c.length; i++)
				c[i]='0';
		}
			
	}
	
	/**
	 * Adds a user to the user list <\br>
	 * WARNING: WILL OVERWRITE OLD USER WITH SAME ID!!!!!!!
	 * @param new_user
	 * @param encryptor
	 * @return 1 on success, -1 on failure 
	 */
	private int encryptAndAddUser(User new_user, AES encryptor){
		if( new_user==null || encryptor==null)
			return -1;
		try{
			byte[] cipher = encryptor.encrypt(new_user);
			user_list.put(new_user.getId(), cipher);
			return 1;
		}catch(ClassCastException e){
			return -1;
		}
	}
	
	/**
	 * Returns a user frm the user list
	 * @param user_name
	 * @param encryptor
	 * @return User on success, null on failure
	 */
	private User getAndDecryptUser(String user_name, AES encryptor){
		if( user_name==null || encryptor==null)
			return null;
		try{
			byte[] cipher = user_list.get(user_name);
			return (User)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return null;
		}
	}
	
	/**
	 * Returns a list containing every user
	 * @param encryptor Hub encryptor
	 * @return Returns a list with every user
	 */
	public List<String> getAllUsers(AES encryptor){
		List<String> list_out = Collections.synchronizedList(new ArrayList<String>());
		Set<String> s = user_list.keySet();
		synchronized(user_list) {
			Iterator <String> i = s.iterator();
			while (i.hasNext()){
				String st = i.next();
				User user = this.getAndDecryptUser(st, encryptor);
				if (user!=null){
					String name = user.name;
					list_out.add(name);
					user.zeroUser();
				}
			}
		}
		if (list_out.isEmpty())
			return null;
		else
			return list_out;
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
		 * array should be zeroed out. Otherwise, it will stay in memory. Zero out with Arrays.fill(password, '0')
		 */
		public User(String u, char[] p){
			if (u==null)
				name = "void";
			else
				name = u;
			password = Arrays.copyOf(p, p.length);
			last_login_time = new Date();
			last_login_address = null;
		}
		
		/**
		 * Zeros out a user's password.
		 */
		public void zeroUser(){
			if(password!=null){
				Arrays.fill(password, '0');
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
		 * Resets an encrypted version of the User's password.
		 * @param p An encrypted version of the new password. NOTE: this parameter will NOT be 
		 * zeroed out after the password is set!!! Use Arrays.fill(password, (byte)0)
		 */
		public void setPass(char[] p){
			if (p==null)
				return;
			
			// Zero out the old pass.
			Arrays.fill(password, '0');
			
			// Create and fill a new pass.
			password = Arrays.copyOf(p, p.length);
		}

		/**
		 * Returns a copy of the byte[] array containing the encrypted password. </br>
		 * NOTE: you MUST zero out the returned array AS SOON AS you are done with it.
		 * @return Returns a copy of the char[] containing the password. Zero out with Arrays.fill(password, (byte)0)
		 */
		private char[] getPass(){
			return Arrays.copyOf(password, password.length);
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