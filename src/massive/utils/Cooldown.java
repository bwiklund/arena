package massive.utils;

public class Cooldown {
	public int state = 0;
	public int max = 0;
	
	public Cooldown(int cooldownTime ) {
		max = cooldownTime;
	}
	
	public void set(int cooldownTime){
		max = cooldownTime;
	}

	public void reset(){
		state = max;
	}
	
	public void tick(){
		if( state >= 0 ){
			state = Math.max(0,state-1);
		}
	}
	
	public boolean ready(){
		return state == 0;
	}

	public void finish() {
		state = -1;
	}
}
