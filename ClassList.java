import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



/**
 * ClassList is a list of ClassData objects.
 * ClassData objects store a class' associated server info and user permissions.
 * (ClassData objects do NOT contain any posts, comments, or other content.)
 * 
 * @author chris
 *
 */
public class ClassList implements Serializable{

	private Map<String, ClassData> classList;
	
	/**
	 * Constructor for the ClassList.
	 */
	public ClassList(){
		classList = Collections.synchronizedMap(new HashMap<String, ClassData>());
	}
	
	/**
	 * Adds a ClassRoom to the list. NOTE: if the classroom name is not unique, the class
	 * will not be added and -1 will be returned.
	 * @param c
	 * @return
	 */
	public int addClass(ClassData c){
		if (classList.containsKey(c.getClassName()))
			return -1;
		else{
			classList.put(c.getClassName(), c);
			return 1;
		}
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
	 * @return User permissions or -1 if user has no permissions for that classroom
	 */
	public int getUserPermissions(String user, String c){
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
	public int setUserPermissions(String user, String c, int per){
		if (per<0 || per>2)
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
	 * Returns a Map containing all users in a classroom.
	 * The key is the user's name, the value is their permissions.
	 * @param c
	 * @return
	 */
	public Map<String, Integer> getClassEnrollment(String c){
		ClassData temp = classList.get(c);
		if (temp !=null)	
			return temp.getEnrolled();
		else
			return null;
	}
	
	/**
	 * Returns A Map of all of the classes a user is enrolled in (pending included)
	 * @param user
	 * @return Returns a Map containing all of the classes a user is enrolled in, null if none exist.
	 */
	public Map<String, Integer> getUserEnrollment(String user){
		Map<String, Integer> out = Collections.synchronizedMap(new HashMap<String, Integer>());
		boolean found = false;
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String className = i.next();
				ClassData cd =	classList.get(className);
				int per = cd.getPermissions(user);
				if (per>-1){
					found = true;
					out.put(cd.getClassName(), per);
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
}
