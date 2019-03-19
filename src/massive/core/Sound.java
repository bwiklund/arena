package massive.core;

import java.util.HashMap;

import massive.shared.TestLauncher;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.events.KillTrigger;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.SamplePlayer;

import org.jbox2d.common.Vec2;

/*import ddf.minim.AudioPlayer;
import ddf.minim.AudioSample;
import ddf.minim.Minim;*/

public class Sound {

	//Minim minim;
	//AudioSample audiosample;

	public AudioContext ac;
	Sample sample;
	public Core core;
	
	public HashMap<String,Sample> sounds = new HashMap<String,Sample>();
	public HashMap<String,SamplePlayer> ambient = new HashMap<String,SamplePlayer>();
	
	public int maxSounds = 80;
	
	public Sound(Core core){
		this.core = core;
		initAudio();
		preloadDefault();
	}

	private void preloadDefault() {
		preload("gunshot.wav");
		preload("reloadstart.wav");
		preload("reloadfinish.wav");
		preload("empty.wav");
		preload("ping0.wav");
		preload("ping1.wav");
		preload("ping2.wav");
		preload("ping3.wav");
		preload("ping4.wav");
		preload("ping5.wav");
		preload("flesh0.wav");
		preload("flesh1.wav");
		preload("snipershot.wav");
		preload("shotgun.wav");
		preload("turretgun.wav");
		preload("grenadetoss.wav");
		preload("grenadeboom.wav");
		preload("beeplow.wav");
		preload("beephigh.wav");

		preload("menuloop.wav");
		preload("victory.wav");
		preload("defeat.wav");

		preload("teamon.wav");
		preload("teamoff.wav");
		preload("enemyon.wav");
		preload("enemyoff.wav");
	}
	
	private void initAudio() {
		
		ac = new AudioContext( 1024 );
		System.out.println("Audio buffer size: " + ac.getBufferSize());
		ac.start();
		//minim = new Minim(proc);
	}
	
	public void preload(String str){
		
		if( !sounds.containsKey(str) ){
			Sample smp = SampleManager.sample(getAudioPath() + str);
			if( smp != null ){
				sounds.put(str,smp);
			} else {
				System.out.println("Couldn't load sound: " + str);
			}
		}
	}
	
	public String getAudioPath(){
		return TestLauncher.filepath+"media/sound/";
	}
	
	public void play(String str, float vol, Vec2 pos){

		if( core.game.input.mute ){ return; }
		if( core.game.isCatchingUp ){ return; }
		
		if( ac.out.getNumberOfConnectedUGens(0) > maxSounds ){
			return;
		} 

		vol*=2;
		//make sounds that are far away quieter
		Vec2 listenerPos = new Vec2();
		if( core.game.currentPlayer != -1 ){
			listenerPos = core.game.ents.players.get( core.game.currentPlayer ).myBody.getPosition();
		}
		float falloff = 14;
		float distanceFromListener = pos.sub(listenerPos).length();
		vol *= falloff/(falloff+distanceFromListener);
		
		preload(str);
		if( sounds.containsKey(str) ){
			SamplePlayer p = new SamplePlayer( ac, sounds.get(str) );
			p.setLoopType(SamplePlayer.LoopType.NO_LOOP_FORWARDS );
			p.setKillOnEnd(true);
			
			
			Gain g = new Gain(ac,2,vol);
			g.addInput(p);
			ac.out.addInput(g);
			
			//get rid of the gain bead if the sound is done
			p.setKillListener( new KillTrigger(g) );
		} else {
			System.out.println("Missing sound: " + str);
		}
	}
	
	public void ambient(String str, float vol, boolean play){
		if( !ambient.containsKey(str) ){
			if( play ){ //don't load a sample not to play it
				Sample smp = SampleManager.sample( getAudioPath() + str);
				SamplePlayer p = new SamplePlayer( ac, smp );
				p.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS );
				
				Gain g = new Gain(ac,2,vol);
				g.addInput(p);
				ac.out.addInput(g);
				
				ambient.put(str,p);
			}
		}
		
		if( !play ){
			if( ambient.containsKey(str) ){
				ambient.get(str).kill();
				ambient.remove(str);
			}
		}
	}
}
