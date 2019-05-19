package com.mygdx.mass.Screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mass.Data.MASS;
//import com.mygdx.mass.MASS;
//import sun.tools.java.Constants;

public class OptionsScreen implements Screen {

    public MASS mass;

    private Skin skin;

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;

    //Change ui skin
    private boolean glassy = false;
    private boolean fs = false;

    public OptionsScreen(MASS mass) {
        this.mass = mass;

        //Chooses ui skin
        skin = new Skin(Gdx.files.internal("neon/skin/neon-ui.json"));
        atlas = new TextureAtlas("neon/skin/neon-ui.atlas");
        if (glassy == true) {
            skin = new Skin(Gdx.files.internal("glassy/glassyui/glassy-ui.json"));
            atlas = new TextureAtlas("glassy/glassyui/glassy-ui.atlas");
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
        Table optionsTable = new Table();

        //Set table to fill stage
        optionsTable.setFillParent(true);

        //Set alignment of contents in the table.
//        optionsTable.center();

        //Create buttons
        TextButton backButton = new TextButton("Back", skin);
        if (glassy == true){
            backButton = new TextButton("Back", skin, "small");
            optionsTable.defaults().pad(5.0f);
        }

        //Add listeners to buttons
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new MainMenuScreen(mass));
            }
        });

        //Add space between table rows
        optionsTable.defaults().pad(5.0f);

        //Add title
        Label title = new Label("Options",skin);
        title.setFontScale(1f,1f);
        title.setAlignment(Align.top);
        optionsTable.row();

        //Add heading
        Label ui = new Label("UI Style:",skin);
        title.setFontScale(1f,1f);
        optionsTable.add(ui);
        optionsTable.row();

        //Create radio buttons
        ButtonGroup<CheckBox> buttonGroup = new ButtonGroup<CheckBox>();
        CheckBox uiCheck = new CheckBox("Neon", skin, "radio");
        buttonGroup.add(uiCheck);
        optionsTable.add(uiCheck);
        uiCheck = new CheckBox("Glassy", skin, "radio");
        buttonGroup.add(uiCheck);
        optionsTable.add(uiCheck);
        optionsTable.row();

        //Add heading
        final Label fullscreen = new Label("Fullscreen: ",skin);
        title.setFontScale(1f,1f);
        optionsTable.add(fullscreen);
        optionsTable.row();

        //Create radio buttons
        ButtonGroup<CheckBox> buttonGroup2 = new ButtonGroup<CheckBox>();
        CheckBox fsCheck = new CheckBox("On", skin, "radio");
        buttonGroup2.add(fsCheck);
        optionsTable.add(fsCheck);
        fsCheck = new CheckBox("Off", skin, "radio");
        fsCheck.setChecked(true);
        buttonGroup2.add(fsCheck);
        optionsTable.add(fsCheck);
        optionsTable.row();

        //Add listeners
        uiCheck.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                if(glassy){
                    glassy = false;
                    System.out.println(glassy);
                } else {
                    glassy = true;
                    System.out.println(glassy);
                }
            }
        });
        fsCheck.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                if(fs){
                    fs = false;
                    System.out.println(fs);
                } else {
                    fs = true;
                    System.out.println(fs);
                }
            }
        });

        //Add back button
        optionsTable.add(backButton);

        //Add table to stage
        stage.addActor(optionsTable);
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
