package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.Screens.MapBuilderScreen;

import java.util.ArrayList;

public class HUD implements Disposable {

    private MapBuilderScreen mapBuilderScreen;

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    public Stage stage;

    Table table;

    Texture texture;
    TextureRegion textureRegion;
    TextureRegionDrawable textureRegionDrawable;

    private ImageButton wall;
    private ImageButton building;
    private ImageButton sentryTower;
    private ImageButton hidingArea;
    private ImageButton targetArea;
    private ImageButton surveillance;
    private ImageButton intruder;
    private ImageButton delete;

    private ImageButton load;
    private ImageButton save;
    private ImageButton clear;
    private ImageButton exit;

    public HUD(final MapBuilderScreen mapBuilderScreen, SpriteBatch batch){
        this.mapBuilderScreen = mapBuilderScreen;

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, batch);

        table = new Table();
        table.setFillParent(true);
        table.bottom();

        texture = new Texture(Gdx.files.internal("button.jpg"));
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);

        wall = new ImageButton(textureRegionDrawable);
        wall.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.WALL);
                System.out.println("Current action: Create wall");
            }
        });
        building = new ImageButton(textureRegionDrawable);
        building.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.BUILDING);
                System.out.println("Current action: Create building");
            }
        });
        sentryTower = new ImageButton(textureRegionDrawable);
        sentryTower.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.SENTRY_TOWER);
                System.out.println("Current action: Create sentry tower");
            }
        });
        hidingArea = new ImageButton(textureRegionDrawable);
        hidingArea.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.HIDING_AREA);
                System.out.println("Current action: Create hiding area");
            }
        });
        targetArea = new ImageButton(textureRegionDrawable);
        targetArea.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.TARGET_AREA);
                System.out.println("Current action: Create targtet area");
            }
        });
        surveillance = new ImageButton(textureRegionDrawable);
        surveillance.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.SURVEILLANCE);
                System.out.println("Current action: Create surveillance");
            }
        });
        intruder = new ImageButton(textureRegionDrawable);
        intruder.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.INTRUDER);
                System.out.println("Current action: Create intruder");
            }
        });

        load = new ImageButton(textureRegionDrawable);
        load.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Load map");
            }
        });
        save = new ImageButton(textureRegionDrawable);
        save.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                MapFileReader mapFileReader = new MapFileReader();
                mapFileReader.saveToFile(mapBuilderScreen.mass.getMap());
                System.out.println("Current action: Save map");
            }
        });
        clear = new ImageButton(textureRegionDrawable);
        clear.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Clear map");
               ArrayList<BoxObject> mapObjectList=  mapBuilderScreen.mass.map.getMapObjects();

                for(int i=0; i<mapObjectList.size(); i++){
                    mapBuilderScreen.mass.world.destroyBody( mapBuilderScreen.mass.map.getMapObjects().get(i).getBody());
                }
                mapObjectList.clear();
            }
        });
        exit = new ImageButton(textureRegionDrawable);
        exit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Exit");
            }
        });
        delete = new ImageButton(textureRegionDrawable);
        delete.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                mapBuilderScreen.setCurrentState(MapBuilderScreen.State.DELETION);
                System.out.println("Current action: Delete");
            }
        });

        table.add(wall);
        table.add(building);
        table.add(sentryTower);
        table.add(hidingArea);
        table.add(targetArea);
        table.add(surveillance);
        table.add(intruder);
        table.add(delete);

        table.row();

        table.add(load).padBottom(10);
        table.add(save).padBottom(10);
        table.add(clear).padBottom(10);
        table.add(exit).padBottom(10);
        table.add(delete).padBottom(10);

        stage.addActor(table);
    }

    public void update(float dt){

    }

    public void dispose() {
        stage.dispose();
    }

}
