package massive.entities.particles;

import massive.core.Game;

import org.jbox2d.common.Vec2;

public class SmokeTrail {

	Vec2 p1,p2;
	
	public int age = 0;
	public int maxAge = 10;
	
	public SmokeTrail(Game game, Vec2 p1, Vec2 p2 ) {
		this.p1 = p1.clone();
		this.p2 = p2.clone();
		
		int numParticles = (int) Math.min(100,p1.sub(p2).length()/0.5f);
		for( int i = 0; i < numParticles; i++ ){
			Vec2 v = p2.sub(p1).mul(i/(float)numParticles).add(p1);
			SmokeParticle s = new SmokeParticle(game, v );
			game.ents.add( s );
		}
	}

}
