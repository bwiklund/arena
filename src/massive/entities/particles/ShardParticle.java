package massive.entities.particles;


import java.util.Vector;

import massive.core.Game;
import massive.core.Util;

import org.jbox2d.common.Vec2;

public class ShardParticle extends Particle {
	
	float gray;
	float baseRadius;
	float angle,dAngle;
	
	Vector<Vec2> points = new Vector<Vec2>();
	
	public ShardParticle(Game game, Vec2 pos) {
		super(game, pos);
		maxLife = 5 + game.random.nextFloat()*30;
		vel = Util.randV(0,1f);
		gray = game.random.nextFloat() * 0 + 50;
		drag = 0.05f;// + game.random.nextFloat() * 0.1f;
		baseRadius = game.random.nextFloat() * 0.1f + 0.15f;
		vel = vel.mul(0.8f);
		dAngle = (game.random.nextFloat() - 0.5f)*3;
		makeShard();
	}
	
	private void makeShard() {
		int n = game.random.nextInt() % 5 + 2;
		for( int i = 0; i < n; i++ ){
			points.add( Util.angleV( 
					(float) (i / (float)n * Math.PI * 2), 
					game.random.nextFloat()*0.6f ) 
			);
		}
	}

	public void step(){
		radius = baseRadius;// + age/maxLife * baseRadius;
		float alpha = (1-age/maxLife) * 250;
		color = game.core.color(gray,gray,gray,alpha);
		angle += dAngle;
		super.step();
	}
	
	public void draw(){
		game.core.noStroke();
		game.core.fill(color);
		game.core.pushMatrix();
		game.core.translate(pos.x,pos.y);
		
		game.core.rotateZ(angle);
		game.core.beginShape();
		int n = points.size();
		for( int i = 0; i < n; i++ ){
			Vec2 p = points.get(i);
			game.core.vertex( p.x,p.y );
		}
		game.core.endShape();
		
		game.core.popMatrix();
	}
	
}
