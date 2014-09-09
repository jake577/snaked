package com.jesttek.snakeWar;

import com.jesttek.snakeWar.Inferfaces.IMoveReceiver;
import com.jesttek.snakeWar.Inferfaces.IPlayServices;

public class PlayServices implements IPlayServices {

	@Override
	public void logon() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logoff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlockAchievement(String achievementID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showLeaderboards() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showAchievements() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSignedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startRandomMultiplayer() {
		// TODO Auto-generated method stub
	}

	@Override
	public void broadcastMessage(byte[] message) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void registerMoveReceiver(IMoveReceiver receiver) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void broadcastReady() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int broadcastReliableMessage(byte[] message) {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public void submitScore(int score) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnectGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkConnection() {
		// TODO Auto-generated method stub
		return false;
	}
}
