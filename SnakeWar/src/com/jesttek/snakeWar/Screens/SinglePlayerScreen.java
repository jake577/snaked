package com.jesttek.snakeWar.Screens;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.Particle;
import com.jesttek.snakeWar.Powerup;
import com.jesttek.snakeWar.Powerup.PowerupType;
import com.jesttek.snakeWar.Snake;
import com.jesttek.snakeWar.Snake.Direction;
import com.jesttek.snakeWar.SnakeWarGame;
import com.jesttek.snakeWar.TextUtil;
import com.jesttek.snakeWar.Controls.PausePopup;
import com.jesttek.snakeWar.Controls.ShaderLabel;
import com.jesttek.snakeWar.Controls.SinglePlayerEndPopup;
import com.jesttek.snakeWar.Enemies.Arcer;
import com.jesttek.snakeWar.Enemies.Chaser;
import com.jesttek.snakeWar.Enemies.Enemy;
import com.jesttek.snakeWar.Enemies.Liner;
import com.jesttek.snakeWar.Enemies.Pulser;
import com.jesttek.snakeWar.Inferfaces.ISaveData.ControlType;

public class SinglePlayerScreen implements Screen{
	
	public static final int POWERUP_SIZE = 10; 
	public static final int POWERUP_SPEED = 0; 
	public static final int POWERUP_LENGTH = 1; 
	public static final int POWERUP_TIME = 2; 
	
	public static final int TIMER_POWERUP_COOLDOWN = 10; //how long it takes for timer powerup to respawn after collecting it.

	private static final int BOARDTOP = 300;
	private static final int BOARDBOTTOM = -300;
	private static final int BOARDLEFT = -500;
	private static final int BOARDRIGHT = 500;
	private static final int BOARDHEIGHT = BOARDTOP-BOARDBOTTOM;
	private static final int BOARDWIDTH = BOARDRIGHT-BOARDLEFT;
	private SnakeWarGame mGame;
	private Stage mStage;
	private Snake mSnake;
	private ArrayList<Rectangle> mObstacles = new ArrayList<Rectangle>();
	private ArrayList<Enemy> mEnemies = new ArrayList<Enemy>();
	private ArrayList<Particle> mParticles = new ArrayList<Particle>();
	private Powerup[] mPowerups = new Powerup[3];
	private boolean mBeingAttacked = false;
	private ShapeRenderer mShapeRenderer = new ShapeRenderer();
	private boolean mGameOver = false;
	private boolean mPaused = false;
	private Random mRandom = new Random();
	private float mScore;
	private ShaderLabel mScoreLabel;
	private ShaderLabel mStartCountdownLabel;
	private float mGameStartCountdown = 2.99f;
	private int mEnemyRound = 0;
	private float mEnemySpawnTime = 10;
	private float mEnemyTimer = mEnemySpawnTime;
	private float mTimePowerupTimer = TIMER_POWERUP_COOLDOWN;
	private float mTotalTime = 0;
	private int mKills = 0;
	private float mDamageMultiplier = 0.25f;
	private SinglePlayerEndPopup mGameOverPopup;
	private PausePopup mPausePopup;
	
	public SinglePlayerScreen(SnakeWarGame game) {
		mGame = game;
		mStage = new Stage(new FitViewport(SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT));	
		mStage.getCamera().position.x = 0;
		mStage.getCamera().position.y = 0;
		setupLayout();
		setupControls();	
		resetGame();
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private void resetGame() {
		mTotalTime = 0;
		mKills = 0;
		mDamageMultiplier = 0.25f;
		mGameStartCountdown = 2.99f;
		mEnemySpawnTime = 10;
		mEnemyTimer = mEnemySpawnTime;
		mTimePowerupTimer = TIMER_POWERUP_COOLDOWN;
		mEnemyRound = 0;
		mTotalTime = 0;
		mScore = 0;
		mPaused = false;
		mGameOver = false;
		mBeingAttacked = false;
		mParticles.clear();
		mEnemies.clear();
		mObstacles.clear();
		mSnake = new Snake(new Coordinate(0,-50), new Coordinate(0,-150), Direction.UP, 170);
		mPowerups[POWERUP_LENGTH] = new Powerup(new Coordinate(-150,-POWERUP_SIZE), PowerupType.IncreaseLength);
		mPowerups[POWERUP_SPEED] = new Powerup(new Coordinate(150,0-POWERUP_SIZE), PowerupType.IncreaseSpeed);
		mPowerups[POWERUP_TIME] = new Powerup(new Coordinate(0,150-POWERUP_SIZE), PowerupType.IncreaseTime);	
		mStartCountdownLabel.setColor(1, 1, 1, 1);
		mStage.addActor(mStartCountdownLabel);
	}
	
	private void setupLayout() {		
		mPausePopup = new PausePopup(SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.GameFont, new ClickListener() {				
			@Override
            public void clicked(InputEvent event, float x, float y ) {  
				SnakeWarGame.SoundPlayer.playButtonClick();     
				SnakeWarGame.AdController.show();
				mGame.setMainMenuScreen();
            }
		},
        new ClickListener() {				
			@Override
            public void clicked(InputEvent event, float x, float y ) { 
				SnakeWarGame.SoundPlayer.playButtonClick();      
            	mPaused = false;
				mPausePopup.dropOut();
            }
		});	
		mStage.addActor(mPausePopup);
		
		mGameOverPopup = new SinglePlayerEndPopup(SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.GameFont, new ClickListener() {				
			@Override
            public void clicked(InputEvent event, float x, float y ) {       
				SnakeWarGame.SoundPlayer.playButtonClick();
				resetGame();
				mGameOverPopup.dropOut();
            }
		},
        new ClickListener() {				
			@Override
            public void clicked(InputEvent event, float x, float y ) {      
				SnakeWarGame.SoundPlayer.playButtonClick(); 
				SnakeWarGame.AdController.show();
            	mGame.setMainMenuScreen();
            }
		});	
		mStage.addActor(mGameOverPopup);
		
		LabelStyle ls = new LabelStyle();
		ls.font = SnakeWarGame.GameFont.Font;
		ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		mStartCountdownLabel = new ShaderLabel("READY", ls, SnakeWarGame.GameFont.Shader);
		mStartCountdownLabel.setFontScale(1.875f);
		mStartCountdownLabel.setWidth(500);
		mStartCountdownLabel.setAlignment(Align.bottom);
		mStartCountdownLabel.setX(-mStartCountdownLabel.getWidth()/2);
		mStartCountdownLabel.setY(0-mStartCountdownLabel.getHeight()/2);
		mStartCountdownLabel.setTouchable(Touchable.disabled);
		mStage.addActor(mStartCountdownLabel);
		
		mScoreLabel = new ShaderLabel("0000000000", ls, SnakeWarGame.GameFont.Shader);
		mScoreLabel.setFontScale(1.875f);
		mScoreLabel.setWidth(500);
		mScoreLabel.setAlignment(Align.center);
		mScoreLabel.setX(-mScoreLabel.getWidth()/2);
		mScoreLabel.setY(SnakeWarGame.VIRTUAL_HEIGHT/2-mScoreLabel.getHeight());
		mStage.addActor(mScoreLabel);		
		mGameOverPopup.setZIndex(500);
		mPausePopup.setZIndex(500);
	}	

	private void setupControls() {
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(mStage);
		multiplexer.addProcessor(new InputAdapter () {
			@Override
			public boolean keyDown (int keyCode) {
				switch(keyCode) {
				case Keys.ESCAPE:
				case Keys.BACK:
					if(!mGameOver && !mPaused) {
						mPausePopup.dropIn();
						mPaused = true;
						SnakeWarGame.SoundPlayer.stopDamageLoop();
					}					
					break;
				case Keys.UP:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnake.setDirection(Direction.UP);
					}
					break;
				case Keys.DOWN:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnake.setDirection(Direction.DOWN);
					}
					break;
				case Keys.LEFT:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnake.setDirection(Direction.LEFT);
					}
					break;
				case Keys.RIGHT:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnake.setDirection(Direction.RIGHT);
					}
					break;
				}
				return true; // return true to indicate the event was handled	
			}
			
			@Override
			public boolean touchDown (int x, int y, int pointer, int button) {
				return false; // return true to indicate the event was handled
			}
			
			@Override
			public boolean touchUp (int x, int y, int pointer, int button) {
				if(SnakeWarGame.SaveController.getControlType() == ControlType.Tap) {
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						Vector3 touch = new Vector3();
						mStage.getCamera().unproject(touch.set(x, y, 0)); 
						Direction direction = mSnake.getDirection();
						if(direction == Direction.UP || direction == Direction.DOWN) {
							//moving vertically
							if(touch.x > 0) {
								mSnake.setDirection(Direction.RIGHT);
							}
							else {
								mSnake.setDirection(Direction.LEFT);
							}
						} 
						else {
							//moving horizontally
							if(touch.y < 0) {		
								mSnake.setDirection(Direction.DOWN);
							}
							else {
								mSnake.setDirection(Direction.UP);
							}
						}
					}
					return true; // return true to indicate the event was handled	
				}
				else {
					return false;
				}
			}
		});
		
		multiplexer.addProcessor(new GestureDetector(new GestureListener() {
			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean longPress(float x, float y) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean fling(float velocityX, float velocityY, int button) {
				if(SnakeWarGame.SaveController.getControlType() == ControlType.Swipe) {
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						if (Math.abs(velocityX) > Math.abs(velocityY)) {
							if(velocityX > 0) {
								mSnake.setDirection(Direction.RIGHT);						
							}
							else {
								mSnake.setDirection(Direction.LEFT);						
							}
						}
						else {
							if(velocityY > 0) {
								mSnake.setDirection(Direction.DOWN);						
							}
							else {
								mSnake.setDirection(Direction.UP);						
							}
						}
					}
					return true;
				}
				else {
					return false;
				}
			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean zoom(float initialDistance, float distance) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean pinch(Vector2 initialPointer1,
					Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
				// TODO Auto-generated method stub
				return false;
			}
			
		}));
		
		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(true);
	}	
	
	/**
	 * @return true if the countdown is complete
	 */
	private boolean doCountdown(float delta) {
		if(mGameStartCountdown <= 0) {	
			if(mGameStartCountdown > -1) {	
				mStartCountdownLabel.setText("GO");
				mGameStartCountdown -= delta;
				mStartCountdownLabel.setFontScale(-mGameStartCountdown*2);
				float fade = 1+mGameStartCountdown;
				mStartCountdownLabel.setColor(fade, fade, fade, fade);
			} 
			else {
				mStartCountdownLabel.remove();
			}
			return true;			
		}
		else {
			int last = (int)(mGameStartCountdown+1); //add 1 to avoid same values for positive 0 and negative 0.
			mGameStartCountdown -= delta;
			if(last != (int)(mGameStartCountdown+1)) {
				SnakeWarGame.SoundPlayer.playCountdownTone((int)(mGameStartCountdown+1));
			}
			if(mGameStartCountdown > 3) {
				mStartCountdownLabel.setText("READY");					
			} 
			else {
				mStartCountdownLabel.setText(((int)mGameStartCountdown+1) + "");
				mStartCountdownLabel.setFontScale((1-(mGameStartCountdown-(int)mGameStartCountdown))*2);
			}
			return false;
		}
	}
	
	private void doGameOver() {
		mGameOverPopup.setResults((int)mScore, mKills, mTotalTime);
		mGameOverPopup.dropIn();
		mGameOver = true;
		SnakeWarGame.SoundPlayer.stopDamageLoop();
		SnakeWarGame.SoundPlayer.playGameOver();
		if(SnakeWarGame.PlayServices.isSignedIn()) {
			SnakeWarGame.PlayServices.submitScore((int)mScore);
		}
		
		if(mKills >= 30) {
			SnakeWarGame.PlayServices.unlockAchievement(SnakeWarGame.ACHIEVEMENT_ID_KILLER);		
		}
		
		if(mTotalTime >= 300) {
			SnakeWarGame.PlayServices.unlockAchievement(SnakeWarGame.ACHIEVEMENT_ID_SURVIVOR);		
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);	
		mScoreLabel.setText(TextUtil.intToZeroPaddedString((int)mScore, 8));
		if(!mGameOver && !mPaused) {
			if(doCountdown(delta)) {	
				mTotalTime+=delta;
				mScore+=(delta*mDamageMultiplier*50);				
				float time = mSnake.getTimeRemaining();		
				
				if(time < 0) {			
					doGameOver();
				}
				
				mSnake.act(delta);
				for(Powerup p:mPowerups) {
					p.act(delta);
					if(p.checkPickup(mSnake.getHeadPosition())) {
						SnakeWarGame.SoundPlayer.playPowerupPickup(p.getPowerupType());
						mScore+=(1000*mDamageMultiplier);
						p.applyEffect(mSnake);
						if(p.getPowerupType() == PowerupType.IncreaseTime) {
							createHealthParticles(p.getPosition(),11);
						}
						p.clearPosition();
					}
				}
				
				for(int i = 0; i < mParticles.size(); i++) {
					Particle p = mParticles.get(i);
					p.act(delta);
					if(p.isDone()) {
						SnakeWarGame.SoundPlayer.playHealthCollect();
						mParticles.remove(i);
						if(mSnake.getTimeRemaining() <= 0.2f) {
							SnakeWarGame.PlayServices.unlockAchievement(SnakeWarGame.ACHIEVEMENT_ID_CLOSE_CALL);
						}
						mSnake.increaseTimeRemaining(1.1f);
						i--;
					}
				}
				
				placePowerups(delta);				
				spawnEnemies(delta);
				checkEnemyHits(delta);
				
				if(checkCollisions(delta)) {				
					doGameOver();
				}
				
				Rectangle newObstacle = mSnake.createObstacle();				
				if(newObstacle != null) {
					killEnemies(newObstacle);
					mObstacles.add(newObstacle);
					SnakeWarGame.SoundPlayer.playObstacleCreate();
				}		

				for(Enemy e: mEnemies) {
					e.act(delta);
				}
			}			
		} 

		drawBoard();		

		mStage.act();
		mStage.draw();		
	}
	
	/**
	 * Check if enemies were killed by the object created
	 * @param obstacle
	 */
	private void killEnemies(Rectangle obstacle) {
		float multiKillBonus = 1;
		int count = 0;
		for(int i = 0; i < mEnemies.size(); i++) {
			Enemy e = mEnemies.get(i);
			if(((obstacle.x <= e.Position.X) && (obstacle.x + obstacle.width >= e.Position.X)) &&
					((obstacle.y <= e.Position.Y) && (obstacle.y + obstacle.height >= e.Position.Y))) {
				count++;
				mKills++;
				SnakeWarGame.SoundPlayer.playEnemyKill();
				i--;			
				float particles = (e.kill()*(Math.min(1.5f,multiKillBonus)))/2;
				mScore+=(1000*multiKillBonus*mDamageMultiplier);	
				multiKillBonus+=0.5;
				mEnemies.remove(e);
				
				//create the particles for health
				createHealthParticles(e.Position, (int)particles);
			}						
		}
		if(count >= 5) {
			SnakeWarGame.PlayServices.unlockAchievement(SnakeWarGame.ACHIEVEMENT_ID_MULTIKILL);
		}
	}
	
	private void createHealthParticles(Coordinate origin, int count) {
		int barLength = (int)(BOARDWIDTH*(mSnake.getTimeRemaining()/80));
		//if barlength is 0 it's game over (or about to be)
		if(barLength > 0) {
			for(int i = 0; i < count; i++) {
				float x = mRandom.nextInt(barLength)-barLength/2;
				float y = BOARDBOTTOM-25;	
				mParticles.add(new Particle(new Coordinate(origin), new Coordinate(x,y), ((float)mRandom.nextInt(20))/10));	
			}
		}
	}
	
	/**
	 * Check if enemies need to be spawned then spawns them
	 * @param delta time since last frame
	 */
	private void spawnEnemies(float delta) {
		mEnemyTimer -= delta;
		if(mEnemyTimer <= 0) {
			if(mEnemies.size() < 10) {
				mEnemyTimer = mEnemySpawnTime;
				mEnemyRound++;
	
				if(mEnemyRound%3 != 0) {
					if(mEnemySpawnTime > 5) {
						mEnemySpawnTime -= 0.3f;
					}
					if(mDamageMultiplier < 1.05) {
						mDamageMultiplier += 0.05;
					}
					Coordinate position = new Coordinate();
					int placement = mRandom.nextInt(5);
					switch(placement) {
						case 0:
							position = new Coordinate(0,0);
							break;
						case 1:
							position = new Coordinate(BOARDLEFT+BOARDWIDTH/4,BOARDTOP-BOARDHEIGHT/4);
							break;
						case 2:
							position = new Coordinate(BOARDRIGHT-BOARDWIDTH/4,BOARDTOP-BOARDHEIGHT/4);
							break;
						case 3:
							position = new Coordinate(BOARDLEFT+BOARDWIDTH/4,BOARDBOTTOM+BOARDHEIGHT/4);
							break;
						case 4:
							position = new Coordinate(BOARDRIGHT-BOARDWIDTH/4,BOARDBOTTOM+BOARDHEIGHT/4);
							break;
					}
					Chaser c = new Chaser(position,10, mSnake.getSpeed()*0.97f, mSnake.getHeadPosition(), mObstacles);
					mEnemies.add(c);				
					SnakeWarGame.SoundPlayer.playEnemySpawn();
				} 
				else {	
					//minor enemy group
					Enemy enemy = new Enemy();
					
					//select enemy type
					int next = mRandom.nextInt(3);
					switch(next) {
					case 0:
						boolean direction = mRandom.nextBoolean();
						enemy = new Liner(new Coordinate(), direction);				
						break;
					case 1:
						int pulseSize = mRandom.nextInt(100)+50;
						enemy = new Pulser(new Coordinate(), pulseSize);
						break;
					case 2:
						int arcSize = mRandom.nextInt(250)+50;
						enemy = new Arcer(new Coordinate(), arcSize);
						break;
					}
					
					//select enemy placement
					float x = mRandom.nextInt(BOARDWIDTH-80)+40;
					float y = mRandom.nextInt(BOARDHEIGHT-80)+40;	
					enemy.Position.X = BOARDLEFT+x;
					enemy.Position.Y = BOARDTOP-y;
					mEnemies.add(enemy);	
					SnakeWarGame.SoundPlayer.playEnemySpawn();		
				}		
			}
			else {
				mEnemyTimer = mEnemySpawnTime*0.8f;
			}
		}
	}
	
	private void drawBoard() {
		Gdx.gl20.glLineWidth(2);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		
		//draw obstacles created
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(0.75f,0.75f,1,1);
		for(int i = 0; i < mObstacles.size(); i++) {
			Rectangle r= mObstacles.get(i);
			mShapeRenderer.rect(r.x, r.y, r.width, r.height);
		}		
		mShapeRenderer.end();
		
		//draw enemies
		for(Enemy e:mEnemies) {
			e.draw(mShapeRenderer);
		}
		
		//draw snake
		mSnake.draw(mShapeRenderer);
	
		//draw powerups	if they've been placed
		for(Powerup p:mPowerups) {
			p.draw(mShapeRenderer);
		}
		
		//draw border
		mShapeRenderer.begin(ShapeType.Line);
		mShapeRenderer.setColor(Color.WHITE);
		mShapeRenderer.rect(BOARDLEFT, BOARDBOTTOM, BOARDWIDTH, BOARDHEIGHT);
		mShapeRenderer.end();		
		
		//blackout outside of the game boundary as an easy way to stop enemies being drawn outside of the boundary
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(Color.BLACK);
		mShapeRenderer.rect(-SnakeWarGame.VIRTUAL_WIDTH/2, BOARDTOP, SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT/2-BOARDTOP); //top
		mShapeRenderer.rect(-SnakeWarGame.VIRTUAL_WIDTH/2, -SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.VIRTUAL_WIDTH/2+BOARDLEFT-1, SnakeWarGame.VIRTUAL_HEIGHT); //left
		mShapeRenderer.rect(BOARDRIGHT, -SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.VIRTUAL_WIDTH/2-BOARDLEFT, SnakeWarGame.VIRTUAL_HEIGHT); //right
		mShapeRenderer.rect(-SnakeWarGame.VIRTUAL_WIDTH/2, -SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT/2-BOARDTOP-1); //bottom
		mShapeRenderer.end();	
		
		//draw the healthbar
		if(mSnake.getTimeRemaining() > 0) {
			if(mBeingAttacked) {
				mShapeRenderer.setColor(Color.RED);	
				SnakeWarGame.SoundPlayer.startDamageLoop();
			} 
			else {
				SnakeWarGame.SoundPlayer.stopDamageLoop();
				mShapeRenderer.setColor(1,0.75f,0.75f,1);				
			}
			mShapeRenderer.begin(ShapeType.Filled);
			float healthBarLength=(BOARDWIDTH*(mSnake.getTimeRemaining()/80));
			mShapeRenderer.rect(0-healthBarLength/2, BOARDBOTTOM-25, healthBarLength, 20);
			mShapeRenderer.end();	
		}
		
		//draw the particles
		mShapeRenderer.begin(ShapeType.Filled);
		for(Particle p : mParticles) {
			mShapeRenderer.setColor(1, 1, 1, p.getAlpha());
			mShapeRenderer.circle(p.getPosition().X, p.getPosition().Y, 3);		
		}
		mShapeRenderer.end();	
		Gdx.gl20.glDisable(GL20.GL_BLEND);		
	}
	
	/**
	 * If powerups need to be placed puts them on the board at random position. Won't place them inside obstacles
	 * @param delta time since last frame
	 */
	private void placePowerups(float delta) {
		if(!mPowerups[POWERUP_TIME].isPlaced()) {
			mTimePowerupTimer-=delta;
		}
		
		if(!mPowerups[POWERUP_LENGTH].isPlaced() || !mPowerups[POWERUP_SPEED].isPlaced()) {
			Coordinate[] positions = new Coordinate[2];
			
			//try 10 times to find a spot to place both powerups.
			for(int i = 0; i < 10; i++) {
				int x = mRandom.nextInt(BOARDWIDTH-2*POWERUP_SIZE)+BOARDLEFT+POWERUP_SIZE;
				int y = mRandom.nextInt(BOARDHEIGHT-2*POWERUP_SIZE)+BOARDBOTTOM+POWERUP_SIZE;
				boolean validPosition = true;
				for(Rectangle obstacle: mObstacles) {
					if((x > obstacle.x && x < obstacle.x+obstacle.width) && (y > obstacle.y && y < obstacle.y+obstacle.height)){
						//powerup is inside of this obstacle
						validPosition = false;
						break;
					}
				}
				if(validPosition) {
					if(positions[0] == null) {
						positions[0] = new Coordinate(x,y);
					} 
					else {
						positions[1] = new Coordinate(x,y);		
						break;
					}
				}
			}
			if(positions[0] != null && positions[1] != null) {
				mPowerups[POWERUP_LENGTH].setPosition(positions[0]);				
				mPowerups[POWERUP_SPEED].setPosition(positions[1]);				
			}
		}
		
		//try to find a spot and place timer powerup if required
		if(!mPowerups[POWERUP_TIME].isPlaced() && (mTimePowerupTimer <= 0)) {
			for(int i = 0; i < 10; i++) {
				int x = mRandom.nextInt(BOARDWIDTH-2*POWERUP_SIZE)+BOARDLEFT+POWERUP_SIZE;
				int y = mRandom.nextInt(BOARDHEIGHT-2*POWERUP_SIZE)+BOARDBOTTOM+POWERUP_SIZE;
				boolean validPosition = true;
				for(Rectangle obstacle: mObstacles) {
					if((x > obstacle.x && x < obstacle.x+obstacle.width) && (y > obstacle.y && y < obstacle.y+obstacle.height)){
						//powerup is inside of this obstacle
						validPosition = false;
						break;
					}
				}
				if(validPosition) {
					mPowerups[POWERUP_TIME].setPosition(new Coordinate(x,y));
					mTimePowerupTimer = TIMER_POWERUP_COOLDOWN;
					break;
				}
			}
		}
	}
	
	/**
	 * Check if player is being hit by enemies and removes time remaining if they are.
	 * @param delta time since last frame
	 */
	private void checkEnemyHits(float delta) {
		mBeingAttacked = false;
		Coordinate head = mSnake.getHeadPosition();
		
		for(Enemy e: mEnemies) {
			if(e.checkCollision(head)) {
				mSnake.decreaseTimeRemaining(delta*e.getAttackStrength()*mDamageMultiplier);
				mBeingAttacked = true;
			}
		}
	}
	
	private boolean checkCollisions(float delta) {		
		Coordinate head = mSnake.getHeadPosition();
		
		//with wall
		if(head.X <= BOARDLEFT || head.X >= BOARDRIGHT) {
			return true;
		} 
		else if(head.Y <= BOARDBOTTOM || head.Y >= BOARDTOP) {
			return true;			
		}
		
		//with obstacles
		Coordinate lastTurn = mSnake.getSnakePoint(1);
		for(int i = 0 ; i < mObstacles.size(); i++) {
			Rectangle r = mObstacles.get(i);
			if((head.X > r.x && head.X < r.x+r.width) && (head.Y > r.y && head.Y < r.y+r.height)) {
				mBeingAttacked = true;
				float damage = 1;
				//damage = area removed
				switch(mSnake.getDirection()) {
					case LEFT:
						damage = ((r.x+r.width)-head.X)*r.height;
						r.width = head.X - r.x;
						break;
					case RIGHT:
						damage = (head.X-r.x)*r.height;
						r.width = (r.x+r.width) - head.X;
						r.x = head.X;
						break;
					case UP:
						damage = (head.Y-r.y)*r.width;		
						r.height = (r.y+r.height) - head.Y;
						r.y = head.Y;				
						break;
					case DOWN:
						damage = ((r.y+r.height)-head.Y)*r.width;
						r.height = head.Y - r.y;					
						break;
				}
				mSnake.decreaseTimeRemaining(delta*(damage*0.5f)*mDamageMultiplier);
			}
			else {
				//check if we "jumped" an obstacle (moving too fast to hit it)
				if(head.Y > r.y && head.Y < r.y + r.height-0.01) {
					if(((head.X > r.x) && (lastTurn.X < r.x)) || 
						((head.X < r.x) && (lastTurn.X > r.x))) {
						mObstacles.remove(i);
						i--;	
					}	
				}

				if(head.X > r.x && head.X < r.x + r.width-0.01) {
					if(((head.Y > r.y) && (lastTurn.Y < r.y)) || 
						((head.Y < r.y) && (lastTurn.Y > r.y))) {
						mObstacles.remove(i);
						i--;			
					}
				}
			}			
		}	
		
		return false;
	}

	@Override
	public void resize(int width, int height) {
		mStage.getViewport().update( width,  height);
		mShapeRenderer.setProjectionMatrix(mStage.getCamera().combined);		
	}

	@Override
	public void show() {
		SnakeWarGame.AdController.load();	
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub			
	}

	@Override
	public void pause() {
		if(!mGameOver && !mPaused) {
			mPausePopup.dropIn();
			mPaused = true;
			SnakeWarGame.SoundPlayer.stopDamageLoop();
		}			
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void dispose() {
		mStage.dispose();		
	}
}
