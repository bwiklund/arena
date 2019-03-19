package massive.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class Diagnostic {
	public HashMap<String,String> strs;
	public static Diagnostic top;
	
	public Diagnostic(){
		top = this;
		strs = new HashMap<String,String>();
	}
	
	public void print(){
		
		Vector<String> v = new Vector<String>(strs.keySet());
	    Collections.sort(v);
	    
	    String out = "===========================";    
	   /* for( Entry<String, String> e : strs.entrySet() ) {        
	        out += "\n" + e.getKey() + e.getValue();
	    }*/
	    
	    Iterator<String> it = v.iterator();
	    while (it.hasNext()) {
	       String element =  (String)it.next();
	       System.out.println( element + " " + (String)strs.get(element));
	    }
	    
	    System.out.println(out);
		
	    strs.clear();
	}
	
	public static void set( String k, String v ){
		top.strs.put(k, v);
	}
}
