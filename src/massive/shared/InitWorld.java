package massive.shared;

import massive.core.Game;


public class InitWorld extends Action {
	private static final long serialVersionUID = 1L;
	
	public long seed;
	
	public InitWorld( long seed ){
		this.seed = seed;
	}
	public void execute(Game game){
		game.initWorld(seed);
	}
}
