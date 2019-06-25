package com.mygdx.mass.Screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.mygdx.mass.Data.Properties;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import com.mygdx.mass.MASS;
//import sun.tools.java.Constants;

public class OptionsScreen implements Screen {

    public MASS mass;
    public MainMenuScreen mainMenuScreen;

    private Skin skin;

    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;

    //Change ui skin
    private boolean neon;
    private boolean fs;
    private boolean raycasting;

    private Label notice;


    public OptionsScreen(MASS mass) {


        for(int i = 0; i<mass.getSettings().size(); i++){
            if (mass.getSettings().get(i).getName().equals("neon") && mass.getSettings().get(i).getSetting().equals("true")) {
                neon = true;
                break;
            } else {
                neon = false;
            }
        }

        for(int i = 0; i<mass.getSettings().size(); i++){
            if (mass.getSettings().get(i).getName().equals("fs") && mass.getSettings().get(i).getSetting().equals("true")) {
                fs = true;
                break;
            } else {
                fs = false;
            }
        }

        for(int i = 0; i<mass.getSettings().size(); i++){
            if (mass.getSettings().get(i).getName().equals("raycasting") && mass.getSettings().get(i).getSetting().equals("true")) {
                raycasting = true;
                break;
            } else {
                raycasting = false;
            }
        }

        this.mass = mass;

        //Chooses ui skin
        skin = new Skin(Gdx.files.internal("neon/skin/neon-ui.json"));
        atlas = new TextureAtlas("neon/skin/neon-ui.atlas");
        if (neon == false) {
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
        TextButton backButton = new TextButton("Save and go back", skin);
        if (neon == false){
            backButton = new TextButton("Save and go back", skin, "small");
//            optionsTable.defaults().pad(5.0f);
        }

        //Add listeners to buttons
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mass.mapSimulatorScreen.dispose();
                mass.mainMenuScreen = new MainMenuScreen(mass);
                ((Game) Gdx.app.getApplicationListener()).setScreen(mass.mainMenuScreen);
            }
        });

        //Add space between table rows
        optionsTable.defaults().pad(5.0f);

        //Add title
        Label title = new Label("Options",skin);
        title.setFontScale(1f,1f);
        title.setAlignment(Align.top);
        optionsTable.row();

        //Create label
        BitmapFont myFont = new BitmapFont();
        myFont.getData().setScale(1, 1);
        notice = new Label(null, new Label.LabelStyle(myFont, Color.YELLOW));

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
        uiCheck.setChecked(!neon);
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
        fsCheck.setChecked(!fs);
        buttonGroup2.add(fsCheck);
        optionsTable.add(fsCheck);
        optionsTable.row();

        //Add heading
        final Label raycastingLabel = new Label("Raycasting: ",skin);
        title.setFontScale(1f,1f);
        optionsTable.add(raycastingLabel);
        optionsTable.row();

        //Create radio buttons
        ButtonGroup<CheckBox> buttonGroup3 = new ButtonGroup<CheckBox>();
        CheckBox raycastingCheck = new CheckBox("On", skin, "radio");
        buttonGroup3.add(raycastingCheck);
        optionsTable.add(raycastingCheck);
        raycastingCheck = new CheckBox("Off", skin, "radio");
        raycastingCheck.setChecked(!raycasting);
        buttonGroup3.add(raycastingCheck);
        optionsTable.add(raycastingCheck);
        optionsTable.row();

        //Add listeners
        uiCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("ui radio clicked");
                if(neon) {
                    neon = false;
                } else {
                    neon = true;
                }
                for(int i = 0; i<mass.getSettings().size(); i++){
                    if (mass.getSettings().get(i).getName().equals("neon")) {
                            mass.getSettings().get(i).setSetting(String.valueOf(neon));
                            mass.writeSettings();
                            break;
                        }
                    }
                mass.optionsScreen = new OptionsScreen(mass);
                ((Game) Gdx.app.getApplicationListener()).setScreen(mass.optionsScreen);

//                ArrayList<Properties> temp = mass.getSettings();
//                if (temp.contains(uitrue)){
//                    temp.remove(uitrue);
//                    temp.add(uifalse);
//                    mass.writeSettings(temp);
//                } else {
//                    temp.add(uitrue);
//                    temp.remove(uifalse);
//                    mass.writeSettings(temp);
//                }
            }
        });
        fsCheck.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println("fs radio clicked");
                if(fs) {
                    fs = false;
                } else {
                    fs = true;
                }
//                notice.setText("Restart required for fullscreen");
                for(int i = 0; i<mass.getSettings().size(); i++){
                        if (mass.getSettings().get(i).getName().equals("fs")) {
                            mass.getSettings().get(i).setSetting(String.valueOf(fs));
                            mass.writeSettings();
                            break;
                        }
                    }
//                ArrayList<Properties> temp2 = mass.getSettings();
//                if (temp2.contains(fstrue)){
//                    temp2.remove(fstrue);
//                    temp2.add(fsfalse);
//                    mass.writeSettings(temp2);
//                } else {
//                    temp2.remove(fsfalse);
//                    temp2.add(fstrue);
//                    System.out.println("temp2 size: " + temp2.size());
//
//                    mass.writeSettings(temp2);
//                }

//                System.out.println(mass.getSettingStrings().toString());
            }
        });

        raycastingCheck.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println("Raycasting radio clicked");
                if(raycasting) {
                    raycasting = false;
                } else {
                    raycasting = true;
                }
                for(int i = 0; i<mass.getSettings().size(); i++){
                    if (mass.getSettings().get(i).getName().equals("raycasting")) {
                        mass.getSettings().get(i).setSetting(String.valueOf(raycasting));
                        mass.writeSettings();
                        break;
                    }
                }
            }
        });

        //Add back button and label
        optionsTable.add(backButton).colspan(3).center();
        optionsTable.row();
//        optionsTable.add(notice).colspan(3).center();

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
