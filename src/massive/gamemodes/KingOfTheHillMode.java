package massive.gamemodes;

import java.util.Map;

import massive.core.Core;
import massive.core.Game;
import massive.entities.Colors;
import massive.entities.PhysicsEntity;
import massive.entities.Player;
import massive.entities.triggers.Trigger;
import massive.entities.triggers.TriggerAmmoStation;

import org.jbox2d.common.Vec2;

public class KingOfTheHillMode extends BasicArenaMap {
	
	//todo: manage teams better
	int teamScores[] = new int[2];
	
	int lastTeamOnHill = -1;
	int teamOnHill = -1;
	
	public KingOfTheHillMode(Game game) {
		super(game);
	}
	
	Trigger hill;
	
	public int time = 0;
	public int timeLimit = 100;
	
	public int scoreLimit = 100;
	public int scoreDiv = 50;
	
	public void setupLevel(){

		hill = new Trigger( game, new Vec2() ){
			/*public void draw(){
				//super.draw();
				
				Vec2 pos = myBody.getPosition();
				game.core.fill(fill);
				game.core.noStroke();
				
				int sides = 10;
				for( int i = 0; i <= sides; i++ ){
					float angle = (float)(i / (double)sides * Math.PI * 2);
					Vec2 c = new Vec2(pos.x+(float)Math.cos(angle)*radius,pos.y+(float)Math.sin(angle)*radius);
					game.core.nGon(c.x, c.y, 24, radius / 10);
				}
			}*/
		};
		game.ents.add( hill );
		
		TriggerAmmoStation ammo1 = new TriggerAmmoStation( game, new Vec2(-width+20,0) );
		ammo1.team = 0;
		game.ents.add( ammo1 );
		TriggerAmmoStation ammo2 = new TriggerAmmoStation( game, new Vec2(width-20,0) );
		ammo2.team = 1;
		game.ents.add( ammo2 );
		
		
		super.setupLevel();
		
		
		/*for( int i = 0; i < 2; i++ ){
			float x = 20+game.random.nextFloat()*30;
			float y = -50+game.random.nextFloat()*100;
			game.ents.add( new Turret( game, new Vec2(x,y),1 ) );
			game.ents.add( new Turret( game, new Vec2(-x,-y),0 ) );
		}*/
		
	}
	
	public void step(){
		if( time++ > timeLimit ){
			//resetMap();
			//time = 0;
		}
		
		for( int team = 0; team < 2; team++ ){
			if( teamScores[team] >= scoreLimit*scoreDiv ){
				teamWonRound(team);
				break;
			}
		}
		
		//int[] teamsOnTheHill = new int[256]; //sloppy
		teamOnHill = -1;
		
		for( PhysicsEntity ep : hill.entitiesInside ){
			if( ep instanceof Player ){
				Player p = (Player)ep;
				int team = p.team;
				if( teamOnHill == -1 ){ //no one on hill yet
					teamOnHill = team;
				} else if( teamOnHill == team ) { //teammates
					//nothing to do. maybe add a total to get points faster?
				} else {
					teamOnHill = -1;
					break; //there are multiple teams on the hill
				}
			}
		}
		
		if( teamOnHill != -1 ){
			teamScores[teamOnHill] += 1;
			hill.fill = game.core.lerpColor( Colors.Team[teamOnHill], 0xff909090, 0.6f );
		} else {
			hill.fill = 0xff909090;
		}
		

		if( game.currentPlayer != -1 ){
			Player p = game.ents.players.get(game.currentPlayer);
			if( teamOnHill != lastTeamOnHill ){
				if( teamOnHill != -1 ){
					if( p.team == teamOnHill ){
						game.sound.play("teamon.wav",2f,hill.myBody.getPosition());
					} else {
						game.sound.play("enemyon.wav",2f,hill.myBody.getPosition());
					}
				} else {
					if( p.team == lastTeamOnHill ){
						game.sound.play("teamoff.wav",2f,hill.myBody.getPosition());
					} else {
						game.sound.play("enemyoff.wav",2f,hill.myBody.getPosition());
					}
				}
			}
		}
		
		lastTeamOnHill = teamOnHill;
	}

	public void teamWonRound(int team) {
		
		if( game.currentPlayer != -1 ){
			Player p = game.ents.players.get(game.currentPlayer);
			if( team == p.team ){
				game.sound.play("victory.wav",0.4f,p.myBody.getPosition());
			} else {
				game.sound.play("defeat.wav",0.4f,p.myBody.getPosition());
			}
		}
		
		teamScores[0] = 0;
		teamScores[1] = 0;
		String playernames = "";
		for( Map.Entry<Integer,Player> e : game.ents.players.entrySet() ){
			if( e.getValue().team == team ){
				e.getValue().killMyTurretsAndStuff();
				playernames += e.getValue().name + ", ";
			}
		}
		game.console.addLine("Team " + (team+1) + " ("+playernames.substring(0,playernames.length()-2) + ") won the round!");
		resetMap();
		teamOnHill = lastTeamOnHill = -1;
	}
	
	

	public void drawUI(){
		Core core = game.core;
		core.textFont( game.core.mediumFont );
		
		core.noStroke();
		
		core.fill( Colors.uibg );
		core.rect( 0,core.h-27,core.w,27);
		core.fill( ~0 );
		
		core.textAlign( Core.LEFT, Core.TOP );
		core.text("Scores: ",10,core.h-20);
		
		core.fill( Colors.Team[0]);
		core.textAlign( Core.RIGHT, Core.TOP );
		core.text(""+teamScores[0]/scoreDiv,100,core.h-20);
		
		core.fill( Colors.Team[1]);
		core.textAlign( Core.LEFT, Core.TOP );
		core.text(""+teamScores[1]/scoreDiv,110,core.h-20);
		
	}
	
	public void playerKilled(PhysicsEntity murderer, PhysicsEntity victim) {
		game.console.addLine( murderer.name + " killed " + victim.name );
		//if( victim.team != murderer.team ){
			//teamScores[murderer.team] += 250;
			//////teamScores[victim.team] -= 500;
		//}
		if( teamScores[victim.team] < 0 ){ teamScores[victim.team] = 0; }
	}
}
