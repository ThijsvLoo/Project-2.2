package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Screens.*;
import com.mygdx.mass.Tools.MapFileReader;
import com.mygdx.mass.Screens.MainMenuScreen;
import com.mygdx.mass.Screens.MapBuilderScreen;

import java.util.ArrayList;

public class MapSimulatorHUD implements Disposable {

    public static final float BUTTON_SIZE = 32;
    public static final float PAD_BOTTOM = 10;

    private MASS mass;

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    public Stage stage;

    Table table;

    private ImageButton wall;
    private ImageButton building;
    private ImageButton door;
    private ImageButton window;
    private ImageButton sentryTower;

    private ImageButton load;
    private ImageButton save;
    private ImageButton move;
    private ImageButton pause;
    private ImageButton exit;

    public MapSimulatorHUD(final MapSimulatorScreen mapSimulatorScreen){
        this.mass = mapSimulatorScreen.mass;

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, mass.batch);

        table = new Table();
        table.setFillParent(true);
        table.bottom();

        //Creating buttons along with their click listener

        wall = createButton("Textures/Buttons/Wall.png");
        wall.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Create wall");
            }
        });
        building = createButton("Textures/Buttons/Building2.png");
        building.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Create building");
            }
        });
        door = createButton("Textures/Buttons/Door2.png");
        door.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Create door");
            }
        });
        window = createButton("Textures/Buttons/Window.png");
        window.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Create window");
            }
        });
        sentryTower = createButton("Textures/Buttons/Tower.png");
        sentryTower.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Create sentry tower");
            }
        });

        load = createButton("Textures/Buttons/Load.png");
        load.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Load map");
            }
        });
        save = createButton("Textures/Buttons/Save.png");
        save.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Save map");
            }
        });
        move = createButton("Textures/Buttons/Move.png");
        move.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Move");
            }
        });
        pause = createButton("Textures/Buttons/Pause.png");
        pause.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Pause");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MapBuilderScreen(mapSimulatorScreen.mass));
            }
        });
        exit = createButton("Textures/Buttons/Exit.png");
        exit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Current action: Exit Simulation");
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(mapSimulatorScreen.mass));
            }
        });

//        table.add(wall).size(BUTTON_SIZE);
//        table.add(building).size(BUTTON_SIZE);
//        table.add(door).size(BUTTON_SIZE);
//        table.add(window).size(BUTTON_SIZE);
//        table.add(sentryTower).size(BUTTON_SIZE);
//
//        //start second row
//        table.row();
//
//        table.add(load).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
//        table.add(save).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
//        table.add(move).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
        table.add(pause).size(BUTTON_SIZE).padBottom(PAD_BOTTOM);
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
