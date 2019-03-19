package massive.weapons;

import massive.core.Game;
import massive.entities.PhysicsEntity;

public class DepthChargeLauncher extends ProjectileRifle {

	
	public Grenade currentCharge = null;
	
	public DepthChargeLauncher(PhysicsEntity owner) {
		super(owner);
		fireRate.set(10);
		magazineSize = 40;
		maxMagazines = 0;
		ui_ammoheight = ui_ammowidth;
		imgPath = "media/image/guns/grenade.png";
		name = "Grenades";
	}
	
	public void shootBullet(Game game) {
		if( currentCharge != null ){
			currentCharge.detonate();
			currentCharge = null;
		} else {
		
			Grenade grenade = new Grenade(game,owner, 
					10, 	//speed
					0 ,	//inaccuracy, 0 == perfect aim
					1000 );	//turns until bullet is deleted
			grenade.entityResponsible = owner;
			currentCharge = grenade;
			//Bullet bullet = new Grenade(game,this, 5, 0.01f, 200 );
			grenade.init();
			game.ents.add( grenade );
			smokePuff(owner.myBody.getAngle());
			game.sound.play("grenadetoss.wav",game.random.nextFloat()*0.03f + 0.1f,owner.myBody.getPosition());
		
		}
	}
	
	public void drawUIammo(Game game, int x, int y, int i) {
		game.core.ellipse(x+ui_ammoheight/2,y+ui_ammoheight/2,ui_ammowidth,ui_ammoheight);
	}
}
