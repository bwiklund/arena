package massive.entities.particles;


import massive.core.Game;
import massive.core.Util;

import org.jbox2d.common.Vec2;

public class StarParticle extends Particle {

	float parallax;
	float alpha;
	
	public StarParticle(Game game, Vec2 pos) {
		super(game, pos);
		maxLife = 1337;
		parallax = -0.5f - game.random.nextFloat() * 0.4f;
		alpha = game.random.nextFloat();
	}
	
	public void step(){
		age = 0;
		radius = 0.1f;
		super.step();
	}
	
	public void draw(){
		
		color = game.core.color(255,255,255,255*alpha * game.render.zoom * 0.6f);
		game.core.stroke(color);
		game.core.strokeWeight(radius);
		
		//parallax
		//game.core.pushMatrix();
		//game.core.scale(10/game.render.zoom);
		game.core.point(pos.x-game.render.parallaxCenter.x*parallax,pos.y-game.render.parallaxCenter.y*parallax);
		//game.core.popMatrix();
	}
	
}
