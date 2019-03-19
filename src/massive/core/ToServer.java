package massive.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

import massive.shared.Action;
import massive.shared.TestLauncher;
import massive.shared.TurnOver;

import org.apache.commons.io.input.CountingInputStream;

public class ToServer extends Thread {
	//private Queue<Action> inq;
	public Queue<Action> outq;

	private Socket socket;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Game game;
	private CountingInputStream counter;

	public ToServer(Game game) {
		this.game = game;
		//inq = new LinkedList<Action>();
		outq = new LinkedList<Action>();
	}

	public void connect() {
		if( socket != null ){ return; }
		try {
			socket = new Socket(TestLauncher.serverAddr, 30000);
			socket.setTcpNoDelay(true);

			int connectionTime = 0;
			int latency = 1;
			int bandwidth = 0;
			socket.setPerformancePreferences(connectionTime, latency, bandwidth);
			System.out.println("Client connected to server. ");

			oos = new ObjectOutputStream(socket.getOutputStream());
			counter = new CountingInputStream(socket.getInputStream());
			ois = new ObjectInputStream(counter);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			System.exit(-1);
		} catch (IOException e) {
			System.out.println("Could not connect");
			System.exit(-1);
		}
	}

	public Reader reader;

	public boolean running = false;
	public int i = 0;

	public void run() {

		connect();

		if (socket == null) {
			System.out.println("Lost the socket");
			System.exit(-1);
			return;
		}

		running = true;

		game.gameRunning = true;
		game.isConnecting = false; 
		
		game.dialogmenu.msg = "Connected, waiting for players...";

		reader = new Reader();
		reader.start();

		while (running) {
			Action a = null;
			// a = outq.poll();

			synchronized (outq) {
				while (a == null) {
					if( running == false ){ return; }
					
					a = pollNextActionToServer();
					if (a == null) {
						try {
							outq.wait();
						} catch (InterruptedException e) {
							
						}
					}
				}
			}

			// synchronized (oos) {
			if (a != null) {
				i++;
				try {
					oos.writeObject(a);
				} catch (SocketException e1) {
					// e1.printStackTrace();
					lostConnectionToServer();
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// }

		}
	}

	public void sendAction(Action a) {
		synchronized (outq) {
			outq.offer(a);
			outq.notifyAll();
		}
	}
	
	private Action pollNextActionToServer() {
		try {
			return outq.poll();
		} catch (Exception e) {
			
		}
		return null;
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
				} catch (Exception e1) {
					lostConnectionToServer();
					//e1.printStackTrace();
					break;
				}
				
				//if (a != null) {
					i++;

					game.gameloop.add(a);
					if (a instanceof TurnOver) {
						game.gameloop.highestTurnInQueue = ((TurnOver) a).turn;
						game.gameloop.serverTurn = ((TurnOver) a).serverTurn;
					}
					
					/*if( a instanceof SetPlayerMotion ){
						if( ((SetPlayerMotion)a).playernumber == game.currentPlayer ){

							System.out.println("SENDING COMMANDS - 3");
						}
					}*/
				//}

			}
			
		}

	}

	private void lostConnectionToServer() {
		if( running || reader.running ){
			disconnect();
			game.leftGame();
		}
	}

	public long getBytesRead() {
		return counter.getByteCount();
	}

	public void disconnect() {
		running = reader.running = false;
		
		synchronized ( outq ) {
			outq.notify();
		};
		
		System.out.println("A");
		
		if( socket != null ){
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			socket = null;
		}

		System.out.println("B");
		try {
			this.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("C");
		
		/*try {
			ois.close();
			//reader.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("D");
		*/
		game.leftGame();
	}
}
