package massive.weapons;

import massive.core.Game;
import massive.core.Util;
import massive.entities.PhysicsEntity;

import org.jbox2d.common.Vec2;

public class MineBuilder extends ProjectileRifle {
	
	public MineBuilder(PhysicsEntity owner) {
		super(owner);
		fireRate.set(60);
		magazineSize = 1;
		maxMagazines = 0;
		ui_ammoheight = ui_ammowidth;
		name = "Mine";
		imgPath = "media/image/guns/mine.png";
	}
	
	public void shootBullet(Game game) {
		Vec2 v = owner.myBody.getPosition().add( Util.angleV( owner.myBody.getAngle(), 2 ) );
		Mine mine = new Mine( game, owner, 0, 0, 100 );
		mine.entityResponsible = owner;
		mine.init();
		mine.myBody.setTransform( v, 0 );
		game.ents.add( mine );
		game.sound.play("empty.wav",game.random.nextFloat()*0.03f + 0.1f,owner.myBody.getPosition());
		
		owner.killOnDeath.add(mine);
	}
	
	public void drawUIammo(Game game, int x, int y, int i) {
		game.core.ellipse(x+ui_ammoheight/2,y+ui_ammoheight/2,ui_ammowidth,ui_ammoheight);
	}
}
