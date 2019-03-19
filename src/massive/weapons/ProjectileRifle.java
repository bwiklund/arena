package massive.weapons;

import massive.core.Core;
import massive.core.Game;
import massive.core.Util;
import massive.entities.Colors;
import massive.entities.PhysicsEntity;
import massive.entities.particles.SmokeParticle;
import massive.utils.Cooldown;

import org.jbox2d.common.Vec2;

public class ProjectileRifle extends Weapon {

	public boolean inited = false;

	public int damagePerShot = 1;
	
	public int maxMagazines = 4;
	public int magazineSize = 18;
	
	public float accuracy = 0.01f;
	
	public float barrelWidth = 0.1f;
	public float barrelLength = 1f;
	
	public int numMagazines = maxMagazines;
	
	public int magazine = magazineSize;
	
	public boolean wasShootingLastTick = false;
	
	public Cooldown fireRate = new Cooldown(5);
	public Cooldown reloadTime = new Cooldown(60);
	
	public String shotSound = "snipershot.wav";
	public float shotVolume = 1;
	
	public ProjectileRifle(PhysicsEntity owner ) {
		super(owner);
	}
	
	public void stepAlways(){
		
	}
	
	public void stepWhileSelected(){
		
		if( !inited ){
			//sloppy
			numMagazines = maxMagazines;
			magazine = magazineSize;
			inited = true;
		}

		fireRate.tick();
		
		Game game = owner.game;
		
		if( magazine > 0 ){
			if( isShooting && fireRate.ready() ){
				shootBullet(game);
				fireRate.reset();
				magazine -= 1;
			} 
		} else {
			if( numMagazines > 0 ){
				
				if( reloadTime.ready() ){
					reloadTime.reset();
					game.sound.play("reloadstart.wav",0.1f,owner.myBody.getPosition());
				}
				
				if( reloadTime.state == reloadTime.max - 2 ){
					game.sound.play("reloadfinish.wav",0.1f,owner.myBody.getPosition());
				}
				
				reloadTime.tick();
				
				if( reloadTime.ready() ){
					numMagazines -= 1;
					magazine = magazineSize;
				}
			}
		}
		
		//click when there is no more ammo left
		if( numMagazines == 0 && magazine == 0 && isShooting && !wasShootingLastTick ){
			game.sound.play("empty.wav",shotVolume*(game.random.nextFloat()*0.03f + 0.1f),owner.myBody.getPosition());
		}
		wasShootingLastTick = isShooting;
	}

	public void shootBullet(Game game) {
		Bullet bullet = new Bullet(game,owner, 
				1000, 	//speed
				accuracy ,	//inaccuracy, 0 == perfect aim
				200 );	//turns until bullet is deleted
				
		//Bullet bullet = new Grenade(game,this, 5, 0.01f, 200 );
		bullet.init();
		game.ents.add( bullet );
		smokePuff(owner.myBody.getAngle());
		game.sound.play(shotSound,game.random.nextFloat()*0.03f + 0.1f,owner.myBody.getPosition());
	}
	
	public void smokePuff(float angle){
		for( int i = 0; i < 3; i++ ){
        	SmokeParticle sp = new SmokeParticle(owner.game, owner.myBody.getPosition() );
        	Vec2 addVel = Util.angleV(angle, 4);
        	addVel.normalize();
        	sp.pos = sp.pos.add(addVel.mul(0.1f));
        	addVel = addVel.mul(0.3f);
        	sp.vel = sp.vel.add(addVel);
			owner.game.ents.add( sp );
		}
	}
	
	public void drawUI(){
		drawAmmo();
		Game game = owner.game;
		int x = game.core.w - 10; int y = 90;
		game.core.textAlign( Core.RIGHT, Core.TOP);
		game.core.textFont( game.core.smallFont );
		game.core.text(name,x,y);
		super.drawUI();
		
	}

	public void drawAmmo() {
		Game game = owner.game;
		game.core.fill(0xffffffff);
		game.core.noStroke();
		int x = game.core.w - 10 - 15; int y = 110;
		
		int cutoff = 0;
		
		if( magazine > 0 ){
			cutoff = magazine;
		} else if( numMagazines == 0 ){
			cutoff = 0;
		} else {
			cutoff = (int) (magazineSize * ( 1 - reloadTime.state / (float)reloadTime.max ));
		}
		for( int i = 0; i < magazineSize; i++ ){
			if( i < cutoff ){
				if( magazine > 0 ){
					game.core.fill(0xffffffff);
				} else {
					game.core.fill(0xffdddddd);
				}
			} else {
				game.core.fill(0xff555555);
			}
			drawUIammo(game, x, y + i * (ui_ammoheight+4), i);
		}
		
		drawMagazines();
	}
	
	public int ui_ammowidth = 15;
	public int ui_ammoheight = 6;
	public void drawUIammo(Game game, int x, int y, int i) {
		game.core.rect(x,y,ui_ammowidth,ui_ammoheight);
	}

	public void drawMagazines() {
		Game game = owner.game;
		game.core.fill(0xffffffff);
		game.core.noStroke();
		int x = game.core.w - 10 - 25; int y = 110;
		/*for( int i = 0; i < numMagazines; i++ ){
			game.core.fill(0xffffffff);
			game.core.rect(x - i * 20,y,10,30);
		}*/
		if( numMagazines > 0 ){
			game.core.fill( 0xffffffff );
			game.core.textFont( game.core.smallFont );
			game.core.textAlign( Core.RIGHT, Core.TOP);
			game.core.text("x"+( numMagazines - ( reloadTime.state > 0 ? 1 : 0)),x,y);
		}
	}
	
	public void reload() {
		if( numMagazines > 0 && magazine < magazineSize ){
			magazine = 0;
		}
	}
	
	public void refillAmmo() {
		//add an extra magazine while reloading, so you get the max

		boolean playSound = false;
		
		
		if( maxMagazines == 0 && magazine < magazineSize ){
			//just fill the clip directly, like grenades
			magazine = magazineSize;
			playSound = true;
		}
		
		int newNumMagazines = maxMagazines + ( reloadTime.state > 0 ? 1 : 0);
		
		if( numMagazines != newNumMagazines ){
			//we put bullets in
			playSound = true;
		}
		
		if( playSound ){
			owner.playReloadSound = true;
		}

		numMagazines = newNumMagazines;
		
	}
	
	public void drawModel(){
		Core c = owner.game.core;

		c.pushMatrix();
		c.pushStyle();
			
			float angle = owner.getDisplayAngle();
			Vec2 v = owner.myBody.getPosition().add( Util.angleV( angle, 0 ) );
			
			c.translate( v.x, v.y, 0 );
			c.rotate(angle);
			c.fill(Colors.Team[owner.team]);
			c.noStroke();
			
			c.beginShape();
			float width = 0.2f;//barrelWidth;
			float length = 0.6f;//barrelLength;
			float start = 0.75f;
			c.vertex(length+start,width);
			c.vertex(length+start,-width);
			c.vertex(start,-width);
			c.vertex(start,width);
			c.vertex(length+start,width);
			c.endShape();
		
		c.popStyle();
		c.popMatrix();
	}
}
