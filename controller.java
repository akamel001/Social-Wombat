//Main controller class. Handles communication between data servers and clients

class Controller extends Thread {

	private initializeData(){
		
	}
	
	private ServerSocket spawnSocketListener(){
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

	public static void main(args[]){
		initializeData();
		ServerSocket s = spawnSocketListener();
		Socket soc = null;
		String str = null;
		Date d = null;
		
		//spin and wait for new communication 
		while(True){
			s.accept();
			InputStream o = soc.getInputStream();
			ObjectInput s = new ObjectInputStream(o);
			str = (String) s.readObject();
			d = (Date) s.readObject();
			s.close();
			
			//create a new socket to replace closed socket
			spawnSocketListener();
		}
	}

}