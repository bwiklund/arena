package massive.weapons;

import massive.entities.PhysicsEntity;

public class AutomaticRifle extends InstantShotRifle {

	public AutomaticRifle(PhysicsEntity owner) {
		super(owner);
		
		fireRate.set(4);
		magazineSize = 18;
		accuracy = 0.1f;
		particleMultiplier = 1.5f;
		reloadTime.set(60);
		damagePerShot = 30;
		shotSound = "gunshot.wav";
		name = "Rifle";
		imgPath = "media/image/guns/rifle.png";
	}

}
