
public class StartServers {

	/**
	 * Pretends to be a sys admin to start up multiple servers
	 * @param args
	 */
	public static void main(String[] args) {
		Server s1 = new Server("1","a".toCharArray());
		System.out.println("IP of 1: " + s1.getIP());
		s1.start();
		
		//We will avoid starting multiple servers on the same ip for 
		//now to simplify things
		/*
		Server s2 = new Server("s2","password".toCharArray());
		System.out.println("IP of s2: " + s2.getIP());
		s2.start();
		Server s3 = new Server("s3","password".toCharArray());
		System.out.println("IP of s3: " + s3.getIP());		
		s3.start();
		*/
	}

}
