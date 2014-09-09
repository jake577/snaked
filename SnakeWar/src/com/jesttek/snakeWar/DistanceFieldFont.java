package com.jesttek.snakeWar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DistanceFieldFont{

	public ShaderProgram Shader;
	public BitmapFont Font;
	
	/**
	 * @param fontName Uses to open the required files "fonts/[fontName].fnt", "shaders[fontName].vert" and "shaders[fontName].frag"
	 */
	public DistanceFieldFont(String fontName) {
		if(Shader == null) {
			Shader = new ShaderProgram(Gdx.files.internal("shaders/"+fontName+".vert"), Gdx.files.internal("shaders/"+fontName+".frag"));
			if (!Shader.isCompiled()) {
			    Gdx.app.error("fontShader", "compilation failed:\n" + Shader.getLog());
			}
		}
		Texture texture = new Texture(Gdx.files.internal("fonts/"+fontName+".png"), true);
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		Font = new BitmapFont(Gdx.files.internal("fonts/"+fontName+".fnt"), new TextureRegion(texture), false);
	}
	
	public void dispose() {
		Font.dispose();
	}
}
