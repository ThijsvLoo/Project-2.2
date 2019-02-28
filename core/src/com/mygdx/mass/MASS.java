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

	public SpriteBatch batch;

	//Virtual Screen size and Box2D Scale(Pixels Per Meter)
	public static final int V_WIDTH = 50;
	public static final int V_HEIGHT = 50;
	public static final float PPM = 10;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
//		setScreen(new BETAscreen());
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
