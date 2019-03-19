package massive.shared;

import massive.core.Game;

/*
 * This tells each client to step the physics simulation and draw the world.
 * If the client is behind, it can skip drawing the world to catch up.
 */
public class TurnOver extends Action {
	private static final long serialVersionUID = 1L;
	
	public int turn;
	public int serverTurn = -1;
	//lets the client know if they should skip drawing between turns, to catch up if they get behind.

	public TurnOver(int turn ){
		this.turn = turn;
	}
	
	public TurnOver(int turn, int serverTurn){
		this.turn = turn;
		this.serverTurn = serverTurn;
	}
	
	public TurnOver cloneForClient( int serverTurn ){
		return new TurnOver( turn, serverTurn );
	}
	
	public void execute( Game game ){
		game.finishTurn();
		game.gameRunning = true;
		game.serverTurn = serverTurn;
		if( !game.isCatchingUp ){
			game.sendCommandsToServer();
		}
		if( turn == 0 ){
			//kind of gross, but whatever
			game.dialogmenu.gameStarted();
		}
	}
}
