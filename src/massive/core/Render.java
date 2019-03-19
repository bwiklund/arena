package massive.core;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;

import massive.entities.Colors;

import org.jbox2d.common.Vec2;

public class Render {
	
	Game game;
	Core core;
	
	public Vec2 camera = new Vec2();
	public Vec2 parallaxCenter = new Vec2();
	public boolean draw3d = false;
	public boolean redraw = false;
	public float angleDamper;

	public float zoom = 7;
	public boolean showLoadingBar = false;

	//mouse focus state stuff
	public boolean takeMouseFocus = false; // we SHOULD take the mouse focus
	boolean hasMouseFocus = false; // we HAVE the mouse focus
	public boolean lostFocus = false; // the player is alt tabbed out, but we'll take focus when we return
	
	public Render(Game game) {
		this.game = game;
		this.core = game.core;
	}

	public void draw() {
		
		draw3d = game.input.draw3d;
		
		game.profiler.start("Rendering");
		
		if( redraw ){
			
			redraw = false;
			
			float cameraAngle = getScreenRotation();
			
			core.background( Colors.bg );
			
			if( game.gameRunning ){
				
				float zoomCoef = 1;
				float offsetCoef = 1;

				if( game.localPlayer != null ){
					zoomCoef = 1;//game.localPlayer.zoom;
					offsetCoef = game.localPlayer.cameraoffset;
					
					camera = game.ents.players.get(game.currentPlayer).myBody.getPosition().clone();
					parallaxCenter = camera.clone();
					Vec2 offset = Util.angleV(cameraAngle, 200 * offsetCoef / zoom);
					camera = camera.add( offset );
				}
				
				core.pushMatrix();
				
				if( draw3d ){
					//core.hint(core.DISABLE_DEPTH_TEST);
					//core.camera(20, 20, 20, 0, 0, 0, 0, 1, 0);
					core.hint(Core.ENABLE_DEPTH_TEST);
					core.rotateX((float) (Math.PI/4) );
					//core.lights();
					core.ambientLight(220, 220, 220);
					core.directionalLight(30, 30, 30, 0, 0, -1);
					core.lightFalloff(1, 0, 0);
				}
				
				core.translate(core.w/2,core.h/2);
				

				core.scale(zoom*zoomCoef);

				
				//if( takeMouseFocus ){
					core.rotate( 0-cameraAngle-(float)(Math.PI/2) );
				//}
				
				core.translate(-camera.x,-camera.y);
				
				game.gameMode.drawGround();
				
				game.ents.drawAll();
				
				core.popMatrix();

				if( draw3d ){
					core.hint(Core.DISABLE_DEPTH_TEST);
					core.hint(Core.DISABLE_DEPTH_SORT);
					core.hint(Core.DISABLE_DEPTH_MASK);
					core.camera();
					core.noLights();
					//core.camera(20, 20, 20, 0, 0, 0, 0, 1, 0);
					//core.rotateX((float) (Math.PI/4));
				}
				

				
				if( game.currentPlayer > -1 ){
					game.ents.players.get(game.currentPlayer).drawUI();
				}

				game.gameMode.drawUI();
				game.console.drawConsole(core);
				//game.dialogmenu.draw(core);
				
				if( game.input.showScore ){
					//showScorePanel();
				}
				
				//game.m_world.drawDebugData();
				
			}
			
			if( core.mouseY > core.h - 10 ){
				//System.out.println(core.frameRate);
			}
		}
		
		if( showLoadingBar ){
			showServerStats();
		}
		

		game.profiler.stop("Rendering");
		if( game.input.showProfiler ){
			game.profiler.printprofile(core);
		}
	}
	
	void captureMouse() {
		if( !hasMouseFocus ){
			game.centerMouse();
		}
		hasMouseFocus  = true;
		byte[]imageByte=new byte[0];  
		Cursor myCursor;  
		Point myPoint=new Point(0,0);  
		Image cursorImage=Toolkit.getDefaultToolkit().createImage(imageByte);  
		myCursor=Toolkit.getDefaultToolkit().createCustomCursor(cursorImage,myPoint,"cursor");  
		game.frame.setCursor(myCursor); 
	}
	
	void releaseMouse(){
		//if( takeMouseFocus ){
			hasMouseFocus = false;
			game.frame.setCursor( new Cursor(0) );
		//}
	}


	private void showServerStats() {
		if( !game.gameloop.toServer.running ){
			return;
		}
		core.textFont( core.smallFont );
		core.fill(~0);
		core.textAlign( Core.LEFT, Core.TOP );
		core.text("Turn " + game.gameloop.currentTurn + " of " + game.serverTurn + " = "
				+ (int)(100*game.gameloop.currentTurn/game.serverTurn) + "%"
				+ " (" +game.gameloop.toServer.getBytesRead() + " bytes)",10,10);
	
	}

	public float getScreenRotation() {
		if( game.currentPlayer > -1 ){

			float angle = 0;
			
			angle = game.ents.players.get(game.currentPlayer).localWalkAngle;//(float) Math.atan2( v.y,v.x );
			angleDamper = angle;
			
			return (float) angle;
		} else {
			return 0;
		}
		
	}

	public Vec2 screen2World( Vec2 screen ){
		Vec2 vWorld = screen.sub( new Vec2(core.w/2f,core.h/2f) );
		vWorld = vWorld.mul( 1/zoom );
		vWorld = vWorld.add( camera );
		return vWorld;
	}
	
	public boolean dontInitiallyTakeMouseFocus = false; //for debugging
	
	public void showMainMenu() {
		game.dialogmenu.draw(core);
		
	}
}
