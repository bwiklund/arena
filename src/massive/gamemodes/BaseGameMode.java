package massive.gamemodes;

import java.util.Map.Entry;

import massive.core.Game;
import massive.entities.PhysicsEntity;
import massive.entities.Player;

import org.jbox2d.common.Vec2;

public class BaseGameMode {
	Game game;

	public BaseGameMode( Game game ){
		this.game = game;
	}
	
	/*
	 * This is where you set the level up, and set up custom triggers to make things happen
	 * in the game state. For example, a king of the hill game would use a trigger to call
	 * custom methods in this class when players stand on it.
	 */
	public void setupLevel(){
		
	}
	
	/*
	 * This is where you draw text, scores, relevant stuff to the game mode
	 */
	public void drawUI(){
		
	}

	public void drawGround(){
		
	}

	
	public void step(){
		
	}

	public void playerKilled(PhysicsEntity murderer, PhysicsEntity victim) {
		
	}

	public void spawnPlayer(Player p) {
		
	}

	public int getStartingTeam() {
		int[] teams = new int[2];
		for( Entry<Integer, Player> e  : game.ents.players.entrySet() ){
			teams[e.getValue().team]++;
		}
		if( teams[0] >= teams[1] ){
			return 1;
		} else {
			return 0;
		}
	}
}
