package massive.entities.particles;

import massive.core.Game;

import org.jbox2d.common.Vec2;

public class ShockwaveParticle extends Particle {

	public float finalRadius;
	public float c;
	
	public ShockwaveParticle(Game game, Vec2 p, float finalRadius, float c ) {
		super(game, p);
		this.finalRadius = finalRadius;
		this.c = c;
		maxLife = 7;
	}
	
	public void step(){
		radius = age/maxLife * finalRadius;
		float alpha = (1-age/maxLife)*127;
		color = game.core.color(255,255,255, alpha * c);
		super.step();
	}
	
	public void draw(){
		game.core.noStroke();
		game.core.stroke(color);
		game.core.strokeWeight( 2 );
		game.core.nGonLines(pos.x,pos.y,36,radius);
	}
}
