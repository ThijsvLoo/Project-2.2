package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Map;
import com.sun.prism.image.ViewPort;

public class HUD implements Disposable {

    private OrthographicCamera camera;
    private Viewport viewport;

    public Stage stage;

    Texture texture;
    TextureRegion textureRegion;
    TextureRegionDrawable textureRegionDrawable;

    private ImageButton building;
    private ImageButton targetArea;
    private ImageButton sentryTower;

    private ImageButton save;
    private ImageButton clear;
    private ImageButton exit;

    public HUD(SpriteBatch spriteBatch){
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        stage = new Stage(viewport, spriteBatch);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom();

        texture = new Texture(Gdx.files.internal("button.jpg"));
        textureRegion = new TextureRegion(texture);
        textureRegionDrawable = new TextureRegionDrawable(textureRegion);

        building = new ImageButton(textureRegionDrawable);
        targetArea = new ImageButton(textureRegionDrawable);
        sentryTower = new ImageButton(textureRegionDrawable);

        save = new ImageButton(textureRegionDrawable);
        clear = new ImageButton(textureRegionDrawable);
        exit = new ImageButton(textureRegionDrawable);

        table.add(building);
        table.add(targetArea);
        table.add(sentryTower);

        table.row();

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
