package com.jesttek.snakeWar.Enemies;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jesttek.snakeWar.Coordinate;

public class Enemy {
	public Coordinate Position;
	
	public void act(float delta) {
	}

	public boolean checkCollision(Coordinate object) {			
		return false;
	}

	public void draw(ShapeRenderer shapeRenderer) {	
	}
	
	/**
	 * Kills this unit
	 * @return the bonus for killing the unit
	 */
	public int kill() {
		return 0;		
	}
	
	/**
	 * @return How much damage the unit does each second
	 */
	public int getAttackStrength() {
		return 20;		
	}
}
