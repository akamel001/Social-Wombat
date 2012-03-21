.SUFFIXES: .java .class
.java.class:
	javac $<

CLASSES = \
			ClassDB.java\
			Server.java\
			ServerList.java\
			ClassData.java\
			Cookie.java\
			ClassList.java\
			Hub.java\
			ServerSocketHandler.java\
			HubSocketHandler.java\
			UserInterface.java\
			ClassRoom.java\
			Client.java\
			Message.java\
			UserList.java\
			ClientSocketHandler.java\
			Post.java\
			SystemLogin.java\
			Log.java\

all: $(CLASSES)
