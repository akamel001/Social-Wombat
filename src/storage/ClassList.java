package storage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import security.AES;


/**
 * ClassList is a list of ClassData objects.
 * ClassData objects store a class' associated server info and user permissions.
 * (ClassData objects do NOT contain any posts, comments, or other content.)
 * 
 * @author chris
 *
 */
public final class ClassList implements Serializable{

	private static final long serialVersionUID = 8630970578639764636L;
	private Map<String, byte[]> classList;

	/**
	 * Constructor for the ClassList.
	 */
	public ClassList(){
		classList = Collections.synchronizedMap(new TreeMap<String, byte[]>());
	}

	/**
	 * Adds a ClassRoom to the list. NOTE: if the classroom name is not unique, the class
	 * will not be added and -1 will be returned.
	 * @param c
	 * @param encryptor An AES object that will be used by the function to encrypt the ClassData object
	 * @return 1 on success, -1 on failure
	 */
	public int addClass(ClassData class_data, AES encryptor){
		if(class_data==null || encryptor==null)
			return -1;
		return encryptAndAddClass(class_data, encryptor);
	}

	/**
	 * Removes a ClassRoom from the list. Returns -1 if classroom does not exist.
	 * @param c
	 * @return
	 */
	public int removeClass(String c){
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
	public int getUserPermissions(String user_name, String class_name, AES encryptor){
		if (user_name==null || class_name==null || encryptor==null)
			return -1;

		// Get class
		ClassData class_out = getAndDecryptClass(class_name, encryptor);
		if(class_out==null)
			return -1;

		// Get permissions
		Integer out = class_out.getPermissions(user_name);
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
	public int setUserPermissions(String user, String class_name, int per, AES encryptor){
		// sanity checks
		if(user==null || class_name==null || encryptor==null)
			return -1;
		if (per<-1 || per>2)
			return -1;

		synchronized(classList){

			// (a): get class
			ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
			if (temp_class==null)
				return -1;

			// (b): set user permissions
			if (temp_class.setPermission(user, per)==-1)
				return -1;

			// (c): reencrypt and re-add class
			return this.encryptAndAddClass(temp_class, encryptor);
		}
	}

	/**
	 * Returns the instructor for a classroom.
	 * @param className The name of the classroom
	 * @param decryptor AES object used to decrypt the ClassData object before getting the instructor's name
	 * @return Returns the value of the instructor field from the ClassData object with the given name.
	 */
	public String getInstructor(String class_name, AES encryptor){
		if (class_name==null || encryptor==null)
			return null;

		// Get class
		ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
		if (temp_class==null)
			return null;
		else 
			//return instructor
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
	protected Map<String, Integer> getClassAll(String class_name, AES encryptor){
		if (class_name == null || encryptor==null)
			return null;

		// Get class
		ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
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
	public Map<String, Integer> getClassEnrolled(String class_name, AES encryptor){
		if (class_name == null || encryptor==null)
			return null;

		// Get class
		ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
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
	public List<String> getClassPending(String class_name, AES encryptor){
		if (class_name == null || encryptor==null)
			return null;

		// Get class
		ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
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
	public Map<String, Integer> getUserEnrollment(String userName, AES encryptor){
		// Sanity check
		if(userName==null || encryptor==null)
			return null;

		Map<String, Integer> out = Collections.synchronizedMap(new TreeMap<String, Integer>());
		boolean found = false;
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String class_name = i.next();
				/*
				 * You must decrypt each class as you iterate through the list.
				 * Since the class is not changed, there is no need to reëncrypt.
				 */
				ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
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
	public int getClassServer(String class_name, AES encryptor){
		if(class_name==null || encryptor==null)
			return -1;

		// Get class
		ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
		if (temp_class==null)
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
	public int getClassPort(String class_name, AES encryptor){
		if(class_name==null || encryptor==null)
			return -1;

		// Get class
		ClassData temp_class = this.getAndDecryptClass(class_name, encryptor);
		if (temp_class==null)
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
		//sanity check
		if(userName==null || encryptor==null)
			return null;

		List<String> classesToBeDeleted = Collections.synchronizedList(new ArrayList<String>());
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String class_name = i.next();

				// (a): Get class
				ClassData current_class = this.getAndDecryptClass(class_name, encryptor);
				if (current_class==null){
					//noop
				}

				// (b): if the user is the instructor, delete the classroom and
				//		add the classroom to the list of classes to be deleted
				else if (current_class.getInstructor().equals(userName)){
					classList.remove(class_name);
					classesToBeDeleted.add(class_name);
				}

				// if the user is not the instructor, call removeUser on the class
				else{
					current_class.removeUser(userName);
				}
				// (c): Put class back into list
				this.encryptAndAddClass(current_class, encryptor);
			}
		}
		return classesToBeDeleted;
	}

	/**
	 * Encrypts and adds a class to the class list
	 * @param c
	 * @param encryptor
	 * @return 1 on success, -1 on failure
	 */
	private int encryptAndAddClass(ClassData c, AES encryptor){
		// sanity check
		if (c==null || encryptor==null)
			return -1;

		synchronized(classList){
			// (a) encrypt + add class
			try{
				byte[] cipher = encryptor.encrypt((Object)c);
				if(cipher!=null){
					classList.put(c.getClassName(), cipher);
					return 1;
				}
				else 
					return -1;
			}catch(ClassCastException e){ 
				return -1;
			}

		}	
	}

	/**
	 * 
	 * @param class_name
	 * @param encryptor
	 * @return The ClassData on success, null on failure
	 */
	private ClassData getAndDecryptClass(String class_name, AES encryptor){
		// sanity check
		if (class_name==null || encryptor==null)
			return null;

		byte[] cipher = classList.get(class_name);
		if (cipher==null)
			return null;

		ClassData class_out;
		try{
			class_out = (ClassData)encryptor.decryptObject(cipher);
			if (class_out==null)
				return null;
			else
				return class_out;
		}catch(ClassCastException e){
			return null;
		}
	}
}
