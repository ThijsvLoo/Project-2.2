package com.mygdx.mass.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mass.MASS;

import java.awt.*;
import java.util.ArrayList;

public class MapBuilderScreen implements Screen {

    private MASS mass;
    private Camera camera;
    private Viewport viewPort;

    //Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;

    public MapBuilderScreen(MASS mass) {
        this.mass = mass;
        camera = new OrthographicCamera();
        viewPort = new ScreenViewport(camera);

        //create our Box2D world, setting 0 gravity in X, 0 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);
        //allows for debug lines of our box2d world.
        debugRenderer = new Box2DDebugRenderer();

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

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            Body body;
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(50,50);
            body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(32/2,32/2);

            body.createFixture(shape, 1.0f);
            shape.dispose();
        }
    }

    public void update(float delta) {
        //handle user input
        handleInput(delta);

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        camera.update();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(238,238,238,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mass.batch.setProjectionMatrix(camera.combined);
        mass.batch.begin();

        mass.batch.end();

        debugRenderer.render(world, camera.combined);
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
        world.dispose();
        debugRenderer.dispose();
    }

}
