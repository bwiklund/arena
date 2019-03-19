package massive.shared;

import massive.core.Game;
import massive.entities.Player;

public class TxtMessage extends Action {
	private static final long serialVersionUID = 1L;
	
	public int playerFrom = -1;
	private String str;

	public TxtMessage(String inputString) {
		this.str = inputString;
	}
	
	public void execute( Game game ){

		Command[] commands = new Command[]{
				
			new Command(){
				@Override
				public boolean attempt(String str, Game game) {
					String[] tokens = str.split("\\s");
					String oldname = game.ents.players.get( playerFrom ).name;
					if( tokens[0].equals("help") ){
						game.console.addLine( "COMMANDS: /help, /name [newname], /switch");
						return true;
					} else {
						return false;
					}
				}
			},
				
			// setting convars
			new Command(){
				@Override
				public boolean attempt(String str, Game game) {
					String[] tokens = str.split("\\s");
					if( tokens[0].equals("set") ){
						if( tokens.length == 3 ){
							game.convars.put(tokens[1],tokens[2]);
						} else {
							game.console.addLine( "Error: wrong # of arguments" );
						}
					} else {
						return false;
					}
					game.console.addLine( tokens[1] + "=" + tokens[2] );
					return true;
				}
			},
			
			// setting player name
			new Command(){
				@Override
				public boolean attempt(String str, Game game) {
					String[] tokens = str.split("\\s");
					String oldname = game.ents.players.get( playerFrom ).name;
					if( tokens[0].equals("name") ){
						if( tokens.length == 2 ){
							if( tokens[1].equals("fermun") ){
								tokens[1] = new String("fermun sucks");
							}
							game.ents.players.get( playerFrom ).name = tokens[1].toString();
						} else {
							game.console.addLine( "Error: wrong # of arguments" );
							return true;
						}
					} else {
						return false;
					}
					game.console.addLine( oldname + " changed name to " + tokens[1] );
					return true;
				}
			},
			
			// switch team
			new Command(){
				@Override
				public boolean attempt(String str, Game game) {
					String[] tokens = str.split("\\s");
					Player player = game.ents.players.get( playerFrom );
					if( tokens[0].equals("switch") ){
						player.team = (player.team+1)%2;
						player.hp = 0;
					} else {
						game.console.addLine( "Error: wrong # of arguments" );
						return false;
					}
					game.console.addLine( player.name + " switched teams" );
					return true;
				}
			}
		};
		
		if( str.length() > 0 ){
			if( str.charAt(0) == '/' ){
				String str2 = str.substring(1);
				for( Command c : commands ){
					if( c.attempt(str2, game) ){
						return;
					}
				}
				// (else)
				game.console.addLine( "Error: " + str );
			} else {
				game.console.addLine( game.ents.players.get( playerFrom ).name, str );
			}
		}
	}
	
	interface Command {
		boolean attempt(String str, Game game);
	}
}
