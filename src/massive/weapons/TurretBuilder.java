package massive.weapons;

import massive.core.Game;
import massive.core.Util;
import massive.entities.PhysicsEntity;
import massive.entities.Player;
import massive.entities.Turret;

import org.jbox2d.common.Vec2;

public class TurretBuilder extends ProjectileRifle {
	
	public Turret myTurret = null;
	
	public TurretBuilder(PhysicsEntity owner) {
		super(owner);
		fireRate.set(60);
		magazineSize = 1;
		maxMagazines = 0;
		ui_ammoheight = ui_ammowidth;
		name = "ROV";
		imgPath = "media/image/guns/turret.png";
	}
	
	public void shootBullet(Game game) {
		Vec2 v = owner.myBody.getPosition().add( Util.angleV( owner.myBody.getAngle(), 2 ) );
		myTurret = new Turret( game, v, owner.team );
		myTurret.gun.playerResponsible = owner;
		//myTurret.highlight = true;
		game.ents.add( myTurret );
		game.sound.play("empty.wav",game.random.nextFloat()*0.03f + 0.1f,owner.myBody.getPosition());
		
		owner.killOnDeath.add(myTurret);
	}
	
	public void drawUIammo(Game game, int x, int y, int i) {
		game.core.ellipse(x+ui_ammoheight/2,y+ui_ammoheight/2,ui_ammowidth,ui_ammoheight);
	}
	
	public void refillAmmo(){
		return;
	}
	
	public void onDiscard(){
		if( myTurret != null ){
			myTurret.hp = 0;
		}
		super.onDiscard();
	}
	
//	public void stepWhileSelected(){
//		super.stepWhileSelected();
//		if( isShooting && myTurret != null ){
//			Player p = (Player)owner;
//			Vec2 target = Util.angleV( p.walkAngle, p.cursorDistance ).add( p.myBody.getPosition());
//			myTurret.walkTarget = target;
//		}
//	}
	
//	public void select(){
//		super.select();
//		if( myTurret != null){
//			myTurret.highlight = true;
//		}
//	}
//	
//	public void deselect(){
//		super.deselect();
//		if( myTurret != null){
//			myTurret.highlight = false;
//		}
//	}
	
	/*public void stepAlways(){
		if( myTurret != null ){
			myTurret.walkTarget = owner.myBody.getPosition();
		}
	}*/
	

}
