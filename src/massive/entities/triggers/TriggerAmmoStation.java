package massive.entities.triggers;

import massive.core.Game;
import massive.entities.PhysicsEntity;
import massive.entities.Player;
import massive.weapons.Weapon;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

public class TriggerAmmoStation extends Trigger {
	
	public float radius = 7f;
	
	public TriggerAmmoStation(Game game, Vec2 p) {
		super(game);
		
		fill = 0x11ffffff;
		stroke = 0x66ffffff;
		
		createBody(p);
		laserIgnore = true;
	}
	
	public void createBody(Vec2 vec) {
		
		float ddensity = 0.0f;
		float dfriction = 0.3f;
		float drestitution = 0.1f;
		
		
		CircleShape sd = new CircleShape();
		sd.m_radius = radius;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.angle = (float) (game.random.nextFloat() * Math.PI * 2);//horizontal ? (float)(Math.PI/2.0):0f;
        bd.linearDamping = 0.1f;
        bd.position = vec.clone();
       
        myBody = game.m_world.createBody(bd);
         
        fix = myBody.createFixture(sd, ddensity);
        fix.m_friction = dfriction;
        fix.m_restitution = drestitution;
 
        fix.setSensor(true);
        fix.getFilterData().maskBits = ~BULLETS;
        
        //dont hit the person who fired it, initially
        //fix.getFilterData().groupIndex = PLAYERGROUPS-from.playerNumber;
        
        myBody.setBullet(true);
        
        myBody.setUserData(this);
        
        super.createBody();
        
	}
	
	public void enter( PhysicsEntity e ){
		super.enter(e);
		if( e instanceof Player ){
			((Player)e).canChangeWeapons = true;
		}
	}
	
	public void leave( PhysicsEntity e ){
		super.leave(e);
		if( e instanceof Player ){
			((Player)e).canChangeWeapons = false;
		}
	}
	
	@Override
	public void inside(PhysicsEntity e ){
		if( e.team == team ){
			if( e instanceof Player ){
				Player p = (Player)e;
				for( Weapon w : p.weapons ){
					w.refillAmmo();
				}
			}
		}
	}
}
