package massive.shared;

import massive.core.Game;
import massive.entities.Player;


public class InitPlayer extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playernumber = 0;
	public boolean itsYou = false;
	public String playerName;
	
	public InitPlayer(int num, String name){
		playernumber = num;
		playerName = name;
	}
	public InitPlayer(int num, String name, boolean isYou) {
		playernumber = num;
		playerName = name;
		itsYou = isYou;
	}
	public void execute(Game game){
		Player player = new Player(game,playernumber,playerName);
		game.ents.add( player );
		if( itsYou ){
			game.currentPlayer = playernumber;
			game.localPlayer = player;
		}
		game.console.addLine(playerName + " joined");
	}
	public InitPlayer cloneToTellPlayerWhoTheyAre(){
		return new InitPlayer(playernumber,playerName,true);
	}
}
