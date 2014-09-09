package com.jesttek.snakeWar;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.jesttek.snakeWar.Powerup.PowerupType;

public class Audio {

	private static int BOUNCE = 0;
	private static int BUTTONCLICK = 1;
	private static int COUNTDOWN = 2;
	private static int DAMAGE = 3;
	private static int ENEMYATTACK1 = 4;
	private static int ENEMYATTACK2 = 5;
	private static int ENEMYATTACK3 = 6;
	private static int ENEMYATTACK4 = 7;
	private static int ENEMYATTACK5 = 8;
	private static int ENEMYKILL = 9;
	private static int ENEMYSPAWN = 10;
	private static int GAMEOVER = 11;
	private static int HEALTH1 = 12;
	private static int HEALTH2 = 13;
	private static int HEALTH3 = 14;
	private static int HEALTH4 = 15;
	private static int HEALTH5 = 16;
	private static int POWERUPLENGTH = 17;
	private static int POWERUPSPEED = 18;
	private static int POWERUPTIME = 19;
	private static int OBSTACLECREATE = 20;
	private static int START = 21;	
	private Random mRandom = new Random();
	private Sound[] mSounds = new Sound[22];	
	private boolean mDamageOn = false;
	
	/**
	 * @param saveController used to check if sound is on/off
	 */
	public Audio() {	
		mSounds[BOUNCE] = Gdx.audio.newSound(Gdx.files.internal("data/bounce.mp3"));
		mSounds[BUTTONCLICK] = Gdx.audio.newSound(Gdx.files.internal("data/buttonClick.mp3"));
		mSounds[COUNTDOWN] = Gdx.audio.newSound(Gdx.files.internal("data/countdown.mp3"));
		mSounds[DAMAGE] = Gdx.audio.newSound(Gdx.files.internal("data/damagePulse.mp3"));
		mSounds[ENEMYATTACK1] = Gdx.audio.newSound(Gdx.files.internal("data/enemyAttack0.mp3"));
		mSounds[ENEMYATTACK2] = Gdx.audio.newSound(Gdx.files.internal("data/enemyAttack1.mp3"));
		mSounds[ENEMYATTACK3] = Gdx.audio.newSound(Gdx.files.internal("data/enemyAttack2.mp3"));
		mSounds[ENEMYATTACK4] = Gdx.audio.newSound(Gdx.files.internal("data/enemyAttack3.mp3"));
		mSounds[ENEMYATTACK5] = Gdx.audio.newSound(Gdx.files.internal("data/enemyAttack4.mp3"));
		mSounds[ENEMYKILL] = Gdx.audio.newSound(Gdx.files.internal("data/enemyKill.mp3"));
		mSounds[ENEMYSPAWN] = Gdx.audio.newSound(Gdx.files.internal("data/enemySpawn.mp3"));
		mSounds[GAMEOVER] = Gdx.audio.newSound(Gdx.files.internal("data/gameOver.mp3"));
		mSounds[HEALTH1] = Gdx.audio.newSound(Gdx.files.internal("data/health1.mp3"));
		mSounds[HEALTH2] = Gdx.audio.newSound(Gdx.files.internal("data/health2.mp3"));
		mSounds[HEALTH3] = Gdx.audio.newSound(Gdx.files.internal("data/health3.mp3"));
		mSounds[HEALTH4] = Gdx.audio.newSound(Gdx.files.internal("data/health4.mp3"));
		mSounds[HEALTH5] = Gdx.audio.newSound(Gdx.files.internal("data/health5.mp3"));
		mSounds[POWERUPLENGTH] = Gdx.audio.newSound(Gdx.files.internal("data/powerupLength.mp3"));
		mSounds[POWERUPSPEED] = Gdx.audio.newSound(Gdx.files.internal("data/powerupSpeed.mp3"));
		mSounds[POWERUPTIME] = Gdx.audio.newSound(Gdx.files.internal("data/powerupTime.mp3"));
		mSounds[OBSTACLECREATE] = Gdx.audio.newSound(Gdx.files.internal("data/squareCreate.mp3"));
		mSounds[START] = Gdx.audio.newSound(Gdx.files.internal("data/start.mp3"));
	}
	
	public void playPowerupPickup(PowerupType type) {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			switch(type) {
			case IncreaseLength:
				mSounds[POWERUPLENGTH].play(0.15f);
				break;
			case IncreaseSpeed:
				mSounds[POWERUPSPEED].play(0.15f);
				break;
			case IncreaseTime:
			case IncreaseOpponentSpeed:
				mSounds[POWERUPTIME].play(0.30f);
				break;
			}
		}
	}
	
	public void playHealthCollect() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			switch(mRandom.nextInt(5)) {
			case 0:
				mSounds[HEALTH1].play(0.15f);
				break;
			case 1:
				mSounds[HEALTH2].play(0.15f);
				break;
			case 2:
				mSounds[HEALTH3].play(0.15f);
				break;
			case 3:
				mSounds[HEALTH4].play(0.15f);
				break;
			case 4:
				mSounds[HEALTH5].play(0.15f);
				break;
			}
		}
	}
	
	public void startDamageLoop() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			if(!mDamageOn) {
				mSounds[DAMAGE].loop();
				mDamageOn = true;
			}
		}
	}
	
	public void stopDamageLoop() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			mSounds[DAMAGE].stop();
			mDamageOn = false;
		}
	}
	
	public void playEnemyAttack(int AttackNumber) {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			switch(AttackNumber) {
			 case 0:
				 mSounds[ENEMYATTACK1].play(0.2f);
				 break;
			 case 1:
				 mSounds[ENEMYATTACK2].play(0.2f);
				 break;
			 case 2:
				 mSounds[ENEMYATTACK3].play(0.2f);
				 break;
			 case 3:
				 mSounds[ENEMYATTACK4].play(0.2f);
				 break;
			 case 4:
				 mSounds[ENEMYATTACK5].play(0.2f);
				 break;		
			}
		}
	}
	
	public void playEnemySpawn() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			 mSounds[ENEMYSPAWN].play(0.15f);
		}
	}
	
	public void playEnemyKill() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			 mSounds[ENEMYKILL].play();
		}
	}
	
	public void playButtonClick() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			 mSounds[BUTTONCLICK].play(0.5f);
		}
	}
	
	public void playGameOver() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			 mSounds[GAMEOVER].play();
		}
	}
	
	public void playBounce() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			 mSounds[BOUNCE].play(0.7f);
		}
	}
	
	public void playObstacleCreate() {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			 mSounds[OBSTACLECREATE].play();
		}
	}
	
	public void playCountdownTone(int countdown) {
		if(SnakeWarGame.SaveController.isSoundOn()) {
			switch(countdown) {
			case 0:
				mSounds[START].play(0.05f);
				break;
			default:
				mSounds[COUNTDOWN].play(0.05f);
				break;
			}
		}
	}
	
	public void stopAll() {
		for(Sound sound:mSounds) {
			sound.stop();
		}
	}
	
	public void dispose() {
		for(Sound sound:mSounds) {
			sound.dispose();
		}
	}
}
