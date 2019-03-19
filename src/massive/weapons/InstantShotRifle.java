package massive.weapons;

import massive.core.Game;
import massive.core.Util;
import massive.entities.PhysicsEntity;
import massive.entities.Player;
import massive.entities.Wall;
import massive.entities.particles.BloodParticle;
import massive.entities.particles.DirectionalPoof;
import massive.entities.particles.ShockwaveParticle;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

public class InstantShotRifle extends ProjectileRifle {
	
	public float splashDamage = 0;
	public float splashRadius = 0;
	public float particleMultiplier = 1;
	
	public InstantShotRifle(PhysicsEntity owner ) {
		super(owner);
		fireRate.set(30);
		magazineSize = 4;
		reloadTime.set(140);
	}

	public void shootBullet(Game game) {
		doRaycast();
		
		game.sound.play(shotSound,game.random.nextFloat()*0.03f + 0.2f,owner.myBody.getPosition());
		smokePuff(owner.myBody.getAngle());
		game.ents.add( new LaserBeam( game, owner.myBody.getPosition(), entityInCrosshairsContactPoint ) );
	}
	

	PhysicsEntity entityInCrosshairs = null;
	Vec2 entityInCrosshairsContactPoint = null;
	float entityInCrosshairsFraction = 1;
	Vec2 lastNormal = null;
	
	public void doRaycast(){
		final InstantShotRifle l = this;

		entityInCrosshairs = null;
		entityInCrosshairsContactPoint = null;
		entityInCrosshairsFraction = 1;
		lastNormal = null;
		
		RayCastCallback cb = new RayCastCallback() {
			
			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				//System.out.println( "RAYCAST " + point.toString() + " " + normal.toString() + " " + fraction);
				if( fraction < l.entityInCrosshairsFraction ){
					PhysicsEntity pe = (PhysicsEntity) fixture.m_body.getUserData();
					if( !pe.laserIgnore ){
						if( pe != l.owner ){
							if( pe instanceof Wall ){
								if( ((Wall)pe).isCover ){
									if( fraction < 0.004 ){
										return -1; //don't hit cover that are close
									}
								}
							}
							l.entityInCrosshairs = pe;
							l.entityInCrosshairsFraction = fraction;
							l.entityInCrosshairsContactPoint = point.clone();
							l.lastNormal = normal.clone();
						}
					}
				}
				return -1;
			}
			
		};
		
		float angle = owner.myBody.getAngle();
		float currentAccuracy = 0;
		if( owner instanceof Player ){
			if( ((Player)owner).isDucking ){
				currentAccuracy = accuracy/2;
			} else {
				currentAccuracy = accuracy;
			}
		}
		angle += (owner.game.random.nextFloat() - 0.5f)*currentAccuracy;
		Vec2 gunTargetVector = owner.myBody.getPosition().add( Util.angleV( angle, 1000 ) );
		
		owner.game.m_world.raycast(cb, owner.myBody.getPosition(), gunTargetVector);
		 
		if( entityInCrosshairs != null ){ 
			entityInCrosshairs.injure( damagePerShot, getPlayerResponsible() );
			if( entityInCrosshairs instanceof Player ){
				doFleshyImpact(entityInCrosshairsContactPoint);
			} else {
				doRicochet(entityInCrosshairsContactPoint,lastNormal);
			}
			
			if( splashDamage > 0 ){
				PhysicsEntity.injureStuffInRadius(owner.game,entityInCrosshairsContactPoint,splashDamage,splashRadius,owner,2);
				owner.game.ents.add( new ShockwaveParticle(owner.game, entityInCrosshairsContactPoint, splashRadius, 1 ));
			}
		}
		
	}
	
	//copied from bullet class
	public void doRicochet(Vec2 vec, Vec2 normal) {
		Game game = owner.game;
		//for( int i = 0; i < 15; i++ ){
		//	game.ents.add( new SparkParticle(game, vec ) );
		//}
		
		new DirectionalPoof(game,vec,normal,(int) (damagePerShot*particleMultiplier) );
		game.sound.play("ping"+(int)(game.random.nextFloat()*6)+".wav",game.random.nextFloat()*0.1f + 0.8f,vec);
	}
	
	public void doFleshyImpact(Vec2 vec) {
		Game game = owner.game;
		for( int i = 0; i < 5; i++ ){
			game.ents.add( new BloodParticle(game, vec ) );
		}
		game.sound.play("flesh"+(int)(game.random.nextFloat()*2)+".wav",game.random.nextFloat()*0.1f + 0.8f,vec);
	}
}
