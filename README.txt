|/////////////////////////////////////////////////////////////////|
				     DATA STORAGE CLASSES
|/////////////////////////////////////////////////////////////////|
ClassData.java -- Used on the Hub to store permissions for a class

ClassDB.java -- Used on the servers to store classroom content

ClassList.java -- Stores a list of ClassData objects on the Hub

ClassRoom.java -- Stores the contents of an individual classroom on the server

Message.java -- Used to encapsulate messages to be sent over TCP

Post.java -- Stores the contents of an individual post.

ClassList.java -- Stores a list of classes. Contains access methods.

ServerList.java -- Stores a list of servers. Contains access methods.

UserList.java -- Stores a list of users. Contains access methods. 

|/////////////////////////////////////////////////////////////////|
				     INTERFACE CLASSES
|/////////////////////////////////////////////////////////////////|
SysAdminInterface.java -- The administrative interface to enroll users and add servers.

SystemLogin.java - A class that helps handle the login process and authentication
for the SystemUserInterface.



UserInterface.java -- The interface for a client.

UserInterfaceHelper.java -- Helper class for the UserInterface.


|/////////////////////////////////////////////////////////////////|
				     SECURITY RELATED
|/////////////////////////////////////////////////////////////////|
AES.java -- A class the handles AES encryption and decryption

CheckSum.java -- Used to calculate a checksum for an object


|/////////////////////////////////////////////////////////////////|
				     HUB - SERVER - CLIENT
|/////////////////////////////////////////////////////////////////|
Client.java -- Background functions class, does communciation with hub.

Hub.java -- Starts up data structures, initiates secure connections with the 
servers, and handles connections from clients initially and then passes that off
to HubSocketHandler.

HubSocketHandler.java -- Handles authentication of clients and all future communications
on that opened secure socket. Forwards messages from a client that are intended 
for a server.

Server.java -- Handles requests from a Hub.

ServerSocketHandler.java -- Helper class to handle requests from a Hub.

SocketPackage.java -- Packaging class to make it easier to send encrypted messages
through sockets.

StartServers.java - Simulation class to act as the system admin starting up a remote server.


|/////////////////////////////////////////////////////////////////|
				     	INSTRUCTIONS
|/////////////////////////////////////////////////////////////////|
1. Run 'javac *.java' on the files in Social-Wombat.
2. Run 'java StartServers' to simulate startup of the servers.
3. Run 'java SysAdminInterface' to act as the System Administrator.
4. The default startup password for a System Administrator is "system admin."
They are encouranged to change it.
5. The system administrator can now add servers by ip, from what was given by StartServers
and they can also enroll users by username and a password.
6. Run 'java UserInterface' and follow the directions on the screen.

Note: We were unable to submit a fully functioning product due to complications with our
cipher that resulted in a lengthy 20+ hour bug that has not been fixed. Because of that,
much of the rest of the system functionality cannot be demonstrated. 

