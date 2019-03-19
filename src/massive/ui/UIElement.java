package massive.ui;

import java.util.Vector;

import massive.core.Core;
import massive.core.Game;
import processing.core.PVector;


public class UIElement {
	public int x,y,w,h;
	public Game game;
	public Vector<UIElement> elements = new Vector<UIElement>();
	public UIElement(Game game, int x, int y, int w, int h) {
		this.game = game;
		this.x = x; this.y = y; this.w = w; this.h = h; 
		init();
	}
	public UIElement(Game game) {
		this.game = game;
		init();
	}
	public void mouseMove( PVector m ) {
		for( UIElement e : elements ){
			//if( e.hitbox(m.x, m.y) ){
				e.mouseMove( fixMouse(m) );
			//}
		}
	}
	
	private PVector fixMouse(PVector m) {
		m = m.get();
		m.sub(x,y,0);
		return m;
	}
	
	public void mouseDown( PVector m ) {
		for( UIElement e : elements ){
			//if( e.hitbox(m.x, m.y) ){
				e.mouseDown( fixMouse(m) );
			//}
		}
	}
	
	public void mouseUp( PVector m ) {
		for( UIElement e : elements ){
			//if( e.hitbox(m.x, m.y) ){
				e.mouseUp( fixMouse(m) );
			//}
		}
	}
	
	public void draw(Core core){
		if( !isVisible ){
			return;
		}
		core.pushMatrix();
		core.translate(x,y);
		for( UIElement e : elements ){
			e.draw(core);
		}
		core.popMatrix();
	}
	
	public boolean hitbox(float mx, float my) {
		return ( mx >= x && my >= y && mx < x+w && my < y+h );
	}
	
	public void init(){;}
	
	public boolean isVisible = true;
	public void setVisible(boolean b) {
		isVisible = b;
	}
}
