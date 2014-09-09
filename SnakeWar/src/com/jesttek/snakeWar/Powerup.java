package com.jesttek.snakeWar;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Powerup {
	private static final int POWERUP_SIZE = 10; 
	
	public enum PowerupType {
		IncreaseLength,
		IncreaseSpeed,
		IncreaseOpponentSpeed,
		IncreaseTime
	}
	
	private boolean mDisabled = false;
	private boolean mPlaced = true;
	private Coordinate mPosition = new Coordinate();
	private float mSize = 0;
	private PowerupType mType = PowerupType.IncreaseLength;
	
	public Powerup(Coordinate position, PowerupType type) {
		mType = type;
		mPosition = position;
	}
	
	public void act(float delta) {
		if(mSize < POWERUP_SIZE) {
			mSize += (delta*POWERUP_SIZE);
		}			
	}

	/**
	 * @param snake
	 * @return true if powerup was picked up
	 */
	public boolean checkPickup(Coordinate snake) {	
		if(mPlaced && !mDisabled) {
			if(Math.sqrt(Math.pow(snake.X-mPosition.X,2)+Math.pow(snake.Y-mPosition.Y,2)) <= mSize) {
				return true;
			}			
		}
		return false;
	}

	public void draw(ShapeRenderer shapeRenderer) {	
		if(mPlaced) {
			switch(mType) {
			case IncreaseLength:
				shapeRenderer.begin(ShapeType.Line);
				if(mDisabled) {
					shapeRenderer.setColor(Color.GRAY);					
				}
				else {
					shapeRenderer.setColor(Color.GREEN);
				}
				shapeRenderer.circle(mPosition.X, mPosition.Y, POWERUP_SIZE);
				shapeRenderer.line(mPosition.X-POWERUP_SIZE/1.3f, mPosition.Y, mPosition.X-POWERUP_SIZE/4, mPosition.Y-POWERUP_SIZE/2);
				shapeRenderer.line(mPosition.X-POWERUP_SIZE/1.3f, mPosition.Y, mPosition.X-POWERUP_SIZE/4, mPosition.Y+POWERUP_SIZE/2);
				shapeRenderer.line(mPosition.X+POWERUP_SIZE/1.3f, mPosition.Y, mPosition.X+POWERUP_SIZE/4, mPosition.Y-POWERUP_SIZE/2);
				shapeRenderer.line(mPosition.X+POWERUP_SIZE/1.3f, mPosition.Y, mPosition.X+POWERUP_SIZE/4, mPosition.Y+POWERUP_SIZE/2);
				shapeRenderer.line(mPosition.X, mPosition.Y, mPosition.X, mPosition.Y);	
				shapeRenderer.end();
				break;
			case IncreaseOpponentSpeed:
				shapeRenderer.begin(ShapeType.Line);
				if(mDisabled) {
					shapeRenderer.setColor(Color.GRAY);					
				}
				else {
					shapeRenderer.setColor(Color.CYAN);
				}
				shapeRenderer.circle(mPosition.X, mPosition.Y, POWERUP_SIZE);		
				shapeRenderer.line(mPosition.X, mPosition.Y+POWERUP_SIZE/2, mPosition.X+POWERUP_SIZE/2, mPosition.Y);
				shapeRenderer.line(mPosition.X+POWERUP_SIZE/2, mPosition.Y, mPosition.X, mPosition.Y-POWERUP_SIZE/2);	
				shapeRenderer.line(mPosition.X-POWERUP_SIZE/2, mPosition.Y+POWERUP_SIZE/2, mPosition.X, mPosition.Y);
				shapeRenderer.line(mPosition.X, mPosition.Y, mPosition.X-POWERUP_SIZE/2, mPosition.Y-POWERUP_SIZE/2);	
				shapeRenderer.end();
				break;
			case IncreaseSpeed:
				shapeRenderer.begin(ShapeType.Line);
				if(mDisabled) {
					shapeRenderer.setColor(Color.GRAY);					
				}
				else {
					shapeRenderer.setColor(Color.RED);
				}
				shapeRenderer.circle(mPosition.X, mPosition.Y, POWERUP_SIZE);		
				shapeRenderer.line(mPosition.X-POWERUP_SIZE/2, mPosition.Y, mPosition.X, mPosition.Y+POWERUP_SIZE/2);
				shapeRenderer.line(mPosition.X+POWERUP_SIZE/2, mPosition.Y, mPosition.X, mPosition.Y+POWERUP_SIZE/2);		
				shapeRenderer.line(mPosition.X-POWERUP_SIZE/2, mPosition.Y-POWERUP_SIZE/2, mPosition.X, mPosition.Y);
				shapeRenderer.line(mPosition.X+POWERUP_SIZE/2, mPosition.Y-POWERUP_SIZE/2, mPosition.X, mPosition.Y);
				shapeRenderer.end();
				break;
			case IncreaseTime:
				shapeRenderer.begin(ShapeType.Line);
				if(mDisabled) {
					shapeRenderer.setColor(Color.GRAY);					
				}
				else {
					shapeRenderer.setColor(Color.YELLOW);
				}
				shapeRenderer.circle(mPosition.X, mPosition.Y, POWERUP_SIZE);	
				shapeRenderer.line(mPosition.X, mPosition.Y+POWERUP_SIZE*0.5f, mPosition.X, mPosition.Y-POWERUP_SIZE*0.5f); 				
				shapeRenderer.line(mPosition.X+POWERUP_SIZE*0.5f, mPosition.Y, mPosition.X-POWERUP_SIZE*0.5f, mPosition.Y); 
				shapeRenderer.end();	
				break;
			}		
		}
	}
	
	/**
	 * @param player Applies the powerups effect to the player
	 */
	public void applyEffect(Snake player) {
		switch(mType) {
		case IncreaseLength:
			player.setLength(player.getLength()+20f);
			break;
		case IncreaseSpeed:
			player.setSpeed(player.getSpeed()+10);
			break;
		case IncreaseTime:
			break;
		}
	}
	
	public PowerupType getPowerupType() {
		return mType;
	}
	
	public Coordinate getPosition() {
		return mPosition;
	}
	
	public void setPosition(Coordinate c) {
		mPosition = c;
		mPlaced = true;
	}
	
	public void clearPosition() {
		mPlaced = false;
	}
	
	public boolean isPlaced() {
		return mPlaced;
	}
	
	/**
	 * Player can't interact with powerup
	 */
	public void disable() {
		mDisabled = true;
	}
}
