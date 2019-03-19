package massive.shared;

import massive.core.Game;
import massive.server.MassiveServer;

public class TestLauncher {
	
	public static String serverAddr = "";
	public static String filepath = "";
	
	public static void main(String[] args) throws InterruptedException {
		
		if( args.length >= 3 ){
			filepath = args[2];
		}
		
		if( args.length > 0 ){
			if( args[0].equals("server") ){
				System.out.println("launching server");
				new MassiveServer(Integer.parseInt(args[1])).start();
			}

			else if( args[0].equals("test") ){
				System.out.println("launching test");
				serverAddr = "127.0.0.1";
				new MassiveServer(Integer.parseInt(args[1])).start();
				
				Thread.sleep(100);
				
				new Game(false);
				
				Thread.sleep(100);
				
				new Game(true);
			} 
			
			else if( args[0].equals("client") ){
				System.out.println("launching client");
				serverAddr = "173.255.213.37";
				new Game(true);
			}
			
			else if( args[0].equals("localclient") ){
				System.out.println("launching client");
				serverAddr = "127.0.0.1";
				new Game(true);
			}
			
			else {
				System.out.println("paul what the fuck yo");
				System.exit(-1);
			}
		} else {
			System.out.println("paul what the fuck yo");
			System.exit(-1);
		}
		
		
		//run();
		
	}
	
	
	
	public static void run(){
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
