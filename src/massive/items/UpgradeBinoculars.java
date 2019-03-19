package massive.items;

import massive.entities.Colors;
import massive.entities.PhysicsEntity;
import massive.entities.Player;
import massive.weapons.Weapon;

public class UpgradeBinoculars extends Weapon {

	public UpgradeBinoculars(PhysicsEntity owner) {
		super(owner);
		this.imgPath = "media/image/guns/nocks.png";
		tint = Colors.binocularColor;
	}
	
	float zoomDamp = 1;
	
	public void stepWhileSelected(){
		if( owner instanceof Player ){
			Player p = ((Player)owner);
			if( isShooting ){
				p.zoomTarget = 4;
			} else {
				p.zoomTarget = 1;
			}
		}
	}

	public void select(){

		if( owner instanceof Player ){
			Player p = ((Player)owner);
			p.zoomTarget = 1;
		}
		super.select();
	}
	public void deselect(){

		if( owner instanceof Player ){
			Player p = ((Player)owner);
			p.zoomTarget = 1;
		}
		super.deselect();
	}
}
