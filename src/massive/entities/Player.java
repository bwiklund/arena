package massive.entities;

import java.util.LinkedList;
import java.util.Vector;

import massive.core.Core;
import massive.core.Game;
import massive.core.Util;
import massive.entities.particles.BloodParticle;
import massive.weapons.AutomaticRifle;
import massive.weapons.DepthChargeLauncher;
import massive.weapons.GrenadeLauncher;
import massive.weapons.InstantShotRifle;
import massive.weapons.TurretBuilder;
import massive.weapons.Weapon;

import org.jbox2d.common.Vec2;




public strictfp class Player extends CircleEntity {


	public int playerNumber;

	public float speedModifier;

	public float walkAngle = 0;
	public float localWalkAngle = 0;
	
	public float cursorDistance = 10;
	public float localCursorDistance = 10;
	
	public float walkingSpeed = 0;
	public float strafe = 0;
	
	protected boolean inDitch = false;
	public boolean isDucking = false;
	public boolean isRunning = false;
	
	public int deathTime = 100;
	public int deathTimer = 0;

	public int currentWeapon;
	public Vector<Weapon> weapons = new Vector<Weapon>();
	public Vector<Item> items = new Vector<Item>();
	public PlayerUpgrades upgrades = new PlayerUpgrades(3,3,0,3,2);
	
	public int hitboxTrailSize = 10;
	public LinkedList<Vec2> hitboxTrail = new LinkedList<Vec2>();
	
	public PlayerModel3d model3d = new PlayerModel3d();
	public PlayerModel2d model2d;
	
	public boolean canChangeWeapons = !false;

	public int armor = 0;
	public float zoomTarget = 1;
	public float cameraoffset = 1;

	public boolean driftMode = false;
	public boolean drawSpaceMode = false;


	
	public Player(Game game, int playerNumber, String playerName) {
		super(game);
		this.name = playerName;
		this.playerNumber = playerNumber;
		team = game.gameMode.getStartingTeam();
		this.radius *= 1.2f;
		createBody();
		turretTargetable = true;
		resetPlayer();
		model2d = new PlayerModel2d(this);
		
		drawingLayer = 1;
	}
	
	public void step(){
		
		resetStatsAndApplyUpgrades();
		
		if( isDucking && inDitch ){
	        fix.getFilterData().maskBits = ~BULLETS;
		} else {
	        fix.getFilterData().maskBits = ~0;
		}
		
		if( isDucking ){
			upgrades.lvl_weaponAccuracy = 3;
		} else {
			upgrades.lvl_weaponAccuracy = 1;
		}
		
		checkPlayerDeath();

		if( isAlive() ){
			doHealing();
			doPlayerMovement();
		}
		
		if( (isDucking && inDitch) || ( isRunning && ( walkingSpeed != 0 || strafe != 0 ) ) ){
			if( weapons.size() > 0 ){
				weapons.get(currentWeapon).isShooting = false;
			}
		}

		if( isAlive() ){
			if( weapons.size() > 0 ){
				weapons.get(currentWeapon).stepWhileSelected();
			}

			for( Weapon w : weapons ){
				w.stepAlways();
			}
		}
		
		super.step();
	}

	public void doHealing() {
		if( hp < 100 ){ hp+=0.05; }
	}

	private void doPlayerMovement() {
		
		if( driftMode ){
			Vec2 anglev = myBody.getLinearVelocity();
			walkAngle = (float) Math.atan2(anglev.y,anglev.x);
			localWalkAngle = walkAngle;
		}
		
		if(isDucking){
			speedModifier *= 0.5f;// + Math.random();
		} else if ( isRunning ){
			speedModifier *= 1.34f;
		}
		
		float angle = walkAngle;
		myBody.setTransform( myBody.getPosition(), angle);
		
		Vec2 moveSpeed = Util.angleV(walkAngle, 1);
		moveSpeed = moveSpeed.mul( walkingSpeed );
		
		Vec2 strafeVec = new Vec2( (float)Math.cos(angle + Math.PI/2), (float)Math.sin(angle + Math.PI/2) );
		strafeVec = strafeVec.mul( strafe );
		moveSpeed = moveSpeed.add(strafeVec);
		
		moveSpeed.normalize();
		
		moveSpeed = moveSpeed.mul( upgrades.getPlayerSpeed() * speedModifier );
		
		myBody.setAwake(true);
		
		if( !driftMode  ){
			myBody.setLinearDamping(0.1f);
			myBody.setLinearVelocity( moveSpeed );
			cameraoffset = 1;
		} else {
			myBody.setLinearDamping(0);
			myBody.applyLinearImpulse( moveSpeed.mul(0.009f), myBody.getPosition() );
			cameraoffset = 0;
		}
	}

	public void checkPlayerDeath() {
		
		if( hp <= 0 && deathTimer == 0 ){
			for( int i = 0; i < 20; i++ ){
				game.ents.add( new BloodParticle(game, myBody.getPosition() ) );
			}
			
			killMyTurretsAndStuff();
			
			if( lastAttacker != null ){
				game.gameMode.playerKilled(lastAttacker, this);
				lastAttacker = null;
			} else {
				game.console.addLine( name + " died" );
			}
			deathTimer = deathTime;
			
			setGhost(true);
		}
		
		if( deathTimer == 1 ){
			setGhost(false);
			resetPlayer();
		}

		deathTimer -= 1;
		deathTimer = Math.max(deathTimer,0);
	}

	public void killMyTurretsAndStuff() {
		for( Weapon w : weapons ){
			w.onOwnerDie(this);
		}
		for( Entity e : killOnDeath ){
			if( e instanceof PhysicsEntity ){
				((PhysicsEntity)e).hp = 0;
			}
		}
		killOnDeath.clear();
	}

	public void setGhost(boolean b) {
		fix.setSensor(b);
		if( b ){
			fix.getFilterData().categoryBits = BULLETS;
		} else {
			fix.getFilterData().categoryBits = 1;
		}
		laserIgnore = b;
		turretIgnore = b;
		turretTargetable = !b;
	}
	
	public boolean isAlive(){
		return deathTimer == 0;
	}

	public void resetPlayer() {
		hp = 100;
		game.gameMode.spawnPlayer(this);
		float oldWalkAngle = walkAngle;
		walkAngle = (float)Math.PI-(float) Math.atan2( myBody.getPosition().y,myBody.getPosition().x );
		localWalkAngle += walkAngle-oldWalkAngle; //this is a wacky fix
		//myBody.setTransform( startSpot, walkAngle );
		hitboxTrail.clear();
		resetInventory();
	}

	private void resetStatsAndApplyUpgrades() {
		armor = 0;
		cameraoffset = (cameraoffset * 8 + zoomTarget) / 9f;
		
		for( Weapon i : weapons ){
			i.affect(this);
		}
		
		int numThings = items.size()+weapons.size();
		speedModifier = 2-(numThings/4.0f);
	}

	public void resetInventory() {
		weapons.clear();
		currentWeapon = 0;
		
		InstantShotRifle rifle =  new AutomaticRifle(this);
		//DepthChargeLauncher rifle = new DepthChargeLauncher(this);
		/*InstantShotRifle sniper = new SniperRifle(this);
		Shotgun shotgun = new Shotgun(this);
		GrenadeLauncer grenadeLauncher = new GrenadeLauncer(this);
		TurretBuilder turretBuilder = new TurretBuilder(this);
		WallBuilder wallBuilder = new WallBuilder(this);*/
		
		weapons.add( rifle );
	}
	
	public void createBody() {
        super.createBody(new Vec2(0,0));
        game.gameMode.spawnPlayer(this);
        fix.getFilterData().groupIndex = PLAYERGROUPS-this.id;
	}
	
	public void draw(){
		
		if( !isAlive() ){
			return;
		}
		
		Vec2 pos = myBody.getPosition();
		
		game.core.stroke(0x22ffffff);
		
		if( isLocalPlayer() ){
			Vec2 aim = Util.angleV(localWalkAngle, 1000);
			game.core.line( pos.x, pos.y, pos.x+aim.x, pos.y+aim.y );
			
			//show cursor. TODO: make this more general
			/*if( currentWeapon > -1 ){
				if( weapons.get(currentWeapon) instanceof GrenadeLauncher ){
					game.core.fill(0x22ffffff);
					game.core.stroke(0x88ffffff);
					Vec2 cursor = Util.angleV(localWalkAngle, localCursorDistance);
					game.core.ellipse( pos.x+cursor.x, pos.y+cursor.y, 1,1 );
				}
			}*/
			
		} else {

			Vec2 aim = Util.angleV( myBody.getAngle(), 1.2f);
			game.core.pushStyle();
			game.core.strokeWeight(0.2f * game.render.zoom);
			game.core.stroke(fill);
			game.core.line( pos.x, pos.y, pos.x+aim.x, pos.y+aim.y );
			game.core.popStyle();
		}
		
		fill = Colors.Team[team];
		
		game.core.noStroke();
		
		if( !game.render.draw3d ){

			model2d.draw();
			//drawhitboxtrail();
		} else {
			model3d.draw(this);
		}
	}
	
	public void injure(int a, PhysicsEntity attacker ){
		//each layer of armor reduces damage by 20%;
		a *= getArmorPercentage();
		super.injure(a,attacker);
	}

	public float getArmorPercentage() {
		float reductionPerLayer = 0.8f;
		float totalReduction = (float) Math.pow(reductionPerLayer,armor);
		return totalReduction;
	}
	
	/*private void updateHitboxTrail() {
		hitboxTrail.add( myBody.getPosition().clone() );
		if( hitboxTrail.size() > hitboxTrailSize ){
			hitboxTrail.remove(0);
		}
	}

	private void drawhitboxtrail() {
		int c = Colors.Team[team];
		c = c & 0x11ffffff;
		game.core.fill(c);
		float drawRadius = 2 * radius;
		for( Vec2 pos : hitboxTrail ){
			game.core.ellipse(pos.x, pos.y, drawRadius, drawRadius);
		}
	}*/

	/*
	 * Finds out if this player is the local player, for various stuff
	 * Woe be to anyone who changes the game state based on this function
	 */
	public boolean isLocalPlayer() {
		if( game.currentPlayer == -1 ){ return false; }
		return game.ents.players.get(game.currentPlayer) == this;
	}

	public void drawUI(){
		drawHealthBar();
		game.core.pushMatrix();
		game.core.translate(0,100);
		if( weapons.size() > 0 ){
			weapons.get(currentWeapon).drawUI();
		}
		game.core.popMatrix();
		
		drawItems();
	}

	private void drawItems() {
		int place = 0;
		for( int i = 0; i < weapons.size(); i++ ){
			float x = game.core.w-70; float y = 400+27*place++;
			weapons.get(i).drawIcon(x,y,70,25,3,i==currentWeapon);
		}
	}

	private void drawHealthBar() {
		int x = game.core.w - 10; int y = 150;
		game.core.noStroke();
		
		game.core.fill( Colors.uibg );
		game.core.rect(game.core.w-70,0,70,game.core.h-27);
		
		game.core.fill(0xff000000);
		game.core.rect(x,y,-15,-upgrades.getPlayerMaxHealth() );
		game.core.fill(0xffaa4444);
		float h = (float) -hp;
		if( h > 0 ){ h = 0 ; }
		game.core.rect(x,y,-15,h);
		
		game.core.textAlign(Core.RIGHT,Core.TOP);
		game.core.textFont( game.core.smallFont );
		
		game.core.fill( Colors.armorColor );
		game.core.text("Armor: " + (100-Math.round(getArmorPercentage()*100)),x,y+10);
		game.core.fill( Colors.speedColor );
		game.core.text("Speed: " + (Math.round(speedModifier*50)),x,y+20);
		
	}


	
	public float getDisplayAngle(){
		if( isLocalPlayer() ){
			return localWalkAngle;
		} else {
			return myBody.getAngle();
		}
	}

	public void die(){
		killMyTurretsAndStuff();
		super.die();
	}
	
}
