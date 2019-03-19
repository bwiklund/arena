package massive.shared;

import massive.core.Game;
import massive.entities.Player;

public strictfp class SetPlayerMotion extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playernumber = 0;
	public float speed = 0;
	public float strafe = 0;
	public float dAngle = 0;
	public float dCursor = 0;
	public boolean ducking = false;
	public boolean running = false;
	
	public SetPlayerMotion(int playernumber, float dAngle, float dCursor, float isWalking, float strafe, boolean ducking, boolean running ){
		this.running = running;
		this.dAngle = dAngle;
		this.dCursor = dCursor;
		this.strafe = strafe;
		this.speed = isWalking;
		this.ducking = ducking;
		this.playernumber = playernumber;
	}
	
	public void execute(Game game){
		Player player = game.ents.players.get( playernumber );
		player.walkAngle += dAngle;
		player.strafe = strafe;
		player.walkingSpeed = speed;
		player.isDucking = ducking;
		player.isRunning = running;
		player.cursorDistance += dCursor;
	}
}
