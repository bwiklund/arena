package massive.entities;

import massive.core.Game;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

public class CircleEntity extends PhysicsEntity {
	
	float radius = 0.8f;
	public int renderDetail = 12;
	
	public CircleEntity(Game game) {
		super(game);
	}

	public void createBody(Vec2 position){
		float ddensity = 1f;
		float dfriction = 0.3f;
		float drestitution = 0.65f;
		
		CircleShape sd = new CircleShape();
		sd.m_radius = radius;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position = position;//game.getPlayerSpawn(team);
        
        bd.angle = (float) (game.random.nextFloat() * Math.PI * 2);//horizontal ? (float)(Math.PI/2.0):0f;
        bd.linearDamping = 0.1f;
       
        myBody = game.m_world.createBody(bd);
        
        fix = myBody.createFixture(sd, ddensity);
        fix.m_friction = dfriction;
        fix.m_restitution = drestitution;
        
        myBody.setSleepingAllowed(false);
        
        super.createBody();
	}
	
	public void draw(){
		Vec2 pos = myBody.getPosition();
		
		game.core.fill(fill); 
		game.core.noStroke();
		
		if( game.render.draw3d ){
			game.core.pushMatrix();
			game.core.translate(pos.x, pos.y);
			game.core.sphereDetail(8);
			game.core.sphere(radius);
			game.core.popMatrix();
		} else {
			game.core.nGon(pos.x, pos.y, renderDetail, radius);
		}
	}
}
