package massive.entities;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import massive.core.Game;

public class Planet extends CircleEntity {

	
	public float mass;
	
	public Planet(Game game, float x, float y, float mass){
		super(game);
		this.mass = mass;
		radius = (float) Math.cbrt(mass) * 0.1f;
		
		createBody(new Vec2(x,y));
		MassData md = new MassData();
		myBody.getMassData(md);
		md.mass = mass;
		myBody.setMassData(md);
        
		renderDetail = 36;
	}
	
	public void createBody(Vec2 position){
		float ddensity = 1;
		System.out.println(ddensity);
		float dfriction = 0.3f;
		float drestitution = 0.65f;
		
		CircleShape sd = new CircleShape();
		sd.m_radius = radius;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position = position;//game.getPlayerSpawn(team);
        
        bd.angle = (float) (game.random.nextFloat() * Math.PI * 2);//horizontal ? (float)(Math.PI/2.0):0f;
        bd.linearDamping = 0;//0.1f;
       
        myBody = game.m_world.createBody(bd);
        
        fix = myBody.createFixture(sd, ddensity);
        
        fix.m_friction = dfriction;
        fix.m_restitution = drestitution;
        
        myBody.setSleepingAllowed(false);
        
        super.createBody();
	}
	
}
