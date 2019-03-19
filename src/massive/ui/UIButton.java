package massive.ui;

import massive.core.Core;
import massive.core.Game;
import processing.core.PVector;

public class UIButton extends UIElement {
	
	public String text;
	public String shortcut;
	public boolean enabled = true;

	public int textOffColor = 0xff222222;
	public int textOnColor = 0xffffffff;
	public int bgOffColor = 0x33000000;
	public int bgOnColor = 0xffffffff;
	
	public UIButton(Game game, int x, int y, int w, int h, String s, String sc ){
		super(game, x,y,w,h);
		shortcut = sc;
		this.text = s;
		
	}
	
	public void mouseDown( PVector m ){
		if( enabled ){
			if( hitbox(m.x,m.y) ){
				doMouseDown();
			}
		}
	}
	public void doMouseDown() {}
	
	public void mouseUp( PVector m ){
		if( enabled ){
			if( hitbox(m.x,m.y) ){
				doMouseUp();
			}
		}
	}
	public void doMouseUp() {}
	
	public void mouseMove( PVector m ){
		if( enabled ){
			hover = false;
			if( hitbox(m.x,m.y) ){
				hover = true;
				doMouseHover();
			}
		}
	}
	public void doMouseHover() {}
	
	public boolean hover;
	public void draw( Core core ){
		
		if( hover ){
			core.fill( bgOnColor );
		} else {
			core.fill( bgOffColor );
		}

		core.noStroke();
		
		doButtonShape(core);

		if( hover ){
			core.fill( textOffColor );
		} else {
			core.fill( textOnColor );
		}
		
		core.textFont( core.smallFont );
		core.textAlign( Core.LEFT, Core.TOP );
		core.text(text,x+5,y+5);
		
		if( shortcut != null ){
			core.textAlign( Core.RIGHT, Core.TOP );
			core.text(shortcut,x+w-5,y+5);
		}
		
		super.draw(core);
	}

	public void doButtonShape(Core core) {
		core.rect(x, y, w, h);
	}
}
