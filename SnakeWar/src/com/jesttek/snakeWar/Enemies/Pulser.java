package com.jesttek.snakeWar.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.SnakeWarGame;

public class Pulser extends Enemy{
	private float mMaxPulseSize = 10;
	private float mTimer =0;
	private float mCurrentSize = 0;
	private int mStage = 0;
	private boolean mPlayedSound = false;
	
	/**
	 * @param position the enemies position
	 * @param pulseSize the max size the attack circle will reach to
	 */
	public Pulser(Coordinate position, float pulseSize) {
		mMaxPulseSize = pulseSize;
		Position = position;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		mTimer += (delta);
		switch (mStage) {
		case 0:
			if(mTimer >= 0.5)
			{
				mStage++;
			}
			break;
		case 1:
			if(mTimer >= 3)
			{
				mTimer = 0;
				mStage++;
			}
			break;
		case 2:
			if(!mPlayedSound) {
				SnakeWarGame.SoundPlayer.playEnemyAttack(3);
				mPlayedSound = true;
			}
			mCurrentSize = mMaxPulseSize-Math.abs((float) (Math.cos(mTimer)*mMaxPulseSize));
			if(mTimer >= 3.1f)
			{
				mStage = 1;
				mTimer = 0;
				mPlayedSound = false;
			}
			break;
		}
	}
	@Override
	public boolean checkCollision(Coordinate object) {

		//check if center circle hit
		if(Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < 10) {
			return true;
		}
		
		//check if pulse hit
		if (Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < mCurrentSize+1) {
			return true;
		}
			
		return false;
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Line);
		switch (mStage) {
		case 0:
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle(Position.X, Position.Y, (mTimer/0.5f)*10);
			break;
		case 1:
			if(mTimer> 1) {
				shapeRenderer.setColor(Color.RED);			
			}
			else
			{
				shapeRenderer.setColor(Color.WHITE);					
			}
			shapeRenderer.circle(Position.X, Position.Y, 3);
			if(mTimer > 2) {
				shapeRenderer.setColor(Color.RED);				
			}
			else
			{
				shapeRenderer.setColor(Color.WHITE);					
			}
			shapeRenderer.circle(Position.X, Position.Y, 6);	
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle(Position.X, Position.Y, 10);
			break;
		case 2:
			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.circle(Position.X, Position.Y, mCurrentSize);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.circle(Position.X, Position.Y, 3);
			shapeRenderer.circle(Position.X, Position.Y, 6);
			shapeRenderer.circle(Position.X, Position.Y, 10);
			break;		
		}
		shapeRenderer.end();
	}
	
	public float getSize() {
		return mCurrentSize;
	}

	@Override
	public int getAttackStrength() {
		return 25;		
	}
	
	@Override
	public int kill() {
		return 10;		
	}
}
