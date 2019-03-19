package massive.entities.particles;


import massive.core.Game;
import massive.core.Util;

import org.jbox2d.common.Vec2;

public class SmokeParticle extends Particle {
	
	float gray;
	public float baseRadius;
	public float size = 1;
	
	public SmokeParticle(Game game, Vec2 pos) {
		super(game, pos);
		maxLife = 20 + game.random.nextFloat()*20;
		vel = Util.randV(0,1f);
		//vel.y*=0.5;
		gray = game.random.nextFloat() * 200 + 30;
		drag = 0.01f;// + game.random.nextFloat() * 0.1f;
		baseRadius = game.random.nextFloat() * 0.2f + 0.3f;
		vel = vel.mul(0.1f);
	}
	
	public void step(){
		radius = size * (baseRadius + age/maxLife * baseRadius);
		float alpha = (1-age/maxLife) * 50;
		color = game.core.color(gray,gray,gray,alpha);
		super.step();
	}
}
