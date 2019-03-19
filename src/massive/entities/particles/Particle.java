package massive.entities.particles;

import massive.core.Game;
import massive.entities.Entity;

import org.jbox2d.common.Vec2;

public class Particle extends Entity {
	public float age = 0;
	public float maxLife;
	public int color;
	public float radius = 5;
	public float drag = 0;
	public float gravity = 0;
	
	public Vec2 vel = new Vec2();
	public Vec2 pos = new Vec2();
	
	public Particle(Game game, Vec2 p){
		super(game);
		pos = p.clone();
	}
	
	public void step(){
		age++;
		if( age > maxLife ){
			removeThis = true;
		}
		vel = vel.mul(1-drag);
		pos = pos.add(vel);
		super.step();
	}
	public void draw(){
		game.core.noStroke();
		game.core.fill(color);
		game.core.ellipse(pos.x,pos.y,radius*2,radius*2);
	}
}
