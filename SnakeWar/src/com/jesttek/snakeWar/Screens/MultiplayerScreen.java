package com.jesttek.snakeWar.Screens;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jesttek.snakeWar.Coordinate;
import com.jesttek.snakeWar.Powerup;
import com.jesttek.snakeWar.Powerup.PowerupType;
import com.jesttek.snakeWar.Snake;
import com.jesttek.snakeWar.Snake.Direction;
import com.jesttek.snakeWar.SnakeWarGame;
import com.jesttek.snakeWar.Controls.MultiplayerEndPopup;
import com.jesttek.snakeWar.Controls.Popup;
import com.jesttek.snakeWar.Controls.ShaderLabel;
import com.jesttek.snakeWar.Inferfaces.IMoveReceiver;
import com.jesttek.snakeWar.Inferfaces.ISaveData.ControlType;

public class MultiplayerScreen implements Screen, IMoveReceiver{

	private class NetMessage {
	    public int ID = -1;
	    public boolean Sending = false;
	    public final byte[] Message;
	    
	    public NetMessage(byte[] message) {
	    	Message = message;
	    }  
	}
	
	private enum EndGameType {
		Win,
		Lose,
		Forfeit,
		NetworkError
	}
	
	public final static int MSG_MOVE = 7;
	public final static int MSG_POWERUP = 9;
	public final static int MSG_POWERUP_POSITIONS = 10;
	public final static int MSG_GAME_OVER = 11;
	public final static int MSG_GAME_LEFT = 12;
	
	public static final int POWERUP_SIZE = 10; 	
	public static final int POWERUP_SPEED = 0; 
	public static final int POWERUP_LENGTH = 1; 
	public static final int POWERUP_OPPONENT_SPEED = 2; 
	
	//How often to send updates to opponent
	public static final float UPDATE_RATE = 0.25f; 
	
	//The maximum amount of time we will wait for an opponent, before assuming they've been disconnected
	public static final float OPPONENT_TIMEOUT = 5; 

	private static final int BOARDTOP = 300;
	private static final int BOARDBOTTOM = -300;
	private static final int BOARDLEFT = -500;
	private static final int BOARDRIGHT = 500;
	private static final int BOARDHEIGHT = BOARDTOP-BOARDBOTTOM;
	private static final int BOARDWIDTH = BOARDRIGHT-BOARDLEFT;
	
	private SnakeWarGame mGame;
	private Stage mStage;
	private Snake mSnakeLocal;
	private ArrayList<Coordinate> mSnakeRemote = new ArrayList<Coordinate>();
	private ArrayList<Rectangle> mObstaclesLocal = new ArrayList<Rectangle>();
	private ArrayList<Rectangle> mObstaclesRemote = new ArrayList<Rectangle>();
	private Powerup[] mPowerupsLocal = new Powerup[3];
	private Powerup[] mPowerupsRemote = new Powerup[3];
	private float mGameStartCountdown = 2.99f;
	private ShapeRenderer mShapeRenderer = new ShapeRenderer();
	private boolean mGameOver = false;
	private boolean mWaitingForOpponent = true;
	private float mMultiplayerUpdateTimer = 0;
	private Random mRandom = new Random();
	private boolean mReady = false;
	private ShaderLabel mCountdownLabel;
	private Popup mGameStartPopup;
	private float mOpponentHeartbeat = 0;
	private ArrayList<NetMessage> mMessageQueue = new ArrayList<NetMessage>();
	private ShaderLabel mInfoLabel;
	private float mBackTimeout = 2; //used to stop user from accidentally pressing back to leave game
	private Rectangle mLastObstacleCreated = null;
	
	public MultiplayerScreen(SnakeWarGame game)
	{
		mGame = game;
		mStage = new Stage(new FitViewport(SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT));
		mStage.getCamera().position.x = 0;
		mStage.getCamera().position.y = 0;
		
		setupLayout();
		setupControls();		
		
		SnakeWarGame.PlayServices.registerMoveReceiver(this);
	}
	
	private void setupLayout()
	{	
		mGameStartPopup = new Popup("Waiting for opponent", SnakeWarGame.GameFont);
		
		mSnakeLocal = new Snake(new Coordinate(0,-100), new Coordinate(0,-200), Direction.UP, 150);
		mSnakeRemote.add(new Coordinate(0,100));
		mSnakeRemote.add(new Coordinate(0,200));
		mPowerupsLocal[POWERUP_LENGTH] = new Powerup(new Coordinate(-150,-POWERUP_SIZE-5), PowerupType.IncreaseLength);
		mPowerupsLocal[POWERUP_SPEED] = new Powerup(new Coordinate(150,-POWERUP_SIZE-5), PowerupType.IncreaseSpeed);
		mPowerupsLocal[POWERUP_OPPONENT_SPEED] = new Powerup(new Coordinate(0,-POWERUP_SIZE-5), PowerupType.IncreaseOpponentSpeed);
		mPowerupsRemote[POWERUP_LENGTH] = new Powerup(new Coordinate(150,POWERUP_SIZE+5), PowerupType.IncreaseLength);
		mPowerupsRemote[POWERUP_SPEED] = new Powerup(new Coordinate(-150,POWERUP_SIZE+5), PowerupType.IncreaseSpeed);
		mPowerupsRemote[POWERUP_OPPONENT_SPEED] = new Powerup(new Coordinate(0,POWERUP_SIZE+5), PowerupType.IncreaseOpponentSpeed);
		mPowerupsRemote[POWERUP_LENGTH].disable();
		mPowerupsRemote[POWERUP_SPEED].disable();
		mPowerupsRemote[POWERUP_OPPONENT_SPEED].disable();

		LabelStyle ls = new LabelStyle();
		ls.font = SnakeWarGame.GameFont.Font;
		ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		mCountdownLabel = new ShaderLabel("", ls, SnakeWarGame.GameFont.Shader);
		mCountdownLabel.setWidth(500);
		mCountdownLabel.setFontScale(1.875f);
		mCountdownLabel.setAlignment(Align.bottom);
		mCountdownLabel.setX(-mCountdownLabel.getWidth()/2);
		mCountdownLabel.setY(0-mCountdownLabel.getHeight()/2);
		mCountdownLabel.setTouchable(Touchable.disabled);
		
		mInfoLabel = new ShaderLabel("Press back again to forfeit game", ls, SnakeWarGame.GameFont.Shader);
		mInfoLabel.setWidth(500);
		mInfoLabel.setColor(0,0,0,1);
		mInfoLabel.setFontScale(1.875f);
		mInfoLabel.setAlignment(Align.bottom);
		mInfoLabel.setX(-mInfoLabel.getWidth()/2);
		mInfoLabel.setY(-SnakeWarGame.VIRTUAL_HEIGHT/2);
		mInfoLabel.setTouchable(Touchable.disabled);
		
		mStage.addActor(mCountdownLabel);		
		mStage.addActor(mGameStartPopup);	
		mStage.addActor(mInfoLabel);
	}	

	private void setupControls()
	{
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(mStage);
		multiplexer.addProcessor(new InputAdapter () 
		{
			@Override
			public boolean keyDown (int keyCode) 
			{
				switch(keyCode)
				{
				case Keys.BACK:
					if(mBackTimeout < 2) {
						doGameOver("GAME FORFEIT\nYOU LOSE", EndGameType.Forfeit); 	
						mInfoLabel.remove();		
					}
					else {
						mBackTimeout = 0;
						mInfoLabel.setColor(1, 1, 1, 1);
					}
					break;
				case Keys.UP:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnakeLocal.setDirection(Direction.UP);
					}
					break;
				case Keys.DOWN:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnakeLocal.setDirection(Direction.DOWN);
					}
					break;
				case Keys.LEFT:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnakeLocal.setDirection(Direction.LEFT);
					}
					break;
				case Keys.RIGHT:
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						mSnakeLocal.setDirection(Direction.RIGHT);
					}
					break;
				}
				return true; // return true to indicate the event was handled
			}
			
			@Override
			public boolean touchDown (int x, int y, int pointer, int button) 
			{
				return false; // return true to indicate the event was handled
			}
			
			@Override
			public boolean touchUp (int x, int y, int pointer, int button) {
				if(SnakeWarGame.SaveController.getControlType() == ControlType.Tap) {
					if(!mGameOver && (mGameStartCountdown <= 0)) {
						Direction direction = mSnakeLocal.getDirection();
						if(direction == Direction.UP || direction == Direction.DOWN) {
							//moving vertically
							if(x > SnakeWarGame.VIRTUAL_WIDTH/2) {
								mSnakeLocal.setDirection(Direction.RIGHT);
							}
							else {
								mSnakeLocal.setDirection(Direction.LEFT);
							}
						} 
						else {
							//moving horizontally
							if(y > SnakeWarGame.VIRTUAL_HEIGHT/2) {		
								mSnakeLocal.setDirection(Direction.DOWN);
							}
							else {
								mSnakeLocal.setDirection(Direction.UP);
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
		multiplexer.addProcessor(new GestureDetector(new GestureListener()
		{

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
								mSnakeLocal.setDirection(Direction.RIGHT);						
							}
							else {
								mSnakeLocal.setDirection(Direction.LEFT);						
							}
						}
						else {
							if(velocityY > 0) {
								mSnakeLocal.setDirection(Direction.DOWN);						
							}
							else {
								mSnakeLocal.setDirection(Direction.UP);						
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

	@Override
	public void receiveMove(byte[] move) {	
		mOpponentHeartbeat = 0;
		ByteBuffer buffer = ByteBuffer.wrap(move);
		switch(move[1]) {
		case MSG_MOVE:
			buffer.position(3);
			ArrayList<Coordinate> snake = new ArrayList<Coordinate>();
			int pointCount = buffer.get();
			for(int i = 0; i < pointCount; i++) {
				snake.add(new Coordinate(-buffer.getFloat(),-buffer.getFloat()));					
			}
			mSnakeRemote = snake;
			
			if(mObstaclesLocal.size() != buffer.get()) {
				if(buffer.capacity() >= 36) {
					Rectangle rn=new Rectangle();
					rn.width = buffer.getFloat();
					rn.height = buffer.getFloat();
					rn.x = -buffer.getFloat()-rn.width; //flipped to orient with our board
					rn.y = -buffer.getFloat()-rn.height; 
					mObstaclesLocal.add(rn);
					SnakeWarGame.SoundPlayer.playObstacleCreate();
				}
			}
			break;
		case MSG_POWERUP:;
			int powerupType = move[2];
			if(powerupType == POWERUP_OPPONENT_SPEED) {
				mPowerupsLocal[POWERUP_SPEED].applyEffect(mSnakeLocal);
			}
			break;
		case MSG_POWERUP_POSITIONS:
			buffer.position(2);
			mPowerupsRemote[POWERUP_SPEED].getPosition().X = -buffer.getFloat();
			mPowerupsRemote[POWERUP_SPEED].getPosition().Y = -buffer.getFloat();
			mPowerupsRemote[POWERUP_LENGTH].getPosition().X = -buffer.getFloat();
			mPowerupsRemote[POWERUP_LENGTH].getPosition().Y = -buffer.getFloat();
			mPowerupsRemote[POWERUP_OPPONENT_SPEED].getPosition().X = -buffer.getFloat();
			mPowerupsRemote[POWERUP_OPPONENT_SPEED].getPosition().Y = -buffer.getFloat();
			break;
		case MSG_GAME_OVER:
			if(!mGameOver) {
				Gdx.app.postRunnable(new Runnable(){
					@Override
					public void run() {
						SnakeWarGame.PlayServices.disconnectGame();
						doGameOver("GAME OVER\nYOU WIN", EndGameType.Win);	
					}
				});	
			}
			break;
		case MSG_GAME_LEFT:
			if(!mGameOver) {
				Gdx.app.postRunnable(new Runnable(){
					@Override
					public void run() {
						SnakeWarGame.PlayServices.disconnectGame();
						doGameOver("OPPONENT QUIT\nYOU WIN", EndGameType.Win);	
					}
				});	
			}
			break;
		}
	}
	
	private void sendLeaveGame() {
		//type - description
		//int   : message type
		//int   : game message type
		byte[] message = new byte[2];
		message[0] = IMoveReceiver.MSG_GAME;
		message[1] = MSG_GAME_LEFT;
		SnakeWarGame.PlayServices.broadcastMessage(message); 	
	}	
	
	/**
	 * Sends a game over message to the other player
	 */
	private void sendGameOver()
	{
		//type - description
		//int   : message type
		//int   : game message type
		byte[] message = new byte[2];
		message[0] = IMoveReceiver.MSG_GAME;
		message[1] = MSG_GAME_OVER;
		SnakeWarGame.PlayServices.broadcastMessage(message); 		
	}
	
	/**
	 * Sends current position to other player
	 * Also sends position of last obstacle created. Obstacle created was previously sent 
	 * as a reliable message, but there was reasonable lag for other player receiving it.
	 * Constantly sending it as part of the move ensures that it will be received by the other player
	 * at some point. And is received far quicker (less lag) when compared to reliableMessage.
	 */
	private void sendMove()
	{
		//type - description
		//int   : message type
		//int   : game message type
		//int   : flags (reserved)
		//int : snake size
		//float : x position of snake head
		//float : y position of snake head
		//float : x position of last corner snake turned
		//float : y position of last corner snake turned
		//int : obstacle count
		//float : last obstacle x position
		//float : last obstacle y position
		//float : last obstacle width
		//float : last obstacle height
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(IMoveReceiver.MSG_GAME);
		out.write(MSG_MOVE);

		byte flags = 0;
		out.write(flags); 
		
		try
		{
			out.write(mSnakeLocal.getPoints().size());
			for(Coordinate c:mSnakeLocal.getPoints())
			{
		    	out.write(ByteBuffer.allocate(4).putFloat(c.X).array());		
		    	out.write(ByteBuffer.allocate(4).putFloat(c.Y).array());
			}  	
			out.write(mObstaclesRemote.size());
			if(mLastObstacleCreated != null) {
				out.write(ByteBuffer.allocate(4).putFloat(mLastObstacleCreated.width).array()); 
				out.write(ByteBuffer.allocate(4).putFloat(mLastObstacleCreated.height).array()); 	
				out.write(ByteBuffer.allocate(4).putFloat(mLastObstacleCreated.x).array());
				out.write(ByteBuffer.allocate(4).putFloat(mLastObstacleCreated.y).array()); 	
			}
			SnakeWarGame.PlayServices.broadcastMessage(out.toByteArray()); 
		}
		catch(Exception ex)
		{
			//there was an error writing to the byte array stream. Just don't send the message this time
		}		
	}

	private void sendPowerupPickedUp(int powerupType)	{
		//type - description
		//int   : message type
		//int   : game message type
		//int   : powerup type
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(IMoveReceiver.MSG_GAME);
		out.write(MSG_POWERUP);
		out.write(powerupType); 	
		NetMessage message = new NetMessage(out.toByteArray());
		int id = SnakeWarGame.PlayServices.broadcastReliableMessage(message.Message); 
		if(id != -1) {
			message.Sending = true;
			message.ID = id;
		}
		mMessageQueue.add(message);		
	}
	
	private void sendPowerupPositions()	{
		//type - description
		//int   : message type
		//int   : game message type
		//float : x position of powerup1
		//float : y position of powerup1
		//float : x position of powerup2
		//float : y position of powerup2
		//float : x position of powerup3
		//float : y position of powerup3
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(IMoveReceiver.MSG_GAME);
		out.write(MSG_POWERUP_POSITIONS);
		try
		{
			for(Powerup p: mPowerupsLocal) {
		    	out.write(ByteBuffer.allocate(4).putFloat(p.getPosition().X).array());		
		    	out.write(ByteBuffer.allocate(4).putFloat(p.getPosition().Y).array());					
			}
			SnakeWarGame.PlayServices.broadcastMessage(out.toByteArray()); 
    	}
		catch(Exception ex)
		{
			//there was an error writing to the byte array stream. Just don't send the message this time
		}	
	}

	/**
	 * @return true if the countdown is complete
	 */
	private boolean doCountdown(float delta) {
		if(mGameStartCountdown <= 0) {	
			if(mGameStartCountdown > -1) {	
				mCountdownLabel.setText("GO");
				mGameStartCountdown -= delta;
				mCountdownLabel.setFontScale(-mGameStartCountdown*2);
				float fade = 1+mGameStartCountdown;
				mCountdownLabel.setColor(fade, fade, fade, fade);
			} 
			else {
				mCountdownLabel.remove();
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
				mCountdownLabel.setText("READY");					
			} 
			else {
				mCountdownLabel.setText(((int)mGameStartCountdown+1) + "");
				mCountdownLabel.setFontScale((1-(mGameStartCountdown-(int)mGameStartCountdown))*2);
			}
			return false;
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(mBackTimeout < 2) {
			mBackTimeout+=delta;
			if(mBackTimeout >= 2) {
				mInfoLabel.setColor(0, 0, 0, 1);						
			} else {
				float fade = 1-mBackTimeout/2;
				mInfoLabel.setColor(fade, fade, fade, 1);						
			}
		}
		
		if(!mReady) {
			mGameStartCountdown -= delta;
			if(mGameStartCountdown <= 0) {
				mGameStartCountdown = 2.99f;
				mCountdownLabel.setText((int)mGameStartCountdown + "");
				mReady = true;
				SnakeWarGame.PlayServices.broadcastReady();	
			}
		}
		else {
			mOpponentHeartbeat += delta;	
			if(mOpponentHeartbeat > OPPONENT_TIMEOUT) {
				if(SnakeWarGame.PlayServices.checkConnection()) {
					doGameOver("OPPONENT LEFT GAME\nYOU WIN", EndGameType.Win);
				}
				else {
					doGameOver("LOST CONNECTION\nENDING GAME", EndGameType.NetworkError);						
				}
			}			
		}
		
		if(!mGameOver && !mWaitingForOpponent) {			
			if(doCountdown(delta)) {
				
				mMultiplayerUpdateTimer+=delta;
				if(mMultiplayerUpdateTimer>=UPDATE_RATE) {
					//send the current position we've moved to
					sendMove();
					
					//check if any reliable messages need to be resent and try resend them
					for(NetMessage msg:mMessageQueue) {
						if(!msg.Sending) {
							int id = SnakeWarGame.PlayServices.broadcastReliableMessage(msg.Message); 
							if(id != -1) {
								msg.Sending = true;
								msg.ID = id;
							}
						}
					}
					mMultiplayerUpdateTimer = 0;
				}
				mSnakeLocal.act(delta);
				for(int i = 0; i < mPowerupsLocal.length; i++) {
					mPowerupsLocal[i].act(delta);
					if(mPowerupsLocal[i].checkPickup(mSnakeLocal.getHeadPosition())) {
						sendPowerupPickedUp(i);					
						SnakeWarGame.SoundPlayer.playPowerupPickup(mPowerupsLocal[i].getPowerupType());
						mPowerupsLocal[i].applyEffect(mSnakeLocal);
						mPowerupsLocal[i].clearPosition();
					}
				}
				placePowerups();
				checkCollisions();
				
				Rectangle newObstacle = mSnakeLocal.createObstacle();				
				if(newObstacle != null) {
					mObstaclesRemote.add(newObstacle);
					mLastObstacleCreated = newObstacle;
					SnakeWarGame.SoundPlayer.playObstacleCreate();
				};
			}
		} 

		drawBoard();

		mStage.act();
		mStage.draw();		
	}
	
	private void drawBoard() {
		Gdx.gl20.glLineWidth(2);
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		//draw local obstacles
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(0.75f,0.75f,1,1);
		for(int i = 0; i < mObstaclesLocal.size(); i++) {
			Rectangle r= mObstaclesLocal.get(i);
			mShapeRenderer.rect(r.x, r.y, r.width, r.height);
		}		

		//draw remote obstacles
		mShapeRenderer.setColor(0.45f,0.45f,0.55f,0.5f);
		for(int i = 0; i < mObstaclesRemote.size(); i++) {
			Rectangle r= mObstaclesRemote.get(i);
			mShapeRenderer.rect(r.x, r.y, r.width, r.height);
		}		
		mShapeRenderer.end();
		
		//draw snake
		mSnakeLocal.draw(mShapeRenderer);
		
		//draw opponent
		mShapeRenderer.begin(ShapeType.Line);
		mShapeRenderer.setColor(Color.GRAY);
		for(int i = 1; i < mSnakeRemote.size(); i+=1)
		{				
			Coordinate c1 = mSnakeRemote.get(i-1);
			Coordinate c2 = mSnakeRemote.get(i);
			mShapeRenderer.line(c1.X, c1.Y,c2.X, c2.Y);		
		}	
		mShapeRenderer.end();	

		//draw powerups	if they've been placed
		for(Powerup p:mPowerupsLocal) {
			p.draw(mShapeRenderer);
		}
		for(Powerup p:mPowerupsRemote) {
			p.draw(mShapeRenderer);
		}
		
		Gdx.gl20.glLineWidth(4);
		//draw border
		mShapeRenderer.begin(ShapeType.Line);
		mShapeRenderer.setColor(Color.WHITE);
		mShapeRenderer.rect(BOARDLEFT, BOARDBOTTOM, BOARDWIDTH, BOARDHEIGHT);
		mShapeRenderer.end();	
		Gdx.gl20.glLineWidth(1);	
		
		//blackout outside of the game boundary as an easy way to stop enemies being drawn outside of the boundary
		mShapeRenderer.begin(ShapeType.Filled);
		mShapeRenderer.setColor(Color.BLACK);
		mShapeRenderer.rect(-SnakeWarGame.VIRTUAL_WIDTH/2, BOARDTOP, SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT/2-BOARDTOP); //top
		mShapeRenderer.rect(-SnakeWarGame.VIRTUAL_WIDTH/2, -SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.VIRTUAL_WIDTH/2+BOARDLEFT-1, SnakeWarGame.VIRTUAL_HEIGHT); //left
		mShapeRenderer.rect(BOARDRIGHT, -SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.VIRTUAL_WIDTH/2-BOARDLEFT, SnakeWarGame.VIRTUAL_HEIGHT); //right
		mShapeRenderer.rect(-SnakeWarGame.VIRTUAL_WIDTH/2, -SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT/2-BOARDTOP-1); //bottom
		mShapeRenderer.end();	
		Gdx.gl20.glDisable(GL20.GL_BLEND);	
	}
	
	/**
	 * If powerups need to be placed puts them on the board at random position. Won't place them inside obstacles
	 * @param delta time since last frame
	 */
	private void placePowerups() {		
		if(!mPowerupsLocal[POWERUP_LENGTH].isPlaced() || !mPowerupsLocal[POWERUP_SPEED].isPlaced() || !mPowerupsLocal[POWERUP_OPPONENT_SPEED].isPlaced()) {
			Coordinate[] positions = new Coordinate[3];
			
			//try 10 times to find a spot to place powerups.
			for(int i = 0; i < 10; i++) {
				int x = mRandom.nextInt(BOARDWIDTH-2*POWERUP_SIZE)+BOARDLEFT+POWERUP_SIZE;
				int y = mRandom.nextInt(BOARDHEIGHT-2*POWERUP_SIZE)+BOARDBOTTOM+POWERUP_SIZE;
				boolean validPosition = true;
				for(Rectangle obstacle: mObstaclesLocal) {
					if((x > obstacle.x && x < obstacle.width) && (y > obstacle.y && y < obstacle.height)){
						//powerup is inside of this obstacle
						validPosition = false;
						break;
					}
				}
				if(validPosition) {
					if(positions[0] == null) {
						positions[0] = new Coordinate(x,y);
					} 
					else if(positions[1] == null) {
						positions[1] = new Coordinate(x,y);								
					}
					else {
						positions[2] = new Coordinate(x,y);		
						break;
					}
				}
			}
			if(positions[0] != null && positions[1] != null) {
				mPowerupsLocal[POWERUP_LENGTH].setPosition(positions[0]);				
				mPowerupsLocal[POWERUP_SPEED].setPosition(positions[1]);				
				mPowerupsLocal[POWERUP_OPPONENT_SPEED].setPosition(positions[2]);	
			}
			sendPowerupPositions();
		}
	}
	
	private void checkCollisions() {		
		Coordinate head = mSnakeLocal.getHeadPosition();
		boolean collided = false;
		//with wall
		if(head.X <= BOARDLEFT || head.X >= BOARDRIGHT) {
			collided= true;
		} 
		else if(head.Y <= BOARDBOTTOM || head.Y >= BOARDTOP) {
			collided = true;			
		}
		
		//with obstacles
		for(int i = 0 ; i < mObstaclesLocal.size(); i++) {
			Rectangle r = mObstaclesLocal.get(i);
			if((head.X >= r.x && head.X <= r.x+r.width) &&(head.Y >= r.y && head.Y <= r.y+r.height)) {
				collided = true;
			}
		}
		
		if(collided)
		{			
			doGameOver("GAME OVER\nYOU LOSE", EndGameType.Lose);
			
			//send final position
			sendMove();	
			sendGameOver();		
		}
	}
	
	/**
	 * Stops game and shows gameover popup
	 * @param message
	 */
	private void doGameOver(String message, EndGameType endType) {
		if(!mGameOver) {
			MultiplayerEndPopup p = new MultiplayerEndPopup(message, SnakeWarGame.VIRTUAL_HEIGHT/2, SnakeWarGame.GameFont, new ClickListener()
			{				
				@Override
	            public void clicked(
	                InputEvent event,
	                float x,
	                float y )
	            {       
					SnakeWarGame.SoundPlayer.playButtonClick();
					SnakeWarGame.AdController.show();
	            	mGame.setMainMenuScreen();
	            }
			});	
			mStage.addActor(p);
			p.dropIn();
			mGameOver = true;	
			SnakeWarGame.SoundPlayer.playGameOver();
			switch (endType) {
			case Win:
				SnakeWarGame.PlayServices.unlockAchievement(SnakeWarGame.ACHIEVEMENT_ID_COMPETITOR);		
				SnakeWarGame.SaveController.addWin();
				if(SnakeWarGame.SaveController.getWinStreak() >= 5) {
					SnakeWarGame.PlayServices.unlockAchievement(SnakeWarGame.ACHIEVEMENT_ID_ON_A_ROLL);						
				}
				break;
			case Lose:
				SnakeWarGame.SaveController.addLose();		
				break;
			case Forfeit:
				sendLeaveGame();
				SnakeWarGame.SaveController.addLose();		
				break;
			case NetworkError:	
				//don't need to do anything
				break;
			}
		}
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		mStage.dispose();		
	}

	@Override
	public void gameStart(float countdown) {
		mOpponentHeartbeat = -5;//set to negative to give extra time for game start countdown
		mGameStartCountdown = countdown;
		mWaitingForOpponent = false;
		
		Gdx.app.postRunnable(new Runnable(){
			@Override
			public void run() {
				mGameStartPopup.clear();
				mGameStartPopup.remove();		
			}
		});
	}

	@Override
	public void opponentDisconnected() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				doGameOver("OPPONENT LEFT GAME\nYOU WIN", EndGameType.Win);				
			}			
		});
	}

	@Override
	public void lostConnection() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				doGameOver("LOST CONNECTION\nENDING GAME", EndGameType.NetworkError);				
			}			
		});		
	}
	
	@Override
	public boolean isReady() {
		return mReady;		
	}

	@Override
	public void sendFailed(int id) {		
		//set the message "Sending" flag to false. So it will be sent again with the next set of messages
		for(NetMessage message:mMessageQueue) {
			if(message.ID == id) {
				message.Sending = false;
				break;
			}
		}		
	}

	@Override
	public void sendSuccess(int id) {
		//clear the message from the queue
		for(int i = 0; i < mMessageQueue.size(); i++) {
			if(mMessageQueue.get(i).ID == id) {
				mMessageQueue.remove(i);
				break;
			}			
		}	
	}
}
