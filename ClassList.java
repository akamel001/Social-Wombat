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
	private Map<String, ClassData> classList;

	/**
	 * Constructor for the ClassList.
	 */
	protected ClassList(){
		classList = Collections.synchronizedMap(new TreeMap<String, ClassData>());
	}

	/**
	 * Adds a ClassRoom to the list. NOTE: if the classroom name is not unique, the class
	 * will not be added and -1 will be returned.
	 * @param c
	 * @return
	 */
	protected int addClass(ClassData c){
		int out;
		synchronized(classList){
			if (classList.containsKey(c.getClassName()))
				out = -1;
			else{
				classList.put(c.getClassName(), c);
				out = 1;
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
	 * @return User permissions or -1 if user has no permissions for that classroom
	 */
	protected int getUserPermissions(String user, String c){
		if (user==null || c==null)
			return -1;
		return classList.get(c).getPermissions(user);
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
	protected int setUserPermissions(String user, String c, int per){
		if (per<-1 || per>2)
			return -1;
		else{
			ClassData tempClass = classList.get(c);
			if (tempClass==null)
				return -1;
			else
				return tempClass.setPermission(user, per);
		}
	}

	/**
	 * Returns the instructor for a classroom.
	 * @param className The name of the classroom
	 * @return Returns the value of the instructor field from the ClassData object with the given name.
	 */
	public String getInstructor(String className){
		ClassData c = classList.get(className);
		if (c==null)
			return null;
		else 
			return c.getInstructor();			
	}
	
	/**
	 * Returns a Map containing all of the users in the class, including pending users, with permissions.
	 * @return Returns a Map of Strings to Integers. The Strings are the names of the users. 
	 * The Integers represent their permissions with respect to the classroom. A value of ) indicates
	 *  pending enrollment, 1 indicates a student, and 2 indicates a TA. 
	 */
	protected Map<String, Integer> getClassAll(String className){
		if (className == null)
			return null;
		ClassData c = classList.get(className);
		if (c !=null)	
			return c.getAllUsers();
		else
			return null;
	}
	
	/**
	 * Returns a map containing all users enrolled in a class.
	 * @return Returns a Map of Strings to Integers. The Strings are the names of the users. 
	 * The Integers represent their permissions with respect to the classroom. A value of 1 indicates a 
	 * student, 2 indicates a TA, 3 indicates an instructor.
	 */
	protected Map<String, Integer> getClassEnrolled(String className){
		if (className == null)
			return null;
		ClassData c = classList.get(className);
		if (c!=null)
			return c.getEnrolledUsers();
		else 
			return null;
	}

	/**
	 * Returns the names of all users with enrollment pending.
	 * @return Returns a list of strings representing the usernames of every user 
	 * in the class with permissions==0.
	 */ 
	protected List<String> getClassPending(String className){
		if (className == null)
			return null;
		ClassData c = classList.get(className);
		if (c!=null)
			return c.getPendingUsers();
		else 
			return null;
	}

	/**
	 * Returns a map of classnames mapped to permissions. 
	 * @param user The relevant username.
	 * @return Returns a Map of Strings to Integers. The Strings are the classes in which the user is 
	 * enrolled. The Integers represent the user's permission with respect to that classroom. A value 
	 * of 1 indicates a 
	 * student, 2 indicates a TA, 3 indicates an instructor.
	 */
	protected Map<String, Integer> getUserEnrollment(String userName){
		Map<String, Integer> out = Collections.synchronizedMap(new TreeMap<String, Integer>());
		boolean found = false;
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String className = i.next();
				ClassData cd =	classList.get(className);
				if (cd.getInstructor().equals(userName)){
					found = true;
					out.put(cd.getClassName(), 3);
				}
				else{
				int per = cd.getPermissions(userName);
					if (per>0){
						found = true;
						out.put(cd.getClassName(), per);
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
	 * @param c the name of the ClassRoom
	 * @return
	 */
	public int getClassServer(String c){
		ClassData temp = classList.get(c);
		if (temp==null)
			return -1;
		else
			return temp.getClassServer();
	}

	/**Returns the port associated with a classroom
	 * 
	 * @param c The name of the classroom
	 * @return Returns port for the class, -1 if class is not present in class list
	 */
	public int getClassPort(String c){
		ClassData temp = classList.get(c);
		if (temp==null)
			return -1;
		else
			return temp.getClassPort();
	}
	
	/**
	 * Removes all of the user's entries from the classlist.
	 * @param userName The name of the user to be deleted.
	 * @return Returns a List<String> containing all of the classes in which the user is in an instructor.
	 */
	public List<String> deleteUser(String userName){
		List<String> classesToBeDeleted = Collections.synchronizedList(new ArrayList<String>());
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String className = i.next();
				ClassData currentClass = classList.get(className);
				
				// if the user is the instructor, delete the classroom and
				// add the classroom to the list of classes to be deleted
				if (currentClass.getInstructor().equals(userName)){
					classList.remove(className);
					classesToBeDeleted.add(className);
				}
				// if the user is not the instructor, call removeUser on the class
				else{
					currentClass.removeUser(userName);
				}
			}
		}
		return classesToBeDeleted;
	}
}
