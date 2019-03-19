package massive.entities;

import java.security.acl.Owner;

import massive.core.Core;
import massive.core.Game;
import massive.core.Util;
import massive.entities.particles.SmokeParticle;
import massive.entities.particles.SparkParticle;
import massive.weapons.InstantShotRifle;

import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

//TODO: decoys /flares to counter turrets?

public class Turret extends CircleEntity {

	public InstantShotRifle gun;
	
	public float angle = 0;
	
	public int excitementState = 0;
	public int maxExcitement = 50;
	
	public PhysicsEntity entityInCrosshairs = null;
	public Vec2 entityInCrosshairsContactPoint = null;
	public float entityInCrosshairsFraction = 1;
	public float fraction = 0;
	
	public boolean isWalking = !true;
	
	public Vec2 gunTargetVector = null;
	
	public PhysicsEntity targetEntity = null;
	public float closestTargetDistance = Integer.MAX_VALUE;
	
	public float walkAngle;
	
	public Vec2 walkTarget = null;
	
	public Turret(Game game, Vec2 position, int team) {
		super(game);
		//radius = 0.55f;
		angle = (float) (game.random.nextFloat() * Math.PI * 2);
		createBody(position);
		
		thinkInterval = 10;
		
		//gun = new ProjectileRifle(this);
		gun = new InstantShotRifle(this);
		gun.damagePerShot = 20;
		gun.maxMagazines = 0;
		gun.magazineSize = 50;
		gun.accuracy = 0.03f;
		gun.fireRate.set(20);
		gun.shotSound = "turretgun.wav";
		
		//gun.magazineReloadTime = 500/2;
		//turretIgnore = true;
		turretTargetable = true;
		this.team = team;
	}

	public void createBody(Vec2 position) {
		super.createBody(position);
		fix.getFilterData().groupIndex = PLAYERGROUPS - this.id;
		myBody.setLinearDamping(2f);
		myBody.setAngularDamping(2f);
	}

	public void step() {
		//copied if statement from player class
		if( hp <= 0 || gun.magazine == 0 ){
			
			for( int i = 0; i < 30; i++ ){
	        	SmokeParticle sp = new SmokeParticle(game, myBody.getPosition() );
	        	sp.vel = sp.vel.mul(8);
	        	sp.maxLife *= 0.5;
	        	sp.drag = 0.2f;
	        	sp.size = 2;
				game.ents.add( sp );
			}
			for( int i = 0; i < 40; i++ ){
				SparkParticle s = new SparkParticle(game, myBody.getPosition() );
				s.vel.mul(15);
				game.ents.add( s );
			}
			removeThis = true;
		}
		
		if( walkTarget != null ){
			//walkAngle += game.random.nextFloat()*0.4f - 0.2f;
			Vec2 walk = walkTarget.sub( myBody.getPosition() );
			if( walk.length() < 0.1 ){
				walkTarget = null;
			} else {
				walk.normalize();
				myBody.applyLinearImpulse( walk.mul(0.5f), myBody.getPosition() );
			}
		}

		super.step();
		
		excitementState = Math.max(0,excitementState-1);
		if( excitementState > 0 && walkTarget == null ){
			gun.isShooting = true;
		} else {
			gun.isShooting = false;
		}
		
		gun.stepWhileSelected();
		
		gun.numMagazines = 1;
	}
	
	public void think(){
		lookForTargets();
	}
	
	
	public float accuracy = 0.08f;
	public Vec2 laserPointer = null;

	public boolean highlight = false;
	
	public void lookForTargets(){
		targetEntity = null;
		closestTargetDistance = 1;
		laserPointer = null;
		
		for( Entity p : game.ents ){
			if( p instanceof PhysicsEntity ){
				PhysicsEntity pe = (PhysicsEntity)p;
				if( pe.turretTargetable ){
					float dist = checkPlayerLineOfSight(pe);
					if( dist < closestTargetDistance ){
						targetEntity = pe;
						closestTargetDistance = dist;
						laserPointer = entityInCrosshairsContactPoint.clone();
					}
				}
			}
			
		}
		
		if(targetEntity != null){
			Vec2 diff = targetEntity.myBody.getPosition().sub(myBody.getPosition());
			float angle = (float) Math.atan2(diff.y,diff.x);
			angle += accuracy * (game.random.nextFloat() - 0.5f);
			myBody.setTransform(myBody.getPosition(),angle);
			excitementState = maxExcitement;
		}
		
	}
	
	public float checkPlayerLineOfSight(PhysicsEntity pe){
		Vec2 p1 = myBody.getPosition();
		gunTargetVector = pe.myBody.getPosition();
		
		final Turret t = this;
		entityInCrosshairs = null;
		entityInCrosshairsContactPoint = gunTargetVector.clone();
		entityInCrosshairsFraction = 1;
		
		RayCastCallback cb = new RayCastCallback() {
			
			@Override
			public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
				//System.out.println( "RAYCAST " + point.toString() + " " + normal.toString() + " " + fraction);
				if( fraction < entityInCrosshairsFraction ){
					PhysicsEntity pe = (PhysicsEntity) fixture.m_body.getUserData();
					if( !pe.turretIgnore && pe != t ){
						t.entityInCrosshairs = pe;
						t.entityInCrosshairsFraction = fraction;
						t.entityInCrosshairsContactPoint = point.clone();
					}
				}
				return -1;
			}
			
		};
		
		game.m_world.raycast(cb, p1, gunTargetVector);
		
		if( entityInCrosshairs != null ){
			if( entityInCrosshairs.turretTargetable ){
				if( entityInCrosshairs.team != this.team ){
					return entityInCrosshairsFraction;
				}
			}
		}

		return Float.MAX_VALUE;
	}
	
	public void draw(){

		Core c = game.core;
		Vec2 pos = myBody.getPosition();
		
		/*if( laserPointer != null ){
			game.core.stroke(0xffff3333);
			game.core.strokeWeight(2);
			game.core.line(pos.x,pos.y,laserPointer.x,laserPointer.y);
		}*/
		
		if( walkTarget != null ){
			c.stroke(0x44ffffff);
			c.strokeWeight(2);
			c.line( pos.x,pos.y,walkTarget.x,walkTarget.y);
		}
		/*if( targetEntity != null ){
			game.core.stroke(Colors.Team[team]);
			Vec2 v = targetEntity.myBody.getPosition();
			game.core.line(pos.x,pos.y,v.x,v.y);
		}*/
		
		/*float fov1 = angle + coneWidth;
		float fov2 = angle - coneWidth;
		Vec2 vfov1 = Util.angleV(fov1, turretRange).add(pos);
		Vec2 vfov2 = Util.angleV(fov2, turretRange).add(pos);
		game.core.line(pos.x,pos.y,vfov1.x,vfov1.y);
		game.core.line(pos.x,pos.y,vfov2.x,vfov2.y);*/

		//game.core.fill(~0);
		//game.core.noStroke();
		
		fill = Colors.Team[team];
		//game.core.ellipse(pos.x, pos.y, radius*2.5f, radius*2.5f);

		game.core.pushStyle();
		int numSpikes = 5;
		float spikeRadius = 1.15f;
		for( int i = 0; i < numSpikes; i++ ){
			game.core.stroke( fill );
			game.core.strokeWeight( 2 );
			Vec2 v = Util.angleV( (float)(i/(float)numSpikes * Math.PI * 2), spikeRadius );
			game.core.line(pos.x,pos.y,pos.x+v.x,pos.y+v.y);
		}
		game.core.popStyle();
		
		if( highlight ){
			c.pushStyle();
			c.noFill();
			c.stroke(0xffffffff);
			c.strokeWeight(2);
			c.nGonLines(pos.x,pos.y,16,1.4f);
			c.popStyle();
		}
		
		super.draw();

		if( !game.render.draw3d ){
			game.core.fill(0xff444444);
			game.core.ellipse(pos.x, pos.y, radius*1.5f, radius*1.5f);
		}
		
		
	}

}
