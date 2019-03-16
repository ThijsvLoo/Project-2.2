package com.mygdx.mass;

import box2dLight.RayHandler;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Screens.MapBuilderScreen;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.World.WorldContactListener;

public class MASS extends Game {

	//General settings constants, don't put specific constants here
	public static final String TITLE = "Multi-Agent Surveillance System";
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 800;
	public static final int ANTI_ALIASING = 16;

	public static final float DEFAULT_ZOOM = 3.0f;

	//Camera and Viewport
	public OrthographicCamera camera;
	public ScreenViewport viewport;
	public float PPM; //Pixels per meter

	public SpriteBatch batch;

	//Box2D
	public World world;
	public Box2DDebugRenderer debugRenderer;
	public WorldContactListener worldContactListener;

	public Map map;

	public RayHandler rayHandler;

	public ShapeRenderer shapeRenderer;

	public MapBuilderScreen mapBuilderScreen;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		PPM = DEFAULT_ZOOM;

		batch = new SpriteBatch();

		//create our Box2D world, setting 0 gravity in X, 0 gravity in Y, and allow bodies to sleep
		world = new World(new Vector2(0, 0), true);
		//allows for debug lines of our box2d world.
		debugRenderer = new Box2DDebugRenderer();
		worldContactListener = new WorldContactListener();
		world.setContactListener(worldContactListener);

		map = new Map(this);

		rayHandler = new RayHandler(world);
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler.setShadows(false);
		rayHandler.setAmbientLight(0.01f, 0.01f, 0.01f, 0.8f);
		rayHandler.setBlurNum(1);

		shapeRenderer = new ShapeRenderer();

		mapBuilderScreen = new MapBuilderScreen(this);
        setScreen(mapBuilderScreen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		super.dispose();
		batch.dispose();;
		debugRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		shapeRenderer.dispose();
		mapBuilderScreen.dispose();
	}

	public Map getMap() { return map; }

	public void setMap(Map map) { this.map = map; }
}
