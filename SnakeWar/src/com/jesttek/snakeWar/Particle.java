package com.jesttek.snakeWar;



public class Particle {
	Coordinate mDirection = new Coordinate();
	private Coordinate mTarget;
	private Coordinate mPosition;
	private float mSpeed = 0;
	private boolean mTargetHit = false;
	private float mAlpha = 1;
	private float mInitialDistance;
	
	/**
	 * @param position The current position of the particle
	 * @param target The end position the particle is moving to
	 * @param initialSpeed speed to start moving at
	 */
	public Particle(Coordinate position, Coordinate target, float initialSpeed) {
		mTarget = target;
		mPosition = position;
		mSpeed = initialSpeed;
		mInitialDistance = (float)Math.sqrt(Math.pow(mTarget.X - mPosition.X,2) + Math.pow(mTarget.Y - mPosition.Y,2));
		mDirection = new Coordinate((mTarget.X - mPosition.X)/mInitialDistance, (mTarget.Y - mPosition.Y)/mInitialDistance);
	}
	
	/**
	 * @param delta time since last frame
	 */
	public void act(float delta) {		
		float distance = (float)Math.sqrt(Math.pow(mTarget.X - mPosition.X,2) + Math.pow(mTarget.Y - mPosition.Y,2));
		mAlpha = Math.min(1, distance/(mInitialDistance-10));
		
		if(mPosition.Y < mTarget.Y) {
			mTargetHit = true;
		}
		
		mSpeed+=(100*delta);
		mPosition.X+=(mSpeed*mDirection.X*delta);
		mPosition.Y+=(mSpeed*mDirection.Y*delta);
	}
	
	/**
	 * @return True when the particle has reached it's target
	 */
	public boolean isDone() {
		return mTargetHit;
	}
	
	/**
	 * @return the transparency of the particle
	 */
	public float getAlpha() {
		return mAlpha;
	}
	
	/**
	 * @return the current position
	 */
	public Coordinate getPosition() {
		return mPosition;
	}
}
