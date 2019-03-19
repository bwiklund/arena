package massive.core;

import java.util.HashMap;
import java.util.Map;

public class Profiler {
	HashMap<String, Long> times = new HashMap<String, Long>();
	HashMap<String, String> info = new HashMap<String, String>();

	public void start( String k ){
	  if( times.containsKey( k ) ){
		  times.remove(k);
	  }
	    
	  times.put( k, System.nanoTime() );
	  
	}

	public void stop( String k ){
	  if( !times.containsKey( k ) ){ return; }
	  else {
	     times.put( k, System.nanoTime() - (times.get( k )) );
	  }
	}
	
	public void info( String k, String v ){
		if( info.containsKey(k) ){
			info.remove(k);
		}
		info.put(k,v);
	}

	void printprofile(Core core){
		core.textFont( core.smallFont );
		core.pushStyle();
		core.textAlign(Core.RIGHT, Core.TOP);
		int i = 0;
		for ( Map.Entry<String,Long> entry : times.entrySet() ) {
			i++;
			core.noStroke();
			core.fill(255);
			String s = (String) entry.getKey();
			core.text(s, -915, 20 + i * 15, 1000, 20);
			float oneFrame = 1000000000/50.0f;
			float len = (float) ((Long) entry.getValue() / oneFrame );
			core.rect(90, 23 + i * 15,
					len * 500, 5);
		}
		for ( Map.Entry<String,String> entry : info.entrySet() ) {
			i++;
			core.noStroke();
			core.fill(255);
			String s = (String) entry.getKey();
			core.text(s + ": " + entry.getValue(), -915, 20 + i * 15, 1000, 20);
		}
		clear();
		core.popStyle();
		core.textFont(core.mediumFont);
	}

	void clear() {
		times.clear();
		info.clear();
	}
}
