package massive.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import massive.shared.Action;
import massive.shared.ClientFinishedTurn;
import massive.shared.InitPlayer;
import massive.shared.PlayerLeft;
import massive.shared.TurnOver;
import massive.shared.TxtMessage;

public class ToClient extends Thread {
	public Socket socket;
	public ObjectOutputStream oos;
	public ObjectInputStream ois;
	MassiveServer server;
	Reader reader;

	public int clientIsAtTurn;
	public String playerName = "";
	public int playerNumber = -1;

	public ToClient(Socket s, MassiveServer fs) {
		this.server = fs;
		this.socket = s;

		try {
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			playerLeftGame();
		}

		reader = new Reader();
		reader.start();
	}

	public boolean running = false;
	public boolean debug = false;
	public int lastActionSentToPlayer = 0;
	
	public void run() {

		running = true;
		try {
			socket.setTcpNoDelay(true);
		} catch (SocketException e2) {
			e2.printStackTrace();
		}

		while (running) {
			
			//boolean catchup = false;
			
			Action a = null;
			
			// here, we wait around until there's an action to send
			synchronized (server.events) {
				while (server.events.size() <= lastActionSentToPlayer ) {
					
					if( !running ){ return; }
					
					try {
						server.events.wait();
					} catch (InterruptedException e) {
						
					}
				}
				a = server.events.get(lastActionSentToPlayer);
			}
			
			// lets the player know which player they are
			if (a instanceof InitPlayer) {
				InitPlayer ip = (InitPlayer) a;
				if (ip.playernumber == this.playerNumber) {
					a = ip.cloneToTellPlayerWhoTheyAre();
				}
			}

			// sent the players unique TurnOver messages that add info about the
			// server state
			if (a instanceof TurnOver) {
				TurnOver ip = (TurnOver) a;
				a = ip.cloneForClient(server.currentTurn);
			}
			
			try {
				oos.writeObject(a);
			} catch (IOException e) {
				playerLeftGame();
				return;
			}

			lastActionSentToPlayer++;

		}

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class Reader extends Thread {

		public boolean running = false;
		public int i = 0;

		public void run() {

			running = true;

			while (running) {

				Action a = null;
				try {
					a = (Action) ois.readObject();
				} catch (Exception e) {
					playerLeftGame();
					return;
				}
				
				if (a != null) {
					if (a instanceof ClientFinishedTurn) {
						ClientFinishedTurn t = (ClientFinishedTurn) a;
						clientIsAtTurn = t.turn; // we don't need to send this
													// back out.
						if (server.checksums[t.turn] == 0) {
							server.checksums[t.turn] = t.checksum;
						} else if (server.checksums[t.turn] != t.checksum) {
							server.shitTheBed();
						}
					} else if (a instanceof TxtMessage ) {
						TxtMessage t = (TxtMessage) a;
						t.playerFrom = playerNumber;
					}
					
					server.addEventAndNotify(a);
				}
			}
		}
	}

	void playerLeftGame() {
		if( running && reader.running ){ //don't send two messages if two exceptions come in
			running = reader.running = false;
			server.addEventAndNotify(new PlayerLeft(playerNumber));
		}
		
		disconnect();
	}

	public void disconnect() {
		running = reader.running = false;
		
		if( socket != null ){
			try {
				socket.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		socket = null;

		synchronized ( server.events ) {
			server.events.notify();
		};
	}
}
