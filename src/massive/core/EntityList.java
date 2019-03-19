package massive.core;

import java.util.LinkedHashMap;
import java.util.Vector;

import massive.entities.Entity;
import massive.entities.PhysicsEntity;
import massive.entities.Player;

import org.jbox2d.common.Vec2;

public strictfp class EntityList extends Vector<Entity> {
	private static final long serialVersionUID = 1L;
	
	public Game game;
	
	public LinkedHashMap<Integer,Player> players = new LinkedHashMap<Integer,Player>();
	
	public EntityList(Game game){
		this.game = game;
	}
	public boolean add(Entity e){
		super.add(e);
		if( e instanceof Player ){
			Player p = (Player) e;
			players.put(p.playerNumber,p);
		}
		return true;
	}
	public void drawAll(){
		int layers = 2;
		for( int layer = 0; layer < layers; layer++ ){
			for( int i = 0; i < this.size(); i++ ){
				Entity e = this.get(i);
				if( e.drawingLayer == layer ){
					e.draw();
				}
			}
		}
	}
	public void stepAll(){
		for( int i = 0; i < this.size(); i++ ){
			this.get(i).step();
		}
	}
	public void clearDeadEntities(){
		for( int i = 0; i < this.size(); i++ ){
			if( this.get(i).removeThis ){
				this.get(i).die();
				this.remove( i-- );
			}
		}
	}
	public float checksum(){
		float sum = 0;
		for( int i = 0; i < this.size(); i++ ){
			Entity e = this.get(i);
			if( e instanceof PhysicsEntity ){
				Vec2 p = ((PhysicsEntity)e).myBody.getPosition();
				sum += p.x + p.y;
			}
		}
		return sum;
	}
}