package massive.shared;

import massive.core.Game;
import massive.entities.Player;

public class DropItem extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playernumber = 0;
	
	//TODO: the server should check that players aren't dropping other people's shit
	//TODO: some kind of field that makes the server set the player id to the player that sent it, forcing that??
	public DropItem(int playerNum){
		this.playernumber = playerNum;
		
	}
	public void execute(Game game){
		Player p = game.ents.players.get( playernumber );
		if( p.weapons.size() > 0 ){
			p.weapons.get(p.currentWeapon).deselect();
			p.weapons.get(p.currentWeapon).onDiscard();
			p.weapons.remove(p.currentWeapon);
			p.currentWeapon = Math.min( p.currentWeapon, p.weapons.size()-1 );
			if( p.currentWeapon > -1 ){
				p.weapons.get(p.currentWeapon).select();
			}
		}
	}
}
