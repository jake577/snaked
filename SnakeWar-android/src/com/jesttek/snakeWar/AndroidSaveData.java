package com.jesttek.snakeWar;

import com.jesttek.snakeWar.Inferfaces.ISaveData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AndroidSaveData implements ISaveData{

	private static final String VERSION = "VERSION";
	private static final String SOUND = "SOUND";
	private static final String CONTROL_TYPE = "CONTROLTYPE";
	private static final String WIN_COUNT = "WINCOUNT";
	private static final String LOSE_COUNT = "LOSECOUNT";
	private static final String CURRENT_WIN_STREAK = "CURRENTWINSTREAK";
	private static final String CURRENT_LOSE_STREAK = "CURRENTLOSESTREAK";
	private static final String BEST_WIN_STREAK = "BESTWINSTREAK";
	private static final String WORST_LOSE_STREAK = "WORSTLOSESTREAK";
	private SharedPreferences mPrefs;
	private Editor mEditor;
	
	/**
	 * @param ctx Gets the application context from this context
	 */
	public AndroidSaveData(Context ctx)
	{
		mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		mEditor = mPrefs.edit();
		
		mEditor.putInt(VERSION, 1);
		mEditor.commit();	
	}

	@Override
	public boolean isSoundOn() {
		return mPrefs.getBoolean(SOUND, true);
	}

	@Override
	public void setSound(boolean mode) {
		mEditor.putBoolean(SOUND, mode);
		mEditor.commit();						
	}

	@Override
	public ControlType getControlType() {
		if(mPrefs.getInt(CONTROL_TYPE, 0) == 0) {
			return ControlType.Swipe;
		}
		else {
			return ControlType.Tap;			
		}
	}

	@Override
	public void setControlType(ControlType controlType) {
		if(controlType == ControlType.Swipe) {
			mEditor.putInt(CONTROL_TYPE, 0);
		}
		else {
			mEditor.putInt(CONTROL_TYPE, 1);			
		}
		mEditor.commit();					
	}

	@Override
	public void addWin() {
		int wins = mPrefs.getInt(WIN_COUNT, 0)+1;
		mEditor.putInt(WIN_COUNT, wins);

		int winStreak = mPrefs.getInt(CURRENT_WIN_STREAK, 0)+1;
		mEditor.putInt(CURRENT_WIN_STREAK, winStreak);

		int bestWinStreak = mPrefs.getInt(BEST_WIN_STREAK, 0);
		if(winStreak > bestWinStreak) {
			mEditor.putInt(BEST_WIN_STREAK, winStreak);
		}
		
		mEditor.putInt(CURRENT_LOSE_STREAK, 0);
		mEditor.commit();
	}

	@Override
	public void addLose() {
		int loses = mPrefs.getInt(LOSE_COUNT, 0)+1;
		mEditor.putInt(LOSE_COUNT, loses);

		int loseStreak = mPrefs.getInt(CURRENT_LOSE_STREAK, 0)+1;
		mEditor.putInt(CURRENT_LOSE_STREAK, loseStreak);

		int bestWinStreak = mPrefs.getInt(WORST_LOSE_STREAK, 0);
		if(loseStreak > bestWinStreak) {
			mEditor.putInt(WORST_LOSE_STREAK, loseStreak);
		}
		
		mEditor.putInt(CURRENT_WIN_STREAK, 0);		
	}

	@Override
	public int getWinStreak() {
		return mPrefs.getInt(CURRENT_WIN_STREAK, 0);
	}

	@Override
	public int getLoseStreak() {
		return mPrefs.getInt(CURRENT_LOSE_STREAK, 0);
	}

	@Override
	public int getWins() {
		return mPrefs.getInt(WIN_COUNT, 0);
	}

	@Override
	public int getLoses() {
		return mPrefs.getInt(LOSE_COUNT, 0);
	}	
}
