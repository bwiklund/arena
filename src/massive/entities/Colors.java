package massive.entities;

public class Colors {

	public static int[] Team = null;

	public static int menubgtransparent = 0xdd646464;
	public static int menubgsolid = 0xff646464;
	
	public static int bg = 0xff888888;
	public static int uibg = 0xcccccccc;

	public static int weaponColor = 0x33cc2200;

	public static int armorColor = 0x88000099;
	public static int speedColor = 0x88009966;

	public static int binocularColor = 0x880047AB;
	
	public Colors(){
		Team = new int[64];
		
		Team[0] = 0xffffffff;
		Team[1] = 0xff000000;
		Team[2] = 0xff66ee66;
	}
}
