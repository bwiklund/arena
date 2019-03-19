package massive.shared;

import massive.core.Game;

public class ClientFinishedTurn extends Action {
	private static final long serialVersionUID = 1L;

	public int turn;
	public float checksum;
	
	public ClientFinishedTurn(int turn){
		this.turn = turn;
	}
	
	public ClientFinishedTurn(int turn, float checksum){
		this.turn = turn;
		this.checksum = checksum;
	}
	
	public void execute( Game game ){
		//nothing
	}
}
