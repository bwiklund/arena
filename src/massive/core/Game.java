package massive.core;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.JFrame;

import massive.dialogs.DialogMainMenu;
import massive.entities.Colors;
import massive.entities.MassiveContactListener;
import massive.entities.Player;
import massive.gamemodes.BaseGameMode;
import massive.gamemodes.BasicSpaceMap;
import massive.gamemodes.KingOfTheHillMode;
import massive.gamemodes.SpaceBattle;
import massive.shared.Action;
import massive.shared.DropItem;
import massive.shared.PlayerChangeWeapon;
import massive.shared.SetPlayerMotion;
import massive.shared.SetPlayerShooting;
import massive.shared.TestLauncher;
import massive.ui.Console;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

 



public strictfp class Game {
	
	
	//game state
	public long seed;
	public int nextId;
	public boolean isConnecting;
	public boolean gameRunning;
	public int currentPlayer;
	public int serverTurn;
	public boolean isCatchingUp;
	double checkSum;

	public Player localPlayer;
	public BaseGameMode gameMode;
	public GameFlowManager gameloop;
	public EntityList ents;
	public Random random;
	
	public Hashtable<String,String> convars;
	
	//drawing
	public Core core;
	public JFrame frame;
	public Render render;
	public Console console = new Console(this);
	public Profiler profiler = new Profiler();
	
	//physics
	public World m_world;
	
	//sound
	public Sound sound;
	
	//input
	public Input input;
	
	//server
	public ToServer fs;
	
	//ui
	public DialogMainMenu dialogmenu;
	boolean pendingReconnect = false;
	
	public Game(boolean live) {
		
		resetEverything();
		
		render = new Render(this);
		startCore();
		render.core = this.core; //ugly
		
		dialogmenu = new DialogMainMenu(this);
		
		dialogmenu.msg = "Loading...";  
		
		input = new Input(this);
		
		sound = new Sound(core);
		
		if( live ){
			//sound.ambient("menuloop.wav",1,true);
		} else {
			input.mute = true;
			render.dontInitiallyTakeMouseFocus = true;
		}
		
		new Colors();
	}


	public void connectToAGame() throws Exception {
		if( gameRunning ){
			throw new Exception("Illegal game state change");
		}
		dialogmenu.msg = "Connecting to " + TestLauncher.serverAddr + "...";
		isConnecting = true;
		gameloop = new GameFlowManager(this);
	}

	
	private void createPhysicsWorld() {
		Vec2 gravity = new Vec2(0.0f, 0.0f);
		boolean doSleep = true;
		m_world = new World(gravity, doSleep);
		m_world.setContactListener( new MassiveContactListener(this) );
	}
	
	public void step() {
		if( gameloop != null ){
			gameloop.readAndExecuteAvailableActions();
		}
		
		if( m_world != null ){
			profiler.info( "Entities",""+ents.size() );
			profiler.info( "PhysBodies",""+m_world.getBodyCount() );
		}
	}
	
	public void initWorld(long seed) {
		this.seed = seed;
		
		resetEverything();
		
		random = new Random(seed);
		createPhysicsWorld();
		startGameMode();
		core.showSplashScreen = false;
		dialogmenu.setVisible(false);
	}


	public void resetEverything() {

		ents = new EntityList(this);
		
		m_world = null;
		currentPlayer = -1;
		seed = -1;
		nextId = 0;
		currentPlayer = -1;
		serverTurn = -1;
		isCatchingUp = false;
		checkSum = 0;

		localPlayer = null;
		gameMode = null;
		
		ents = new EntityList(this);
		
		convars = new Hashtable<String,String>();
	}
	
	public void startGameMode() {
		gameMode = new KingOfTheHillMode(this);//new SpaceBattle(this);//new BasicSpaceMap(this);//
		gameMode.setupLevel();
	}
	
	public void finishTurn() {

		profiler.start("Simulation");
		
		float stepTime = 0.1f;
		
		int substeps = 1;
		for( int i = 0 ; i<substeps; i++ ){
			m_world.step(stepTime/(float)substeps, 1, 1);
			ents.stepAll();
			gameMode.step();
			ents.clearDeadEntities();
		}
		
		render.redraw = true;
		
		profiler.stop("Simulation");
	}
	
	public void sendCommandsToServer() {
		
		if( !render.takeMouseFocus ){
			return;
		}
		
		if( currentPlayer > -1 && render.hasMouseFocus ){
			
			float walkspeed = 0;
			float strafe = 0;
			float dAngle = 0;
			float dCursor = 0;

			//get the mouse motion and reset the position
			int mousex = MouseInfo.getPointerInfo().getLocation().x;
			mousex -= frame.getX();
			float dx = mousex - core.w/2;

			int mousey = MouseInfo.getPointerInfo().getLocation().y;
			mousey -= frame.getY();
			float dy = mousey - core.h/2;
			
			//float cursor = ents.players.get( currentPlayer ).localCursorDistance;
			dCursor = -dy/20f;
			ents.players.get( currentPlayer ).localCursorDistance += dCursor;
			
			centerMouse(); 
			
			float sensitivity = 0.0015f;
			dAngle = dx * sensitivity;
		
			strafe = (input.strafeLeft ? -1 : 0 ) + (input.strafeRight ? 1 : 0 );
			walkspeed = (input.goForward ? 1 : 0 ) + (input.goBackward ? -1 : 0 );

			boolean isDucking = input.duck;
			boolean isRunning = input.run;
			
			ents.players.get( currentPlayer ).localWalkAngle += dAngle;
			
			
			Action playerMove = new SetPlayerMotion( currentPlayer, dAngle, dCursor, walkspeed, strafe, isDucking, isRunning);
			gameloop.toServer.sendAction( playerMove );
			
			//shooting
			boolean isShooting = input.leftMouse || input.pendingShot || input.rightMouse;
			gameloop.toServer.sendAction( new SetPlayerShooting( currentPlayer, isShooting) );
			
			if( input.pendingWeaponSwap != 0 ){
				gameloop.toServer.sendAction( new PlayerChangeWeapon( currentPlayer, -1, input.pendingWeaponSwap ) );
			}
			
			if( input.lastWeaponNumPressed > -1) {
				gameloop.toServer.sendAction( new PlayerChangeWeapon(currentPlayer, input.lastWeaponNumPressed, 0 ));
			}
			
			if( input.pendingDrop ){
				gameloop.toServer.sendAction( new DropItem(currentPlayer) );
			}
			
			input.pendingDrop = false;
			input.pendingShot = false;
			input.pendingWeaponSwap = 0;
			input.lastWeaponNumPressed = -1;
		}
	}


	public void centerMouse() {
		try {
			Robot robot;
			robot = new Robot();
			
		    robot.mouseMove(core.w/2 + frame.getX(),core.h/2  + frame.getY());
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public void startCore(){
		frame = new JFrame("Massive");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Core embed = new Core(this);
		frame.add(embed);
		embed.init();
		while (embed.defaultSize&&!embed.finished)
		 try {Thread.sleep(5);} catch (Exception e) {}
		frame.pack();
		frame.setVisible(true);
		core = embed;
	}

	public void removePlayer(int playerNumber) {
		Player leaving = ents.players.get(playerNumber);
		if( leaving != null ){
			leaving.die();
			ents.players.remove(playerNumber);
			ents.remove(leaving);
			console.addLine(leaving.name + " disconnected" );
		}
	}
	
	public void leftGame() {
		//TODO: drop back to the main menu instead of dieing
		//System.out.println("Lost connection to server.");
		//System.exit(-1);
		
		gameloop = null;
		isConnecting = false;
		gameRunning = false;
		resetEverything();
		
		pendingReconnect  = true;
	}

	public boolean isInGameOrConnectingToGame() {
		//System.out.println(gameRunning + " " + isConnecting);
		return gameRunning || isConnecting;
	}
	
}
