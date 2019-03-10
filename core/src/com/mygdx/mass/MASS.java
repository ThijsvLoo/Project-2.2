package com.mygdx.mass;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Screens.MapBuilderScreen;

public class MASS extends Game {

	public static final String TITLE = "Multi-Agent Surveillance System";

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
	public PointLight pointLight;

	public ShapeRenderer shapeRenderer;

	public MapBuilderScreen mapBuilderScreen;

	@Override
	public void create () {
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		PPM = 3;

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
		rayHandler.setBlurNum(0);
		pointLight = new PointLight(rayHandler, 360, new Color(1,1,1,1), 100, camera.position.x, camera.position.y);

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
		batch.dispose();
		world.dispose();
		debugRenderer.dispose();
		rayHandler.dispose();
		shapeRenderer.dispose();
	}
}
