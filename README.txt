Files and descriptions:

ClassData.java --  ClassData objects store a class' associated server info and user permissions.

ClassDB.java -- Class to store and access ClassRoom objects.

ClassList.java -- ClassList is a list of ClassData objects.

ClassRoom.java -- ClassRoom stores threads and their associated comments.

Client.java -- Class containing functions used for the UserInterface to communicate with hub

ClientSocketHandler.java -- Socket Handler to communicate with hub 

Cookie.java -- Custom serialized data structure stored inside of a message. Used for storing sessions 

Hub.java -- Center for communicating between clients and servers. 

HubSocketHandler.java -- Socket handler class used for the hub

Message.java -- Custom serialized data structure used to transfer packets over sockets

Post.java -- Used to hold a thread and all it's associated comments.

Server.java -- Class that listens for connections from the hub and stores user data based on request

ServerList.java -- ServerList holds info and access methods for server info.

ServerSocketHandler.java -- Socket handler to communicate with hub

UserInterface.java -- This class is for anything dealing with what the user sees and the work flow 
of the social network. However, it does not deal with the behind-the-scenes logic for any of the 
actions, which is done in the client class.

UserList.java -- Holds user ids.

X-------------------------------------------------------------------------------------X

Instructions:
	
	* Installation: 
		- To install the system download the repository from github (https://github.com/plumppapaya/Social-Wombat/zipball/
	
	* Compiling:
		- Navigate to the directory of the downloaded repository and type 'make' 
		  
	* Running:
		- To run the system start three separate terminals
		  Terminal 1: type 'java Hub'
		  Terminal 2: type 'java Server'
		  Terminal 3: type 'java UserInterface'

Tutorial:

	After a user starts the UserInterface class, they will see a console that they can use to navigate the system. Options are given in the top portion of the console while input is accepted at the bottom portion.  