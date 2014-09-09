package com.jesttek.snakeWar;
import com.jesttek.snakeWar.Inferfaces.ISaveData;

public class SaveData implements ISaveData{
	private ControlType mControlType = ControlType.Swipe;
	private boolean mSound = true;
	private int mWinCount = 0;
	private int mLoseCount = 0;
	private int mCurrentWinStreak = 0;
	private int mCurrentLoseStreak = 0;
	private int mBestWinStreak = 0;
	private int mWorstLoseStreak = 0;

	@Override
	public boolean isSoundOn() {
		return mSound;
	}

	@Override
	public void setSound(boolean mode) {
		mSound = mode;
		
	}

	@Override
	public ControlType getControlType() {
		return mControlType;
	}

	@Override
	public void setControlType(ControlType controlType) {
		mControlType = controlType;
	}

	@Override
	public void addWin() {
		mWinCount++;
		mCurrentWinStreak++;
		mCurrentLoseStreak = 0;
		mBestWinStreak = Math.max(mBestWinStreak, mCurrentWinStreak);
		
	}

	@Override
	public void addLose() {
		mLoseCount++;			
		mCurrentLoseStreak++;
		mCurrentWinStreak = 0;
		mWorstLoseStreak = Math.max(mWorstLoseStreak, mCurrentLoseStreak);
	}

	@Override
	public int getWinStreak() {
		return mCurrentWinStreak;
	}

	@Override
	public int getLoseStreak() {
		return mCurrentLoseStreak;
	}

	@Override
	public int getWins() {
		return mWinCount;
	}

	@Override
	public int getLoses() {
		return mLoseCount;
	}

}
