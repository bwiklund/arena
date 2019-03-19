package massive.weapons;

import massive.core.Game;
import massive.entities.PhysicsEntity;
import massive.entities.particles.BloodParticle;
import massive.entities.particles.SmokeParticle;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;




public class Bullet extends PhysicsEntity {

	
	public PhysicsEntity from;
	
	public int age = 0;
	public int maxAge = 50;
	float speed = 12;
    float inaccuracy = 0.01f;
	float radius = 0.15f;
	public int damage = 30;
	public boolean willRicochet = false;

	private static final int coverTimeout = 2;
	
	public Bullet(Game game, PhysicsEntity from, float speed, float inaccuracy, int maxAge ) {
	
		super(game);	
		
		this.speed = speed;
		this.inaccuracy = inaccuracy;
		this.maxAge = maxAge;
		this.from = from;
		turretIgnore = true;
		laserIgnore = true;
	}
	
	public void init(){
		createBody();
		launchTheBullet();
	}
	
	public void step(){
		if(age++>maxAge){
			this.removeThis = true;
		}
		
		if(age > coverTimeout ){
			//no more cover for you
	        fix.getFilterData().maskBits = ~(BULLETS);
	        fix.getFilterData().groupIndex = 0;
		}
	}

	public void launchTheBullet() {
        myBody.setTransform( from.myBody.getPosition(), 0 );
        lastPos = from.myBody.getPosition();
        float angle = (float) (from.myBody.getAngle() + (game.random.nextFloat()*2-1)*inaccuracy);
        
        Vec2 vel = new Vec2( (float)Math.cos(angle) * speed, (float)Math.sin(angle) * speed );
        myBody.setLinearVelocity(vel);
        
        
	}

	
	public void createBody() {
		
		float ddensity = 1f;
		float dfriction = 0.3f;
		float drestitution = 0.1f;
		
		
		CircleShape sd = new CircleShape();
		sd.m_radius = radius;
        
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.angle = (float) (game.random.nextFloat() * Math.PI * 2);//horizontal ? (float)(Math.PI/2.0):0f;
        bd.linearDamping = 0.1f;
       
        myBody = game.m_world.createBody(bd);
         
        fix = myBody.createFixture(sd, ddensity);
        fix.m_friction = dfriction;
        fix.m_restitution = drestitution;
 
        fix.getFilterData().categoryBits = BULLETS;
        fix.getFilterData().maskBits = ~(BULLETS | COVER);
        
        //dont hit the person who fired it, initially
        fix.getFilterData().groupIndex = PLAYERGROUPS-entityResponsible.id;
        
        myBody.setBullet(true);
        
        super.createBody();
        
	}
	
	Vec2 lastPos;

	public void draw(){
		game.core.pushStyle();
		game.core.strokeWeight(1);
		Vec2 pos = myBody.getPosition();
		//game.core.fill(0xffffffff);
		//game.core.stroke(0xffffffff);
		int alpha = (int)(255 - 200*Math.pow(age/(float)maxAge,2));
		if( alpha < 0 ){ alpha = 0; }
		int s = 0x00ffffff | ( alpha << 24 );
		game.core.stroke(s);
		//game.core.ellipse(pos.x, pos.y, radius*2, radius*2);
		game.core.line(pos.x,pos.y,lastPos.x,lastPos.y);
		
		lastPos = myBody.getPosition().clone();
		game.core.popStyle();
	}

	public void doRicochet() {
		for( int i = 0; i < 3; i++ ){
			game.ents.add( new SmokeParticle(game, myBody.getPosition() ) );
		}
		game.sound.play("ping"+(int)(game.random.nextFloat()*6)+".wav",game.random.nextFloat()*0.1f + 0.8f,myBody.getPosition());
	}
	
	public void doFleshyImpact() {
		for( int i = 0; i < 3; i++ ){
			game.ents.add( new BloodParticle(game, myBody.getPosition() ) );
		}
		game.sound.play("flesh"+(int)(game.random.nextFloat()*2)+".wav",game.random.nextFloat()*0.1f + 0.8f,myBody.getPosition());
	}

	public int getDamage() {
		return (int) (damage * (1 - age / (float)maxAge));
	}
}
