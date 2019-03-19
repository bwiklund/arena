package massive.gamemodes;

import java.util.Vector;

import massive.core.Core;
import massive.core.Game;
import massive.entities.Colors;
import massive.entities.Entity;
import massive.entities.Planet;
import massive.entities.Player;
import massive.entities.Wall;

import org.jbox2d.common.Vec2;

public class BasicArenaMap extends BaseGameMode {
	
	public static final int NUM_WALLS = 47;
	
	public float width = 120;
	public float height = 70;
	
	public BasicArenaMap(Game game) {
		super(game);
	}

	public void setupLevel(){
		
		for( int i = 0; i < NUM_WALLS; i++ ){
			float x = game.random.nextFloat();
			float y = -1+game.random.nextFloat()*2;
			x*=(width-30);y*=(height-5);
			
			float dwidth = 2f+game.random.nextFloat()*10;
			float dheight = 2f+game.random.nextFloat()*10;
			
			/*if( game.random.nextFloat() < 0.1f ){
				dwidth = 1f;
				dheight = 3f+game.random.nextFloat()*2;
			}*/
			
			if( game.random.nextFloat() < 0.1f ){
				dwidth = 2f;
				dheight = 8f+game.random.nextFloat()*8;
			}
			
			if( x*x + y*y < 0.5 * 0.5 * 400 ){
				continue;
			}
			
			float angle = (float) (game.random.nextFloat() * Math.PI * 2);
			if( game.random.nextFloat() < 0.8 ){
				float straightangle = (float) (Math.round(angle / (Math.PI / 2)) * Math.PI * 2);
				angle = straightangle;//(angle + straightangle*3) / 4;
			}
			
			Wall first = new Wall(game,-x,-y,dwidth,dheight,angle);
			game.ents.add( first );
			first.fill = game.core.blendColor(Colors.bg,0xff222222,game.core.DODGE);
			
			Wall mirror = new Wall(game,x,y,dwidth,dheight,angle+(float)Math.PI);
			game.ents.add( mirror );
			mirror.fill = game.core.blendColor(Colors.bg,0xffbbbbbb,game.core.MULTIPLY);
		}
		

		
		//add walls outside
		Wall w;
		w = new Wall(game,width,0,10,height*2+10,0);
		w.height = 6;
		game.ents.add( w );
		w = new Wall(game,-width,0,10,height*2+10,0);
		w.height = 6;
		game.ents.add( w );
		w = new Wall(game,0,-height,width*2+10,10,0);
		w.height = 6;
		game.ents.add( w );
		w = new Wall(game,0,height,width*2+10,10,0) ;
		w.height = 6;
		game.ents.add( w );
		
	}
	
	public void drawGround(){
		game.core.fill( game.core.blendColor(Colors.bg,0xffeeeeee,game.core.MULTIPLY));
		game.core.pushMatrix();
		game.core.translate(0,0,-1);
		game.core.rect(width,height,-width,-height*2);
		game.core.popMatrix();
	}

	public void drawUI(){
		Core core = game.core;
		core.textFont( core.mediumFont );
		core.textAlign( Core.LEFT, Core.TOP );
		core.text("HI!",20,20);
	}

	public void spawnPlayer(Player p) {
		if( p.team == 0 ){
			p.myBody.setTransform( new Vec2(-width+20,0), p.myBody.getAngle() );
		} else {
			p.myBody.setTransform( new Vec2( width-20,0), p.myBody.getAngle() );
		}
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

	
}
