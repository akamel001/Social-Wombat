import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * ClassData objects store a class' associated server info and user permissions.
 * (ClassData objects do NOT contain any posts, threads or other content.)
 * @author chris
 *
 */
public class ClassData{
	private int id;
	private String name;
	private String instructor;
	private int server;
	private int port;
	private Map<String, Integer> userList;

	/**
	 * This constructor disallows creating a class without an instructor.
	 */
	private ClassData(){}

	/**
	 * Creates a class with the passed user as instructor
	 */
	public ClassData(String inst){
		userList = Collections.synchronizedMap(new HashMap<String, Integer>());
		instructor = inst;
		id = -1;
		name = null;
		server = -1;
		port = -1;
	}
	
	/**
	 * Returns a Map containg all of the users enrolled in the class.
	 * @return
	 */
	public Map<String, Integer> getEnrolled(){
		return userList;
	}

	/**
	 * Sets the name of the class.
	 */
	public int setClassName(String n){
		//TODO: test for alphanumeric!
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
	public boolean isInstructor(String i){
		return i.equals(instructor);
	}

	/**
	 * Sets the server for the class.
	 */
	public int setClassServer(int s, int p){
		if (s<1 || p<1)	
			return -1;
		else{
			server = s;
			port = p;
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

	/** Adds a user for this class with default permmissions=0.
	 * 
	 * @param id
	 * @return
	 */
	public int addUser(String id){
		if (id.equals("") || id == null)
			return -1;
		if(userList.containsKey(id))
			return -1;
		else{
			userList.put(id, 0);
			return 0;
		}
	}


	/**
	 * Removes a user from the class list. 
	 * @param id
	 * @return
	 */
	public int removeUser(String id){
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
		Integer p = userList.get(id);
		if (p==null)
			return -1;
		else
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
		if (p<-1 || p>2)
			return -1;
		// if per is -1, remove user from list
		else if(p==-1){
			userList.remove(id);
			return 1;
		}
		// if per is 0 and user is not in list, add the user
		else if (p==0 && !userList.containsKey(id)){
			addUser(id);
			return 1;
		}
		else{
			userList.put(id, p);
			return 1;
		}
	}

	/**
	 * Returns a newline-separated list of users whose permission is 0.
	 * First line is the name of the Classroom.
	 */
	public String pendingToString(){
		boolean found = false;
		String out = System.getProperty("line.separator") + name + ": Pending users:";
		Set<String> s = userList.keySet();
		synchronized(userList) {  
			Iterator<String> i = s.iterator(); // Must be in synchronized block
			while (i.hasNext()){
				if (userList.get(i.next()) == 0){
					found = true;
					out += System.getProperty("line.separator") + " - " + i.next();
				}
			}
		}
		if (!found)
			return out += System.getProperty("line.separator") + " - [no users awaiting enrollment]";
		else
			return out;
	}

	/**
	 * Returns a newline-separated String of all users associated with a class as well 
	 * as their permissions.
	 * @return
	 */
	public String allUsersToString(){
		String out = System.getProperty("line.separator") + name + ": All users:";
		out += System.getProperty("line.separator") + instructor + " (Instructor)";
		
		Set<String> s = userList.keySet();

		synchronized(userList) {  
			Iterator<String> i = s.iterator(); 
			while (i.hasNext()){
				String user = i.next(); //username
				out += System.getProperty("line.separator") + " - " + user;
				int per = userList.get(user);
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
				}
			}
		}
		return out;
	}
}