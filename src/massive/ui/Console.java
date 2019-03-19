package massive.ui;

import java.util.Vector;

import massive.core.Core;
import massive.core.Game;
import massive.core.Input;
import massive.shared.TxtMessage;

public class Console {
	public Vector<ConsoleLine> textLines = new Vector<ConsoleLine>();
	public boolean hasFocus = false;
	public String inputString = "";
	private Game game;
	
	public Console( Game game ){
		this.game = game;
	}
	
	public void addLine(String string) {
		textLines.add(new ConsoleLine("",string,400));
	}
	
	public void addLine(String name, String string) {
		textLines.add(new ConsoleLine(name,string,400));
	}
	
	int caretCounter = 0;
	int caretInterval = 30;
	
	public void drawConsole(Core core) {
		core.textFont(core.smallFont);
		core.noStroke();
		
		for( ConsoleLine cl : textLines ){
			cl.tstamp -= 1;
		}
		for( int i = 1; i < 10; i++ ){
			int index = textLines.size()-i;
			if( index < 0 || index >= textLines.size() ){ break; }
			ConsoleLine cl = textLines.get( index );
			cl.tstamp -= 1;
			if( cl.tstamp <= 0 ){
				textLines.remove(index); i--;
			} else {

				core.textAlign( Core.RIGHT, Core.BOTTOM );
				core.fill( 0xff000000 );
				core.text(cl.leftstr,85,core.h - 80 - 20*i);

				core.textAlign( Core.LEFT, Core.BOTTOM );
				if( cl.leftstr.equals("") ){
					core.fill( 0xffffcc99 );
				} else {
					core.fill( 0xffffffff );
				}
				core.text(cl.str,100,core.h - 80 - 20*i);
			}
		}

		if( hasFocus ){
			core.textAlign( Core.LEFT, Core.BOTTOM );
			core.fill( 0xffffffff );
			String str = "" + inputString;
			core.text(str,100,core.h - 80 - 0);
			
			caretCounter = ( (caretCounter + 1) % caretInterval );
			if( caretCounter > caretInterval / 2 ){
				core.fill( 0xffffffff );
				core.text("|",100+core.textWidth(str)-2,core.h-80);
			}
		}
		
		
		core.textFont(core.mediumFont);
	}
	
	public class ConsoleLine {
		public String leftstr;
		public String str;
		public int tstamp;
		public ConsoleLine( String leftstr, String str, int t ){
			this.str = str;
			this.leftstr = leftstr;
			tstamp = t;
		}
	}

	public void keyPressed(char keyCode) {
		if( keyCode == Input.KEY_BACKSPACE ){
			if( inputString.length() > 0 ){
				inputString = inputString.substring(0, inputString.length()-1 );
			}
		} else {
			inputString += keyCode;
		}
	}
	
	public void sendLine() {
		game.gameloop.toServer.sendAction( new TxtMessage(inputString) );
		hasFocus = false;
		inputString = "";
	}
}
