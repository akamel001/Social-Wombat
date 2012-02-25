import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;



/**
 * ClassList is a list of ClassData objects.
 * ClassData objects store a class' associated server info and user permissions.
 * (ClassData objects do NOT contain any posts, threads or other content.)
 * 
 * @author chris
 *
 */
public class ClassList {

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
	 * Returns a newline-separated String of all pending users for the selected ClassRoom
	 * @param c the name of the ClassRoom
	 * @return
	 */
	public String pendingToString(String c){
		ClassData temp = classList.get(c);
		if (temp==null)
			return null;
		else
			return temp.pendingToString();
	}
	
	/**
	 * Returns a newline-separated String of ALL users (as well as their permissions) for a selected classroom.
	 * @param c the name of the classroom
	 * @return
	 */
	public String usersToString(String c){
		ClassData temp = classList.get(c);
		if (temp==null)
			return null;
		else
			return temp.allUsersToString();
	}
	
	public String classesToString(String user){
		String out = System.getProperty("line.separator") + "Class List:";
		boolean found = false;
		Set<String> s = classList.keySet();
		synchronized(classList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String className = i.next();
				ClassData cd =	classList.get(className);
				int per = cd.getPermissions(user);
				if (per>0 && per<4){
					found = true;					
					out += System.getProperty("line.separator") + " - " + cd.getClassName();
					switch (per) {
					case 0: 
						out += " (Pending)";
						break;
					case 1: 
						out += " (Enrolled)";
						break;
					case 2: 
						out += " (TA)";
						break;
					case 3: 
						out += " (Instructor)";
						break;
					}
				}
			}
		}
		if (!found)
			out += System.getProperty("line.separator") + " - [none available]";
		return out;
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
