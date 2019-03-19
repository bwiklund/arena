package massive.weapons;


import massive.core.Game;
import massive.entities.PhysicsEntity;
import massive.entities.particles.ShockwaveParticle;
import massive.entities.particles.SmokeParticle;
import massive.entities.particles.SparkParticle;
import massive.utils.Cooldown;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;



public class Mine extends Bullet {
	
	public Mine(Game game, PhysicsEntity from, float speed, float inaccuracy, int maxAge) {
		super(game, from, speed, inaccuracy, maxAge);
		radius = 8f;
		safety.reset();
		fuse.reset();
	}
	
	public Cooldown safety = new Cooldown(100);
	public Cooldown fuse = new Cooldown(25);
	
	public void init(){
		super.init();

		myBody.setType( BodyType.STATIC );
		fix.setSensor(true);
        fix.getFilterData().maskBits = ~(BULLETS);
        fix.getFilterData().groupIndex = 0;
	}
	
	public float damageRadius = 12;
	public float physicsPush = 0.002f;
	public float maxDamage = 200;
	public boolean detonate = false;
	
	public void step(){
		safety.tick();
		
		if( detonate ){
			fuse.tick();
		}
		
		if( fuse.ready() || hp <= 0 ){
			
			for( int i = 0; i < 30; i++ ){
	        	SmokeParticle sp = new SmokeParticle(game, myBody.getPosition() );
	        	sp.vel = sp.vel.mul(10);
	        	sp.maxLife *= 0.5;
	        	sp.drag = 0.2f;
	        	sp.size = 2;
				game.ents.add( sp );
			}

			for( int i = 0; i < 40; i++ ){
				SparkParticle s = new SparkParticle(game, myBody.getPosition() );
				s.vel = s.vel.mul(3);
				game.ents.add( s );
			}
			
			for( int i = 0; i < 20; i++ ){
				Bullet shrapnel = new Bullet( game, this, 100, 1000, 50 + (int)game.random.nextFloat()*50 );
				shrapnel.entityResponsible = entityResponsible;
				shrapnel.init();
				shrapnel.damage = 15;
				shrapnel.myBody.setLinearDamping( game.random.nextFloat() * 0.01f );
				shrapnel.willRicochet = true;
				game.ents.add( shrapnel );
			}

			ShockwaveParticle s = new ShockwaveParticle(game, myBody.getPosition(), damageRadius, 1f );
			game.ents.add( s );
			
			game.sound.play("grenadeboom.wav",2f,myBody.getPosition());
			injureStuffInRadius(entityResponsible.game,myBody.getPosition(),maxDamage,damageRadius,entityResponsible,physicsPush);
			game.m_world.destroyBody(myBody);
			this.removeThis = true;
			
		}
	}

	public void draw(){
		Vec2 pos = myBody.getPosition();
		
		if( safety.ready() ){
			game.core.fill(0x33000000);
		} else {
			game.core.fill(0xffffffff);
		}

		game.core.ellipse(pos.x, pos.y, radius*0.13f, radius*0.13f);

		if( fuse.state < fuse.max ){
			game.core.fill(0xffff3333);
			game.core.ellipse(pos.x, pos.y, radius*0.06f, radius*0.06f);
		}

	}

	public void startFuse() {
		if( safety.ready() ){
			game.sound.play("beephigh.wav",3f,myBody.getPosition());
			detonate = true;
		}
	}

}
