import java.net.*;

//Main controller class. Handles communication between data servers and clients

class Controller extends Thread {

	public Controller(){
		//randomass contructor
	}

	private static void initializeData(){

	}

	private static ServerSocket spawnSocketListener(){
		ServerSocket ser = null;
		try { 
			ser = new ServerSocket(8020);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println("Error during socket creation");
			System.exit(1);
		}
		return ser;
	}

	public static void main(String args[]){
		initializeData();		

		//spin and wait for new communication 
		while(true){

			ServerSocket s = spawnSocketListener();
			try {
				Socket soc = s.accept();
				//spawn a thread to handle the object
				SocketHandler p = new SocketHandler(soc);
			    p.start();
			} catch (Exception e){
				System.out.println(e.getMessage());
				System.out.println("An error occured while trying to accept a message from a socket");
			}


		}
	}

}