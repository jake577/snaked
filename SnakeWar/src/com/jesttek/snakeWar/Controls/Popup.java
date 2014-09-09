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

public class Popup extends Group{
	
	private ShaderLabel mMessage;
	private ShaderTextButton mButton;
	protected Table mTable;
	
	/**
	 * @param message The message to display in the popup. Null if only using buttonText.
	 * @param buttonText The button text to display
	 * @param font The font used in the popup
	 * @param clickListener The click listener for the button
	 */
	public Popup(String message, String buttonText, DistanceFieldFont font, ClickListener clickListener) {	
		NinePatchDrawable buttonBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("buttonBackground")),6,6,6,6));
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.font = font.Font;
		buttonStyle.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
		buttonStyle.up = buttonBackground;
		
		mButton = new ShaderTextButton(buttonText,buttonStyle, font.Shader);
		mButton.getLabel().setFontScale(0.65625f);
		mButton.getLabel().setAlignment(Align.bottom);
		mButton.addListener( clickListener );
		mTable = new Table();
		
		NinePatchDrawable popupBackground = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("popupBackground")),11,11,11,11));
		mTable.setBackground(popupBackground);
		if(message == null)	{
			mTable.add(mButton);	
			mTable.setWidth(mButton.getWidth() + 40);
			mTable.setHeight(mButton.getHeight() + 20);
		}
		else {
			LabelStyle ls = new LabelStyle();
			ls.font = font.Font;
			ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
			mMessage = new ShaderLabel(message, ls, font.Shader);
			mMessage.setFontScale(0.65625f);
			mMessage.setAlignment(Align.top);
			mTable.add(mMessage);	
			mTable.row();
			mTable.add(mButton).width(250).height(40);	
			mTable.setWidth(384);
			mTable.setHeight(mMessage.getHeight() + 60);
		}
		
		this.setX(0 - mTable.getWidth()/2);
		this.setY(0 - mTable.getHeight()/2);
		this.addActor(mTable);
	}
	
	/**
	 * Popup that doesn't have a button, just shows text. Should be used as a wait/load dialog.
	 * @param message
	 * @param font
	 */
	public Popup(String message, DistanceFieldFont font) {
		LabelStyle ls = new LabelStyle();
		ls.font = font.Font; 
		ls.fontColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);	
		
		mMessage = new ShaderLabel(message, ls, font.Shader);
		mMessage.setFontScale(0.65625f);
		mMessage.setAlignment(Align.bottom);
		mTable = new Table();
		mTable.add(mMessage);	
		NinePatchDrawable background = new NinePatchDrawable(new NinePatch(new TextureRegion(SnakeWarGame.TextureAtlas.findRegion("popupBackground")),11,11,11,11));	
		mTable.setBackground(background);
		mTable.setWidth(mMessage.getWidth() + 20);
		mTable.setHeight(mMessage.getHeight() + 10);
		this.setX(0 - mTable.getWidth()/2);
		this.setY(0 - mTable.getHeight()/2);
		this.addActor(mTable);		
	}	
}
