package com.mygdx.mass.Scenes;

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
import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.Screens.MapBuilderScreen;
import com.mygdx.mass.Tools.MapFileReader;

import java.util.ArrayList;

public class HUD implements Disposable {

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
    private ImageButton move;
    private ImageButton delete;
    private ImageButton clear;
    private ImageButton undo;
    private ImageButton redo;
    private ImageButton simulate;
    private ImageButton exit;

    public HUD(final MapBuilderScreen mapBuilderScreen, SpriteBatch batch){
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
            }
        });
        building = createButton("Textures/Buttons/Wall.png");
        building.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.BUILDING);
                System.out.println("Current action: Create building");
            }
        });
        door = createButton("Textures/Buttons/Wall.png");
        door.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.DOOR);
                System.out.println("Current action: Create door");
            }
        });
        window = createButton("Textures/Buttons/Wall.png");
        window.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.WINDOW);
                System.out.println("Current action: Create window");
            }
        });
        sentryTower = createButton("Textures/Buttons/Wall.png");
        sentryTower.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.SENTRY_TOWER);
                System.out.println("Current action: Create sentry tower");
            }
        });
        hidingArea = createButton("Textures/Buttons/Wall.png");
        hidingArea.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.HIDING_AREA);
                System.out.println("Current action: Create hiding area");
            }
        });
        targetArea = createButton("Textures/Buttons/Wall.png");
        targetArea.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.TARGET_AREA);
                System.out.println("Current action: Create targtet area");
            }
        });
        guard = createButton("Textures/Buttons/Wall.png");
        guard.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.GUARD);
                System.out.println("Current action: Create guard");
            }
        });
        intruder = createButton("Textures/Buttons/Wall.png");
        intruder.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.INTRUDER);
                System.out.println("Current action: Create intruder");
            }
        });

        load = createButton("Textures/Buttons/Wall.png");
        load.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Load map");
            }
        });
        save = createButton("Textures/Buttons/Wall.png");
        save.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                MapFileReader.saveToFile(mapBuilderScreen.mass.getMap());
                System.out.println("Current action: Save map");
            }
        });
        move = createButton("Textures/Buttons/Wall.png");
        move.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.MOVE);
                System.out.println("Current action: Move");
            }
        });
        delete = createButton("Textures/Buttons/Wall.png");
        delete.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.DELETION);
                System.out.println("Current action: Delete");
            }
        });
        clear = createButton("Textures/Buttons/Wall.png");
        clear.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Clear map");
               ArrayList<BoxObject> mapObjectList=  mapBuilderScreen.mass.map.getBoxObjects();

                for(int i=0; i<mapObjectList.size(); i++){
                    mapBuilderScreen.mass.world.destroyBody( mapBuilderScreen.mass.map.getBoxObjects().get(i).getBody());
                }
                mapObjectList.clear();
            }
        });
        undo = createButton("Textures/Buttons/Wall.png");
        undo.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Undo");
            }
        });
        redo = createButton("Textures/Buttons/Wall.png");
        redo.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Redo");
            }
        });
        simulate = createButton("Textures/Buttons/Wall.png");
        simulate.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Simulate");
            }
        });
        exit = createButton("Textures/Buttons/Wall.png");
        exit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Exit");
            }
        });

        table.add(wall).size(64);
        table.add(building).size(64);
        table.add(door).size(64);
        table.add(window).size(64);
        table.add(sentryTower).size(64);
        table.add(hidingArea).size(64);
        table.add(targetArea).size(64);
        table.add(guard).size(64);
        table.add(intruder).size(64);

        //start second row
        table.row();

        table.add(load).size(64).padBottom(10);
        table.add(save).size(64).padBottom(10);
        table.add(move).size(64).padBottom(10);
        table.add(delete).size(64).padBottom(10);
        table.add(clear).size(64).padBottom(10);
        table.add(undo).size(64).padBottom(10);
        table.add(redo).size(64).padBottom(10);
        table.add(simulate).size(64).padBottom(10);
        table.add(exit).size(64).padBottom(10);

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

    public void dispose() {
        stage.dispose();
    }

}
