package massive.shared;

import massive.core.Game;

public class ServerMessage extends Action {
	private static final long serialVersionUID = 1L;
	
	public String msg;
	
	public ServerMessage( String msg ){
		this.msg = msg;
	}
	
	public void execute(Game game){
		System.out.println(">> " + msg);
	}
}
