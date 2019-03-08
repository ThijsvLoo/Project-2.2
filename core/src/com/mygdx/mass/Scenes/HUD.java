package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HUD implements Disposable {

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    public Stage stage;

    Table table;

    Texture texture;
    TextureRegion textureRegion;
    TextureRegionDrawable textureRegionDrawable;

    private ImageButton building;
    private ImageButton sentryTower;
    private ImageButton hidingArea;
    private ImageButton targetArea;

    private ImageButton load;
    private ImageButton save;
    private ImageButton clear;
    private ImageButton exit;

    public HUD(SpriteBatch batch){
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, batch);

        table = new Table();
        table.setFillParent(true);
        table.bottom();

        texture = new Texture(Gdx.files.internal("button.jpg"));
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);

        building = new ImageButton(textureRegionDrawable);
        sentryTower = new ImageButton(textureRegionDrawable);
        hidingArea = new ImageButton(textureRegionDrawable);
        targetArea = new ImageButton(textureRegionDrawable);

        load = new ImageButton(textureRegionDrawable);
        save = new ImageButton(textureRegionDrawable);
        clear = new ImageButton(textureRegionDrawable);
        exit = new ImageButton(textureRegionDrawable);

        table.add(building);
        table.add(sentryTower);
        table.add(hidingArea);
        table.add(targetArea);

        table.row();

        table.add(load).padBottom(10);
        table.add(save).padBottom(10);
        table.add(clear).padBottom(10);
        table.add(exit).padBottom(10);

        stage.addActor(table);
    }

    public void update(float dt){

    }

    public void dispose() {
        stage.dispose();
    }

}
