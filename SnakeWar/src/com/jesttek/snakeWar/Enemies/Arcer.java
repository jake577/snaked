package com.jesttek.snakeWar.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.SnakeWarGame;

public class Arcer extends Enemy{
	private static final int COOLDOWN = 4;
	private int mStep = -1;
	private float mTimer = 0;
	private float mMaxSize;
	private float mCurrentSize = 0;
	private float mAttackRotation = 0;
	private float mEnemyRotation = 0;
	private boolean mReverse = false;
	private boolean[] mActiveSegments = {false, true, false, false, false, true, false, false};
	private boolean mSpawning = true;
	private boolean mPlayedSound = false;
	
	/**
	 * @param position the enemies position
	 * @param maxSize max size the arc will reach
	 */
	public Arcer(Coordinate position, float maxSize) {
		mMaxSize = maxSize;
		Position = position;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		switch(mStep)
		{
		case -1: //spawning
			mTimer+=delta;
			if(mTimer >= 1.5f) {
				mSpawning = false;
				mStep = 0;
				mTimer = 0;
			}
			break;
		case 0:	//grow arc to max size
			if(!mPlayedSound) {
				SnakeWarGame.SoundPlayer.playEnemyAttack(0);
				mPlayedSound = true;
			}
			mCurrentSize += delta*50;
			if(mCurrentSize >= mMaxSize) {
				mCurrentSize = mMaxSize; 
				mTimer = 0;
				mStep++;
				mPlayedSound = false;
			}
			break;
		case 1: //pause a bit
			mTimer+=delta;
			if(mTimer >= 0.5) {
				mTimer = 0;
				mStep++;
			}
			break;
		case 2: //rotate	

			if(!mReverse)
			{
				mAttackRotation += delta*40;
				if(mAttackRotation >= 180) {
					mStep++;
					mAttackRotation = 180;
				}
			}
			else
			{
				mAttackRotation += delta*-40;
				if(mAttackRotation <= -180) {
					mStep++;
					mAttackRotation = -180;
				}
			}
			break;
		case 3: //pause a bit
			mTimer+=delta;
			if(mTimer >= 0.5) {
				mTimer = 0;
				mStep++;
			}
			break;
		case 4: //shrink
			mCurrentSize -= delta*50;
			if(mCurrentSize <= 0) {
				mCurrentSize = 0;
				mStep++;
				mAttackRotation = 0;
			}
			break;
		case 5: //cooldown
			mTimer+=delta;
			if(mTimer >= COOLDOWN) {
				mTimer = 0;
				mStep=0;
				mReverse = !mReverse;
			}
			break;
		}
		mEnemyRotation += delta*20;
	}
	
	@Override
	public boolean checkCollision(Coordinate object) {
		if(!mSpawning) {
			//check if center circle hit
			if(Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < 10) {
				return true;
			}
			
			//check if arcs hit
			if(Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < mCurrentSize+1) {
				float objectAngle = (float) Math.toDegrees(Math.atan((object.Y - Position.Y)/(object.X - Position.X)));
				float angle = mAttackRotation;
				for(int i = 0; i< 8; i++) {
					if(mActiveSegments[i]) {
						if((objectAngle >= angle && objectAngle <= angle+45) ||
							(objectAngle+360 >= angle && objectAngle+360 <= angle+45) ||
							(objectAngle-360 >= angle && objectAngle-360 <= angle+45)) {
							return true;
						}
					}					
					angle+=45;
				}
			}			
		}
		return false;
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.WHITE);
		if(mStep >= 0) {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.circle(Position.X, Position.Y, 10);
			if(mStep == 5) {
				shapeRenderer.setColor(1, 1-mTimer/COOLDOWN, 1-mTimer/COOLDOWN, 1);				
			}
			shapeRenderer.arc(Position.X, Position.Y, 10, mEnemyRotation, 45);
			shapeRenderer.arc(Position.X, Position.Y, 10, 180+mEnemyRotation, 45);
			float start = 0;
			for(int i = 0; i< 8; i++)
			{
				if(mActiveSegments[i]) {
					shapeRenderer.arc(Position.X, Position.Y, getSize(), start+mAttackRotation, 45);
				}					
				start+=45;
			}
			shapeRenderer.end();
		}
		else {
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.circle(Position.X, Position.Y, Math.min((mTimer/0.5f)*10,10));
			shapeRenderer.arc(Position.X, Position.Y, (mTimer/1.5f)*10, mEnemyRotation, 45);
			shapeRenderer.arc(Position.X, Position.Y, (mTimer/1.5f)*10, 180+mEnemyRotation, 45);
			shapeRenderer.end();
		}
	}
	
	@Override
	public int kill() {
		return 10;		
	}

	@Override
	public int getAttackStrength() {
		return 30;		
	}
	
	public float getSize() {
		return mCurrentSize;
	}
	
	public float getMaxSize() {
		return mMaxSize;
	}
}
