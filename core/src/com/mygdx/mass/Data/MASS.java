package com.mygdx.mass.Data;

import box2dLight.RayHandler;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Screens.MainMenuScreen;
import com.mygdx.mass.Screens.MapBuilderScreen;
import com.mygdx.mass.Screens.MapSimulatorScreen;
import com.mygdx.mass.Screens.OptionsScreen;
import com.mygdx.mass.Tools.MapFileReader;
import com.mygdx.mass.Data.Properties;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.World.WorldContactListener;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class MASS extends Game{

	//General settings constants, don't put specific constants here
	public static final String TITLE = "Multi-Agent Surveillance System";
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 800;
	public static final int ANTI_ALIASING = 16;

	public static final float FIXED_TIME_STEP = 1/60f;

	public static final float CAMERA_SPEED = 40.0f; //unit in meter per sec
	public static final float MINIMAL_ZOOM = 3.0f;
	public static final float MAXIMAL_ZOOM = 40.0f;

	//Camera and Viewport
	public OrthographicCamera camera;
	public ScreenViewport viewport;
	public float PPM; //Pixels per meter

	public SpriteBatch batch;

	//Box2D
	public World world;
	public Box2DDebugRenderer debugRenderer;
	public WorldContactListener worldContactListener;

	public static Map map;

	public RayHandler rayHandler;

	public ShapeRenderer shapeRenderer;

	public MainMenuScreen mainMenuScreen;
	public MapBuilderScreen mapBuilderScreen;
	public MapSimulatorScreen mapSimulatorScreen;
	public OptionsScreen optionsScreen;

	private Properties properties;
	private ArrayList<Properties> settings = new ArrayList<Properties>();

	private static DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
	public static DecimalFormat largeIntegerFormat = new DecimalFormat("###,###,###", symbols);
	public static DecimalFormat largeDoubleFormat = new DecimalFormat("###,###.00", symbols);

	@Override
	public void create(){
		readSettings();
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		PPM = MINIMAL_ZOOM;

		batch = new SpriteBatch();

		//create our Box2D world, setting 0 gravity in X, 0 gravity in Y, and allow bodies to sleep
		world = new World(new Vector2(0, 0), true);
		//allows for debug lines of our box2d world.
		debugRenderer = new Box2DDebugRenderer();
		worldContactListener = new WorldContactListener(this);
		world.setContactListener(worldContactListener);

		map = new Map(this);

		rayHandler = new RayHandler(world);
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);
		rayHandler.setShadows(false);
		rayHandler.setAmbientLight(0.01f, 0.01f, 0.01f, 0.8f);
		rayHandler.setBlurNum(1);

		shapeRenderer = new ShapeRenderer();

		mainMenuScreen = new MainMenuScreen(this);
		mapBuilderScreen = new MapBuilderScreen(this);
		mapSimulatorScreen = new MapSimulatorScreen(this);
        setScreen(mainMenuScreen);
	}

	@Override
	public void render(){
		super.render();
	}
	
	@Override
	public void dispose(){
		super.dispose();
		batch.dispose();
		debugRenderer.dispose();
		rayHandler.dispose();
		world.dispose();
		shapeRenderer.dispose();
		mapBuilderScreen.dispose();
	}

	public Map getMap(){
		return map;
	}

	public void setMap(Map map){
		mapSimulatorScreen.setMap(map);
		this.map = map;
	}

	protected void readSettings(){
		settings.clear();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(
					"config.properties"));
			String line = br.readLine();
			while (line != null) {
				String split[] = line.split(": ");
				Properties toAdd = new Properties(split);
				settings.add(toAdd);
				// read next line
				line = br.readLine();

				if (toAdd.getName().equals("fs") && toAdd.getSetting().equals("true")){
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				}
				if (toAdd.getName().equals("fs") && toAdd.getSetting().equals("false")){
					Gdx.graphics.setWindowedMode(WINDOW_WIDTH,WINDOW_HEIGHT);
				}
			}
//			System.out.println("File was read. The size was " + settings.size());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Properties> getSettings(){
		return settings;
	}


	public void writeSettings(){
//		settings = newSettings;
		BufferedWriter bw = null;
		FileWriter fw = null;

//		System.out.println(getSettings().size());
		try {
			fw = new FileWriter("config.properties", false);
			bw = new BufferedWriter(fw);

			ArrayList<String> listy = new ArrayList<String>();
			String prop;
			for(int i = 0; i < 2; i++){
				Properties current = getSettings().get(i);
				prop = (current.getLine());
				listy.add(prop);
			}
//			System.out.println(listy.size());
			for (int j = 0; j < listy.size(); j++){
				prop = listy.get(j);
				bw.write(prop);
				bw.newLine();
			}
			listy.clear();

//			System.out.println("File was written. The size was " + settings.size() + ". The contents was: ");
			System.out.println(getSettingStrings()[0]);
			System.out.println(getSettingStrings()[1]);

			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readSettings();
	}

	public String[] getSettingStrings(){
		String[] pop = new String[settings.size()];
		for (int k=0; k<settings.size(); k++){
			pop[k] = getSettings().get(k).getLine();
		}
		return pop;
	}
}
