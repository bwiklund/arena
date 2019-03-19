package massive.entities;

import massive.core.Game;
import massive.entities.triggers.Trigger;
import massive.weapons.Bullet;
import massive.weapons.Grenade;
import massive.weapons.Mine;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;



public class MassiveContactListener implements ContactListener {
	
	
	public Game mGame;

	public MassiveContactListener(Game game) {
		mGame = game;
	}
	
	interface ContactRules {
		boolean evaluate(PhysicsEntity a, PhysicsEntity b);
		void doIt(PhysicsEntity a, PhysicsEntity b);
	}
	
	ContactRules[] mContactRules = new ContactRules[]{
			
		//bullets hurt players
		/*new ContactRules(){
			@Override
			public boolean evaluate(PhysicsEntity a, PhysicsEntity b) {
				return a instanceof Bullet && b instanceof Player;
			}
			@Override
			public void doIt(PhysicsEntity a, PhysicsEntity b) {
				Player p =((Player)b);
				//if( p.inDitch && p.isDucking ){
					//no injure
					//b.injure(25);
					//((Bullet)a).doFleshyImpact();
					//a.deleteThis = true;
				//} else {
				b.injure(25, a);
				((Bullet)a).doFleshyImpact();
				a.deleteThis = true;
				//} 
			}
		},*/
		
		//bullets hit walls and damage stuff
		new ContactRules(){
			@Override
			public boolean evaluate(PhysicsEntity a, PhysicsEntity b) {
				return (a instanceof Bullet) && !(a instanceof Grenade); 
			}
			@Override
			public void doIt(PhysicsEntity a, PhysicsEntity b) {
				if( a instanceof Mine ){
					((Mine)a).startFuse();
					return;
				}
				Bullet abullet = ((Bullet)a);
				if( b instanceof Player ){
					abullet.willRicochet = false;
					abullet.doFleshyImpact();
				} else {
					abullet.doRicochet();
				}
				b.injure(abullet.getDamage(),a.entityResponsible);
				if( !abullet.willRicochet ){
					a.removeThis = true;
				}
			}
		},
		
		//triggers are... triggers
		new ContactRules(){
			@Override
			public boolean evaluate(PhysicsEntity a, PhysicsEntity b) {
				return a instanceof Trigger;
			}
			@Override
			public void doIt(PhysicsEntity a, PhysicsEntity b) {
				((Trigger)a).enter(b);
			}
		}
	};
	
	

	@Override
	public void beginContact(Contact contact) {
		
		PhysicsEntity a = (PhysicsEntity) contact.m_fixtureA.m_body.getUserData();
		PhysicsEntity b = (PhysicsEntity) contact.m_fixtureB.m_body.getUserData();
		
		for(ContactRules r : mContactRules){
			if(r.evaluate(a, b)){
				r.doIt(a, b);
				return;
			}
		}
		
		for(ContactRules r : mContactRules){
			if(r.evaluate(b, a)){
				r.doIt(b, a);
				return;
			}
		}
		
		/*
		if( a instanceof Bullet && !(a instanceof Grenade) && b instanceof Player ){
			b.injure(25);
			((Bullet)a).doFleshyImpact();
			a.deleteThis = true;
		} else if( b instanceof Bullet && !(b instanceof Grenade) && a instanceof Player ){
			a.injure(25);
			((Bullet)b).doFleshyImpact();
			b.deleteThis = true;
			
			
		} else if( a instanceof Bullet && !(a instanceof Grenade) ){
			((Bullet)a).doRicochet();
			a.deleteThis = true;
		} else if( b instanceof Bullet && !(b instanceof Grenade) ){
			((Bullet)b).doRicochet();
			b.deleteThis = true;
		}
		
		
		if( a instanceof Trigger ){
			((Trigger)a).enter(b);
		}
		if( b instanceof Trigger ){
			((Trigger)b).enter(a);
		}
		*/
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		PhysicsEntity a = (PhysicsEntity) contact.m_fixtureA.m_body.getUserData();
		PhysicsEntity b = (PhysicsEntity) contact.m_fixtureB.m_body.getUserData();
		
		if( a instanceof Trigger ){
			((Trigger)a).leave(b);
		}
		if( b instanceof Trigger ){
			((Trigger)b).leave(a);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
