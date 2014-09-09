package com.jesttek.snakeWar.Inferfaces;

public interface IMoveReceiver {
    
	public final static int MSG_PING_REQUEST = 0;
	public final static int MSG_PING_REPLY = 1;
	public final static int MSG_READY = 2;
	public final static int MSG_REQUEST_START= 3;
	public final static int MSG_STARTING = 4;
	public final static int MSG_GAME = 6;
	public final static int MSG_DISCONNECT = 7;

	public void receiveMove(byte[] msg);
	public boolean isReady();
	public void gameStart(float countdown);
	public void opponentDisconnected();
	public void lostConnection();
	public void sendFailed(int id);
	public void sendSuccess(int id);
}
