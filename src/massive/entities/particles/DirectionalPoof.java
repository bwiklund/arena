package massive.entities.particles;

import massive.core.Game;

import org.jbox2d.common.Vec2;

public class DirectionalPoof {
	// the intended look:
	// http://i1100.photobucket.com/albums/g406/yju895/001.gif?t=1309625195
	
	// elements:
	// 1. blurry white smoke that moves slowly and smoothly
	// 2. spinning shards of debris
	
	public DirectionalPoof( Game game, Vec2 vec, Vec2 normal, int numParticles ){
		//for( int i = 0; i < 15; i++ ){
		//	game.ents.add( new SparkParticle(game, vec ) );
		//}
		for( int i = 0; i < numParticles; i++ ){
			SmokeParticle s = new SmokeParticle(game, vec );
			s.baseRadius *= 1.5f;
			if( Vec2.dot( normal, s.vel ) < 0 ){
				continue;
			}
			game.ents.add( s );
			
		}
		for( int i = 0; i < numParticles; i++ ){
			ShardParticle s = new ShardParticle(game, vec);
			if( Vec2.dot( normal, s.vel ) < 0 ){
				continue;
			}
			game.ents.add( s );
		}
	}
}
