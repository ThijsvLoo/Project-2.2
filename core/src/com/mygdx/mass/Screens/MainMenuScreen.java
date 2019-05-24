package com.mygdx.mass.Screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mass.Data.MASS;
//import com.mygdx.mass.MASS;
//import sun.tools.java.Constants;

public class MainMenuScreen implements Screen {

    public MASS mass;

    private Skin skin;

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;

    //Change ui skin
    private boolean glassy = false;


    public MainMenuScreen(MASS mass) {
        this.mass = mass;

        for(int i = 0; i<mass.getSettings().size(); i++){
            if (mass.getSettings().get(i).getName().equals("neon") && mass.getSettings().get(i).getSetting().equals("true")) {
                glassy = false;
                break;
            } else {
                glassy = true;
            }
        }

        //Chooses ui skin
        if (glassy == true) {
            skin = new Skin(Gdx.files.internal("glassy/glassyui/glassy-ui.json"));
            atlas = new TextureAtlas("glassy/glassyui/glassy-ui.atlas");
        } else {
            skin = new Skin(Gdx.files.internal("neon/skin/neon-ui.json"));
            atlas = new TextureAtlas("neon/skin/neon-ui.atlas");
        }

        batch = mass.batch;
        camera = mass.camera;
        viewport = mass.viewport;
        viewport.apply();
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, -5);
        camera.update();

        stage = new Stage(viewport, batch);


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Reduces button size
        mainTable.setTransform(true);

        //Set alignment of contents in the table.
        mainTable.center();

        //Create Label
        Label welcome = new Label("Multi-Agent Surveillance System",skin);
        welcome.setFontScale(1f,1f);
        //Group top = new Group();
        //top.addActor(welcome);
        //top.setScale(2f,2f);


        //Create buttons
        TextButton simulateButton;
        TextButton builderButton;
        TextButton optionsButton;
        TextButton exitButton;

        if(glassy == false) {
            simulateButton = new TextButton("Simulation", skin);
            builderButton = new TextButton("Map builder", skin);
            optionsButton = new TextButton("Options", skin);
            exitButton = new TextButton("Exit", skin);
        } else {
            simulateButton = new TextButton("Simulation", skin, "small");
            builderButton = new TextButton("Map builder", skin, "small");
            optionsButton = new TextButton("Options", skin, "small");
            exitButton = new TextButton("Exit", skin, "small");
            mainTable.defaults().pad(5.0f);
        }

        //Add listeners to buttons
        builderButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MapBuilderScreen(mass));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        simulateButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MapBuilderScreen(mass));
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MapSimulatorScreen(mass));
            }
        });
        optionsButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new OptionsScreen(mass));
            }
        });


        mainTable.add(welcome);
        mainTable.row();
        mainTable.add(simulateButton);
        mainTable.row();
        mainTable.add(builderButton);
        mainTable.row();
        mainTable.add(optionsButton);
        mainTable.row();
        mainTable.add(exitButton);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
            viewport.update(width, height);
            camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
            camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }

}
