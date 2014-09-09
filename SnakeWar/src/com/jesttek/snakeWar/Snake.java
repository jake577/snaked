package com.jesttek.snakeWar;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class Snake {
	public enum Direction {
		UP,
		DOWN,
		LEFT,
		RIGHT
	}	

	private Direction mCurrentDirection; // direction the snake was moving at last frame
	private Direction mNextDirection; // the direction snake is moving, including any new direction change since last frame
	private ArrayList<Coordinate> mTurnPoints = new ArrayList<Coordinate>();
	private float mLength = 100;
	private float mMoveSpeed = 50f;
	private boolean mObstacleCreated = false;
	private float mTimeRemaining = 45;
	
	/**
	 * @param head start position
	 * @param tail start position
	 */
	public Snake(Coordinate head, Coordinate tail, Direction facing, float length) {
		mLength = length;
		mNextDirection = facing;
		mTurnPoints.add(head);
		mTurnPoints.add(tail);		
	}
	
	public void act(float delta) {	
		if(mNextDirection != mCurrentDirection) {
			mObstacleCreated = false;
			Coordinate c = new Coordinate(mTurnPoints.get(0));
			mTurnPoints.add(1, c);
			if(mTurnPoints.size() >= 6) {
				mTurnPoints.remove(mTurnPoints.size()-1);
			}
			mCurrentDirection = mNextDirection;
		}
		
		mTimeRemaining -= delta;
		Coordinate c = mTurnPoints.get(0);
		switch(mNextDirection) {	
			case UP:
				c.Y+=(delta*mMoveSpeed);
				break;
			case DOWN:
				c.Y-=(delta*mMoveSpeed);
				break;
			case LEFT:
				c.X-=(delta*mMoveSpeed);
				break;
			case RIGHT:
				c.X+=(delta*mMoveSpeed);
				break;
		}

		//check snake length
		float length = 0;
		for(int i = 0; i < mTurnPoints.size()-1; i++) {
			Coordinate c1 = mTurnPoints.get(i);
			Coordinate c2 = mTurnPoints.get(i+1);
			length = length + Math.abs(c1.X - c2.X) + Math.abs(c1.Y - c2.Y);			
		}

		//remove length at end of snake
		float lengthToRemove = length-mLength;
		while(lengthToRemove > 0) {
			Coordinate last = mTurnPoints.get(mTurnPoints.size()-1);
			Coordinate secondLast = mTurnPoints.get(mTurnPoints.size()-2);
			//check if last section is horizontal or vertical
			if(last.X == secondLast.X) {
				//check length		
				float segmentLength = Math.abs(last.Y - secondLast.Y);
				if(lengthToRemove < segmentLength) {
					//check which direction to end point
					if(last.Y > secondLast.Y) {
						last.Y -= lengthToRemove;
					}
					else {
						last.Y += lengthToRemove;					
					}
					lengthToRemove = 0;
				}
				else {
					//tail moves past corner. So remove corner
					length -=Math.abs(last.Y - secondLast.Y);
					mTurnPoints.remove(mTurnPoints.size()-1);	
					lengthToRemove -= segmentLength;		
				}
			}
			else {
				//check length			
				float segmentLength = Math.abs(last.X - secondLast.X);
				if(lengthToRemove < segmentLength) {
					//check which direction to end point
					if(last.X > secondLast.X) {
						last.X -= lengthToRemove;
					}
					else {
						last.X += lengthToRemove;					
					}
					lengthToRemove = 0;
				}
				else {
					//tail moves past corner. So remove corner
					length -=Math.abs(last.X - secondLast.X);
					mTurnPoints.remove(mTurnPoints.size()-1);
					lengthToRemove -= segmentLength;					
				}
			}
		}
	}
	
	public void draw(ShapeRenderer shapeRenderer) {
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.PINK);
		for(int i = 1; i < mTurnPoints.size(); i+=1)
		{				
			Coordinate c1 = mTurnPoints.get(i-1);
			Coordinate c2 = mTurnPoints.get(i);
			shapeRenderer.line(c1.X, c1.Y,c2.X, c2.Y);		
		}	
		shapeRenderer.end();	
	}
	
	/**
	 * Checks if a new obstacle has been created on the last move and returns its defining rectangle.
	 * @return The defining rectangle of the obstacle created or null if no obstacle was created.
	 */
	public Rectangle createObstacle() {
		if(mTurnPoints.size() >= 5 && !mObstacleCreated) {
			Coordinate c1 = mTurnPoints.get(0);
			Coordinate c2 = mTurnPoints.get(1);
			Coordinate c3 = mTurnPoints.get(3);
			Coordinate c4 = mTurnPoints.get(4);
			boolean createObstacle = false;
			if((mNextDirection == Direction.LEFT) || (mNextDirection == Direction.RIGHT)) {
				//snake is currently moving horizontal				
				if(((c3.Y >= c1.Y) && (c4.Y <= c1.Y)) || ((c4.Y >= c1.Y) && (c3.Y <= c1.Y))) {
					if(((c1.X >= c3.X) && (c2.X <= c3.X)) || ((c1.X <= c3.X) && (c2.X >= c3.X))) {
						createObstacle = true;
					}
				}
			}
			else {
				//snake is currently moving vertical		
				if(((c3.X >= c1.X) && (c4.X <= c1.X)) || ((c4.X >= c1.X) && (c3.X <= c1.X))) {
					if(((c1.Y >= c3.Y) && (c2.Y <= c3.Y)) || ((c1.Y <= c3.Y) && (c2.Y >= c3.Y))) {
						createObstacle = true;
					}
				}
			}
			
			if(createObstacle) {
				float bottom = Math.min(c2.Y, c3.Y);
				float left = Math.min(c2.X, c3.X);
				float width = Math.abs(c2.X-c3.X);
				float height = Math.abs(c2.Y-c3.Y);
				mObstacleCreated = true;
				return new Rectangle(left, bottom, width, height);
			}
		}
		return null;
	}
	
	/**
	 * Changes the direction of the snake. Doesn't change if it is the opposite to the current direction.
	 * @param direction The direction to start moving in.
	 * @return True if direction was set
	 */
	public boolean setDirection(Direction direction) {
		if(mCurrentDirection != direction) {
			if(((mCurrentDirection == Direction.LEFT || mCurrentDirection == Direction.RIGHT) && (direction == Direction.UP || direction == Direction.DOWN)) ||
				((mCurrentDirection == Direction.UP || mCurrentDirection == Direction.DOWN) && (direction == Direction.LEFT || direction == Direction.RIGHT))) {
				mNextDirection = direction;
				return true;
			}			
		}
		return false;
	}
	
	/**
	 * @return The direction the snake is currently moving.
	 */
	public Direction getDirection() {
		return mNextDirection;
	}
	
	/**
	 * @return The current snake speed
	 */
	public float getSpeed() {
		return mMoveSpeed;
	}
	
	/**
	 * Sets the current snake speed
	 * @param speed 
	 */
	public void setSpeed(float speed) {
		mMoveSpeed = speed;
	}
	
	/**
	 * @return The current snake length
	 */
	public float getLength() {
		return mLength;
	}
	
	/**
	 * Sets the current snake length
	 * @param length 
	 */
	public void setLength(float length) {
		mLength = length;
	}
	
	/**
	 * @return Current position of the snakes head
	 */
	public Coordinate getHeadPosition() {
		return mTurnPoints.get(0);
	}
	
	/**
	 * Get one of the coordinates that defines the snakes position
	 * @param index
	 * @return the coordinate
	 */
	public Coordinate getSnakePoint(int index) {
		return mTurnPoints.get(index);
	}
	
	/**
	 * @return Time remaining in seconds until snake dies
	 */
	public float getTimeRemaining() {
		return mTimeRemaining;
	}
	
	/**
	 * Increases time remaining until snake dies
	 * @param time amount to increase by (in seconds)
	 */
	public void increaseTimeRemaining(float time) {
		mTimeRemaining+=time;
	}
	
	/**
	 * Decreases time remaining until snake dies
	 * @param time amount to increase by (in seconds)
	 */
	public void decreaseTimeRemaining(float time) {
		mTimeRemaining-=time;
	}
	
	/**
	 * Get each turn point that defines the snakes position
	 * @return They list of snake position coordinates
	 */
	public ArrayList<Coordinate> getPoints() {
		return mTurnPoints;
	}
}
