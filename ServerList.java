import java.io.Serializable;
import java.net.*;
import java.util.Collections;
import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 * ServerList holds info and access methods for server info.
 * 
 * @author chris d
 *
 */
public final class ServerList implements Serializable{
	
	private static final long serialVersionUID = -5018052157599076338L;
	private Map<Integer, ServerData> serverList;
	private int nextId; 
	
	/**
	 * Creates a list of servers. First server created has an id of 1.
	 */
	public ServerList(){
		serverList = Collections.synchronizedMap(new TreeMap<Integer, ServerData>());
		nextId = 1; // first server created is #1
	}

	/**
	 * Adds a new server to the server list.<br>
	 * <br>
	 * Creates a new server object with the passed ip address and port. The
	 * server id is automatically created.<br>
	 * NOTE: two ports cannot have both the same ip and port.
	 * @param ip The ip address of the server.
	 * @param port The port number of the server.
	 * @param pass The password for the server. NOTE: the passed char[] is zeroed out as soon as the password is set! 
	 * It cannot be used again!!!!
	 * @param encrypter An AES object used to encrypt the password.
	 * @return Returns the server id or -1 if the ip/port combination already exists in the list.
	 */
	public int addServer(InetAddress ip, int port, char[] pass, AES encrypter){
		if(ip==null || port<0 || port>49150 || encrypter==null)
			return -1;
		
		// check to see if ip/port combo exists
		// if it does, disallow
		Set<Integer> set = serverList.keySet();  		
		synchronized(serverList) { 
			Iterator<Integer> i = set.iterator();
			while (i.hasNext()){
				int k = i.next();
				ServerData temp = serverList.get(k);
				if (temp.getAddress().equals(ip) && temp.getPort()==port){
					return -1;
				}
			}
			ServerData s = new ServerData(nextId);
			s.setAddress(ip);
			s.setPort(port);
			s.setPassword(encrypter.encrypt(pass));
			// Zero out passed password
			for(int j=0; j<pass.length; j++)
				pass[j]='0';
			serverList.put(nextId, s);
		}
		int out = nextId;
		nextId++;
		return out;
	}

	/**
	 * Removes a server from the server list.
	 * @param s The id of the server to be removed.
	 * @return 1 if a server with that id exists, -1 otherwise.
	 */
	public int removeServer(int s){
		if (serverList.remove(s)!=null)
			return 1;
		else 
			return -1;
	}
	
	/**
	 * Returns the ip address for a server.
	 * @param s The id of the server whose ip is to be found.
	 * @return Returns and InetAddress of the server. Returns null if the server is not in the server list.
	 */
	public InetAddress getAddress(int s){
		ServerData temp = serverList.get(s);
		if (temp==null)
			return null;
		else 
			return temp.getAddress();
	}
	
	/**
	 * Returns the port number for a server.
	 * @param s The id of the server whose port is to be found.
	 * @return Returns the port number of the server. Returns -1 if the server is not in the server list.
	 */
	public int getPort(int s){
		ServerData temp = serverList.get(s);
		if (temp==null)
			return -1;
		else
			return temp.getPort();
	}
	
	
	/**
	 * Gives the index of the last server created
	 * @return
	 */
	public int getLastServer(){
		return nextId-1;
	}

	/**
	 * Server class holds the info for a single server.
	 * @author chris
	 * The only part of this class that is encrypted is the password.
	 */
	public final class ServerData implements Serializable{
		
		private static final long serialVersionUID = -2846614313431062929L;
		private final int id;
		private InetAddress ip;
		private int port;
		private byte[] password;

		
		@SuppressWarnings("unused")
		private ServerData(){id=-1;}
		
		/**
		 * Creates a ServerList object with default values.<br>
		 * <br>
		 * Default values:<br>
		 * 		id = -1<br>
		 * 		ip = null<br>
		 * 		port = -1<br>
		 */
		protected ServerData(int i){
			id = i;
			ip = null;
			port = -1;
		}

		/** Returns the server id
		 * 
		 * @return
		 */
		protected int getId(){
			return id;
		}

		/** Returns the ip address of the server.
		 * 
		 * @return
		 */
		protected InetAddress getAddress(){
			return ip;
		}

		/**
		 * Sets the server's ip to the passed address.<br>
		 * <br>
		 * IP CAN ONLY BE SET ONCE! If the ip has already been set, it cannot be reset.<br>
		 * @param i
		 * @return Returns -1 if passed value is null, 1 on success.
		 */
		protected int setAddress(InetAddress i){
			if (i== null || ip!=null)
				return -1;
			else{
				ip = i;
				return 1;
			}
		}

		/** Returns the port number for the server.
		 * 
		 * @return
		 */
		protected int getPort(){
			return port;
		}

		/** Sets the port to the passed int<br>
		 * <br>
		 * PORT CAN ONLY BE SET ONCE! If the port has already been set, it cannot be reset.
		 * @param p Must be 0>=p<=49151
		 * @return
		 */
		protected int setPort(int p){
			if (p<0 || p>49151)
				return -1;
			else{
				port = p;
				return 1;
			}
		}
		
		/**
		 * Sets the password for the server. </br>
		 * NOTE: this function zeros out the passed array.
		 * @param p The new encrypted password. 
		 * @return Returns 1 on success, which, hopefully, is always.
		 */
		protected int setPassword(byte[] p){
			// zero out old password
			if (password != null){
				for (int i=0; i<password.length; i++)
					password[i]='0';
			}
			// copy in new password
			password = new byte[p.length];
			for (int i=0; i<password.length; i++)
				password[i]=p[i];
			// Zero out passed byte array
			for (int i=0; i<p.length; i++)
				p[i]=0;
			return 1;
		}
		
		/**
		 * Returns an encrypted copy of the server's password.
		 * @return Returns a copy of the password. NOTE: you must zero put the returned array as soon as you are finished with it!!!!!! 
		 */
		protected byte[] getPassword(){
			if (password==null)
				return null;
			else{
				byte[] out = new byte[password.length];
				for(int i=0; i<password.length; i++)
					out[i]=password[i];
				return out;
			}
		}
		
	}
}