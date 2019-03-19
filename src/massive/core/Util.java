package massive.core;

import org.jbox2d.common.Vec2;


public class Util {
	public static Vec2 randV( float r1, float r2 ){
		float r = (float) (Math.random() * (r2-r1) + r1);
		float angle = (float) (Math.random() * Math.PI * 2);
		return angleV( angle, r );
	}
	public static Vec2 angleV( float angle, float r ){
		return new Vec2( (float)Math.cos(angle)*r, (float)Math.sin(angle)*r );
	}
	public static Vec2 rotateVec(Vec2 localAnchor, float angle) {
		float anchorAngle = (float)Math.atan2( localAnchor.y, localAnchor.x );
		return angleV( angle + anchorAngle, localAnchor.length() );
	}
}
