package massive.weapons;

import massive.core.Core;
import massive.entities.Colors;
import massive.entities.Item;
import massive.entities.PhysicsEntity;
import massive.shared.TestLauncher;
import processing.core.PImage;

public class Weapon extends Item {
	public PhysicsEntity owner;
	public boolean isShooting;
	public String name = "";
	public PhysicsEntity playerResponsible;
	
	public Weapon(PhysicsEntity owner){
		this.playerResponsible = this.owner = owner;
	}
	
	public void stepAlways(){
		
	}
	
	public void stepWhileSelected(){
		
	}
	
	public int tint = Colors.weaponColor;
	
	public String imgPath;
	public PImage img;
	public void drawUI() {
		Core c = owner.game.core;
		checkImageLoaded();
		if( img != null ){
			c.imageMode(Core.CORNER);
			float zoom = 1;
			float x = c.width - img.width/zoom; float y = c.height - 200;
			c.image( img,x,y,img.width/zoom,img.height/zoom );
		}
	}
	public float drawIcon(float x, float y, float w, float h, float iconShrink, boolean selected){
		Core c = owner.game.core;
		checkImageLoaded();
		if( img != null ){
			c.imageMode(Core.CENTER);
			
			int tinter = c.lerpColor( 0xff999999, tint, 0.4f );
			int darktinter = c.lerpColor( 0xff000000, tint, 0.4f );
			
			if( !selected ){ c.tint(darktinter); }
			c.fill( tinter );
			c.rect(x,y,w,h);
			c.image( img,x+w/2,y+h/2,img.width/iconShrink,img.height/iconShrink );
			c.noTint();
			return img.width/iconShrink;
		}
		return 0;
	}
	public void checkImageLoaded() {
		Core c = owner.game.core;
		if( img == null && imgPath != null ){
			img = c.loadImage(TestLauncher.filepath + imgPath);
		}
		if( img == null && imgPath != null ){
			System.out.println("Missing image: " + imgPath );
		}
	}
	
	public void drawModel(){
		
	}
	public void reload() {
		
	}
	public void refillAmmo(){
		
	}
	public PhysicsEntity getPlayerResponsible(){
		return playerResponsible;
	}
	
	public boolean isSelected = false;
	public void select(){
		isShooting = false;
		isSelected = true;
	}
	public void deselect(){
		isShooting = false;
		isSelected = false;
	}
	public void onDiscard(){
		
	}
}
