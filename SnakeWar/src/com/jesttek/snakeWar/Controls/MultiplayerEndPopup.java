package com.jesttek.snakeWar.Controls;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.jesttek.snakeWar.DistanceFieldFont;
import com.jesttek.snakeWar.SnakeWarGame;

/**
 * @author Jake
 * Animated popup for the end of a single player game
 */
public class MultiplayerEndPopup extends Group{

	private enum State {
		DropIn,
		DropOut,
		Waiting
	}
	private float mVelocity = -150;	
	private Table mTable;
	private float mStartPosition;
	private State mState = State.Waiting;
	
	/**
	 * @param text The game end message displayed
	 * @param screenTop The top of the screen
	 * @param font The font used in the popup
	 * @param retryListener The click listener to retry level
	 * @param menuButton The click listener to return to menu
	 */
	public MultiplayerEndPopup(String text, float screenTop, DistanceFieldFont font, ClickListener menuListener) {
		NinePatchDrawable buttonBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("buttonBackground")),6,6,6,6));
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = font.Font;
		buttonStyle.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
		buttonStyle.up = buttonBackground;
		ShaderTextButton menuButton = new ShaderTextButton("Return To Menu",buttonStyle, font.Shader);
		menuButton.getLabel().setAlignment(Align.bottom);
		menuButton.getLabel().setFontScale(0.65625f);
		menuButton.addListener(menuListener);
		mTable = new Table();
		
		NinePatchDrawable popupBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("popupBackground")),11,11,11,11));
		mTable.setBackground(popupBackground);

		LabelStyle ls = new LabelStyle();
		ls.font = font.Font;
		ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
		
		ShaderLabel title = new ShaderLabel(text, ls, font.Shader);
		title.setAlignment(Align.bottom);
		title.setFontScale(0.65625f);
		mTable.add(title);		
		mTable.row();
		mTable.add(menuButton).width(250).height(40);
		mTable.setWidth(384);
		mTable.setHeight(144);
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
	 * Drop the popup in from the top of the screen
	 */
	public void dropIn() {
		mTable.setY(mStartPosition);
		mState = State.DropIn;
	}
}
