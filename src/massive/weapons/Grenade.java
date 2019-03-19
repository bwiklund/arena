package massive.weapons;


import massive.core.Game;
import massive.entities.PhysicsEntity;
import massive.entities.particles.ShockwaveParticle;
import massive.entities.particles.SmokeParticle;
import massive.entities.particles.SparkParticle;

import org.jbox2d.common.Vec2;



public class Grenade extends Bullet {
	
	public Grenade(Game game, PhysicsEntity from, float speed, float inaccuracy, int maxAge) {
		super(game, from, speed, inaccuracy, maxAge);
		radius = 0.3f;
	}
	
	public float damageRadius = 15;
	public float physicsPush = 0.002f;
	public float maxDamage = 200;
	public int blinkState = 0;
	
	public void step(){
		age++;

		blinkState -= 1;
		
		int beep1 = maxAge / 4;
		int beep2 = beep1 * 2;
		int beep3 = beep1 * 3;
		
		if( age == beep1 || age == beep2 ){
			game.sound.play("beeplow.wav",3f,myBody.getPosition());
			blinkState = 4;
		}else if( age == beep3 ){
			System.out.println(age + " " + maxAge );
			game.sound.play("beephigh.wav",3f,myBody.getPosition());
			blinkState = 4;
		}else{
			
		}
		
		if(age==beep1||age==beep2||age==beep3){
			ShockwaveParticle s = new ShockwaveParticle(game, myBody.getPosition(), damageRadius, 0.2f );
			game.ents.add( s );
		}
		
		if(age>maxAge){
			
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
			
			/*for( int i = 0; i < 60; i++ ){
				Bullet shrapnel = new Bullet( game, this, 100, 1000, 50 + (int)game.random.nextFloat()*50 );
				shrapnel.owner = from;
				shrapnel.willRicochet = true;
				shrapnel.init();
				shrapnel.fix.m_restitution = 0.3f;
				game.ents.add( shrapnel );
			}*/

			ShockwaveParticle s = new ShockwaveParticle(game, myBody.getPosition(), damageRadius, 1f );
			game.ents.add( s );
			
			game.sound.play("grenadeboom.wav",2f,myBody.getPosition());
			injureStuffInRadius(entityResponsible.game,myBody.getPosition(),maxDamage,damageRadius,entityResponsible,physicsPush);
			game.m_world.destroyBody(myBody);
			this.removeThis = true;
			
		}
		
		if( age > 2 ){
	        fix.getFilterData().maskBits = ~(BULLETS);
	        fix.getFilterData().groupIndex = 0;
		}
	}
	
	


	public void createBody(){
		super.createBody();
        myBody.m_linearDamping = 0.3f;
	}

	public void draw(){
		Vec2 pos = myBody.getPosition();
		if(blinkState > 0){
			game.core.fill(0xffff0000);
		} else {
			game.core.fill(0xffffffff);
		}
		game.core.ellipse(pos.x, pos.y, radius*2, radius*2);
	}




	public void detonate() {
		age = maxAge;
	}

}
