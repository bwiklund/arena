package massive.dialogs;

import massive.core.Core;
import massive.core.Game;
import massive.entities.Colors;
import massive.shared.TestLauncher;
import massive.ui.UIElement;
import processing.core.PImage;

public class DialogMainMenu extends UIElement {
	
	public String msg = "";
	
	public DialogMainMenu(Game game) {
		super(game,182,200,0,0);
	}
	
	public void hideMenu(){
		game.dialogmenu.setVisible(false);
		if( !game.render.dontInitiallyTakeMouseFocus ){
			game.render.takeMouseFocus = true;
		}
		game.sound.ambient("menuloop.wav",1,false);
	}
	
	public void showMenu(){
		game.dialogmenu.setVisible(true);
		game.render.takeMouseFocus = false;
	}

	public void init(){
		
		elements.add( new UIElement(game, 10,30,100,100 ){
			
			public void init(){
				
				elements.add( new UIButtonMainMenu( game, 0,40,game.core.w,30,"Join a game",null){
					public void doMouseDown(){
						if( !game.isInGameOrConnectingToGame() ){ 
							try {
								game.connectToAGame();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							hideMenu();
						}
					}
					public void draw(Core c){
						if( game.isInGameOrConnectingToGame() ){
							if( game.gameloop.currentTurn == 0 ){
								text = "Connected, waiting for players...";
							} else {
								text = "Return to game";
							}
						} else {
							text = "Join a game";
						}
						super.draw(c);
					}
				});
				
				
				
				elements.add( new UIButtonMainMenu( game, 26,120,game.core.w,30,"Toggle Sound (ENABLED)",null){
					 public void doMouseDown(){
						 if( game.sound.ac.out.getGain() == 1f ){
							 game.sound.ac.out.setGain(0f);
							 msg = "Sound disabled";
							 this.text = "Toggle Sound (DISABLED)";
						 } else {
							 game.sound.ac.out.setGain(1f);
							 msg = "Sound enabled";
							 this.text = "Toggle Sound (ENABLED)";
						 }
					 }
				});
				
				elements.add( new UIButtonMainMenu( game, 39,160,game.core.w,30,"Quit",null){
					 public void doMouseDown(){
						 //TODO: this should probably do something more
						 System.exit(0);
					 }
				});
				
			}
		});
	}


	PImage img_splash;
	int loadingBlinker = 0;
	
	public void draw(Core core){
		
		if( !isVisible ){
			return;
		}
		
		if( img_splash == null ){
			img_splash = core.loadImage( TestLauncher.filepath + "media/image/splash.png" );
			if( img_splash == null ){
				System.out.println("Couldn't load splash.png");
				System.exit(-1);
			}
		}
		
		//if( game.gameRunning ){
		//	core.fill( Colors.menubgtransparent );
		//} else {
			core.fill( Colors.menubgsolid );
		//}
		core.rect(0,0,core.width,core.height);
		
		core.imageMode( Core.CORNER );
		core.image(img_splash,50,50);
		
		if( loadingBlinker++ % 100 < 50 ){
			core.fill(0xffffffff); 
		} else {
			core.fill(0xffaaaaaa);
		}
		core.textFont( core.mediumFont );
		core.textAlign( Core.CENTER, Core.TOP );
		core.text(msg,core.w/2,core.h-100);
		
		core.fill(0x06ffffff);
		core.rect(0,178,core.w,core.h-178);
		
		super.draw(core);
	}

	public void gameStarted() {
		hideMenu();
		msg = "Connected, game in progress";
	}
}
