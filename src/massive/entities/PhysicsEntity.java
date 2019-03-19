package massive.entities;


import massive.core.Game;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;



public class PhysicsEntity extends Entity {

	public static final int BULLETS = 			1<<1;
	public static final int COVER = 			1<<2;
	public static final int DITCH = 			1<<3;
	
	public static final int PLAYERGROUPS = -100;
	
	public Body myBody;
	public Fixture fix;

	public String name;

	public PhysicsEntity lastAttacker;
	public PhysicsEntity entityResponsible;
	
	public int fill = ~0;
	public int stroke = ~0;
	
	public boolean turretIgnore = false;
	public boolean turretTargetable = false;
	
	public boolean laserIgnore = false;
	
	public double hp = 100;
	public boolean playReloadSound = false;
	
	public PhysicsEntity(Game game){
		super(game);
	}
	
	public void createBody() {
		myBody.setUserData(this);
	}

	public void injure(int i, PhysicsEntity attacker) {
		hp-=i;
		lastAttacker = attacker;
	}
	
	public void step(){
		if( playReloadSound ){
			playReloadSound = false;
			game.sound.play("reloadfinish.wav",0.1f,myBody.getPosition());
		}
		super.step();
	}
	
	public void die(){
		if( myBody != null ){
			game.m_world.destroyBody(myBody);
			myBody = null;
			fix = null;
		}
		super.die();
	}
	
	public static void injureStuffInRadius( Game game, Vec2 source, float damage, float range, PhysicsEntity originalOwner, float physicsPush ) {
		for( Entity e : game.ents ){
			if( e instanceof PhysicsEntity ){
				PhysicsEntity pe = (PhysicsEntity)e;
				float dist =  pe.myBody.getPosition().sub( source ).length();
				if( dist < range ){
					float damage2 = 1+(( 1 - dist / range ) * damage);
					pe.injure((int) damage2,originalOwner);
					
					Vec2 push = pe.myBody.getPosition().sub( source );
					push.normalize();
					push = push.mul(damage2 * physicsPush);
					pe.myBody.applyForce(push, pe.myBody.getPosition() );
				}
			}
		}
	}
	
	public float getDisplayAngle(){
		return myBody.getAngle();
	}
}
