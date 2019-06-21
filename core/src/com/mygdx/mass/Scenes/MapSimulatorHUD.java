package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Screens.*;
import com.mygdx.mass.Screens.MainMenuScreen;
import com.mygdx.mass.Screens.MapBuilderScreen;
import com.mygdx.mass.Tools.MapData;

import java.io.*;

public class MapSimulatorHUD implements Disposable {

    public static final float BUTTON_SIZE = 32;
    public static final float PAD_BOTTOM = 10;

    private MASS mass;
    private MapSimulatorScreen mapSimulatorScreen;

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    public Stage stage;

    Table table;
    Table miniTable;

    private ImageButton wall;
    private ImageButton building;
    private ImageButton door;
    private ImageButton window;
    private ImageButton sentryTower;

    private ImageButton load;
    private ImageButton save;
    private ImageButton reset;
    private ImageButton pause;
    private ImageButton exit;
    private ImageButton clear;
    private ImageButton stop;
    private ImageButton simulate;

    public Slider speed;
    public SelectBox<Guard.State> guardState;
    public SelectBox<Intruder.State> intruderState;

    private int prevSpeed;
    private boolean paused;
    private Skin glassySkin = new Skin(Gdx.files.internal("glassy/glassyui/glassy-ui.json"));

    public MapSimulatorHUD(final MapSimulatorScreen mapSimulatorScreen){
        this.mapSimulatorScreen = mapSimulatorScreen;
        this.mass = mapSimulatorScreen.mass;

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, mass.batch);

        table = new Table();
        table.setFillParent(true);
        table.bottom();

        miniTable = new Table();
//        miniTable.setFillParent(true);

        //Creating buttons along with their click listener
//
//        wall = createButton("Textures/Buttons/Wall.png");
//        wall.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Create wall");
//            }
//        });
//        building = createButton("Textures/Buttons/Building2.png");
//        building.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Create building");
//            }
//        });
//        door = createButton("Textures/Buttons/Door2.png");
//        door.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Create door");
//            }
//        });
//        window = createButton("Textures/Buttons/Window.png");
//        window.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Create window");
//            }
//        });
//        sentryTower = createButton("Textures/Buttons/Tower.png");
//        sentryTower.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Create sentry tower");
//            }
//        });
//
//        load = createButton("Textures/Buttons/Load.png");
//        load.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Load map");
//            }
//        });
//        save = createButton("Textures/Buttons/Save.png");
//        save.addListener(new ClickListener() {
//            public void clicked(InputEvent event, float x, float y){
//                System.out.println("Current action: Save map");
//            }
//        });
        reset = createButton("Textures/Buttons/Reset2.png");
        reset.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Reset");
                try{
                    MapData mapData;
                    File mapFile = new File("temp.ser");
                    InputStream inStream = new FileInputStream(mapFile);
                    ObjectInputStream fileObjectIn = new ObjectInputStream(inStream);
                    mapData = (MapData) fileObjectIn.readObject();
                    fileObjectIn.close();
                    inStream.close();
                    mass.getMap().clearMap();
                    mapData.loadMap(mass);
                } catch(IOException e){
                    System.out.println("IO error");
                    e.printStackTrace();
                } catch(ClassNotFoundException e){
                    System.out.println("Class not found");
                    e.printStackTrace();
                }
            }
        });
        clear = createButton("Textures/Buttons/Reset.png");
        clear.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Clear timers");
                mapSimulatorScreen.resetSimulationTimers();

            }
        });

        pause = createButton("Textures/Buttons/Pause.png");
        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                pauseSim();
            }
        });

        simulate = createButton("Textures/Buttons/Play.png");
        simulate.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapSimulatorScreen.setWorldSpeedFactor(prevSpeed);
                setTable();
            }
        });

        stop = createButton("Textures/Buttons/Stop.png");
        stop.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Stop Simulation");
                System.out.println("Simulation speed was set to: "+mapSimulatorScreen.getWorldSpeedFactor());
                System.out.println("Simulation step counter: "+mapSimulatorScreen.getSimulationStep());
                System.out.println("Simulation time: "+mapSimulatorScreen.getSimulationTime());
                for(int i = 0; i < mass.map.getAgents().size(); i++) {
//                    mass.map.getAgents().get(i).route.clear();
//                    mass.map.getAgents().get(i).setDestination(null);
                }
                ((Game) Gdx.app.getApplicationListener()).setScreen(mapSimulatorScreen.mass.mapBuilderScreen);
            }
        });
        exit = createButton("Textures/Buttons/Exit.png");
        exit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Exit Simulation");
                mass.PPM = mass.MINIMAL_ZOOM;
                mass.viewport.setUnitsPerPixel(1/mass.PPM);
                mass.viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
                ((Game) Gdx.app.getApplicationListener()).setScreen(mapSimulatorScreen.mass.mainMenuScreen);
            }
        });

        speed = new Slider(1, 500, 1, false, glassySkin);
        speed.setValue(mapSimulatorScreen.getWorldSpeedFactor());
        speed.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mapSimulatorScreen.setWorldSpeedFactor((int)(speed.getValue()));
                System.out.println("Current action: Changed World Speed To " +mapSimulatorScreen.getWorldSpeedFactor());
                if(paused && mapSimulatorScreen.getWorldSpeedFactor() != 0){
                    setTable();
                }
                if(!paused && mapSimulatorScreen.getWorldSpeedFactor() == 0){
                    if(prevSpeed == 0){
                        prevSpeed = 1;
                    }
                    setTable();
                }
            }
        });

        guardState = new SelectBox<Guard.State>(new Skin(Gdx.files.internal("guard/glassyui/glassy-ui.json")));
        guardState.setItems(Guard.State.values());
        for (Guard g : mass.getMap().getGuards()) {
            guardState.setSelected(g.getCurrentState());
        }
        guardState.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (Guard g : mass.getMap().getGuards()) {
                    g.setCurrentState(guardState.getSelected());
                }
            }
        });

        intruderState = new SelectBox<Intruder.State>(new Skin(Gdx.files.internal("intruder/glassyui/glassy-ui.json")));
        intruderState.setItems(Intruder.State.values());
        for (Intruder i : mass.getMap().getIntruders()) {
            intruderState.setSelected(i.getCurrentState());
        }
        intruderState.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (Intruder i : mass.getMap().getIntruders()) {
                    i.setCurrentState(intruderState.getSelected());
                }
            }
        });

        float sliderWidth = 500.f;
        int colSpan = (int) Math.ceil(sliderWidth/BUTTON_SIZE);
        table.defaults().pad(2.0f);
        table.setSize(sliderWidth, 1.0f);
        table.add(speed).size(sliderWidth,BUTTON_SIZE).expandX();
        //start second row
        table.row();
        miniTable.defaults().pad(2.0f);
        paused = false;
        table.add(miniTable).padBottom(PAD_BOTTOM).expandX();
        miniTable.add(reset).size(BUTTON_SIZE);
        miniTable.add(clear).size(BUTTON_SIZE);
        miniTable.add(pause).size(BUTTON_SIZE);
        miniTable.add(stop).size(BUTTON_SIZE);
        miniTable.add(exit).size(BUTTON_SIZE);
        miniTable.add(guardState);
        miniTable.add(intruderState);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);

        miniTable.removeActor(simulate);
    }

    private ImageButton createButton(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        TextureRegion textureRegion = new TextureRegion(texture);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        ImageButton imageButton = new ImageButton(textureRegionDrawable);
        return imageButton;
    }



    public void update(float dt){
        if(!speed.isDragging()) speed.setValue(mapSimulatorScreen.getWorldSpeedFactor());
//        guardState.act(dt);
//        guardState.getList().act(dt);
        guardState.getScrollPane().act(dt);
        intruderState.getScrollPane().act(dt);

    }

    public void resize(int width, int height) {
    }

    public void dispose() {
        stage.dispose();
    }

    public Table getTable() { return table; }

    public void setTable(){
        miniTable.clearChildren();
        miniTable.add(reset).size(BUTTON_SIZE);
        miniTable.add(clear).size(BUTTON_SIZE);
        if(paused){
            miniTable.add(pause).size(BUTTON_SIZE);
            paused = false;
        } else {
            miniTable.add(simulate).size(BUTTON_SIZE);
            paused = true;
        }
        miniTable.add(stop).size(BUTTON_SIZE);
        miniTable.add(exit).size(BUTTON_SIZE);
        miniTable.add(guardState);
        miniTable.add(intruderState);
    }

    public void setGuardState(Guard.State item){
        guardState.setSelected(item);
    }

    public void setIntruderState(Intruder.State item){
        intruderState.setSelected(item);
    }

    public void pauseSim(){
        prevSpeed = mapSimulatorScreen.getWorldSpeedFactor();
        speed.setValue(0);
        mapSimulatorScreen.setWorldSpeedFactor(0);
        setTable();
    }

}
