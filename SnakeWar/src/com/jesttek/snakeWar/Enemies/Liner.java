package com.jesttek.snakeWar.Enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.SnakeWarGame;

public class Liner extends Enemy{

	public static final float LINEWIDTH = 10;
	private float mBeamLength = 0;
	private float mTimer = 0;
	private boolean mVertical = false;
	private int mStep = 0;
	private boolean mPlayedSound = false;
	
	/**
	 * @param position the enemies position
	 * @param verticle If true liner starts with a vertical kill line and moves horizontally. Otherwise has a horizontal kill line and moves vertically
	 */
	public Liner(Coordinate position, boolean vertical) {
		mVertical = vertical;
		Position = position;
	}	

	@Override
	public void act(float delta) {
		mTimer += delta;
		switch(mStep) {
		case 0: //spawn
			if(mTimer >= 1) {
				mTimer = 0;
				mStep++;
			}
			break;
		case 1: //open up
			if(mTimer >= 1) {
				mTimer = 0;
				mStep++;
			}
			break;
		case 2: //extend "arms"
			mBeamLength+=(delta*500);
			if(mTimer >= 1) {
				mBeamLength = 500;
				mTimer = 0;
				mStep++;
				mPlayedSound = false;
			}
			if(!mPlayedSound) {
				SnakeWarGame.SoundPlayer.playEnemyAttack(3);
				mPlayedSound = true;
			}
			break;
		case 3: //retract "arms"
			mBeamLength-=(delta*500);
			if(mTimer >= 1) {
				mBeamLength = 0;
				mTimer = 0;
				mStep++;
			}
			break;
		case 4: //close
			if(mTimer >= 1) {
				mTimer = 0;
				mStep++;
			}
			break;
		case 5: //wait a bit
			if(mTimer >= 1) {
				mTimer = 0;
				mStep = 1;
				mVertical = !mVertical;
			}
			break;
		}
	}
	
	@Override
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.begin(ShapeType.Line);
		switch (mStep) {
		case 0:	//spawning 
			float size = 10*mTimer;
			shapeRenderer.circle(Position.X, Position.Y, size);
			break;
		case 1:
			float gap1 = LINEWIDTH/2*mTimer;
			if(mVertical) {
				shapeRenderer.arc(Position.X-gap1, Position.Y, 10, 90, 179.9f);	
				shapeRenderer.arc(Position.X+gap1, Position.Y, 10, -90, 180);				
			} 
			else {
				shapeRenderer.arc(Position.X, Position.Y+gap1, 10, 0,180);	
				shapeRenderer.arc(Position.X, Position.Y-gap1, 10, 180,179.9f);					
			}
			break;
		case 2:
		case 3:
			if(mVertical) {
				shapeRenderer.arc(Position.X-LINEWIDTH/2, Position.Y, 10, 90, 179.9f);	
				shapeRenderer.arc(Position.X+LINEWIDTH/2, Position.Y, 10, -90, 180);	
				shapeRenderer.rect(Position.X-LINEWIDTH/2,Position.Y-mBeamLength/2, LINEWIDTH, mBeamLength);	
			} 
			else {
				shapeRenderer.arc(Position.X, Position.Y+LINEWIDTH/2, 10, 0,180);	
				shapeRenderer.arc(Position.X, Position.Y-LINEWIDTH/2, 10, 180,179.9f);	
				shapeRenderer.rect(Position.X-mBeamLength/2,Position.Y-LINEWIDTH/2, mBeamLength, LINEWIDTH);							
			}
			break;
		case 4:
			float gap2 = LINEWIDTH/2*(1-mTimer);
			if(mVertical) {
				shapeRenderer.arc(Position.X-gap2, Position.Y, 10, 90, 179.9f);	
				shapeRenderer.arc(Position.X+gap2, Position.Y, 10, -90, 180);				
			} 
			else {
				shapeRenderer.arc(Position.X, Position.Y+gap2, 10, 0,180);	
				shapeRenderer.arc(Position.X, Position.Y-gap2, 10, 180,179.9f);					
			}
			break;
		case 5:
			shapeRenderer.circle(Position.X, Position.Y, 10);
			break;
		}
		shapeRenderer.end();
	}
	
	@Override
	public boolean checkCollision(Coordinate object) {
		
		if(mStep == 0 || mStep == 5) {
			//circle hasn't split, just check a single circle collision
			float size = 10;
			if(mStep == 0) {
				//get size of circle when spawning
				size = 10*mTimer;
			}
			if(Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < size) {
				return true;
			}
		}
		else {
			float gap = LINEWIDTH/2;
			switch(mStep) {
			case 1:
				gap = LINEWIDTH/2*mTimer;
				break;
			case 4:
				gap = LINEWIDTH/2*(1-mTimer);
				break;
			}
			//circle has split apart, just treat as two circles.
			if(mVertical) {
				if((Math.sqrt(Math.pow(Math.abs(object.X-Position.X+gap),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < 10) || 
						(Math.sqrt(Math.pow(Math.abs(object.X-Position.X-gap),2) + Math.pow(Math.abs(object.Y-Position.Y),2)) < 10)) {
					return true;
				}
			}
			else {
				if((Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y+gap),2)) < 10) || 
						(Math.sqrt(Math.pow(Math.abs(object.X-Position.X),2) + Math.pow(Math.abs(object.Y-Position.Y-gap),2)) < 10)) {
					return true;
				}
			}
		}
		
		//check if rectangle beam hit
		Rectangle r;
		if(mVertical) {
			r = new Rectangle(Position.X-LINEWIDTH/2,Position.Y-mBeamLength/2, LINEWIDTH, mBeamLength);
		} 
		else {
			r = new Rectangle(Position.X-mBeamLength/2,Position.Y-LINEWIDTH/2, mBeamLength, LINEWIDTH);		
		}

		if((object.X >= r.x) && (object.X <= r.x+r.width) && (object.Y >= r.y) && (object.Y <= r.y+r.height)) {
			return true;
		}
		return false;
	}
	
	@Override
	public int kill() {
		return 10;		
	}

	@Override
	public int getAttackStrength() {
		return 40;		
	}
}
