package massive.weapons;

import massive.core.Game;
import massive.entities.PhysicsEntity;


public class Shotgun extends ProjectileRifle {

	int numPellets = 10;
	
	public Shotgun(PhysicsEntity owner) {
		super(owner);
		accuracy = 0.1f;
		fireRate.set(30);
		magazineSize = 2;
		maxMagazines = 10;
		barrelWidth *= 2;
		barrelLength *= 0.8f;
		reloadTime.set(100);
		name = "Shotgun";
		shotSound = "shotgun.wav";
		shotVolume = 2;
		ui_ammoheight = ui_ammowidth*2;
		imgPath = "media/image/guns/shotgun.png";
	}


	public void shootBullet(Game game) {
		
		for( int i = 0; i < numPellets; i++ ){
			Bullet bullet = new Bullet(game,owner, 
					1000, 	//speed
					accuracy ,	//inaccuracy, 0 == perfect aim
					60 );	//turns until bullet is deleted
			//Bullet bullet = new Grenade(game,this, 5, 0.01f, 200 );
			bullet.entityResponsible = owner;
			bullet.init();
			bullet.damage = 15;
			bullet.myBody.setLinearDamping( game.random.nextFloat() * 0.01f );
			bullet.willRicochet = true;
			game.ents.add( bullet );
		}
		
		
		smokePuff(owner.myBody.getAngle());
		
		game.sound.play(shotSound,shotVolume*(game.random.nextFloat()*0.03f + 0.1f),owner.myBody.getPosition());
	}
}
