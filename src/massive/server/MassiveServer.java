package massive.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import org.json.simple.JSONObject;

import massive.shared.Action;
import massive.shared.InitPlayer;
import massive.shared.InitWorld;
import massive.shared.TurnOver;



public class MassiveServer extends Thread {
	
	Vector<ToClient> clients;
	Vector<Action> events;
	MassiveConnectionListener connectionListener;
	PingListener pingListener;

	long seed = 0;
	boolean running = false;
	boolean gamestarted = false;
	
	public int nextPlayerNumber = 0;
	
	public int minimumPlayers = 1;
	
	public int maximumLatencyTurns = 4;
	public int turnOfSlowestClient = 0;
	public int turnOfFastestClient = 0;
	
	public int targetFrameRate = 50;
	public long lastFrameTime = 0;
	
	public int currentTurn = 0;
	public int numberOfTurnsPerReboot = targetFrameRate * 60 * 20;
	
	public int admin_id = 0;
	
	public boolean waitForSlowPlayers = false;
	
	public int port;
	public int pingport = 30001;
	
	//TODO: this should be something less stupid
	public float[] checksums = new float[10000000];
	
	public MassiveServer(int port){
		this.port = port;
		chooseRandomSeed();
	    initEventList();
	    startListening();
	}

	public void chooseRandomSeed() {
		Random seeder = new Random();
		seed = seeder.nextLong();
	    System.out.println("Starting server with seed: " + seed);
	}

	public void initEventList() {
		events = new Vector<Action>();
		events.add( new InitWorld( seed ) ); //the first message should tell the game to start up, before players join. it doesn't actually run until there are 2 players.
	}


	public void startListening() {
		clients = new Vector<ToClient>();
	    connectionListener = new MassiveConnectionListener(this); 
	    connectionListener.start();
	    pingListener = new PingListener(this);
	    pingListener.start();
	}
	
	public void run(){
		running = true; 
		while(running){
			if( clients.size() >= minimumPlayers && !gamestarted ){
				startGame();
			}
			
			dropDisconnectedPlayers();
			
			if(gamestarted){
				restartServerIfNeeded();
				advanceGameTurn();
			}
			
			waitOneFrame();
		}
	}

	private void restartServerIfNeeded() {
		synchronized (clients) {
			if( currentTurn >= numberOfTurnsPerReboot ){
				/*for( ToClient tc : clients ){
					tc.disconnect();
				}
				
				clients.clear();
				nextPlayerNumber = 0;
				currentTurn = 0;
				checksums = new float[10000000];
				events.clear();
	*/
				System.out.println("Session ended.");
				System.exit(-1);
			}
		}
	}

	private void dropDisconnectedPlayers() {
		for( int i = 0; i < clients.size(); i++ ){
			ToClient tc = clients.get(i);
			if( !tc.running ){
				clients.remove(i--);
			}
		}
	}

	public void waitOneFrame() {
		double msPerFrame = 1000d / targetFrameRate;
		try {
			Thread.sleep( (long) msPerFrame );
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void advanceGameTurn() {
		boolean advance = false;
		if( waitForSlowPlayers ){
			turnOfSlowestClient = 1000000000;
			turnOfFastestClient = -1;
			for( ToClient tc : clients ){
				if( tc.clientIsAtTurn < turnOfSlowestClient ){
					turnOfSlowestClient = tc.clientIsAtTurn;
				}
				if( tc.clientIsAtTurn > turnOfFastestClient ){
					turnOfFastestClient = tc.clientIsAtTurn;
				}
			}
			if( turnOfSlowestClient > turnOfFastestClient - maximumLatencyTurns ){
				advance = true;
			}
		} else {
			advance = true;
		}
		
		if( advance ){
			addEventAndNotify(new TurnOver( currentTurn++ ));
		}
	}
	
	private void startGame() {
		System.out.println("Server - starting game");
		gamestarted = true;
	}
	
	
	public class MassiveConnectionListener extends Thread {
		boolean running = false;
		private MassiveServer fs;
		public MassiveConnectionListener( MassiveServer fs ){
			this.fs = fs;
		}
		public void run(){
			running = true;
			try {
				
			    ServerSocket serverSocket = new ServerSocket(port, 10 );
				serverSocket.setPerformancePreferences(0, 1, 0);
				
			    while(running){
					Socket s = serverSocket.accept();
					
					synchronized (clients) {
						System.out.println("A client connected: " + s.getRemoteSocketAddress() );
						ToClient tc = new ToClient(s, fs);
						tc.playerNumber = nextPlayerNumber++;
					    clients.add( tc );
					    tc.start();
					    
					    String n = "Player" + (tc.playerNumber+1);
					    addEventAndNotify(new InitPlayer( tc.playerNumber, n ) );
					    
					    System.out.println("Server accepted a client: " + tc.playerNumber);
						
					}
				}
			    
			} catch (IOException e) {
			    System.out.println("Could not listen on port: " + port);
			    System.exit(-1);
			}
		}
	}
	
	public class PingListener extends Thread {
		boolean running = false;
		private MassiveServer fs;
		public PingListener( MassiveServer fs ){
			this.fs = fs;
		}
		public void run(){
			running = true;
			try {
				
				System.out.println("Ping listener on port " + pingport);
			    ServerSocket serverSocket = new ServerSocket(pingport, 10 );
				serverSocket.setPerformancePreferences(0, 1, 0);
				
			    while(running){
						Socket socket = serverSocket.accept();
						
						try {
							OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
						    BufferedWriter wr = new BufferedWriter(osw);
						    wr.write("HTTP/1.1 200 OK\nServer: Apache/2.2.11 (Unix)\nX-Powered-By: PHP/5.2.8\nDate: Fri, 16 Oct 2009 23:05:07 GMT\nContent-Type: text/html; charset=UTF-8\nConnection: close\n\n");
						    //wr.write("\n\n<b>hello!</b>");
						    
						    JSONObject obj=new JSONObject();
						    obj.put("name","Official Massive Server");
						    obj.put("numplayers",clients.size());
						    obj.put("timerunning",currentTurn);
						    obj.put("maxtime",numberOfTurnsPerReboot);
						    
						    wr.write(obj.toJSONString());
						    
						    wr.flush();
						    osw.flush();
						    socket.getOutputStream().flush();
						    
						    Thread.sleep(1);
						    
						    socket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			    
			} catch (IOException e) {
			    System.out.println("Could not listen on port: " + pingport);
			    System.exit(-1);
			}
		}
	}

	
	public void addEventAndNotify(Action a){
		synchronized( events ){
			events.add(a);
			events.notifyAll();
		}
	}
	
	public int getEventSize(){
		return events.size();
	}

	public void shitTheBed() {
		System.out.println("FAAAAAAAAAART");
		System.exit(-1);
	}

}




