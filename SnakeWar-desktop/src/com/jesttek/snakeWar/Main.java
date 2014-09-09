package com.jesttek.snakeWar;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SnakeWar";
		cfg.width = 1280;
		cfg.height = 768;

        SnakeWarGame.SaveController = new SaveData();
        SnakeWarGame.PlayServices = new PlayServices();
        SnakeWarGame.AdController = new InterstitialAd();
		new LwjglApplication(new SnakeWarGame(), cfg);
	}
}
