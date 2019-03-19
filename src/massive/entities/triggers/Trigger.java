package massive.entities.triggers;

import java.util.HashSet;

import massive.core.Game;
import massive.entities.PhysicsEntity;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;



public class Trigger extends PhysicsEntity {

	public HashSet<PhysicsEntity> entitiesInside = new HashSet<PhysicsEntity>();
	
	public float radius = 7f;
	public int fill = 0xff777777;
	
	public Trigger(Game game){
		super(game);
		this.game = game;
		turretIgnore = true;
	}
	
	public Trigger(Game game, Vec2 p) {
	
		super(game);
		createBody(p);
		turretIgnore = true;
		laserIgnore = true;
	}
	
	public void step(){
		for( PhysicsEntity e : entitiesInside ){
			inside(e);
		}
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
	
	public void draw(){
		Vec2 pos = myBody.getPosition();
		game.core.fill(fill);
		game.core.noStroke();
		game.core.nGon(pos.x, pos.y, 24, radius);
	}

	public void enter(PhysicsEntity e) {
		entitiesInside.add(e);
	}

	public void leave(PhysicsEntity e) {
		entitiesInside.remove(e);
	}
	
	/*
	 * This gets called on each entity inside the trigger, once per turn
	 */
	public void inside(PhysicsEntity e){
		
	}
}