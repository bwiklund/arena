package massive.core;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import massive.shared.PlayerManualReload;


public class Input implements MouseWheelListener {
	
	public boolean leftMouse = false;
	public boolean rightMouse = false;
	
	public boolean pendingShot = false;
	
	public int pendingWeaponSwap = 0;
	
	public Game game;
	public boolean strafeLeft = false;
	public boolean strafeRight = false;
	public boolean goForward = false;
	public boolean goBackward = false;
	public boolean duck = false;
	public boolean run = false;
	public boolean mute = false;

	public int KEY_CONSOLE = 'T';
	
	public int KEY_RELOAD = 'R';

	public int KEY_DUCK = 17; //ctrl
	public int KEY_RUN = 16; //shift

	public int KEY_DROP = 'Q';
	public boolean pendingDrop = false;
	
	public int KEY_TOGGLEGUNS = '\t'; //ctrl
	public int lastWeaponNumPressed = -1;
	
	public int KEY_SHOWSCORE = '\t';
	public boolean showScore = false;
	
	public int KEY_SHOWPROFILER = 'P';
	public boolean showProfiler = false;
	public boolean draw3d = false;

	public int KEY_DRAW3D = 'O';

	public static final int 	KEY_BACKSPACE = 8;
	public static final int 	KEY_DELETE = 127;
	private static final int 	KEY_ENTER = 10;
	
	public boolean keys[] = new boolean[4096];
	
	public void pressedEsc(){
		if( game.gameRunning ){
			if( !game.dialogmenu.isVisible ){
				game.dialogmenu.setVisible( true );
			} else {
				game.dialogmenu.setVisible( false );
			}
		}
			
	}
	
	public Input(Game game){
		this.game = game;
		game.core.addMouseWheelListener(this);
	}
	
	public void mouse( int button, boolean state ){
		if( button == 0 ){
			leftMouse = state;
			if( game.render.takeMouseFocus ){
				pendingShot = state || pendingShot;
			}
		}
		if( button == 1 ){
			rightMouse = state;
			if( game.render.takeMouseFocus ){
				pendingShot = state || pendingShot;
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//System.out.println(e);
		//TODO: make this work again for the FPS game mode
		if( e.getWheelRotation() > 0 ){
			//game.render.zoom *= 0.9f;
			pendingWeaponSwap = 1;
		}

		if( e.getWheelRotation() < 0 ){
			//game.render.zoom *= 1.1f;
			pendingWeaponSwap = -1;
		}
	}
	
	public void keyPressed( int keyCode, boolean isDown ){
		
		
		if( keyCode >= keys.length ){
			System.out.println("Key out of range: " + keyCode );
			return;
		}

		//save the state
		keys[keyCode] = isDown;
		
		//let the console take keys if it's open
		
		if( isDown ){

			if( keyCode == KEY_ENTER ){
				if( !game.console.hasFocus ){
					game.console.hasFocus = true;
					return;
				}
			}
			
			if( game.console.hasFocus ){
				if( keyCode == KEY_ENTER ){
					game.console.sendLine();
				} else if ( keyCode >= 32 || keyCode == KEY_BACKSPACE ){ //ignore nonprintable keys
					game.console.keyPressed(game.core.key);
				}
				return;
			}
			
		}
		
		// and then do other stuff if the console isn't up
		
		if( keyCode == 65 ){ // a
			strafeLeft = isDown;
		}
		if( keyCode == 68 ){ // d
			strafeRight = isDown;
		}
		if( keyCode == 87 ){ // w
			goForward = isDown;
		}
		if( keyCode == 83 ){ // s
			goBackward = isDown;
		}
		if( keyCode == KEY_SHOWPROFILER ){ // s
			if( isDown ){
				showProfiler = !showProfiler;
			}
		}
		
		if( keyCode == 77 ){ // m
			if( isDown ){
				mute = !mute;
			}
		}
		
		if( keyCode == KEY_SHOWSCORE ){
			showScore = isDown;
		}
		
		if( keyCode == KEY_DRAW3D ){
			if( isDown ){
				draw3d = !draw3d;
			}
		}
		
		if( keyCode == KEY_DUCK ){
			duck = isDown;
		}
		
		if( keyCode == KEY_RUN ){
			run = isDown;
		}
		
		if( keyCode == KEY_TOGGLEGUNS ){
			if( isDown ){
				pendingWeaponSwap = 1;
			}
		}
		
		if( keyCode == KEY_DROP ){
			pendingDrop = pendingDrop | isDown;
		}
		
		if( keyCode == KEY_RELOAD ){
			if(isDown){
				game.gameloop.toServer.sendAction( new PlayerManualReload( game.currentPlayer ) );
			}
		}
		if( keyCode == '1' || 
			keyCode == '2' ||
			keyCode == '3' ||
			keyCode == '4' ||
			keyCode == '5' ||
			keyCode == '6' ||
			keyCode == '7' ||
			keyCode == '8' ||
			keyCode == '9' ||
			keyCode == '0' ){
			if( isDown ){
				lastWeaponNumPressed = keyCode-'1'; 
			}
		}
		
	}
}
