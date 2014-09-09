package com.jesttek.snakeWar.Screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.jesttek.snakeWar.SnakeWarGame;
import com.jesttek.snakeWar.Controls.Popup;
import com.jesttek.snakeWar.Controls.ShaderLabel;
import com.jesttek.snakeWar.Controls.ShaderTextButton;
import com.jesttek.snakeWar.Inferfaces.ISaveData.ControlType;

public class MainMenuScreen implements Screen {
	
	private SnakeWarGame mGame;
	private Stage mStage;
	private ShaderLabel mTitle;
	private ShaderLabel mTipsLabel;
	private ShaderTextButton mMultiplayerButton;
	private ShaderTextButton mSinglePlayerButton;
	private ShaderTextButton mInstructionsButton;
	private ShaderTextButton mLeaderboardsButton;
	private ShaderTextButton mAchievementsButton;
	private ShaderTextButton mSoundButton;
	private ShaderTextButton mControlTypeButton;
	private ShaderTextButton mRateButton;
	private ImageButton mSettingsButton;
	private Group mSettingsPopup = new Group();
	private Image mSigninButton;
	private boolean mSignedin = false;
	private boolean mOpenSettings = false;
	private Popup mSignInPopup;	
	
	public MainMenuScreen (SnakeWarGame game) {
		mGame = game;		
		mStage = new Stage(new FitViewport(SnakeWarGame.VIRTUAL_WIDTH, SnakeWarGame.VIRTUAL_HEIGHT));		
		mStage.getCamera().position.x = 0;
		mStage.getCamera().position.y = 0;
		
		Gdx.input.setInputProcessor(mStage);		
		Gdx.input.setCatchBackKey(true);		
		
		setupLayout();
		setupListeners();
	}
	
	private void setupLayout() {
		LabelStyle titleStyle = new LabelStyle();		
		titleStyle.font = SnakeWarGame.GameFont.Font;
		titleStyle.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		mTitle = new ShaderLabel("SNAKED", titleStyle, SnakeWarGame.GameFont.Shader);
		mTitle.setWidth(SnakeWarGame.VIRTUAL_WIDTH);
		mTitle.setX(-SnakeWarGame.VIRTUAL_WIDTH/2);
		mTitle.setY(SnakeWarGame.VIRTUAL_HEIGHT/2 - mTitle.getHeight());
		mTitle.setAlignment(Align.top);
		mTitle.setFontScale(1.875f);
		mStage.addActor(mTitle);

		NinePatchDrawable background = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("buttonBackground")),6,6,6,6));
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = SnakeWarGame.GameFont.Font; //SnakeWarGame.FontLoader.getFont(42);
		buttonStyle.up = background;
		int spacing = 75;
		int y = 200;
		mSinglePlayerButton = new ShaderTextButton("Singleplayer",buttonStyle, SnakeWarGame.GameFont.Shader);
		mSinglePlayerButton.setY(y);
		setupButtonLayout(mSinglePlayerButton);
		y-=spacing;
		mMultiplayerButton = new ShaderTextButton("Multiplayer",buttonStyle, SnakeWarGame.GameFont.Shader);
		mMultiplayerButton.setY(y);
		setupButtonLayout(mMultiplayerButton);
		y-=spacing;
		mInstructionsButton = new ShaderTextButton("How To Play",buttonStyle, SnakeWarGame.GameFont.Shader);
		mInstructionsButton.setY(y);
		setupButtonLayout(mInstructionsButton);
		y-=spacing;
		mLeaderboardsButton = new ShaderTextButton("Leaderboards",buttonStyle, SnakeWarGame.GameFont.Shader);
		mLeaderboardsButton.setY(y);
		setupButtonLayout(mLeaderboardsButton);
		y-=spacing;
		mAchievementsButton = new ShaderTextButton("Achievements",buttonStyle, SnakeWarGame.GameFont.Shader);	
		mAchievementsButton.setY(y);
		setupButtonLayout(mAchievementsButton);		
		y-=spacing;
		mRateButton = new ShaderTextButton("Rate This App",buttonStyle, SnakeWarGame.GameFont.Shader);
		mRateButton.setY(y);
		setupButtonLayout(mRateButton);

		LabelStyle tipsStyle = new LabelStyle();		
		tipsStyle.font = SnakeWarGame.GameFont.Font;
		tipsStyle.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
		
		mTipsLabel = new ShaderLabel("TIP: Try to kill multiple enemies at the same time\nThis gives you a more health and score than killing them individually", tipsStyle, SnakeWarGame.GameFont.Shader);
		mTipsLabel.setY(y);
		mTipsLabel.setWidth(SnakeWarGame.VIRTUAL_WIDTH);
		mTipsLabel.setX(-SnakeWarGame.VIRTUAL_WIDTH/2);
		mTipsLabel.setFontScale(0.677f);		
		mTipsLabel.setAlignment(Align.bottom, Align.center);
		loadTip();

		mStage.addActor(mSinglePlayerButton);
		mStage.addActor(mMultiplayerButton);
		mStage.addActor(mInstructionsButton);
		mStage.addActor(mLeaderboardsButton);
		mStage.addActor(mAchievementsButton);
		mStage.addActor(mTipsLabel);
		//mStage.addActor(mRateButton);
		
		if(SnakeWarGame.PlayServices.isSignedIn()) {
			mSigninButton = new Image(SnakeWarGame.TextureAtlas.findRegion("signout"));
			mSignedin = true;
		}
		else {
			mSigninButton = new Image(SnakeWarGame.TextureAtlas.findRegion("signin"));	
			mSignedin = false;		
		}
		mSigninButton.setHeight(81);
		mSigninButton.setWidth(240);
		mSigninButton.setX(SnakeWarGame.VIRTUAL_WIDTH/2-mSigninButton.getWidth()-10);
		mSigninButton.setY(-SnakeWarGame.VIRTUAL_HEIGHT/2+10);
		mStage.addActor(mSigninButton);
		
		ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
		imageButtonStyle.up = background;
		mSettingsButton = new ImageButton(new TextureRegionDrawable(SnakeWarGame.TextureAtlas.findRegion("settingsButton")));
		mSettingsButton.setStyle((ButtonStyle)imageButtonStyle);
		mSettingsButton.setWidth(81);
		mSettingsButton.setHeight(81);
		mSettingsButton.setX(-SnakeWarGame.VIRTUAL_WIDTH/2+10);
		mSettingsButton.setY(-SnakeWarGame.VIRTUAL_HEIGHT/2+10);
		mSettingsButton.setColor(0.75f,0.75f,0.75f,1);
		mStage.addActor(mSettingsButton);
		setupSettingsPopup();
	}
	
	private void setupButtonLayout(ShaderTextButton button) {
		button.getLabel().setFontScale(1.3125f);
		button.setColor(0.75f,0.75f,0.75f,1);		
		button.setWidth(500);
		button.setX(-250);
		button.getLabel().setAlignment(Align.bottom);
	}
	
	private void setupSettingsPopup() {
		Table table = new Table();		
		NinePatchDrawable popupBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("popupBackground")),11,11,11,11));
		table.setBackground(popupBackground);
		
		NinePatchDrawable background = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("buttonBackground")),6,6,6,6));
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = SnakeWarGame.GameFont.Font;	//SnakeWarGame.FontLoader.getFont(31);
		buttonStyle.up = background;
		
		mSoundButton = new ShaderTextButton("Sound: ON",buttonStyle, SnakeWarGame.GameFont.Shader);
		if(!SnakeWarGame.SaveController.isSoundOn()) {
			mSoundButton.setText("Sound: OFF");
		}
		mSoundButton.setColor(0.75f,0.75f,0.75f,1);	
		mSoundButton.getLabel().setAlignment(Align.bottom);
		
		mControlTypeButton = new ShaderTextButton("Control Type: SWIPE",buttonStyle, SnakeWarGame.GameFont.Shader);
		if(SnakeWarGame.SaveController.getControlType() == ControlType.Tap) {
			mControlTypeButton.setText("Control Type: TAP");
		}
		mControlTypeButton.setColor(0.75f,0.75f,0.75f,1);	
		mControlTypeButton.getLabel().setAlignment(Align.bottom);

		LabelStyle ls = new LabelStyle();
		ls.font = SnakeWarGame.GameFont.Font; //SnakeWarGame.FontLoader.getFont(31);
		ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);			
		ShaderLabel title = new ShaderLabel("SETTINGS", ls, SnakeWarGame.GameFont.Shader);
		title.setAlignment(Align.center);
		table.add(title);		
		table.row();
		table.add(mSoundButton).width(320);	
		table.row().padBottom(5);
		table.add(mControlTypeButton).width(320);
		table.setWidth(330);
		table.setHeight(200);			
		mSettingsPopup.setY(mSettingsButton.getY()+2);
		mSettingsPopup.setX(mSettingsButton.getX());
		mSettingsPopup.setScale(0);
		mSettingsPopup.addActor(table);
	}

	private void setupListeners() {					
		mSinglePlayerButton.addListener( new ClickListener() {
            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y )
            {     
            	SnakeWarGame.SoundPlayer.playButtonClick();
                mGame.createSinglePlayerGame();    
            }
        } );		
		
		mMultiplayerButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	SnakeWarGame.SoundPlayer.playButtonClick();
            	if(!SnakeWarGame.PlayServices.isSignedIn()) {
            		mSignInPopup = new Popup("Sign in with Google+ to use multiplayer", "close", SnakeWarGame.GameFont, new ClickListener() {
			            @Override
			            public void clicked(InputEvent event, float x, float y ) {  
			            	mSignInPopup.remove();
			            }
			        } );	
            		mStage.addActor(mSignInPopup);
            	}
            	else {
            		SnakeWarGame.PlayServices.startRandomMultiplayer();
            	}
            }
        } );
		
		mInstructionsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {      
            	SnakeWarGame.SoundPlayer.playButtonClick();
                mGame.setInstructionsScreen();   
            }
        } );	
		
		mLeaderboardsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {   
            	SnakeWarGame.SoundPlayer.playButtonClick();  
            	SnakeWarGame.PlayServices.showLeaderboards();  
            }
        } );
		
		mAchievementsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	SnakeWarGame.SoundPlayer.playButtonClick();
            	SnakeWarGame.PlayServices.showAchievements();  
            }
        } );		
		
		mSoundButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	SnakeWarGame.SaveController.setSound(!SnakeWarGame.SaveController.isSoundOn());
            	if(SnakeWarGame.SaveController.isSoundOn()) {
            		mSoundButton.setText("Sound: ON");            		
            	}
            	else {
        			mSoundButton.setText("Sound: OFF");            		
            	}    
            	SnakeWarGame.SoundPlayer.playButtonClick();        	
            }
        } );	
		
		mControlTypeButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {        	
            	SnakeWarGame.SoundPlayer.playButtonClick(); 
            	if(SnakeWarGame.SaveController.getControlType() == ControlType.Tap) {
        			mControlTypeButton.setText("Control Type: SWIPE");   
                	SnakeWarGame.SaveController.setControlType(ControlType.Swipe);         		
            	}
            	else {
        			mControlTypeButton.setText("Control Type: TAP");  
                	SnakeWarGame.SaveController.setControlType(ControlType.Tap);          		
            	}    
            }
        } );
		
		mRateButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	SnakeWarGame.SoundPlayer.playButtonClick();
            	Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.jesttek.SnakeWarGame");
            }
        } );
		
		mSigninButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	SnakeWarGame.SoundPlayer.playButtonClick();
        		if(SnakeWarGame.PlayServices.isSignedIn())
        		{
        			SnakeWarGame.PlayServices.logoff();
        		}
        		else
        		{
        			SnakeWarGame.PlayServices.logon();        			
        		}
            }
        } );
		
		mSettingsButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	SnakeWarGame.SoundPlayer.playButtonClick();
        		mOpenSettings = !mOpenSettings;
        		if(mOpenSettings) {
        			mStage.addActor(mSettingsPopup);
        			mSettingsPopup.setZIndex(0);
        		}
            }
        } );		
	}
	
	private void loadTip() {
		Random r = new Random();
		switch(r.nextInt(6)) {
		case 0:
			mTipsLabel.setText("TIP: Try to kill multiple enemies at the same time\nThis gives you more health and score than killing them individually");
			break;
		case 1:
			mTipsLabel.setText("TIP: Taking the speed powerup will help you gain health quicker\nBut it will also make some enemies harder to avoid");
			break;
		case 2:
			mTipsLabel.setText("TIP: Keeping close to enemies when killing them might be risky\nBut it will mean the obstacle created will be smaller and easier to avoid");
			break;
		case 3:
			mTipsLabel.setText("TIP: Sign in to compete with friends for high scores");
			break;
		case 4:
			mTipsLabel.setText("TIP: Some enemies you probably want to kill ASAP\nFor other it might be better to leave a but to try them kill in groups");
			break;
		case 5:
			if(r.nextInt(10) == 1) {
				mTipsLabel.setText("TIP: Always check for toilet paper before sitting down");				
			}
			else {
				mTipsLabel.setText("TIP: The longer you last the more damage each enemy will do to you\nAfter a few minutes any damage will almost kill you instantly");
			}
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if(Gdx.input.isKeyPressed(Keys.BACK)) {
			Gdx.app.exit();
		}
		
		if(SnakeWarGame.PlayServices.isSignedIn() != mSignedin) {
			if(SnakeWarGame.PlayServices.isSignedIn()) {
				mSigninButton.setDrawable(new TextureRegionDrawable(SnakeWarGame.TextureAtlas.findRegion("signout")));
				mSignedin = true;
			}
			else {
				mSigninButton.setDrawable(new TextureRegionDrawable(SnakeWarGame.TextureAtlas.findRegion("signin")));	
				mSignedin = false;		
			}
		}
		
		//slide the settings popup open/close
		if(mOpenSettings) {
			if(mSettingsPopup.getScaleX() < 1) {
				mSettingsPopup.scaleBy(delta*3.75f);
				if(mSettingsPopup.getScaleX() > 1) {
					mSettingsPopup.setScale(1);
				}
			}
			
			if(mSettingsPopup.getX() < mSettingsButton.getX()+mSettingsButton.getWidth()) {
				mSettingsPopup.setX(mSettingsPopup.getX()+delta*300);				
			}	
		}
		else {			
			if(mSettingsPopup.getScaleX() > 0) {
				mSettingsPopup.scaleBy(-delta*3.75f);
				if(mSettingsPopup.getScaleX() <= 0) {
					mSettingsPopup.setScale(0);
					mSettingsPopup.remove();
				}
			}

			if(mSettingsPopup.getX() > mSettingsButton.getX()) {
				mSettingsPopup.setX(mSettingsPopup.getX()-delta*300);				
			}		
		}
		
		mStage.act(delta);
		mStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		mStage.getViewport().update( width,  height);
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
