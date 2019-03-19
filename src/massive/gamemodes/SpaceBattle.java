package massive.gamemodes;

import massive.core.Core;
import massive.core.Game;
import massive.entities.Entity;
import massive.entities.Player;
import massive.entities.Wall;

import org.jbox2d.common.Vec2;

public class SpaceBattle extends BaseGameMode {
	
	public static final int NUM_WALLS = 47;
	public static final int NUM_DITCHES = 0;
	
	public float width = 120;
	public float height = 70;
	
	public SpaceBattle(Game game) {
		super(game);
	}

	public void setupLevel(){
		
		
		
	}
	
	public void drawGround(){
		
	}

	public void drawUI(){
		Core core = game.core;
		core.textFont( core.mediumFont );
		core.textAlign( Core.LEFT, Core.TOP );
		core.text("HI!",20,20);
	}

	public Vec2 getPlayerSpawn(int team) {
		if( team == 0 ){
			return new Vec2(-width+20,0);
		} else {
			return new Vec2(width-20,0);
		}
	}

	public void resetMap() {
		for( Entity e : game.ents ){
			if( e instanceof Player ){
				((Player) e).resetPlayer();
			} else {
				e.die();
			}
		}
		setupLevel();
	}
}
