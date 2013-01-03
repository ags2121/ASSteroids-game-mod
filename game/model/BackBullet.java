package edu.uchicago.cs.java.finalproject.game.model;

public class BackBullet extends Bullet {
		
	public BackBullet(Falcon fal){
		super(fal);
	    setDeltaX( -1*(fal.getDeltaX() + Math.cos( Math.toRadians( fal.getOrientation() ) ) * getFIRE_POWER()) );
	    setDeltaY( -1*(fal.getDeltaY() + Math.sin( Math.toRadians( fal.getOrientation() ) ) * getFIRE_POWER()) );
	}
}
