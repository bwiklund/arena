package massive.weapons;

import massive.core.Core;
import massive.core.Game;
import massive.core.Util;
import massive.entities.Colors;
import massive.entities.PhysicsEntity;
import massive.entities.Wall;

import org.jbox2d.common.Vec2;

public class WallBuilder extends ProjectileRifle {

	public WallBuilder(PhysicsEntity owner) {
		super(owner);
		fireRate.set(20);
		magazineSize = 3;
		maxMagazines = 0;
		ui_ammoheight = ui_ammowidth;
		name = "Wall";
		imgPath = "media/image/guns/wall.png";
	}
	
	public void shootBullet(Game game) {
		float angle = owner.myBody.getAngle();
		Vec2 v = owner.myBody.getPosition().add( Util.angleV( angle, 2+game.random.nextFloat()*0.01f ) );
		
		Wall wall = new BuiltWall(game, v.x, v.y, 1,3, angle  );
		wall.height = 1.3f + game.random.nextFloat()*0.1f;
		game.ents.add( wall );
		game.sound.play("empty.wav",game.random.nextFloat()*0.03f + 0.1f,owner.myBody.getPosition());
	}
	
	public void drawUIammo(Game game, int x, int y, int i) {
		game.core.ellipse(x+ui_ammoheight/2,y+ui_ammoheight/2,ui_ammowidth,ui_ammoheight);
	}
	
	private class BuiltWall extends Wall {
		
		public static final int TOTAL_HP = 800;
		private int startcolor;

		public BuiltWall(Game game, float x, float y, float dwidth, float dheight, float angle) {
			super(game, x, y, dwidth, dheight, angle);
			startcolor = game.core.lerpColor( 0xff888888, Colors.Team[owner.team], 0.6f );
			fill = startcolor;
			hp = TOTAL_HP;
		}

		@Override
		public strictfp void draw() {
			float scaler = ((float)hp / (float)TOTAL_HP);
			fill = game.core.lerpColor(startcolor, 0x88FF6666, 1-scaler);
			super.draw();
		}
		
	}

	
	public void drawModel(){
		Core c = owner.game.core;

		c.pushMatrix();
		c.pushStyle();
			
			float angle = owner.getDisplayAngle();
			Vec2 v = owner.myBody.getPosition().add( Util.angleV( angle, 2 ) );
			
			c.translate( v.x, v.y, 0 );
			c.rotate(angle);
			c.noFill();
			c.stroke(0xffffffff);
			
			c.beginShape();
			c.vertex(0.5f,1.5f);
			c.vertex(0.5f,-1.5f);
			c.vertex(-0.5f,-1.5f);
			c.vertex(-0.5f,1.5f);
			c.vertex(0.5f,1.5f);
			c.endShape();
		
		c.popStyle();
		c.popMatrix();
	}
}
