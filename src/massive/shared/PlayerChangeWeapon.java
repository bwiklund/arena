package massive.shared;

import massive.core.Game;
import massive.entities.Player;
import massive.items.UpgradeArmor;
import massive.items.UpgradeBinoculars;
import massive.weapons.AutomaticRifle;
import massive.weapons.GrenadeLauncher;
import massive.weapons.MineBuilder;
import massive.weapons.Shotgun;
import massive.weapons.SniperRifle;
import massive.weapons.TurretBuilder;
import massive.weapons.WallBuilder;
import massive.weapons.Weapon;

public class PlayerChangeWeapon extends Action {
	private static final long serialVersionUID = 1L;

	public int playernumber = 0;
	public short direction = 0;
	private short weaponNum = 0;

	public PlayerChangeWeapon(int player, int weaponNum, int direction) {
		this.playernumber = player;
		this.weaponNum = (short) weaponNum;
		this.direction = (short) direction;
	}

	public void execute(Game game){
		Player p = game.ents.players.get( playernumber );
		if( p.isAlive() ){
			
			boolean playSound = false;
			
			if( direction != 0 ){
				if(p.weapons.size() > 0){
					p.weapons.get(p.currentWeapon).deselect();
					p.currentWeapon = ( p.currentWeapon + direction + p.weapons.size() ) % p.weapons.size();
					p.weapons.get(p.currentWeapon).select();
					if( p.weapons.size() > 1 ){playSound = true;}
				}
				
			} else if(!p.canChangeWeapons){ 
				//change current weapons
				if(weaponNum < p.weapons.size()){
					if( p.currentWeapon != weaponNum ){
						p.weapons.get(p.currentWeapon).deselect();
						p.currentWeapon = weaponNum;
						p.weapons.get(p.currentWeapon).select();
						playSound = true;
					}
				}
				
			} else {
				
				//buy new weapons in weapon zones
				Weapon newWeapon = null;
				
				switch( weaponNum ){
					case 0: newWeapon = new AutomaticRifle(p); break;
					case 1: newWeapon = new Shotgun(p); break;
					case 2: newWeapon = new SniperRifle(p); break;
					case 3: newWeapon = new GrenadeLauncher(p); break;
					case 4: newWeapon = new WallBuilder(p); break;
					case 5: newWeapon = new TurretBuilder(p); break;
					case 6: newWeapon = new MineBuilder(p); break;
					
					case 7: newWeapon = new UpgradeBinoculars(p); break;
					case 8: newWeapon = new UpgradeArmor(p); break;
				}
				
				if( newWeapon != null ){
					if( p.weapons.size() > 0 ){
						p.weapons.get(p.currentWeapon).deselect();
					}
					if( p.weapons.size() < 5 ){
						p.weapons.add( newWeapon );
						p.currentWeapon = p.weapons.size()-1;
						p.weapons.get(p.currentWeapon).select();
						playSound = true;
					}
				}
				
				/*items.add( new UpgradeSpeed() );
				items.add( new UpgradeSpeed() );*/
			}
			
			if( playSound ){
				game.sound.play("reloadfinish.wav",0.1f,p.myBody.getPosition());
			}
		}
	}
}
