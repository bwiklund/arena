package massive.entities;

import massive.core.Core;
import massive.core.Game;
import massive.entities.triggers.Trigger;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

public class Ditch extends Trigger {

	public float dwidth;
	public float dheight;
	public float dfriction = 0.3f;
	public float drestitution = 0.65f;
	float x,y;
	public float angle = 0;
	
	public Ditch(Game game, float x, float y, float dwidth, float dheight, float angle) {
		super(game);
		this.x=x;
		this.y=y;
		this.dwidth=dwidth;
		this.dheight=dheight;
		this.angle = angle;
		createBody();
		laserIgnore = true;
	}
	
	public void createBody() {
	
		PolygonShape sd = new PolygonShape();
	    sd.setAsBox(.5f*dwidth, .5f*dheight);
	    
	    BodyDef bd = new BodyDef();
	    bd.type = BodyType.STATIC;
	    bd.position = new Vec2(x, y);
	    bd.angle = angle;
	    
	    myBody = game.m_world.createBody(bd);
	    
	    fix = myBody.createFixture(sd, 0.0f);
	    fix.m_friction = dfriction;
	    fix.m_restitution = drestitution;
	    fix.setSensor(true);
	    fix.getFilterData().maskBits = ~BULLETS;
	    fix.getFilterData().categoryBits = DITCH;
	    fix.setUserData(this);
	    
	    super.createBody();
	}
	
	public void draw(){
		game.core.fill(0xff282828);
		game.core.noStroke();
		drawTheVertexes();
	}
	
	public void drawTheVertexes(){
		Core core = game.core;
		
		PolygonShape p = (PolygonShape) myBody.m_fixtureList.m_shape;
		Vec2[] verts = p.m_vertices;
		Vec2[] drawverts = new Vec2[verts.length];
		
		for( int i = 0; i < verts.length; i++ ){
			drawverts[i] = myBody.getWorldPoint( verts[i] );
		}
		
		core.beginShape();
		for( int i = 0; i < 4; i++ ){
			core.vertex( drawverts[i].x, drawverts[i].y );
		}
		core.vertex( drawverts[0].x, drawverts[0].y );
		core.endShape();
	}

	@Override
	public void enter(PhysicsEntity e) {
		if(e instanceof Player){
			entitiesInside.add((Player) e);
			((Player)e).inDitch = true;
		}
		
	}

	@Override
	public void inside(PhysicsEntity e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leave(PhysicsEntity e) {
		if(e instanceof Player){
			entitiesInside.remove(e);
			((Player)e).inDitch = false;
		}
	}

}
