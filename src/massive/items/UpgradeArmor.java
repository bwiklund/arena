package massive.items;

import massive.entities.Colors;
import massive.entities.PhysicsEntity;
import massive.entities.Player;
import massive.weapons.Weapon;

public class UpgradeArmor extends Weapon {
	public UpgradeArmor(PhysicsEntity owner) {
		super(owner);
		this.imgPath = "media/image/guns/shield.png";
		tint = Colors.armorColor;
	}

	public void affect( Player p ){
		p.armor += 1;
	}
}
