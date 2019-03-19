package massive.entities;

import massive.core.Core;
import massive.core.Game;

import org.jbox2d.common.Vec2;

import processing.core.PVector;

public class PlayerModel3d {
	public int footstep = 0;
	float walkingDamper = 0;
	float restAngle = 0;//angle;

	public void draw( Player player ){
		Vec2 p = player.myBody.getPosition();
		Core core = player.game.core;
		Game game = player.game;
		
		int clothColor = Colors.Team[player.team];
		float height3d = 0.2f;

		core.pushStyle();
		
		footstep++;
		float stridesteps = 26;
		float phase = footstep / stridesteps;
		// PVector hips = new PVector(0,0,0);

		float hipwidth = 1f * height3d;
		float bodywidth = 4f * height3d;
		float torsoheight = 7f * height3d;
		float headsize = (0.75f * height3d + 0.75f)*0.5f;

		float leglength = 7f * height3d;

		float jumpyness = 0.8f;

		float iswalking = Math.min(player.walkingSpeed+Math.abs(player.strafe),1) / player.upgrades.getPlayerSpeed();
		walkingDamper = (walkingDamper * 3 + iswalking) / 4;
		
		float strideEntusiasm = 0.7f * walkingDamper;

		float jumpy = walkingDamper * jumpyness
				* (float) Math.sin(phase * 2 * Math.PI * 2);
		// if( jumpy < 0 ){ jumpy = 0; }
		PVector hips = new PVector(0, -jumpy - leglength, 0);
		// hips.add(p);

		PVector rightleg = new PVector(-hipwidth, 0, 0);
		rightleg.add(hips);
		float rightlegangle = strideEntusiasm
				* (float) Math.sin(phase * Math.PI * 2);
		PVector rightfootoffset = new PVector(0, leglength
				* (float) Math.cos(rightlegangle), leglength
				* (float) Math.sin(rightlegangle));

		PVector leftleg = new PVector(hipwidth, 0, 0);
		leftleg.add(hips);
		float leftlegangle = 0 - strideEntusiasm
				* (float) Math.sin(phase * Math.PI * 2);
		PVector leftfootoffset = new PVector(0, leglength
				* (float) Math.cos(leftlegangle), leglength
				* (float) Math.sin(leftlegangle));

		// camera(20,20,20, 0,0,0, 0,1,0);
		// translate(width/2,height/2);

		game.core.stroke(clothColor);
		game.core.strokeWeight(1.5f * game.render.zoom * height3d * 3);

		game.core.fill(clothColor);
		// rotateY(frameCount*0.01f);
		// proc.ortho(-100,+100, -100,+100, 100,-100);

		game.core.pushMatrix();

		// proc.scale(10);

		game.core.noLights();
		//proc.hint(proc.DISABLE_DEPTH_TEST);

		game.core.translate(p.x, p.y, 0);

		//game.core.rotateX((float) (Math.PI / 4) );
		game.core.rotateX((float) (-Math.PI / 2) );
		
		//if( v.mag() > 0 ){ //don't lock sideways when you stop
			restAngle =(float)( Math.PI/2 ) - player.myBody.getAngle();//(float)(Math.atan2(v.y, v.x) + Math.PI / 2);
		//}
		
		game.core.rotateY( restAngle );
		// proc.ellipse(p.x,p.y,10,10);
		game.core.beginShape(Core.LINE);
		game.core.vertex(rightleg.x, rightleg.y, rightleg.z);
		rightleg.add(rightfootoffset);
		game.core.vertex(rightleg.x, rightleg.y, rightleg.z);
		game.core.endShape();

		game.core.beginShape(Core.LINE);
		game.core.vertex(leftleg.x, leftleg.y, leftleg.z);
		leftleg.add(leftfootoffset);
		game.core.vertex(leftleg.x, leftleg.y, leftleg.z);
		game.core.endShape();

		PVector headCenter = new PVector(hips.x, hips.y - torsoheight - headsize/2, hips.z);
		
		game.core.noStroke();
		game.core.pushMatrix();
		game.core.translate(hips.x, hips.y - torsoheight / 2 + 0.2f, hips.z);
		game.core.box(bodywidth, torsoheight, bodywidth / 2);
		game.core.popMatrix();

		
		game.core.pushMatrix();
		game.core.translate(headCenter.x,headCenter.y,headCenter.z);
		game.core.box(headsize, headsize * 1.6f, headsize);
		game.core.popMatrix();
		

		//proc.hint(proc.ENABLE_DEPTH_TEST);

		game.core.popMatrix();
		core.popStyle();
	}
}
