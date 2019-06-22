package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Screens.MapBuilderScreen;
import com.mygdx.mass.Tools.MapData;
import com.mygdx.mass.Tools.MapFileReader;

import java.io.*;

public class MapBuilderHUD implements Disposable {

    public static final float BUTTON_SIZE = 32;
    public static final float PAD_BOTTOM = 10;

    private MapBuilderScreen mapBuilderScreen;

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    public Stage stage;

    Table table;

    private ImageButton wall;
    private ImageButton building;
    private ImageButton door;
    private ImageButton window;
    private ImageButton sentryTower;
    private ImageButton hidingArea;
    private ImageButton targetArea;
    private ImageButton guard;
    private ImageButton intruder;

    private ImageButton load;
    private ImageButton save;
    private ImageButton random;
    private ImageButton delete;
    private ImageButton clear;
    private ImageButton undo;
    private ImageButton redo;
    private ImageButton simulate;
    private ImageButton exit;

    private MASS mass;

    public MapBuilderHUD(final MapBuilderScreen mapBuilderScreen, SpriteBatch batch){
        mass = mapBuilderScreen.mass;
        this.mapBuilderScreen = mapBuilderScreen;

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, batch);

        table = new Table();
        table.setFillParent(true);
        table.bottom();

        //Creating buttons along with their click listener

        wall = createButton("Textures/Buttons/Wall.png");
        wall.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.WALL);
                System.out.println("Current action: Create wall");
                mapBuilderScreen.getInfo().updateBuildTool("Wall");
            }
        });
        building = createButton("Textures/Buttons/Building2.png");
        building.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.BUILDING);
                System.out.println("Current action: Create building");
                mapBuilderScreen.getInfo().updateBuildTool("Building");
            }
        });
        door = createButton("Textures/Buttons/Door2.png");
        door.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.DOOR);
                System.out.println("Current action: Create door");
                mapBuilderScreen.getInfo().updateBuildTool("Door");
            }
        });
        window = createButton("Textures/Buttons/Window.png");
        window.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.WINDOW);
                System.out.println("Current action: Create window");
                mapBuilderScreen.getInfo().updateBuildTool("Window");
            }
        });
        sentryTower = createButton("Textures/Buttons/Tower.png");
        sentryTower.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.SENTRY_TOWER);
                System.out.println("Current action: Create sentry tower");
                mapBuilderScreen.getInfo().updateBuildTool("Sentry tower");
            }
        });
        hidingArea = createButton("Textures/Buttons/Hiding2.png");
        hidingArea.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.HIDING_AREA);
                System.out.println("Current action: Create hiding area");
                mapBuilderScreen.getInfo().updateBuildTool("Hiding area");
            }
        });
        targetArea = createButton("Textures/Buttons/Target.png");
        targetArea.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.TARGET_AREA);
                System.out.println("Current action: Create target area");
                mapBuilderScreen.getInfo().updateBuildTool("Target area");
            }
        });
        guard = createButton("Textures/Buttons/Guard.png");
        guard.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.GUARD);
                System.out.println("Current action: Create guard");
                mapBuilderScreen.getInfo().updateBuildTool("Guard");
            }
        });
        intruder = createButton("Textures/Buttons/Intruder2.png");
        intruder.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.INTRUDER);
                System.out.println("Current action: Create intruder");
                mapBuilderScreen.getInfo().updateBuildTool("Intruder");
            }
        });

        load = createButton("Textures/Buttons/Load.png");
        load.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
				System.out.println("Current action: Load map");
				MapFileReader.loadMapFromFile(mass);
//                mass = mapBuilderScreen.mass;
//				//mapBuilderScreen.mass.loadMap();
//                mass.world = new World(new Vector2(0, 0), true);
//
//                //allows for debug lines of our box2d world.
//                mass.debugRenderer = new Box2DDebugRenderer();
//                mass.worldContactListener = new WorldContactListener(mass);
//                mass.world.setContactListener(mapBuilderScreen.mass.worldContactListener);
//                mass.rayHandler.removeAll();
//
//                mass.map = MapFileReader.loadMapFromFile(mapBuilderScreen.mass);
//                //mapBuilderScreen.updateMap();
//                mass.mapBuilderScreen = new MapBuilderScreen(mapBuilderScreen.mass);
//                //mapBuilderScreen = mass.mapBuilderScreen;
//
//                mass.setScreen(mapBuilderScreen);

            }
        });
        save = createButton("Textures/Buttons/Save.png");
        save.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                MapFileReader.saveMapToFile(mapBuilderScreen.mass.getMap());
                System.out.println("Current action: Save map");
            }
        });
        random = createButton("Textures/Buttons/Random.png");
        random.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.mass.getMap().clearMap();
                mapBuilderScreen.mass.getMap().addOuterWalls();

                mapBuilderScreen.numberOfObjects = 1;
                mapBuilderScreen.random(MapBuilderScreen.State.TARGET_AREA);

                mapBuilderScreen.maxObjects = 20;
                mapBuilderScreen.maxSize = 50;
                mapBuilderScreen.numberOfObjects = (int) (Math.random() * mapBuilderScreen.maxObjects);

//                mapBuilderScreen.randomState = MapBuilderScreen.State.BUILDING;
                mapBuilderScreen.random(MapBuilderScreen.State.BUILDING);
//                mapBuilderScreen.randomState = MapBuilderScreen.State.WALL;
                mapBuilderScreen.random(MapBuilderScreen.State.WALL);
//                mapBuilderScreen.randomState = MapBuilderScreen.State.HIDING_AREA;
                mapBuilderScreen.maxSize = 10;
                mapBuilderScreen.random(MapBuilderScreen.State.HIDING_AREA);
//                mapBuilderScreen.randomState = MapBuilderScreen.State.SENTRY_TOWER;
                mapBuilderScreen.random(MapBuilderScreen.State.SENTRY_TOWER);
//                mapBuilderScreen.randomState = MapBuilderScreen.State.TARGET_AREA;
//                mapBuilderScreen.random(MapBuilderScreen.State.GUARD);
                mapBuilderScreen.random(MapBuilderScreen.State.INTRUDER);


                System.out.println("Current action: Randomize");
            }
        });
        delete = createButton("Textures/Buttons/Delete.png");
        delete.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.DELETION);
                System.out.println("Current action: Delete");
                mapBuilderScreen.getInfo().updateBuildTool("Delete");
            }
        });
        clear = createButton("Textures/Buttons/Clear.png");
        clear.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
            	System.out.println("Current action: Clear map");
            	mapBuilderScreen.mass.getMap().clearMap();
            	mapBuilderScreen.mass.getMap().addOuterWalls();
            	mass.mapSimulatorScreen.hud.setIntruderState(Intruder.State.NONE);
            	mass.mapSimulatorScreen.hud.setGuardState(Guard.State.NONE);

            }
        });
        undo = createButton("Textures/Buttons/Undo.png");
        undo.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Undo");
                System.out.println(mass.map.undo.size());
                if (mass.map.undo.size()>4) {
                    BoxObject temp = mass.map.undo.pop();
                    mass.map.redo.push(temp);
                    switch (temp.getObjectType()) {
                        case WALL:
                            mass.map.getWalls().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                        case BUILDING:
                            mass.map.getBuildings().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                        case HIDING_AREA:
                            mass.map.getHidingAreas().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                        case SENTRY_TOWER:
                            mass.map.getSentryTowers().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                        case TARGET_AREA:
                            mass.map.getTargetAreas().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                        case DOOR:
                            mass.map.getDoors().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                        case WINDOW:
                            mass.map.getWindows().remove(temp);
                            mass.world.destroyBody(temp.getBody());
                            break;
                    }
                }

            }
        });
        redo = createButton("Textures/Buttons/Redo.png");
        redo.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Redo");
                if (mass.map.redo.size()>0) {
                    BoxObject temp = mass.map.redo.pop();
                    switch (temp.getObjectType()) {
                        case WALL:
                            mass.map.addWall(temp.getRectangle());
                            break;
                        case BUILDING:
                            mass.map.addBuilding(temp.getRectangle());
                            break;
                        case HIDING_AREA:
                            mass.map.addHidingArea(temp.getRectangle());
                            break;
                        case SENTRY_TOWER:
                            mass.map.addSentryTower(temp.getRectangle());
                            break;
                        case TARGET_AREA:
                            mass.map.addTargetArea(temp.getRectangle());
                            break;
                        case DOOR:
                            mass.map.addDoor(temp.getRectangle());
                            break;
                        case WINDOW:
                            mass.map.addWindow(temp.getRectangle());
                            break;
                    }
                }
            }
        });
        simulate = createButton("Textures/Buttons/Play.png");
        simulate.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Simulate");
                mapBuilderScreen.getInfo().updateBuildTool("None");

//                initiating the individual map of all agents, giving it the starting information
                for (Guard guard : mass.getMap().getGuards()) {
                    guard.getIndividualMap().setWalls(mass.getMap().getWalls());
                    guard.getIndividualMap().setGuards(mass.getMap().getGuards());
                }
                for (Intruder intruder : mass.getMap().getIntruders()) {
                    intruder.getIndividualMap().setWalls(mass.getMap().getWalls());
                    intruder.getIndividualMap().setIntruders(mass.getMap().getIntruders());
                }

                try {
                File mapFile = new File("temp.ser");
                OutputStream outStream = new FileOutputStream(mapFile);
                ObjectOutputStream fileObjectOut = new ObjectOutputStream(outStream);
                MapData mapData = new MapData(mapBuilderScreen.mass.getMap());
                fileObjectOut.writeObject(mapData);
                fileObjectOut.close();
                outStream.close();
                } catch(IOException e){
                    e.printStackTrace();
                    e.getCause();
                    e.getMessage();
                    System.out.println("input IO error");
                }

                mapBuilderScreen.mass.setScreen(mapBuilderScreen.mass.mapSimulatorScreen);
            }
        });
        exit = createButton("Textures/Buttons/Exit.png");
        exit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Exit");
                mass.PPM = mass.MINIMAL_ZOOM;
                mass.viewport.setUnitsPerPixel(1/mass.PPM);
                mass.viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
                ((Game) Gdx.app.getApplicationListener()).setScreen(mapBuilderScreen.mass.mainMenuScreen);

//                Gdx.app.getApplicationListener().dispose();
            }
        });

        table.defaults().pad(2.0f);

        table.add(wall).size(BUTTON_SIZE);
        table.add(building).size(BUTTON_SIZE);
        table.add(door).size(BUTTON_SIZE);
        table.add(window).size(BUTTON_SIZE);
        table.add(sentryTower).size(BUTTON_SIZE);
        table.add(hidingArea).size(BUTTON_SIZE);
        table.add(targetArea).size(BUTTON_SIZE);
        table.add(guard).size(BUTTON_SIZE);
        table.add(intruder).size(BUTTON_SIZE);

        //start second row
        table.row();

        table.add(load).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(save).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(random).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(delete).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(clear).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(undo).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(redo).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(simulate).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(exit).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);

        stage.addActor(table);
    }

    private ImageButton createButton(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        TextureRegion textureRegion = new TextureRegion(texture);
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(textureRegion);
        ImageButton imageButton = new ImageButton(textureRegionDrawable);
        return imageButton;
    }

    public void update(float dt){

    }

    public void resize(int width, int height) {

    }

    public void dispose() {
        stage.dispose();
    }

    public Table getTable() { return table; }

}
