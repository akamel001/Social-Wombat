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
	
	public int addClass(String cid, String uid){
		if (containsClass(cid)){
			return -1;
		}
		else{
			classList.add(new Permissions(cid, uid, 3));
		}
		return -1;
	}
	
	public int addUser(String cid, String uid){
		if (containsUser(cid, uid)){
			return -1;
		}
		else{
			classList.add(new Permissions(cid, uid, 0));
		}
		return -1;
	}
	
	public int changePermissions(String cid, String uid, int n){
		synchronized(classList) {
			Iterator<Permissions> i = classList.iterator(); 
			while (i.hasNext()){
				if (i.next().getClassId() == cid && i.next().getUserId() ==uid){
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
	public boolean removeClass(String cid){
		return false;
	}

	/*
	 * Removes all tuples containing a user from the list.
	 */
	public boolean removeUser(String uid){
		return false;
	}
	
	

	/*
	 * 
	 */
	private class Permissions{
		final private String classId;
		final private String userId;
		private int per;

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

		public void setPermission(int p){
			per = p;
		}
	}
}
