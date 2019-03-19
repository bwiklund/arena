package massive.shared;

import massive.core.Game;

public class PlayerLeft extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playerNumber;
	
	public PlayerLeft(int playerNumber) {
		this.playerNumber = playerNumber;
	}
	
	public void execute(Game game){
		game.removePlayer(playerNumber);
	}
}
