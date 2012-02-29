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
 * @author chris
 *
 */
public class ServerList implements Serializable{

	private static final long serialVersionUID = -5018052157599076338L;
	private Map<Integer, ServerData> serverList;
	private int nextId; 
	
	public ServerList(){
		serverList = Collections.synchronizedMap(new TreeMap<Integer, ServerData>());
		nextId = 0;
	}

	/**
	 * Adds a new server to the server list.<br>
	 * <br>
	 * Creates a new server object with the passed ip address and port. The
	 * server id is automatically created.<br>
	 * NOTE: two ports cannot have both the same ip and port.
	 * @param ip The ip address of the server.
	 * @param port The port number of the server.
	 * @return Returns the server id or -1 if the ip/port combination already exists in the list.
	 */
	public int addServer(InetAddress ip, int port){
		if(ip==null)
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
			nextId ++;
			ServerData s = new ServerData();
			s.setId(nextId);
			s.setAddress(ip);
			s.setPort(port);
			serverList.put(nextId, s);
		}
		return nextId;
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
	 * Server class holds the info for a single server.
	 * @author chris
	 *
	 */
	public class ServerData implements Serializable{
		
		private static final long serialVersionUID = -2846614313431062929L;
		private int id;
		private InetAddress ip;
		private int port;

		/**
		 * Creates a ServerList object with default values.<br>
		 * <br>
		 * Default values:<br>
		 * 		id = -1<br>
		 * 		ip = null<br>
		 * 		port = -1<br>
		 */
		public ServerData(){
			id = -1;
			ip = null;
			port = -1;
		}

		/** Returns the server id
		 * 
		 * @return
		 */
		public int getId(){
			return id;
		}

		/**
		 * Sets the id for a server.<br>
		 * <br>
		 * Id CAN ONLY BE SET ONCE! If the id has already been set, it cannot be reset.
		 * @param i
		 * @return
		 */
		public int setId(int i){
			if (i<0 || id>0)
				return -1;
			else{
				id = i;
				return 1;
			}
		}

		/** Returns the ip address of the server.
		 * 
		 * @return
		 */
		public InetAddress getAddress(){
			return ip;
		}

		/**
		 * Sets the server's ip to the passed address.<br>
		 * <br>
		 * IP CAN ONLY BE SET ONCE! If the ip has already been set, it cannot be reset.<br>
		 * @param i
		 * @return Returns -1 if passed value is null, 1 on success.
		 */
		public int setAddress(InetAddress i){
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
		public int getPort(){
			return port;
		}

		/** Sets the port to the passed int<br>
		 * <br>
		 * PORT CAN ONLY BE SET ONCE! If the port has already been set, it cannot be reset.
		 * @param p Must be 0>=p<=49151
		 * @return
		 */
		public int setPort(int p){
			if (port<0 || p<0 || p>49151)
				return -1;
			else{
				port = p;
				return 1;
			}
		}
	}
}