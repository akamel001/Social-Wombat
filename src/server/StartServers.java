package server;



public final class StartServers {

	/**
	 * Pretends to be a sys admin to start up multiple servers
	 * @param args
	 */
	public static void main(String[] args) {
		Server s1 = new Server("1","password".toCharArray());
		System.out.println("IP of 1: " + s1.getIP());
		s1.start();
	}

}
