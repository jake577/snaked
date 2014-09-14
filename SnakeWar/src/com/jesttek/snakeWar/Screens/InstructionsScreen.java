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
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.Particle;
import com.jesttek.snakeWar.Powerup;
import com.jesttek.snakeWar.TextUtil;
import com.jesttek.snakeWar.Powerup.PowerupType;
import com.jesttek.snakeWar.Snake;
import com.jesttek.snakeWar.Snake.Direction;
import com.jesttek.snakeWar.SnakeWarGame;
import com.jesttek.snakeWar.Controls.ShaderLabel;
import com.jesttek.snakeWar.Controls.ShaderTextButton;
import com.jesttek.snakeWar.Enemies.Chaser;
import com.jesttek.snakeWar.Enemies.Enemy;
import com.jesttek.snakeWar.Enemies.Liner;
import com.jesttek.snakeWar.Enemies.Pulser;

public class InstructionsScreen implements Screen{
	
	public static final int POWERUP_SIZE = 10; 
	public static final int POWERUP_SPEED = 0; 
	public static final int POWERUP_LENGTH = 1; 
	public static final int POWERUP_TIME = 2; 
	
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
	private Enemy[] mEnemies = new Enemy[2];
	private ArrayList<Particle> mParticles = new ArrayList<Particle>();
	private Powerup[] mPowerups = new Powerup[3];
	private boolean mBeingAttacked = false;
	private ShapeRenderer mShapeRenderer = new ShapeRenderer();
	private float mScore = 0;
	private int mStep = 0;
	private Random mRandom = new Random();
	private float mTimePowerupTimer = 7;
	private float mEnemyTimer = 10;

	private ShaderLabel mScoreLabel;
	private ShaderTextButton mNextButton;
	private ShaderTextButton mPreviousButton;
	private Image mInstructionsImage;
	private ShaderLabel mInstructionsLabel;
	
	public InstructionsScreen(SnakeWarGame game) {
		mGame = game;
		mStage = new Stage(new FitViewport(SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT));	
		mStage.getCamera().position.x = 0;
		mStage.getCamera().position.y = 0;
		setupLayout();
		setupControls();	
		setupButtons();	
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);		
	}	

	private void setupButtons() {
		mNextButton.addListener( new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y )
            {     
            	SnakeWarGame.SoundPlayer.playButtonClick();
                mStep++; 
                showInstruction();
            }
        } );
		
		mPreviousButton.addListener( new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y )
            {     
            	SnakeWarGame.SoundPlayer.playButtonClick();
            	if(mStep > 0) {
	                mStep--;   
	                showInstruction();
            	}
            }
        } );
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
					mGame.setMainMenuScreen();
					break;
				}
				return true;
			}
		});				
		
		Gdx.input.setInputProcessor(multiplexer);
		Gdx.input.setCatchBackKey(true);		
	}	
	
	private void setupLayout() {			
		mSnake = new Snake(new Coordinate(0,-50), new Coordinate(0,-150), Direction.UP, 350);
		mPowerups[POWERUP_LENGTH] = new Powerup(new Coordinate(-150,-POWERUP_SIZE), PowerupType.IncreaseLength);
		mPowerups[POWERUP_SPEED] = new Powerup(new Coordinate(150,0-POWERUP_SIZE), PowerupType.IncreaseSpeed);
		mPowerups[POWERUP_TIME] = new Powerup(new Coordinate(0,150-POWERUP_SIZE), PowerupType.IncreaseTime);
		
		LabelStyle lsScore = new LabelStyle();
		lsScore.font = SnakeWarGame.GameFont.Font;//SnakeWarGame.FontLoader.getFont(60);
		lsScore.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);		
		
		mScoreLabel = new ShaderLabel("0000000000", lsScore, SnakeWarGame.GameFont.Shader);
		mScoreLabel.setWidth(500);
		mScoreLabel.setAlignment(Align.center);
		mScoreLabel.setX(-mScoreLabel.getWidth()/2);
		mScoreLabel.setY(SnakeWarGame.VIRTUAL_HEIGHT/2-mScoreLabel.getHeight());
		mScoreLabel.setFontScale(1.875f);

		NinePatchDrawable background = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("buttonBackground")),6,6,6,6));
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = SnakeWarGame.GameFont.Font;
		buttonStyle.up = background;
		mNextButton = new ShaderTextButton("Next",buttonStyle, SnakeWarGame.GameFont.Shader);
		mNextButton.setWidth(250);
		mNextButton.setY(BOARDBOTTOM-mNextButton.getHeight()-10);
		mNextButton.setX(BOARDRIGHT-125);
		mNextButton.align(Align.center);
		mNextButton.getLabel().setFontScale(1.3125f);
		mNextButton.getLabel().setAlignment(Align.bottom);
		mPreviousButton = new ShaderTextButton("Prev",buttonStyle, SnakeWarGame.GameFont.Shader);
		mPreviousButton.setWidth(250);
		mPreviousButton.setY(BOARDBOTTOM-mNextButton.getHeight()-10);
		mPreviousButton.setX(BOARDLEFT-125);
		mPreviousButton.align(Align.center);
		mPreviousButton.getLabel().setFontScale(1.3125f);
		mPreviousButton.getLabel().setAlignment(Align.bottom);
		
		mInstructionsImage = new Image(SnakeWarGame.TextureAtlas.findRegion("Instructions1"));
		mInstructionsImage.setX(-SnakeWarGame.VIRTUAL_WIDTH/2);
		mInstructionsImage.setY(-SnakeWarGame.VIRTUAL_HEIGHT/2);

		LabelStyle lsInstruction = new LabelStyle();
		lsInstruction.font = SnakeWarGame.GameFont.Font;//SnakeWarGame.FontLoader.getFont(24);
		lsInstruction.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		mInstructionsLabel = new ShaderLabel("instructions\ninstructions\ninstructions", lsInstruction, SnakeWarGame.GameFont.Shader);
		mInstructionsLabel.setWidth(500);
		mInstructionsLabel.setAlignment(Align.center);
		mInstructionsLabel.setX(-250);
		mInstructionsLabel.setY(BOARDBOTTOM);
		mInstructionsLabel.setFontScale(0.75f);
		mInstructionsImage.setTouchable(Touchable.disabled);
		
		mStage.addActor(mNextButton);	
		mStage.addActor(mPreviousButton);	
		mStage.addActor(mScoreLabel);		
		mStage.addActor(mInstructionsImage);
		mInstructionsImage.setZIndex(550);
	}	
	
	private void showInstruction() {
		switch(mStep) {
			case 0:
				mSnake = new Snake(new Coordinate(0,-50), new Coordinate(0,-150), Direction.UP, 350);
				mInstructionsImage.setDrawable(new TextureRegionDrawable(SnakeWarGame.TextureAtlas.findRegion("Instructions1")));
				mStage.addActor(mInstructionsImage);
				mInstructionsLabel.remove();
				mParticles.clear();
				mObstacles.clear();
				break;
			case 1:
				mEnemies[0] = null;
				mEnemies[1] = null;
				mObstacles.clear();
				mParticles.clear();
				mSnake = new Snake(new Coordinate(0,-50), new Coordinate(0,-150), Direction.UP, 350);
				mSnake.setSpeed(150);
				mSnake.setDirection(Direction.LEFT);
				mPowerups[POWERUP_LENGTH] = new Powerup(new Coordinate(-150,-POWERUP_SIZE), PowerupType.IncreaseLength);
				mPowerups[POWERUP_SPEED] = new Powerup(new Coordinate(150,0-POWERUP_SIZE), PowerupType.IncreaseSpeed);
				mPowerups[POWERUP_TIME] = new Powerup(new Coordinate(0,150-POWERUP_SIZE), PowerupType.IncreaseTime);
				mInstructionsImage.remove();
				mInstructionsLabel.setText("You can move horizontal or vertical. Two control types can be set in the settings\n"+
											"Swipe - swipe in the direction you want the snake to move\n"+
											"Tap - Tap the side of the screen you want the snake to move towards");
				mStage.addActor(mInstructionsLabel);
				break;
			case 2:
				mEnemies[0] = null;
				mEnemies[1] = null;
				mObstacles.clear();
				mParticles.clear();
				mInstructionsLabel.setText("Enemies appear at regular intervals. You lose health faster while touching them.\n"+
						"Trace a square around them to kill them, increasing your health and score.\n"+
						"This also creates a square that will damage you when you move through it\n");	
				createEnemies();
				mNextButton.setText("Next");
				mInstructionsImage.remove();
				mStage.addActor(mScoreLabel);
				break;
			case 3:
				mEnemies[0] = null;
				mEnemies[1] = null;
				mObstacles.clear();
				mParticles.clear();
				mScoreLabel.remove();
				mInstructionsImage.setDrawable(new TextureRegionDrawable(SnakeWarGame.TextureAtlas.findRegion("Instructions2")));
				mStage.addActor(mInstructionsImage);
				mInstructionsLabel.setText("This is just you VS an opponent with no other enemies\n"+
						"In multiplayer you try to get your opponent to hit your squares.\n"+
						"Unlike single player, hitting squares will be an instant game over\n");
				mNextButton.setText("Done");
				break;
			case 4:
				mGame.setMainMenuScreen();				
				break;
		}
	}
	
	public void createEnemies() {
		if(mStep == 2) {
			boolean mCreated = false;
			if(mEnemies[0] == null) {
				Chaser c = new Chaser(new Coordinate(0,0),10, mSnake.getSpeed(), mSnake.getHeadPosition(), mObstacles);	
				mEnemies[0] = c;
				mCreated = true;
			}
			if(mEnemies[1] == null) {
				if(mRandom.nextBoolean()) {
					Liner l = new Liner(new Coordinate(170,130), true);
					mEnemies[1] = l;
				}
				else {
					Pulser p = new Pulser(new Coordinate(-170,-30), 50);
					mEnemies[1] = p;
				}
				mCreated = true;
			}		
			if(mCreated) {
				SnakeWarGame.SoundPlayer.playEnemySpawn();
			}
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);		
		mScoreLabel.setText(TextUtil.intToZeroPaddedString((int)mScore, 8));

		if(mStep != 3) {
			mEnemyTimer-=delta;
			if(mEnemyTimer <= 0) {
				mEnemyTimer = 15;
				createEnemies();
			}
			
			for(Powerup p:mPowerups) {
				p.act(delta);
				if(p.checkPickup(mSnake.getHeadPosition())) {
					mScore+=(100);
					p.applyEffect(mSnake);
					SnakeWarGame.SoundPlayer.playPowerupPickup(p.getPowerupType());
					if(p.getPowerupType() == PowerupType.IncreaseTime) {
						createHealthParticles(p.getPosition(),11);
					}
					p.clearPosition();
				}
			}
			
			//re-place powerups if they've been picked up
			if(!mPowerups[POWERUP_LENGTH].isPlaced() || !mPowerups[POWERUP_LENGTH].isPlaced()) {
				mPowerups[POWERUP_LENGTH] = new Powerup(new Coordinate(-350,-50), PowerupType.IncreaseLength);
				mPowerups[POWERUP_SPEED] = new Powerup(new Coordinate(300,210), PowerupType.IncreaseSpeed);			
			}
	
			if(!mPowerups[POWERUP_TIME].isPlaced()) {
				mTimePowerupTimer-=delta;
				if(mTimePowerupTimer <= 0) {
					mPowerups[POWERUP_TIME].setPosition(new Coordinate(0,150-POWERUP_SIZE));
					mTimePowerupTimer = 7f;
				}
			}
			
			Rectangle newObstacle = mSnake.createObstacle();				
			if(newObstacle != null) {
				killEnemies(newObstacle);
				mObstacles.add(newObstacle);
			}		
		}
		
		drawBoard();
		checkEnemyHits(delta);
		checkCollisions(delta);
		animate(delta);
		mStage.act();
		mStage.draw();
	}
	
	/**
	 * Check if enemies were killed by the object created
	 * @param obstacle
	 */
	private void killEnemies(Rectangle obstacle) {
		float multiKillBonus = 1;
		for(int i = 0; i < mEnemies.length; i++) {
			if(mEnemies[i] != null) {
				if(((obstacle.x <= mEnemies[i].Position.X) && (obstacle.x + obstacle.width >= mEnemies[i].Position.X)) &&
						((obstacle.y <= mEnemies[i].Position.Y) && (obstacle.y + obstacle.height >= mEnemies[i].Position.Y))) {	
					SnakeWarGame.SoundPlayer.playEnemyKill();
					float particles = (mEnemies[i].kill()*(Math.min(2,multiKillBonus)))/2;
					mScore+=(100*multiKillBonus);	
					multiKillBonus+=0.5;
					
					//create the particles for health
					createHealthParticles(mEnemies[i].Position, (int)particles);
					mEnemies[i] = null;
				}	
			}
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
	
	private void attackEnemy() {
		switch(mSnake.getDirection()) {
		case UP:					
			if(mSnake.getHeadPosition().Y >= mEnemies[1].Position.Y + 40) {
				mSnake.setDirection(Direction.RIGHT);
			}
			break;
		case DOWN:
			if(mSnake.getHeadPosition().Y <= mEnemies[1].Position.Y - 45) {
				mSnake.setDirection(Direction.LEFT);
			}
			break;
		case LEFT:
			if(mSnake.getHeadPosition().X <= mEnemies[1].Position.X - 20) {
				mSnake.setDirection(Direction.UP);
			}
			break;
		case RIGHT:
			if(mSnake.getHeadPosition().X >= mEnemies[1].Position.X + 20) {
				mSnake.setDirection(Direction.DOWN);
			}
			break;
		}
	}
	
	private void animate(float delta) {
		if(mStep > 0) {
			//kill and respawn snake if it runs out of time
			if(mSnake.getTimeRemaining() <= 0) {
				mSnake.setLength(mSnake.getLength()-2);
				mSnake.setSpeed(0);
				if(mSnake.getLength() <= 0) {
					mEnemies[0] = null;
					mEnemies[1] = null;
					mSnake = new Snake(new Coordinate(0,-50), new Coordinate(0,-150), Direction.UP, 350);
					mSnake.setSpeed(150);
					mSnake.setDirection(Direction.LEFT);
				}
			}

			if(mEnemies[1] != null) {
				attackEnemy();
			}
			else {
				switch(mSnake.getDirection()) {
				case UP:					
					if(mSnake.getHeadPosition().Y >= 140) {
						mSnake.setDirection(Direction.RIGHT);
					}
					break;
				case DOWN:
					if(mSnake.getHeadPosition().Y <= -50) {
						mSnake.setDirection(Direction.LEFT);
					}
					break;
				case LEFT:
					if(mSnake.getHeadPosition().X <= -200) {
						mSnake.setDirection(Direction.UP);
					}
					break;
				case RIGHT:
					if(mSnake.getHeadPosition().X >= 200) {
						mSnake.setDirection(Direction.DOWN);
					}
					break;
				}
			}
			
			mSnake.act(delta);	
			for(Enemy e: mEnemies) {
				if(e != null) {
					e.act(delta);
				}
			}
			
			for(int i = 0; i < mParticles.size(); i++) {
				Particle p = mParticles.get(i);
				p.act(delta);
				if(p.isDone()) {
					SnakeWarGame.SoundPlayer.playHealthCollect();
					mParticles.remove(i);
					if(mSnake.getTimeRemaining() > 0) {
						mSnake.increaseTimeRemaining(0.5f);
					}
					i--;
				}
			}
		}		
	}
	
	private void drawBoard() {
		Gdx.gl20.glLineWidth(2);
		Gdx.gl20.glEnable(GL20.GL_BLEND);

		
		//draw border
		mShapeRenderer.begin(ShapeType.Line);
		mShapeRenderer.setColor(Color.WHITE);
		mShapeRenderer.rect(BOARDLEFT, BOARDBOTTOM, BOARDWIDTH, BOARDHEIGHT);
		mShapeRenderer.end();
		if(mStep != 3) {
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
				if(e != null) {
					e.draw(mShapeRenderer);
				}
			}
			
			//draw snake
			mSnake.draw(mShapeRenderer);
		
			//draw powerups	if they've been placed
			for(Powerup p:mPowerups) {
				p.draw(mShapeRenderer);
			}		
			
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
				} 
				else {
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
		}
		Gdx.gl20.glDisable(GL20.GL_BLEND);		
	}	

	/**
	 * Check if player is being hit by enemies and removes time remaining if they are.
	 * @param delta time since last frame
	 */
	private void checkEnemyHits(float delta) {
		mBeingAttacked = false;
		Coordinate head = mSnake.getHeadPosition();
		
		for(Enemy e: mEnemies) {
			if(e != null && e.checkCollision(head)) {
				mSnake.decreaseTimeRemaining(delta*e.getAttackStrength());
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
				mSnake.decreaseTimeRemaining(delta*damage/10);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
