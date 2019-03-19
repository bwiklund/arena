package massive.shared;

import massive.core.Game;
import massive.entities.Player;

public class PlayerManualReload extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playernumber = 0;
	
	public PlayerManualReload(int num){
		playernumber = num;
	}
	public void execute(Game game){
		Player p = game.ents.players.get( playernumber );
		if( p.isAlive() ){
			if( p.weapons.size() > 0 ){
				p.weapons.get(p.currentWeapon).reload();
			}
		}
	}
}
