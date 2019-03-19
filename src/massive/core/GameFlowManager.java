package massive.core;

import java.util.Vector;

import massive.shared.Action;
import massive.shared.ClientFinishedTurn;
import massive.shared.TurnOver;




public class GameFlowManager extends Vector<Action> {
	private static final long serialVersionUID = 1L;
	
	Game game;
	int currentEvent = 0;
	public ToServer toServer;
	public int currentTurn;
	public int highestTurnInQueue;

	public int serverTurn = -1;
	
	public GameFlowManager( Game game ){
		this.game = game;
		toServer = new ToServer(game);
		toServer.start();
	}
	
	public void readAndExecuteAvailableActions(){
		executeAvailableActions();
		//checkSync();
	}
	
	public void executeAvailableActions() {
		for( ; currentEvent < this.size(); currentEvent++ ){
			Action a = this.get(currentEvent);
			a.execute(game);
			if( a instanceof TurnOver ){
				currentEvent++;
				currentTurn = ((TurnOver)a).turn;
				//TODO: this is important if the server wants to know stuff about players
				//game.gameloop.toServer.sendAction( new ClientFinishedTurn(currentTurn, game.ents.checksum() )  );
				break;
			}
		}
	}
	
	public void checkSync() {
		if( currentEvent % 100 == 0 ){
			System.out.println( currentEvent + ": " + game.ents.checksum() );
		}
	}

	public int numberOfTurnsBehind(){
		return highestTurnInQueue - currentTurn;
	}
	
	public int numberOfTurnsBehindServer() {
		return serverTurn - currentTurn;
	}
	
}
