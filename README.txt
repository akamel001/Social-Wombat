***NAMES AND DESCRIPTIONS OF FILES***
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

***INSTRUCTIONS***

1. Navigate to the Social-Wombat directory and compile the source code:

    $ javac *.java
    
2. Simulate the startup of a server:

    $ java StartServers

3. Start the system admin interface:

    $ java SysAdminInterface
The default startup password for a System Administrator is "system admin". The System Administrator is encouraged to change it.

After the system administrator logs in he can start the hub by selecting to do so from the interface. Once the hub has been started, servers can be added by ip which is obtained from StartServers. The administrator can also enroll users by username and a password.*  

4. Start a client and log in: 

    $ java UserInterface
    
***TUTORIAL***


