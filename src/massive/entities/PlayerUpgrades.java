package massive.entities;

public class PlayerUpgrades {
	//6 points total?
	public int lvl_weaponFireRate = 3;
	public int lvl_weaponMagazineSize = 3;
	public int lvl_weaponCalliber = 0;
	public int lvl_weaponAccuracy = 0;
	public int lvl_playerSpeed = 2;
	
	public PlayerUpgrades(
			int lvl_weaponFireRate,
			int lvl_weaponMagazineSize,
			int lvl_weaponCalliber,
			int lvl_weaponAccuracy,
			int lvl_playerSpeed ){
		this.lvl_weaponFireRate 	= lvl_weaponFireRate;
		this.lvl_weaponMagazineSize = lvl_weaponMagazineSize;
		this.lvl_weaponCalliber 	= lvl_weaponCalliber;
		this.lvl_weaponAccuracy 	= lvl_weaponAccuracy;
		this.lvl_playerSpeed 		= lvl_playerSpeed;
	}
	public int getWeaponFireRate(){
		int[] levels = {50,25,15,10,5};
		return levels[lvl_weaponFireRate]/2;
	}

	public int getWeaponMagazineSize(){
		int[] levels = {4,8,12,18};
		return levels[lvl_weaponMagazineSize];
	}
	
	public int getWeaponCalliber(){
		int[] levels = {60,30,15,10};
		return levels[lvl_weaponCalliber];
	}
	
	public float getWeaponAccuracy(){
		float[] levels = {0.1f,0.05f,0.025f,0.01f};
		return levels[lvl_weaponAccuracy];
	}

	public float getPlayerSpeed(){
		float[] levels = {2.0f,2.2f,2.4f,2.8f};
		return levels[lvl_playerSpeed];
	}
	
	public int getPlayerMaxHealth() {
		return 100;
	}
	public void resetStats() {
		// TODO Auto-generated method stub
		
	}
}