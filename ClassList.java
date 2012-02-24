import java.util.*;

/**
 * @author cd
 *
 */

public class ClassList {
	private List <Permissions> classList;  // NOTE: CHANGE TO TWO SYNCHRONIZED MAPS -- one keyed on classes, the other on users!!!!

	public ClassList(){
		List<Permissions> classList = Collections.synchronizedList(new ArrayList<Permissions>());
	}

	/*
	 * Returns a string containing a list of all classes for which a user has permissions.
	 */
	public String getUserPermissions(String id){
		String out = ""; 
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator();
			while (i.hasNext()){
				if (i.next().getUserId() == id && i.next().getPermission()>=0){
					out += i.next().getClassId() + " - ";
					switch (i.next().getPermission()) {
					case 0: 
						out += "Pending";
						break;
					case 1: 
						out += "Enrolled";
						break;
					case 2: 
						out += "TA";
						break;
					case 3: 
						out += "Instructor";
						break;
					}
					out += System.getProperty("line.separator");
				}
			}
		}
		return out;
	}

	/*
	 * Returns true if there is a single tuple containing the class id
	 */
	public boolean containsClass(String cid){
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); // Must be in synchronized block
			while (i.hasNext()){
				if (i.next().getClassId() == cid)
					return true;
			}
		}
		return false;
	}
	
	/*
	 * Returns true if there is a tuple with the class id/user id pair.
	 */
	public boolean containsUser(String cid, String uid){
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); // Must be in synchronized block
			while (i.hasNext()){
				if (i.next().getClassId() == cid && i.next().getUserId() ==uid)
					return true;
			}
		}
		return false;
	}

	/*
	 * Returns a user's permissions for a class
	 */
	public int getClassPermissions(String cid, String uid){
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); // Must be in synchronized block
			while (i.hasNext()){
				if (i.next().getClassId() == cid && i.next().getUserId() == uid)
					return i.next().getPermission();
			}
		}
		return -1;
	}
	
	/*
	 * Adds a class to the list. The passed userId is set as the owner. 
	 * If the class exists, returns -1.
	 */
	public int addClass(String cid, String uid){
		if (containsClass(cid)){
			return -1;
		}
		else{
			classList.add(new Permissions(cid, uid, 3));
		}
		return -1;
	}
	
	/*
	 * Adds a user/class pair with the default permissions (0).
	 * If a user/class pair exists, returns -1.
	 */
	public int addUser(String cid, String uid){
		if (containsUser(cid, uid)){
			return -1;
		}
		else{
			classList.add(new Permissions(cid, uid, 0));
		}
		return -1;
	}
	
	/*
	 * Changes permissions on a user/class pair.
	 * If the new value is out of range (or if n=3, which would change permissions
	 * to owner), returns -1.
	 */
	public int changePermissions(String cid, String uid, int n){
		if(n<0 || n>2)
			return -1;
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); 
			while (i.hasNext()){
				if (i.next().getClassId() == cid && i.next().getUserId() == uid){
					i.next().setPermission(n);
					return 0;
				}
			}	
		}
		return -1;
	}
	
	/*
	 * Removes all tuples containing a class from the list.
	 */
	public int removeClass(String cid){
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); 
			while (i.hasNext()){
				if (i.next().getClassId() == cid){
					classList.remove(i.next());
				}
			}
			return 0;
		}
	}

	/*
	 * Removes all tuples containing a user from the list.
	 */
	public int removeUser(String uid){
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); 
			while (i.hasNext()){
				if (i.next().getUserId() == uid){
					classList.remove(i.next());
				}
			}
			return 0;
		}
	}
	
	/*
	 * Returns the InetAddress of the server that stores the information
	 * for a given classroom
	 */
	public int getServer(String cid){
		return -1;
	}
	
	/*
	 * Subclass used to store class/user/permission tuple.
	 */
	private class Permissions{
		final private String classId;
		final private String userId;
		private int per;

		/*
		 * Prohibits creation of a Permissions object without a class and user id.
		 */
		private Permissions(){
			classId = null;
			userId = null;
			per = 0;
		}

		public Permissions(String c, String u, int p){
			classId = c;
			userId = u;
			if (p<-1 || p>3)
				per = 0;
			else
				per = p;
		}

		public Permissions(String c, String u){
			classId = c;
			userId = u;
			per = -1;
		}

		public String getUserId(){
			return userId;
		}

		public String getClassId(){
			return classId;
		}

		public int getPermission(){
			return per;
		}

		/*
		 * Sets permissions to the passed value. Does not allow change to be either out of
		 * range, or for permission to be set to 3 (owner).
		 */
		public int setPermission(int p){
			if (p<-1 || p>2)
				return -1;
			else{
				per = p;
				return p;
			}
		}
	}
}
