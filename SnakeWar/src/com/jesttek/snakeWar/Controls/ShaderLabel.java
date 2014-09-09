package com.jesttek.snakeWar.Controls;


import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ShaderLabel extends Label{

	private ShaderProgram mShader;
	
	public ShaderLabel(CharSequence text, LabelStyle style, ShaderProgram shader) {
		super(text, style);
		mShader = shader;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setShader(mShader);
		super.draw(batch, parentAlpha);
		batch.setShader(null);
	}
}
