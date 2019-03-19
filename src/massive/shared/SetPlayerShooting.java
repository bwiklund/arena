package massive.shared;

import massive.core.Game;
import massive.entities.Player;

public class SetPlayerShooting extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playernumber = 0;
	public boolean isShooting = false;
	
	public SetPlayerShooting(int num, boolean isShooting){
		playernumber = num;
		this.isShooting = isShooting;
	}
	public void execute(Game game){
		Player p = game.ents.players.get( playernumber );

		if( p.weapons.size() > 0 ){
			p.weapons.get(p.currentWeapon).isShooting = isShooting;
		}
	}
}
