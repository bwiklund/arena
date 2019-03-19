package massive.entities;

import massive.core.Core;
import massive.core.Game;

import org.jbox2d.common.Vec2;

public class PlayerModel2d {
	
	//Vector<SpiderLeg> legs = new Vector<SpiderLeg>();
	Player player;
	
	public PlayerModel2d(Player player){
		this.player = player;
//		float anchor = 1f;
//		float foot = 2;
//		int num = 8;
//		for( int i = 0; i < num; i++ ){
//			float angle = (float) (i / (float)num * Math.PI * 2);
//			legs.add( new SpiderLeg( Util.angleV( angle, anchor), Util.angleV( angle, foot) ) );
//		}
	}
	
	public void draw() {
		Game game = player.game;
		Core c = game.core;
		
		float drawRadius = player.radius * ( player.isDucking ? 0.7f : 1 );
		Vec2 pos = player.myBody.getPosition();
		
		c.fill(player.fill);
		c.noStroke();
		
		c.nGon(pos.x, pos.y, 24, drawRadius-0.5f);
		c.noFill();
		c.stroke(player.fill);
		c.strokeWeight(2);
		c.nGonLines(pos.x, pos.y, 24, drawRadius);
		
		
		
		//draw ship pointer
		c.noStroke();
		c.fill(player.fill);
		c.pushMatrix();
		c.translate(pos.x,pos.y);
		c.rotate( player.isLocalPlayer() ? player.localWalkAngle : player.myBody.getAngle() );
		c.beginShape( c.POLYGON );
		
		c.vertex(0,drawRadius);
		c.vertex(drawRadius,0);
		c.vertex(0,-drawRadius);
		c.vertex(drawRadius*2,0);
		
		c.endShape();
		c.popMatrix();
		
		
		
		//draw armor
		//c.stroke(Colors.armorColor);
		//c.strokeWeight(3);
		//c.nGonLines(pos.x, pos.y, 24, drawRadius + 0.6f);
		
		c.strokeWeight(1);
		
		if( player.weapons.size() > 0 ){
			player.weapons.get(player.currentWeapon).drawModel();
		}
		
//		for( SpiderLeg sl : legs ){
//			sl.step();
//		}
//		
//		//c.pushMatrix();
//		//c.translate( pos.x,pos.y );
//		//c.rotate(player.myBody.getAngle());
//		for( SpiderLeg sl : legs ){
//			sl.draw();
//		}
		//c.popMatrix();
	}
	
//	public class SpiderLeg{
//		public Vec2 localAnchor; // (local) position the leg starts at
//		public Vec2 neutralFoot; // (world) position the foot rests at
//		public Vec2 currentFoot;
//		public Vec2 targetFoot;
//		public SpiderLeg( Vec2 anchor, Vec2 foot ){
//			this.localAnchor = anchor;
//			this.neutralFoot = foot;
//			this.currentFoot = neutralFoot.clone();
//			this.targetFoot = currentFoot.clone();
//		}
//		public void step(){
//			
//		}
//		public void draw(){
//			
//			Vec2 rotatedAnchor = Util.rotateVec( localAnchor, player.myBody.getAngle() );
//			Vec2 worldAnchor = player.myBody.getPosition().add(rotatedAnchor);
//			Vec2 legLength = targetFoot.sub(worldAnchor);
//			
//
//			Vec2 rotatedNeutralFoot = Util.rotateVec( neutralFoot, player.myBody.getAngle() );
//			Vec2 worldNeutralFoot = player.myBody.getPosition().add(rotatedNeutralFoot);
//			
//			double angleOff = Math.atan2(rotatedAnchor.y,rotatedAnchor.x) 
//							- Math.atan2(legLength.y,legLength.x);
//
//			Vec2 rotatedFoot = Util.rotateVec( neutralFoot, player.myBody.getAngle() );
//			Vec2 overStep = worldNeutralFoot.sub(targetFoot);
//			
//			if( overStep.length() > 3 ){
//				
//				overStep.normalize(); overStep = overStep.mul(2);
//				
//				targetFoot = player.myBody.getPosition().add(rotatedFoot).add(overStep.mul(0.5f));
//			}
//			
//			currentFoot = currentFoot.mul(1).add(targetFoot).mul(1/2.0f);
//			
//			Core c = player.game.core;
//			c.strokeWeight(2); //todo: a real shape
//			c.stroke(player.fill);
//			
//			c.line(worldAnchor.x,worldAnchor.y,currentFoot.x,currentFoot.y);
//		}
//	}
	
}
