package massive.entities.particles;


import massive.core.Game;
import massive.core.Util;

import org.jbox2d.common.Vec2;

public class BloodParticle extends Particle {
	
	float gray;
	float baseRadius;
	
	public BloodParticle(Game game, Vec2 pos) {
		super(game, pos);
		maxLife = 30 + game.random.nextFloat()*90;
		vel = Util.randV(0,1f);
		//vel.y*=0.5;
		drag = 0.01f + game.random.nextFloat() * 0.1f;
		baseRadius = game.random.nextFloat() * 0.2f + 0.2f;
		vel = vel.mul(0.1f);
	}
	
	public void step(){
		radius = baseRadius - age/maxLife * baseRadius;
		color = game.core.color(245,10,10,100);
		super.step();
	}
	
}
