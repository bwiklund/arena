package massive.dialogs;

import massive.core.Core;
import massive.core.Game;
import massive.ui.UIButton;

public class UIButtonMainMenu extends UIButton {

	public UIButtonMainMenu(Game game, int x, int y, int w, int h, String s, String sc) {
		super(game, x, y, w, h, s, sc);
	}
	
	public void doButtonShape(Core c) {
		c.fill( 0x66ffffff );
		c.beginShape();
		//core.rect(x, y, w, h);
		c.vertex(x-10,y);
		c.vertex(x+w,y);
		c.vertex(x+w,y+h);
		c.vertex(x,y+h);
		c.endShape();
		
		c.fill( 0x33ffffff );
		c.beginShape();
		c.vertex(x-10,y);
		c.vertex(x-w,y);
		c.vertex(x-w,y+h);
		c.vertex(x,y+h);
		c.endShape();
	}
	
	public boolean hitbox(float mx, float my) {
		return ( my >= y && my < y+h );
	}
	
}
