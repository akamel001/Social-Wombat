import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.List;

/**
 * ClassData objects store a class' associated server info and user permissions.
 * (ClassData objects do NOT contain any posts, threads or other content.)
 * @author chris
 *
 */
public class ClassData implements Serializable{

	private static final long serialVersionUID = 8032346506582721809L;
	private int id;
	private String name;
	private String instructor;
	private int server;
	private int port;
	private Map<String, Integer> userList;

	/**
	 * This constructor disallows creating a class without an instructor.
	 */
	@SuppressWarnings("unused")
	private ClassData(){}

	/**
	 * Creates a class with the passed user as instructor
	 */
	public ClassData(String inst){
		userList = Collections.synchronizedMap(new TreeMap<String, Integer>());
		instructor = inst;
		id = -1;
		name = null;
		server = -1;
		port = -1;
	}

	/**
	 * Sets the name of the class.
	 */
	public int setClassName(String n){
		name = n;
		return 1;
	}

	/**
	 * Returns the ClassRoom name
	 */
	public String getClassName(){
		return name;
	}

	/**
	 * Returns the ClassRoom id
	 */
	public int getClassId(){
		return id;
	}

	/**
	 * Returns the ClassRoom server
	 */
	public int getClassServer(){
		return server;
	}

	/**
	 * Returns the ClassRoom port
	 */
	public int getClassPort(){
		return port;
	}

	/**
	 * Returns true if the passed string is the instructor's name
	 */
	public boolean isInstructor(String userList){
		if (userList==null)
			return false;
		return userList.equals(instructor);
	}

	/**
	 * Sets the server for the class.
	 */
	public int setClassServer(int serverIp, int portNum){
		if (serverIp<1 || portNum<1)	
			return -1;
		else{
			server = serverIp;
			port = portNum;
			return 1;
		}
	}

	/**
	 * Sets the id for the class.
	 */
	public int setClassId(int i){
		if (id<1)
			return -1;
		else{
			id = i;
			return 1;
		}		
	}

	/**
	 * Removes a user from the class list. 
	 * @param id
	 * @return
	 */
	public int removeUser(String id){
		if (id==null)
			return -1;
		if (userList.remove(id) == null)
			return -1;
		else
			return 0;
	}

	/**
	 * Returns the permissions for the passed user.
	 * @param user The username to be checked.
	 * @return Returns the permissions (0-3) for the user. Returns -1 if user is not present.
	 */
	public int getPermissions(String user){
		if (user==null)
			return -1;
		if (user.equals(instructor))
			return 3;
		Integer p = userList.get(user);
		if (p==null){
			return -1;
		} else
			return (int)p;
	}

	/**
	 * Sets permissions for a user.<br>
	 * NOTE: Passing a permission of -1 removes the student from the class.
	 * NOTE: Passing a permission of 0 with an id that does not exist will add that id to the list.
	 * @param id The id of the user to be changed.
	 * @param p The value to which the permissions will be changed. Value must be -1<=p<=2<br>
	 * -1: removes user from list<b>
	 * @return
	 */
	public int setPermission(String id, int p){
		// if input is out of range, return -1
		if (p<-1 || p>2)
			return -1;
		// if per is -1, remove user from list
		else if(p==-1){
			userList.remove(id);
			return 1;
		}
		// if per is 0 and user is not in list, add the user
		else if (p==0 && !userList.containsKey(id)){
			userList.put(id, p);
			return 1;
		}
		// Otherwise, just add em to the class 
		else{
			userList.put(id, p);
			return 1;
		}
	}


	/**
	 * Returns the instructor for the class.
	 * @return the value of the instructor <i>field</i>
	 */
	protected String getInstructor(){
		return instructor;
	}
	/**
	 * Returns the names of all users with enrollment pending.
	 * @return Returns a list of strings representing the usernames of every user in the class with permissions==0.
	 */
	protected List<String> getPendingUsers(){
		boolean found = false;
		List<String> out = Collections.synchronizedList(new ArrayList<String>());
		Set<String> s = userList.keySet();
		if(userList.isEmpty())
			return null;
		synchronized(userList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				if (userList.get(i.next()) == 0){
					found = true;
					out.add(i.next());
				}
			}
		}
		if (found)
			return out;
		else
			return null;
	}
	
	/**
	 * Returns a map containing all users enrolled in a class.
	 * @return Returns a Map of Strings to Integers. The Strings are the names of the users. 
	 * The Integers represent their permissions with respect to the classroom. A value of 1 indicates a 
	 * student, 2 indicates a TA.
	 */
	protected Map<String, Integer> getEnrolledUsers(){
		boolean found = false;
		Map<String, Integer> out = Collections.synchronizedMap(new TreeMap<String, Integer>());
		// add instuctor
		out.put(instructor, 3);
		Set<String> s = userList.keySet();
		if (userList.isEmpty())
			return null;
		synchronized(userList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String c = i.next();
				int val = userList.get(c); 
				if (val > 0){
					found = true;
					out.put(c,val);
				}
			}
		}
		if (found)
			return out;
		else
			return null;
	}
	
	/**
	 * Returns a Map containing all of the users in the class, including pending users, with permissions.
	 * @return Returns a Map of Strings to Integers. The Strings are the names of the users. 
	 * The Integers represent their permissions with respect to the classroom. A value of ) indicates
	 *  pending enrollment, 1 indicates a student, and 2 indicates a TA. 
	 */
	protected Map<String, Integer> getAllUsers(){
		Map<String, Integer> out = Collections.synchronizedMap(new TreeMap<String, Integer>(userList));
		// add instructor
		out.put(instructor, 3);
		return out;
	}

}