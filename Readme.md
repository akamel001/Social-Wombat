###INSTRUCTIONS

1. Navigate to the Social-Wombat directory and compile the source code:

		$ javac *.java
    
2. Switch into the scripts directory:

		$ cd scripts
    
3. Simulate the startup of a server:

		$ ./startServer
    
    `Remember the IP(s) listed here.`

4. a) Start the system admin interface:

		$ ./startSysAdminInterface
    
   b) The default startup password for a System Administrator is "system admin". The System Administrator is encouraged to change it. 
   Type in "system admin" and press "Enter"
   
   c) Start the hub.
   
		Select option "1" ("Start the hub. ") and press "Enter"
   
   d) Add a server(s)
   
		Select option "4" ("Add a server.") and press "Enter"
		Specify the IP address(es) from step 3.
   
   e) Connect to all added servers.
   
		Select option "5" ("Connect to all servers.") and press "Enter"
   
   f) Register a user(s).
   
		Select option "2" ("Register a user in the system.") and press "Enter".
	`Follow the on screen instructions.`
    
5. a) Start a client and log in: 

		$ ./startUserInterface
   
    b) Log in with one of the username/password combos from step 4. f).
   
**Note: To completely wipe the system run ./cleanup in the scripts directory.**
    
###TUTORIAL
Follow the onscreen directions to:

	* Create a classroom.
	* Request enrollment in a particular classroom.
	* Confirm or deny enrollment requests.
	* Promote students to teaching assistant status or demote teaching assistants to student status.
	* Create threads within a classroom.
	* Comment on threads.
	* Delete your classroom.
	* Remove yourself from enrolled user for a particular classroom.

###NAMES AND DESCRIPTIONS OF FILES

DATA STORAGE CLASSES  
 
* [ClassData.java] - Used on the Hub to store permissions for a class

* [ClassDB.java] - Used on the servers to store classroom content

* [ClassList.java] - Stores a list of ClassData objects on the Hub

* [ClassRoom.java] - Stores the contents of an individual classroom on the server

* [Message.java] - Used to encapsulate messages to be sent over TCP

* [Post.java] - Stores the contents of an individual post.

* [ClassList.java] - Stores a list of classes. Contains access methods.

* [ServerList.java] - Stores a list of servers. Contains access methods.

* [UserList.java] - Stores a list of users. Contains access methods. 


INTERFACE CLASSES

* [SysAdminInterface.java] - The administrative interface to enroll users and add servers.

* [SystemLogin.java] - A class that helps handle the login process and authentication
for the SystemUserInterface.

* [UserInterface.java] - The interface for a client.

* [UserInterfaceHelper.java] - Helper class for the UserInterface.

SECURITY CLASSES

* [AES.java] - A class the handles AES encryption and decryption

* [CheckSum.java] - Used to calculate a checksum for an object


CLIENT - HUB - SERVER CLASSES 

* [Client.java] - Background functions class, does communication with hub.

* [DataObject.java] - Wrapper class to enable checksumming of persistent data.

* [Hub.java] - Starts up data structures, initiates secure connections with the 
servers, and handles connections from clients initially and then passes that off
to HubSocketHandler.

* [HubSocketHandler.java] - Handles authentication of clients and all future communications on that opened secure socket. Forwards messages from a client that are intended for a server.

* [Server.java] - Handles requests from a Hub.

* [ServerSocketHandler.java] - Helper class to handle requests from a Hub.

* [SocketPackage.java] - Packaging class to make it easier to send encrypted messages
through sockets.

* [StartServers.java] - Simulation class to act as the system admin starting up a remote server.

