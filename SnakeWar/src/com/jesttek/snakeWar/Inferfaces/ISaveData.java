package com.jesttek.snakeWar.Inferfaces;

public interface ISaveData {
	
	public enum ControlType {
		Tap,
		Swipe
	}
	
	public boolean isSoundOn();

	public void setSound(boolean mode);
	
	public ControlType getControlType();

	public void setControlType(ControlType controlType);

	/**
	 * Add to the count of players multiplayer wins. 
	 * Increase win streak and best winstreak if required
	 */
	public void addWin();

	/**
	 * Add to the count of players multiplayer loses. 
	 * Increase lose streak and worst lose streak if required
	 */
	public void addLose();
	
	public int getWinStreak();
	
	public int getLoseStreak();
	
	public int getWins();
	
	public int getLoses();
}
