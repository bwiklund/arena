package massive.core;
import javax.media.opengl.GL;

import massive.shared.TestLauncher;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;
import processing.opengl.PGraphicsOpenGL;


/*
 * This is the Processing applet that has all the 
 * drawing methods, and controls the drawing loop.
 */
public class Core extends PApplet {
	private static final long serialVersionUID = 1L;
	
	public Game game;
	public int w = 900;
	public int h = 700;
	
	public Core(Game game){
		this.game = game;
	}
	
	public void setup(){
		size( w,h,OPENGL );
		
		frameRate(80);
		initFonts();
	}

	public long lastFrameDrawn = 0;
	public long avgDrawTime = 0;
	
	public PGraphicsOpenGL pgl;
	public GL gl;
	
	public boolean showSplashScreen = true;
	
	public void draw(){
		
		if( game.pendingReconnect ){
			//try {
				//Thread.sleep(1000);
			//} catch (InterruptedException e) {
				//e.printStackTrace();
			//}
			//System.out.println("Reconnecting");
			game.pendingReconnect = false;
			try {
				game.dialogmenu.showMenu();
			} catch (Exception e) {
				
			}
		}
		
		game.render.lostFocus = !game.frame.isActive();
		
		if( game.render.takeMouseFocus && !game.render.lostFocus && !game.dialogmenu.isVisible ){
			game.render.captureMouse();
		} else {
			game.render.releaseMouse();
		}
		
		/*if( showSplashScreen ){
			game.render.showMainMenu();
		}*/
		
		if( lastFrameDrawn == 0 ){
			lastFrameDrawn = System.nanoTime();
		}
		
		if( game.gameRunning ){
			doGameLoop();
		}

		game.dialogmenu.draw(this);
	}

	public void doGameLoop() {
		//if we are behind, or have just loaded a game, catch up
		game.render.showLoadingBar = false;
		
		int catchupThreshold = 100; // turns behind before the game skips drawing to catch up
		
		for( int i = 0; i < 1000; i++ ){
			
			game.isCatchingUp = game.gameloop.numberOfTurnsBehindServer() > catchupThreshold;
			/*System.out.println( 
				game.gameloop.numberOfTurnsBehindServer() + 
				" " + game.gameloop.numberOfTurnsBehind()  
				+ " " + game.gameloop.toServer.outq.size() );*/
			
			game.step();
			
			if( game.gameloop.numberOfTurnsBehind() < 1 && game.localPlayer != null ){
				break;
			}
			
			if( i > 5 ){
				game.render.showLoadingBar = true;
			}
			
			//game.isCatchingUp = false;
			//game.isCatchingUp = false;
		}
		
		game.profiler.info("Behind",""+game.gameloop.numberOfTurnsBehindServer() );
		
		long drawStartTime = System.nanoTime();
		game.render.draw();
		long drawStopTime = System.nanoTime();
		avgDrawTime = drawStopTime-drawStartTime;
		
		lastFrameDrawn = System.nanoTime();
	}
	
	public void mousePressed(){
		if( mouseButton == LEFT ){
			game.input.mouse( 0, true );
		}
		if( mouseButton == RIGHT ){
			game.input.mouse( 1, true );
		}
		game.dialogmenu.mouseDown( new PVector(mouseX,mouseY) );
	}
	public void mouseReleased(){
		if( mouseButton == LEFT ){
			game.input.mouse( 0, false );
		}
		if( mouseButton == RIGHT ){
			game.input.mouse( 1, false );
		}
		game.dialogmenu.mouseUp( new PVector(mouseX,mouseY) );
	}
	public void mouseMoved(){
		game.dialogmenu.mouseMove( new PVector(mouseX,mouseY) );
	}
	public void keyPressed(){
		game.input.keyPressed( keyCode, true );
		if (key == ESC) {
			game.input.pressedEsc();
			key = 0;
		}
	}
	public void keyReleased(){
		game.input.keyPressed( keyCode, false );
	}
	
	public PFont mediumFont;
	public PFont smallFont; //this is different
	
	private void initFonts() {
		mediumFont = createFont( TestLauncher.filepath + "media/fonts/verdanab.ttf", 14);
		smallFont = createFont( TestLauncher.filepath + "media/fonts/verdanab.ttf", 10);
		if( mediumFont == null || smallFont == null ){ System.out.println("Could not load fonts."); }
		textFont(mediumFont);
		textMode( MODEL );
	}

	public void nGon(float x, float y, float sides, float r){
		beginShape( TRIANGLE_FAN );
		vertex(x,y);
		for( int i = 0; i <= sides; i++ ){
			float angle = (float)(i / (double)sides * Math.PI * 2);
			vertex(x+(float)Math.cos(angle)*r,y+(float)Math.sin(angle)*r);
		}
		endShape();
	}

	public void nGonLines(float x, float y, float sides, float r){
		noFill();
		beginShape();
		for( int i = 0; i <= sides; i++ ){
			float angle = (float)(i / (double)sides * Math.PI * 2);
			vertex(x+(float)Math.cos(angle)*r,y+(float)Math.sin(angle)*r);
		}
		endShape();
	}
}
