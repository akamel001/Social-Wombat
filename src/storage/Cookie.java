package storage;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Cookie objects hold session info.
 * @author chris
 *
 */
public class Cookie implements Serializable{
	
	private static final long serialVersionUID = 1585622412283602252L;
	private final Calendar expTime;
	private String user_name;
		
	/**
	 * Creates a cookie with a passed session key.
	 * @param s
	 */
	public Cookie(String s){
		expTime = Calendar.getInstance();
		expTime.add(Calendar.MINUTE, 10);
		user_name = s;
	}
	
	/** Returns the session key for the cookie.
	 * 
	 * @return
	 */
	public String getKey(){
		return user_name;
	}
	
	public void setKey(String s){
		user_name = s;
	}
	
	/** Returns true if the cookie's timestamp is greater than the current time.
	 * 
	 * @return boolean
	 */
	public boolean isValid(){
		Calendar now = Calendar.getInstance(); 
		return now.before(expTime);
	}
}