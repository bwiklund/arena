package massive.weapons;

import massive.core.Game;
import massive.entities.PhysicsEntity;
import massive.entities.particles.SmokeTrail;

public class SniperRifle extends InstantShotRifle {

	public SniperRifle(PhysicsEntity owner) {
		super(owner);
		damagePerShot = 40;
		accuracy = 0.03f;
		
		fireRate.set(50);ui_ammoheight = 10;
		
		name = "Sniper";
		splashDamage = 40;
		splashRadius = 8;
		particleMultiplier = 8;
		barrelLength *= 1.5;
		barrelWidth *= 1.3f;
		imgPath = "media/image/guns/sniper.png";
	}
	
	public void shootBullet( Game game ){
		super.shootBullet(game);
		new SmokeTrail(game,owner.myBody.getPosition(), entityInCrosshairsContactPoint);
	}

}
