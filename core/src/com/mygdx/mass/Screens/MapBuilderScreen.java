package com.mygdx.mass.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mass.MASS;


public class MapBuilderScreen implements Screen {

    private MASS mass;
    Texture texture;
    private Camera camera;
    private Viewport viewPort;

    public MapBuilderScreen(MASS mass) {
        this.mass = mass;
        texture = new Texture("badlogic.jpg");
        camera = new OrthographicCamera();
        viewPort = new ScreenViewport(camera);
        camera.position.set(viewPort.getScreenWidth()/2,viewPort.getScreenHeight()/2,0); //maybe
    }

    @Override
    public void show() {

    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {            //testing
            camera.position.x -= 300 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {            //testing
            camera.position.x += 300 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {            //testing
            camera.position.y += 300 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {            //testing
            camera.position.y -= 300 * delta;
        }
    }

    public void update(float delta) {
        handleInput(delta);
        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mass.batch.setProjectionMatrix(camera.combined);
        mass.batch.begin();
        mass.batch.draw(texture,0,0);
        mass.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
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

    }

}
