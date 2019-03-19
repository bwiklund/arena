package massive.entities;

import java.util.Vector;

import massive.core.Game;

public class Entity {

	public int id;
	public int team = -1;
	
	public Game game;
	
	//TODO: make this private with a getter
	public boolean removeThis = false;
	public boolean removed = false;
	
	public int thinkInterval = 1;
	public int thinkCountdown = 0;
	
	public int drawingLayer = 0;

	
	public Entity( Game game ){
		this.game = game;
		this.id = game.nextId++;
	}
	
	public void step() {
		thinkCountdown = ( thinkCountdown - 1 ) % thinkInterval;
		if( thinkCountdown == 0 ){
			think();
		}
	}
	
	public void think(){
		
	}
	
	public void draw() {
		
	}
	
	public void drawUI() {
		
	}

	public Vector<Entity> killOnDeath = new Vector<Entity>();
	public void die() {
		if( removed == true ){
			return; //don't get into loops
		}
		removed = true;
		for( Entity e : killOnDeath ){
			e.die();
		}
	}
	
	
}
