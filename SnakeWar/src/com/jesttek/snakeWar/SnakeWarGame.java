package com.jesttek.snakeWar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.jesttek.snakeWar.Inferfaces.IInterstitialAd;
import com.jesttek.snakeWar.Inferfaces.IPlayServices;
import com.jesttek.snakeWar.Inferfaces.ISaveData;
import com.jesttek.snakeWar.Screens.InstructionsScreen;
import com.jesttek.snakeWar.Screens.MainMenuScreen;
import com.jesttek.snakeWar.Screens.MultiplayerScreen;
import com.jesttek.snakeWar.Screens.SinglePlayerScreen;

public class SnakeWarGame extends Game {

	public static String ACHIEVEMENT_ID_SURVIVOR = "CgkIsc2GhfcPEAIQBA";
	public static String ACHIEVEMENT_ID_KILLER = "CgkIsc2GhfcPEAIQBQ";
	public static String ACHIEVEMENT_ID_CLOSE_CALL = "CgkIsc2GhfcPEAIQBg";
	public static String ACHIEVEMENT_ID_MULTIKILL = "CgkIsc2GhfcPEAIQBw";
	public static String ACHIEVEMENT_ID_COMPETITOR = "CgkIsc2GhfcPEAIQCA";
	public static String ACHIEVEMENT_ID_ON_A_ROLL = "CgkIsc2GhfcPEAIQCQ";
	
	public static int VIRTUAL_WIDTH = 1280;
	public static int VIRTUAL_HEIGHT = 768;
	public static final float ASPECT_RATIO = (float) VIRTUAL_WIDTH / (float) VIRTUAL_HEIGHT;
	public static IPlayServices PlayServices;
	public static ISaveData SaveController;
	public static IInterstitialAd AdController;
	public static TextureAtlas TextureAtlas;
	public static Audio SoundPlayer;
	public static DistanceFieldFont GameFont;
	private Screen mActiveScreen;
	
	@Override
	public void create() {		
		SoundPlayer = new Audio();
		TextureAtlas = new TextureAtlas(Gdx.files.internal("textures/snakeWar.pack"));
		GameFont = new DistanceFieldFont("Roboto-Regular");
		setMainMenuScreen();
	}

	@Override
	public void dispose() {
		TextureAtlas.dispose();
		SoundPlayer.dispose();
	}
	
	/**
	 * Displays the game level screen
	 */
	public void setMultiplayerScreen()
	{		
		Screen newScreen = new MultiplayerScreen(this);
		setScreen(newScreen);
		if(mActiveScreen!= null){
			mActiveScreen.dispose();
		}
		mActiveScreen = newScreen;
	}
	
	/**
	 * Displays the game level screen
	 */
	public void setMainMenuScreen()
	{		
		Screen newScreen = new MainMenuScreen(this);
		setScreen(newScreen);
		if(mActiveScreen!= null){
			mActiveScreen.dispose();
		}
		mActiveScreen = newScreen;
	}
	
	/**
	 * Displays the instructions screen
	 */
	public void setInstructionsScreen()
	{		
		Screen newScreen = new InstructionsScreen(this);
		setScreen(newScreen);
		if(mActiveScreen!= null){
			mActiveScreen.dispose();
		}
		mActiveScreen = newScreen;
	}
	
	/**
	 * Starts a game
	 */
	public void createSinglePlayerGame()
	{	
		Screen newScreen = new SinglePlayerScreen(this);
		setScreen(newScreen);
		if(mActiveScreen!= null){
			mActiveScreen.dispose();
		}
		mActiveScreen = newScreen;
	}
}
