package com.jesttek.snakeWar.Controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.jesttek.snakeWar.DistanceFieldFont;
import com.jesttek.snakeWar.SnakeWarGame;
import com.jesttek.snakeWar.Inferfaces.ISaveData.ControlType;

/**
 * @author Jake
 * Popup for the end of a single player game
 */
public class PausePopup extends Group{

	private enum State {
		DropIn,
		DropOut,
		Waiting
	}
	private float mVelocity = -150;	
	private Table mTable;
	private float mStartPosition;
	private State mState = State.Waiting;
	private ShaderTextButton mControlButton;
	private ShaderTextButton mSoundButton;
	
	/**
	 * @param screenTop The top of the screen
	 * @param font The font used in the popup
	 * @param menuListener The click listener to return to menu
	 * @param resumeListener The click listener to resume the game
	 */
	public PausePopup(float screenTop, DistanceFieldFont font, ClickListener menuListener, ClickListener resumeListener) {		
		NinePatchDrawable buttonBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("buttonBackground")),6,6,6,6));
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = font.Font; 
		buttonStyle.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
		buttonStyle.up = buttonBackground;
		
		mSoundButton = new ShaderTextButton("Sound: ON",buttonStyle, font.Shader);
		mSoundButton.getLabel().setAlignment(Align.bottom);
		mSoundButton.getLabel().setFontScale(0.65625f);
		mSoundButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {    
            	SnakeWarGame.SaveController.setSound(!SnakeWarGame.SaveController.isSoundOn());
            	if(SnakeWarGame.SaveController.isSoundOn()) {
            		mSoundButton.setText("Sound: ON");            		
            	}
            	else {
        			mSoundButton.setText("Sound: OFF");    
        			SnakeWarGame.SoundPlayer.stopAll();   		
            	}      
            	SnakeWarGame.SoundPlayer.playButtonClick();      	
            }
        } );	
		
		mControlButton = new ShaderTextButton("Control Type: SWIPE",buttonStyle, font.Shader);
		mControlButton.getLabel().setAlignment(Align.bottom);
		mControlButton.getLabel().setFontScale(0.65625f);
		mControlButton.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y ) {  
            	if(SnakeWarGame.SaveController.getControlType() == ControlType.Tap) {
            		mControlButton.setText("Control Type: SWIPE");   
            		SnakeWarGame.SaveController.setControlType(ControlType.Swipe);         		
            	}
            	else {
            		mControlButton.setText("Control Type: TAP");  
            		SnakeWarGame.SaveController.setControlType(ControlType.Tap);          		
            	}         
            	SnakeWarGame.SoundPlayer.playButtonClick();
            }
        } );
		
		ShaderTextButton menuButton = new ShaderTextButton("Return To Menu",buttonStyle, font.Shader);
		menuButton.getLabel().setAlignment(Align.bottom);
		menuButton.getLabel().setFontScale(0.65625f);
		menuButton.addListener(menuListener);
		
		ShaderTextButton resumeButton = new ShaderTextButton("Resume",buttonStyle, font.Shader);
		resumeButton.getLabel().setAlignment(Align.bottom);
		resumeButton.getLabel().setFontScale(0.65625f);
		resumeButton.addListener(resumeListener);
		
		mTable = new Table();		
		NinePatchDrawable popupBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("popupBackground")),11,11,11,11));
		mTable.setBackground(popupBackground);

		LabelStyle ls = new LabelStyle();
		ls.font = font.Font;
		ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
		
		ShaderLabel title = new ShaderLabel("PAUSED", ls, font.Shader);
		title.setAlignment(Align.bottom);
		title.setFontScale(0.65625f);
		
		mTable.add(title).height(40);	
		mTable.row();
		mTable.add(resumeButton).width(250).height(40);
		mTable.row();
		mTable.add(mSoundButton).width(250).height(40);
		mTable.row();	
		mTable.add(mControlButton).width(250).height(40);
		mTable.row();
		mTable.add(menuButton).width(250).height(40);
		mTable.setWidth(384);
		mTable.setHeight(216);
		mStartPosition = screenTop+mTable.getHeight()/2;
		mTable.setY(mStartPosition);

		setX(-mTable.getWidth()/2);
		setY(-mTable.getHeight()/2);
		addActor(mTable);
	}
	
	public void act(float delta) {
		switch(mState) {
		case DropIn:
			mVelocity-=(1960*delta);
			mTable.setY(mTable.getY() + (mVelocity*delta));
			if(mTable.getY() <= 0) {
				mVelocity = -mVelocity*0.2f;
				mTable.setY(0);			
				if(mVelocity <= 10) {
					mState = State.Waiting;
				} else {
					SnakeWarGame.SoundPlayer.playBounce();
				}
			}
			break;
		case DropOut:
			mVelocity-=(1960*delta);
			mTable.setY(mTable.getY() + (mVelocity*delta));
			if(mTable.getY() <= -(mStartPosition+mTable.getHeight())) {
				mState = State.Waiting;
				mTable.setY(mStartPosition);
			}
			break;
		case Waiting:
			break;		
		}
	}
	
	/**
	 * Prepare popup and drop it in from the top of the screen
	 */
	public void dropIn() {
		
    	if(SnakeWarGame.SaveController.getControlType() == ControlType.Tap) {
    		mControlButton.setText("Control Type: TAP");          		
    	}
    	else {
    		mControlButton.setText("Control Type: SWIPE");          		
    	}    

    	if(SnakeWarGame.SaveController.isSoundOn()) {
    		mSoundButton.setText("Sound: ON");            		
    	}
    	else {
			mSoundButton.setText("Sound: OFF");            		
    	}    
    	
		mTable.setY(mStartPosition);
		mState = State.DropIn;
	}
	
	/**
	 * Drop the popup out the bottom of the screen
	 */
	public void dropOut() {
		mState = State.DropOut;		
	}
}
