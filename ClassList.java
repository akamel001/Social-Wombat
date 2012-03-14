import java.io.Serializable;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * ClassList is a list of ClassData objects.
 * ClassData objects store a class' associated server info and user permissions.
 * (ClassData objects do NOT contain any posts, comments, or other content.)
 * 
 * @author chris
 *
 */
public class ClassList implements Serializable{

	private static final long serialVersionUID = 8630970578639764636L;
	private Map<String, byte[]> classList;

	/**
	 * Constructor for the ClassList.
	 */
	protected ClassList(){
		classList = Collections.synchronizedMap(new TreeMap<String, byte[]>());
	}

	/**
	 * Adds a ClassRoom to the list. NOTE: if the classroom name is not unique, the class
	 * will not be added and -1 will be returned.
	 * @param c
	 * @param encryptor An AES object that will be used by the function to encrypt the ClassData object
	 * @return
	 */
	protected int addClass(ClassData c, AES encryptor){
		if (c==null || encryptor==null)
			return -1;
		int out = -1;
		synchronized(classList){
			if (classList.containsKey(c.getClassName()))
				out = -1;
			else{
				byte[] cipher;
				try{
					cipher = encryptor.encrypt((Object)c);
					if(cipher!=null){
						classList.put(c.getClassName(), cipher);
						out = 1;
					}
					else 
						out = -1;
				}catch(ClassCastException e){ 
					out = -1;
				}
			}
		}
		return out;
	}

	/**
	 * Removes a ClassRoom from the list. Returns -1 if classroom does not exist.
	 * @param c
	 * @return
	 */
	protected int removeClass(String c){
		if( classList.remove(c)!=null)
			return 1;
		else
			return -1;
	}

	/**
	 * Returns the permissions for a user with regards to a particular classroom.
	 * @param user The user's name
	 * @param c The name of the classroom
	 * @param decryptor An AES object that will be used by the function to decrypt the ClassData object
	 * @return User permissions or -1 if user has no permissions for that classroom
	 */
	protected int getUserPermissions(String user, String c, AES decryptor){
		if (user==null || c==null || decryptor==null)
			return -1;
		byte[] cipher = classList.get(c);
		ClassData class_out;
		try{
			class_out = (ClassData)decryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return -1;
		}
		if(class_out==null)
			return -1;
		Integer out = class_out.getPermissions(user);
		if (out==null)
			return -1;
		else
			return out;
	}

	/**
	 * Sets the permissions for a specific user in a class. <br>
	 * <br>
	 * NOTE: passing -1 as the third parameter will cause the user to be removed from the class.<br>
	 * NOTE: TO ADD A USER to a class (as pending), use this function with permissions set to 0! If 
	 * the user is not enrolled in the class, it will be added to it with permissions of 0. If the 
	 * user is enrolled in the class, its permissions will be set to 0.
	 * @param user The user whose permissions will be changed.
	 * @param c The class in which that user is enrolled.
	 * @param per The new permission value.
	 * @return Returns 1 on success, -1 otherwise.
	 */
	protected int setUserPermissions(String user, String class_name, int per, AES encryptor){
		if(user==null || class_name==null || encryptor==null)
			return -1;
		if (per<-1 || per>2)
			return -1;
		else{
			
			/*
			 * In order to change a user's permissions, you must (a) decrypt the class object, 
			 * (b) do your work on it,  (c) reencrypt it, and then (d) reinsert the cipher into the 
			 * class list (replacing the old class).
			 */
			
			// (a): decrypt the class object
			byte[] cipher = classList.get(class_name);
			ClassData temp_class;
			try{
				temp_class = (ClassData)encryptor.decryptObject(cipher);
			}catch(ClassCastException e){ 
				return -1; 
			}
			if (temp_class==null)
				return -1;
			
			// (b): set user permissions
			else{
				int out = temp_class.setPermission(user, per);
				
				// (c): reencrypt class
				try{
					cipher = encryptor.encrypt((Object)temp_class);
				}catch(ClassCastException e){ 
					return -1; 
				}
				
				// (d): reinsert class into class list
				classList.put(temp_class.getClassName(), cipher);
				return out;
			}
		}
	}

	/**
	 * Returns the instructor for a classroom.
	 * @param className The name of the classroom
	 * @param decryptor AES object used to decrypt the ClassData object before getting the instructor's name
	 * @return Returns the value of the instructor field from the ClassData object with the given name.
	 */
	public String getInstructor(String className, AES decryptor){
		if (className==null || decryptor==null)
			return null;
		ClassData temp_class;
		byte[] cipher = classList.get(className);
		try{
			temp_class = (ClassData)decryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return null;
		}
		if (temp_class==null)
			return null;
		else 
			return temp_class.getInstructor();			
	}

	/**
	 * Returns a Map containing all of the users in the class, including pending users, with permissions.
	 * @param className The name of the class, duh
	 * @param The AES object used for encryption
	 * @return Returns a Map of Strings to Integers. The Strings are the names of the users. 
	 * The Integers represent their permissions with respect to the classroom. A value of ) indicates
	 *  pending enrollment, 1 indicates a student, and 2 indicates a TA. 
	 */
	protected Map<String, Integer> getClassAll(String className, AES encryptor){
		if (className == null || encryptor==null)
			return null;
		ClassData temp_class;
		byte[] cipher = classList.get(className);
		try{
			temp_class = (ClassData)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return null;
		}
		if (temp_class==null)
			return null;	
		else
			return temp_class.getAllUsers();
	}

	/**
	 * Returns a map containing all users enrolled in a class.
	 * @param className The name of the class
	 * @param The AES object used for encryption
	 * @return Returns a Map of Strings to Integers. The Strings are the names of the users. 
	 * The Integers represent their permissions with respect to the classroom. A value of 1 indicates a 
	 * student, 2 indicates a TA, 3 indicates an instructor.
	 */
	protected Map<String, Integer> getClassEnrolled(String className, AES encryptor){
		if (className == null || encryptor==null)
			return null;
		ClassData temp_class;
		byte[] cipher = classList.get(className);
		try{
			temp_class = (ClassData)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return null;
		}
		if (temp_class==null)
			return null;	
		else
			return temp_class.getEnrolledUsers();
	}

	/**
	 * Returns the names of all users with enrollment pending.
	 * @param className The name of the class
	 * @param The AES object used for encryption
	 * @return Returns a list of strings representing the usernames of every user 
	 * in the class with permissions==0.
	 */ 
	protected List<String> getClassPending(String className, AES encryptor){
		if (className == null || encryptor==null)
			return null;
		ClassData temp_class;
		byte[] cipher = classList.get(className);
		try{
			temp_class = (ClassData)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return null;
		}
		if (temp_class==null)
			return null;	
		else
			return temp_class.getPendingUsers();
	}

	/**
	 * Returns a map of classnames mapped to permissions. 
	 * @param user The relevant username.
	 * @return Returns a Map of Strings to Integers. The Strings are the classes in which the user is 
	 * enrolled. The Integers represent the user's permission with respect to that classroom. A value 
	 * of 1 indicates a 
	 * student, 2 indicates a TA, 3 indicates an instructor.
	 */
	protected Map<String, Integer> getUserEnrollment(String userName, AES encryptor){
		Map<String, Integer> out = Collections.synchronizedMap(new TreeMap<String, Integer>());
		boolean found = false;
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String className = i.next();
				/*
				 * You must decrypt each class as you iterate through the list.
				 * Since the class is not changed, there is no need to reëncrypt.
				 */
				ClassData temp_class;
				byte[] cipher = classList.get(className);
				try{
					temp_class = (ClassData)encryptor.decryptObject(cipher);
				}catch(ClassCastException e){
					return null;
				}
				if (temp_class==null){
					// noöp
				}
				else if ( temp_class.getInstructor().equals(userName)){
					found = true;
					out.put( temp_class.getClassName(), 3);
				}
				else{
					int per =  temp_class.getPermissions(userName);
					if (per>0){
						found = true;
						out.put( temp_class.getClassName(), per);
					}	
				}
			}
		}
		if (found)
			return out;
		else
			return null;
	}

	/**
	 * Returns the number of the server on which the passed class is stored
	 * @param className The name of the ClassRoom.
	 * @param encryptor An AES object used to decrypt the ClassRoom object
	 * @return Returns the server number on success, -1 on 
	 */
	public int getClassServer(String className, AES encryptor){
		ClassData temp_class;
		byte[] cipher = classList.get(className);
		try{
			temp_class = (ClassData)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return -1;
		}
		if(temp_class==null)
			return -1;
		else
			return temp_class.getClassServer();
	}

	/**
	 * Returns the port associated with a classroom
	 * @param className The name of the classroom
	 * @param encryptor The AES object used to decrypt the classromm object
	 * @return Returns port for the class, -1 if class is not present in class list
	 */
	public int getClassPort(String className, AES encryptor){
		ClassData temp_class;
		byte[] cipher = classList.get(className);
		try{
			temp_class = (ClassData)encryptor.decryptObject(cipher);
		}catch(ClassCastException e){
			return -1;
		}
		if(temp_class==null)
			return -1;
		else
			return temp_class.getClassPort();
	}

	/**
	 * Removes all of the user's entries from the classlist.
	 * @param userName The name of the user to be deleted.
	 * @return Returns a List<String> containing all of the classes in which the user is in an instructor.
	 */
	public List<String> deleteUser(String userName, AES encryptor){
		List<String> classesToBeDeleted = Collections.synchronizedList(new ArrayList<String>());
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String className = i.next();
				
				/*
				 * In order to change a class, you must (a)deecrypt it, (b) do your work on it,
				 * (c) reencrypt it, and then (d) reinsert the cipher into the class
				 */
				
				// (a): Decrypt class
				ClassData current_class;
				byte[] cipher = classList.get(className);
				try{
					current_class = (ClassData)encryptor.decryptObject(cipher);
				}catch(ClassCastException e){
					current_class=null;
				}
				// (b): do your business
				if (current_class==null){
					//noop
				}
				// if the user is the instructor, delete the classroom and
				// add the classroom to the list of classes to be deleted
				else if (current_class.getInstructor().equals(userName)){
					classList.remove(className);
					classesToBeDeleted.add(className);
				}
				// if the user is not the instructor, call removeUser on the class
				else{
					current_class.removeUser(userName);
				}
				// (c): reencrypt class
				try{
					cipher = encryptor.encrypt((Object)current_class);
				}catch(ClassCastException e){ 
					//TODO: how to handle? 
				}
				// (d): put class back in class list
				if(cipher!=null)
					classList.put(current_class.getClassName(), cipher);
			}
		}
		return classesToBeDeleted;
	}
}
