package massive.entities.particles;


import massive.core.Game;
import massive.core.Util;

import org.jbox2d.common.Vec2;

public class SparkParticle extends Particle {
	
	float gray;
	float baseRadius;
	
	public SparkParticle(Game game, Vec2 pos) {
		super(game, pos);
		maxLife = 10 + game.random.nextFloat()*10;
		vel = Util.randV(0,1f);
		//vel.y*=0.5;
		gray = game.random.nextFloat() * 150 + 100;
		drag = 0.0f;// + game.random.nextFloat() * 0.1f;
		baseRadius = game.random.nextFloat() * 0.1f + 0.15f;
		vel = vel.mul(0.3f);
	}
	
	public void step(){
		radius = baseRadius;// + age/maxLife * baseRadius;
		float alpha = (1-age/maxLife) * 250;
		color = game.core.color(gray,gray,gray,alpha);
		super.step();
	}
	
}
