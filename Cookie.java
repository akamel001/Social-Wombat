import java.util.Calendar;

public class Cookie{
	private final Calendar expTime;
	private final String sessionKey;
	
	public Cookie(String s){
		expTime = Calendar.getInstance();
		expTime.add(Calendar.MINUTE, 10);
		sessionKey = s;
	}
	
	public String getKey(){
		return sessionKey;
	}
	
	public boolean isValid(){
		Calendar now = Calendar.getInstance(); 
		return now.before(expTime);
	}
}