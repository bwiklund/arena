package massive.entities;

//import org.jbox2d.collision.PolygonDef;

import massive.core.Core;
import massive.core.Game;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;



strictfp public class Wall extends PhysicsEntity {
	
	
	public float dwidth;
	public float dheight;
	public float ddensity = 0;//1f;
	public float dfriction = 0.3f;
	public float drestitution = 0.65f;
	public float height;
	
	public boolean isCover = false;
	private final static float maxCoverWidth = 2;
	
	float x,y;
	public float angle = 0;
	
	public Wall(Game game, float x, float y, float dwidth, float dheight, float angle ){
		super(game);
		hp = Integer.MAX_VALUE;
		this.x=x;
		this.y=y;
		this.dwidth=dwidth;
		this.dheight=dheight;
		this.angle = angle;
		/*float basecolor = game.random.nextFloat() * 40 + 150;
		this.fill = game.core.color(
				game.random.nextFloat() * 10 + basecolor,
				game.random.nextFloat() * 10 + basecolor,
				game.random.nextFloat() * 5 + basecolor);*/
		this.fill = 0xffaaaaaa;
		
		this.height = game.random.nextFloat()*1+2;
		if( game.random.nextFloat() < 0.1f ){
			height *= 2;
		}
		createBody();
	}
	
	
	public void createBody() {
			
			//ddensity = 50;
		//}
		
		PolygonShape sd = new PolygonShape();
        sd.setAsBox(.5f*dwidth, .5f*dheight);
        
        if( dwidth < maxCoverWidth || dheight < maxCoverWidth ){
        	isCover = true;
        }
        
        BodyDef bd = new BodyDef();
        if( ddensity != 0 ){
        	bd.type = BodyType.DYNAMIC;
        } else {
        	bd.type = BodyType.STATIC;
        }
        bd.position = new Vec2(x, y);
        bd.angle = angle;
        
        myBody = game.m_world.createBody(bd);
        myBody.m_linearDamping = 0.1f;
        myBody.m_angularDamping = 0.1f;
        
        fix = myBody.createFixture(sd, ddensity);
        fix.m_friction = dfriction;
        fix.m_restitution = drestitution;

        
        if( isCover ){
        	fix.getFilterData().categoryBits = COVER;
        	fill = 0xff444444;
        	turretIgnore = true;
        }
        
        //Vec2 inward = new Vec2(-x*0.1f,-y*0.1f);
       // Vec2 rand = new Vec2(game.random.nextFloat()*0.4f-0.2f,game.random.nextFloat()*0.4f-0.2f);
        //myBody.setLinearVelocity(rand);
        
        super.createBody();
	}
	
	public void draw(){
		game.core.fill(fill);
		//game.core.stroke(0xffffffff);
		game.core.noStroke();
		if( game.render.draw3d ){
			draw3d();
		}else {
			drawTheVertexes();
		}
	}
	
	public void step(){
		if( hp <= 0 ){
			removeThis = true;
		}
	}
	
	private void draw3d() {
		Vec2 pos = myBody.getPosition();
		
		game.core.pushMatrix();
		game.core.translate(pos.x, pos.y, height/2);
		game.core.rotateZ(angle);
		game.core.sphereDetail(9);
		game.core.box(dwidth,dheight,height);
		game.core.popMatrix();
	}


	public void drawTheVertexes(){
		Core core = game.core;
		
		if( removed ){ return; }
		
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
		
//		core.gl.glBegin( GL.GL_QUADS );
//		for( int i = 0; i < 4; i++ ){
//			core.gl.glVertex3f( drawverts[i].x, drawverts[i].y,0 );
//		}
//		//core.gl.glVertex3f( drawverts[0].x, drawverts[0].y,0 );
//		core.gl.glEnd();
	}
}
