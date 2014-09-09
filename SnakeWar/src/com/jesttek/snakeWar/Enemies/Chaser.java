package com.jesttek.snakeWar.Enemies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.SnakeWarGame;

public class Chaser extends Enemy{
	private float mTimer = 0;
	private int mStep = 0;
	private float mSize = 10;
	private float mCurrentSize = 0;
	private float mMaxSpeed = 0;
	private float mSpeed = 2;
	private boolean mSpawning = true;	
	private ArrayList<Rectangle> mObstacles = new ArrayList<Rectangle>();
	private Coordinate mTarget;
	private boolean mPlayedSound = false;
	
	/**
	 * @param position The chasers position
	 * @param size The size of the follower
	 * @param speed The speed the chaser moves at
	 * @param targetLocation The position the chaser will start moving towards
	 * @param obstacles The obstacles list
	 */
	public Chaser(Coordinate position, float size, float maxSpeed, Coordinate targetLocation, ArrayList<Rectangle> obstacles) {
		mMaxSpeed = maxSpeed;
		mSpeed = mMaxSpeed/5;
		mSize = size;
		Position = position;
		mTarget = targetLocation;
		mObstacles = obstacles;
	}
	
	/**
	 * @param targetLocation The new postion to start moving towards
	 */
	public void setTarget(Coordinate targetLocation) {
		mTarget = targetLocation;
	}
	
	@Override
	public void act(float delta) {
		mTimer+=delta;
		switch(mStep) {
		case 0: //slow
			if(mTimer >= 2) {
				mStep++;
				mTimer = 0;	
			}
			break;
		case 1: //speed up
			if(mTimer <= 2) {
				if(mSpeed <= mMaxSpeed) {
					mSpeed+=(2f*mSpeed*delta);
				}
				else {
					mSpeed = mMaxSpeed;
				}
				if(!mPlayedSound) {
					SnakeWarGame.SoundPlayer.playEnemyAttack(1);
					mPlayedSound = true;
				}
			}
			else {
				mStep++;
				mPlayedSound = false;
				mTimer = 0;				
			}
			break;
		case 2: //slow down
			if(mTimer <= 2) {
				mSpeed-=(2f*mSpeed*delta);
			}
			else {
				mStep = 0;	
				mTimer = 0;
				mSpeed = mMaxSpeed/10;
			}
			break;
		}

		float x = mTarget.X-Position.X;
		float y = mTarget.Y-Position.Y;
		float magnitude = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		
		//move in x direction and check collision, move to edge of obstacle if collided
		Position.X+=(x/magnitude*delta*mSpeed);
		for(int i = 0 ; i < mObstacles.size(); i++) {
			Rectangle r = mObstacles.get(i);
			if((Position.X > r.x && Position.X < r.x+r.width) &&(Position.Y > r.y && Position.Y < r.y+r.height)) {
				//collided, move back to the edge of the obstacle that we entered from
				if(x < 0) {
					Position.X=r.x+r.width;
				}
				else {
					Position.X=r.x;					
				}
			}
		}
		
		//same for y direction
		Position.Y+=(y/magnitude*delta*mSpeed);
		for(int i = 0 ; i < mObstacles.size(); i++) {
			Rectangle r = mObstacles.get(i);
			if((Position.X > r.x && Position.X < r.x+r.width) &&(Position.Y > r.y && Position.Y < r.y+r.height)) {
				//collided, move back to the edge of the obstacle that we entered from
				if(y < 0) {
					Position.Y=r.y+r.height;
				}
				else {
					Position.Y=r.y;					
				}
			}
		}

		if(mSpawning) {
			mCurrentSize += (delta*4);
			if(mCurrentSize > mSize) {
				mCurrentSize = mSize;
				mSpawning = false;
			}
		}
	}

	@Override
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Line);
		if(mStep==1) {
			shapeRenderer.setColor(1, 1-mSpeed/mMaxSpeed, 1-mSpeed/mMaxSpeed, 1);
		}
		else {
			shapeRenderer.setColor(Color.WHITE);			
		}
		shapeRenderer.circle(Position.X, Position.Y, mCurrentSize);
		shapeRenderer.end();
	}
	
	@Override
	public boolean checkCollision(Coordinate object) {
		if(!mSpawning) {
			if(Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < mCurrentSize) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int kill() {
		return 10;		
	}

	@Override
	public int getAttackStrength() {
		return 15;		
	}
	
	public float getSize() {
		return mCurrentSize;
	}
}
