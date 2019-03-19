package massive.gamemodes;

import java.util.Vector;

import massive.core.Core;
import massive.core.Game;
import massive.core.Util;
import massive.entities.Colors;
import massive.entities.Entity;
import massive.entities.PhysicsEntity;
import massive.entities.Planet;
import massive.entities.Player;
import massive.entities.Wall;
import massive.entities.particles.StarParticle;

import org.jbox2d.common.Vec2;

public class BasicSpaceMap extends BaseGameMode {
	
	public static final int NUM_PLANETS = 8;
	public static final int NUM_BG_STARS = 5000;
	
	public float width = 100;

	public Vector<Planet> gravitySources = new Vector<Planet>();
	
	public BasicSpaceMap(Game game) {
		super(game);
	}

	public void setupLevel(){

		Colors.Team[0] = 0xffff9966;
		Colors.Team[1] = 0xff6699ff;
		
		for( int i = 0; i < NUM_BG_STARS; i++ ){
			float x = game.random.nextFloat()*2-1;
			float y = game.random.nextFloat()*2-1;
			x*=width*2;
			y*=width*2;
			
			StarParticle p1 = new StarParticle(game, new Vec2(x,y) );
			game.ents.add(p1);
		}

		
		for( int i = 0; i < NUM_PLANETS; i++ ){
			/*float x = game.random.nextFloat()*2-1;
			float y = game.random.nextFloat();

			x*=width*1f;
			y*=height*1f;*/
			
			float dist = (float) ((i+3) / (float)NUM_PLANETS ) * width;
			float angle = (float) (game.random.nextFloat() * Math.PI * 2);
			Vec2 v = Util.angleV(angle, dist );
			
			float m = game.random.nextFloat()*30000+3000;
			Planet p = new Planet(game,v.x,v.y,m);
			
			game.core.colorMode( game.core.HSB,1 );
			int c = game.core.color( game.random.nextFloat(), game.random.nextFloat(), game.random.nextFloat()*0.3f + 0.7f );
			p.fill = c;
			game.core.colorMode( game.core.RGB, 255 );
			
			
			//centripetal force = force of gravity, do math
			float neutralVelocity = (float)Math.sqrt( 10 * G * 1000000 / dist );
			Vec2 vVel = Util.angleV(angle+(float)Math.PI/2, neutralVelocity );
			p.myBody.setLinearVelocity(vVel);
			
			System.out.println(m + " - " + p.myBody.getMass() );
				
			game.ents.add(p);
			gravitySources.add(p);
		}

		Planet p = new Planet(game,0,0,1000000); //sun
		game.ents.add(p);
		gravitySources.add(p);
		
	}
	
	public void drawGround(){
		/*game.core.fill( game.core.blendColor(Colors.bg,0xffeeeeee,game.core.MULTIPLY));
		game.core.pushMatrix();
		game.core.translate(0,0,-1);
		game.core.rect(width,height,-width,-height*2);
		game.core.popMatrix();*/
		game.core.background(0);
	}

	public void drawUI(){
		Core core = game.core;
		core.textFont( core.mediumFont );
		core.textAlign( Core.LEFT, Core.TOP );
		core.text("HI!",20,20);
	}

	public void spawnPlayer(Player p) {
		if( p.team == 0 ){
			p.myBody.setTransform( new Vec2(-width*0.5f+20,0), p.myBody.getAngle() );
		} else {
			p.myBody.setTransform( new Vec2( width*0.5f-20,0), p.myBody.getAngle() );
		}
		p.driftMode = true;
	}

	public void resetMap() {
		for( Entity e : game.ents ){
			if( e instanceof Player ){
				((Player) e).resetPlayer();
			} else {
				e.removeThis = true;
			}
		}
		setupLevel();
	}
	
	public void step(){
		doGravity();
	}
	
	public static float G = 0.000001f;

	private void doGravity() {
		for( Planet p : gravitySources ){
			for( Entity e : game.ents ){
				if( p == e ){ continue; }
				//if( e instanceof Planet ){ continue; }
				
				if( e instanceof PhysicsEntity ){ //todo: make a running cache of these at the start
					PhysicsEntity pe = (PhysicsEntity)e;
					pe.myBody.setLinearDamping(0);
					
					Vec2 vec = p.myBody.getPosition().clone();
					vec = vec.sub( pe.myBody.getPosition() );
					
					float distSq = vec.lengthSquared();
					if( distSq == 0 ){ continue; }
					
					float forceScalar = G * p.myBody.getMass()+pe.myBody.getMass()/distSq;
					
					vec.normalize();
					vec = vec.mul( forceScalar );
					
					pe.myBody.applyForce(vec, pe.myBody.getWorldCenter() );
				}
			}
		}
	}
}
