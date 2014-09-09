package com.jesttek.snakeWar.Inferfaces;

public interface IPlayServices {
	public void logon();
	public void logoff();
	public void unlockAchievement(String achievementID);
	public void showLeaderboards();
	public void showAchievements();
	public void startRandomMultiplayer();
	public boolean isSignedIn();	
	public void submitScore(int score);
	
	/**
	 * Broadcasts a message to multiplayer opponents without confirming they have received it.
	 * @param message The message to broadcast
	 * @return The ID for the message sent. -1 if it failed to send.
	 */
	public void broadcastMessage(byte[] message);
	
	/**
	 * Reliable broadcasts a message to multiplayer opponents.
	 * @param message The message to broadcast
	 * @return False if message failed to send
	 */
	public int broadcastReliableMessage(byte[] message);
	public void broadcastReady();
	public void registerMoveReceiver(IMoveReceiver receiver);	
	public void disconnectGame();	
	public boolean checkConnection();	
}
