package massive.weapons;

import massive.core.Game;
import massive.entities.Entity;

import org.jbox2d.common.Vec2;

public class LaserBeam extends Entity {

	Vec2 p1,p2;
	
	public int age = 0;
	public int maxAge = 10;
	
	public LaserBeam(Game game, Vec2 p1, Vec2 p2 ) {
		super(game);
		if( p1 == null || p2 == null ){
			age = maxAge;
			return;
		}
		this.p1 = p1.clone();
		this.p2 = p2.clone();
	}
	
	public void step(){
		age++;
		if( age > maxAge ){
			removeThis = true;
		}
	}
	
	public void draw(){
		int alpha = (int)(150 - age/(float)maxAge * 150);
		game.core.stroke(0x00ffffff | (alpha << 24) );
		game.core.line(p1.x,p1.y,p2.x,p2.y);
	}

}
