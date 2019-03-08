package com.mygdx.mass;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.mass.Screens.BETAscreen;
import com.mygdx.mass.Screens.MapBuilderScreen;

public class MASS extends Game {

	public static final String TITLE = "Multi-Agent Surveillance System";

	//Virtual Screen size
	public static final int VIRTUAL_WIDTH = 50;
	public static final int VIRTUAL_HEIGHT = 50;

	public SpriteBatch batch;

	@Override
	public void create () {
		batch = new SpriteBatch();
        setScreen(new MapBuilderScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();
	}
}
