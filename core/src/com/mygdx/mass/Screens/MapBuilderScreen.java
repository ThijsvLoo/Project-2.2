package com.mygdx.mass.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Map;
import com.mygdx.mass.Scenes.HUD;

public class MapBuilderScreen implements Screen {

    private MASS mass;

    //Camera and Viewport
    private OrthographicCamera camera;
    private ScreenViewport viewPort;

    private HUD hud;

    //Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;

    //Box2D Scale(Pixels Per Meter)
    private float PPM = 1;

    public MapBuilderScreen(MASS mass) {
        this.mass = mass;
        camera = new OrthographicCamera();
        camera.position.set(Map.WIDTH/2,Map.HEIGHT/2,0.0f);
        viewPort = new ScreenViewport(camera);
        viewPort.setUnitsPerPixel(1/PPM);

        hud = new HUD(mass.batch);

        //create our Box2D world, setting 0 gravity in X, 0 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);
        //allows for debug lines of our box2d world.
        debugRenderer = new Box2DDebugRenderer();

        Gdx.input.setInputProcessor(new InputHandler());
    }

    @Override
    public void show() {
        createOuterWall();
    }

    private void createOuterWall() {
        float thickness = 4;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        PolygonShape polygonShape = new PolygonShape();

        //North wall
        bodyDef.position.set(new Vector2(Map.WIDTH/2,Map.HEIGHT));
        polygonShape.setAsBox((Map.WIDTH + thickness)/2,thickness/2);
        world.createBody(bodyDef).createFixture(polygonShape,1.0f);

        //East wall
        bodyDef.position.set(new Vector2(Map.WIDTH,Map.HEIGHT/2));
        polygonShape.setAsBox(thickness/2,(Map.HEIGHT + thickness)/2);
        world.createBody(bodyDef).createFixture(polygonShape,1.0f);

        //South wall
        bodyDef.position.set(new Vector2(Map.WIDTH/2,0));
        polygonShape.setAsBox((Map.WIDTH + thickness)/2,thickness/2);
        world.createBody(bodyDef).createFixture(polygonShape,1.0f);

        //West wall
        bodyDef.position.set(new Vector2(0,Map.HEIGHT/2));
        polygonShape.setAsBox(thickness/2,(Map.HEIGHT + thickness)/2);
        world.createBody(bodyDef).createFixture(polygonShape,1.0f);

        polygonShape.dispose();
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= 100 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += 100 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += 100 * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= 100 * delta;
        }else if (Gdx.input.isKeyPressed(Input.Keys.I) && PPM < 30) {
            viewPort.setUnitsPerPixel(1/(++PPM));
            viewPort.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        } else if (Gdx.input.isKeyPressed(Input.Keys.O) && PPM > 1) {
            viewPort.setUnitsPerPixel(1/(--PPM));
            viewPort.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        }
    }

    public void update(float delta) {
        //handle user input
        handleInput(delta);

        hud.update(delta);

        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(238,238,238,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        mass.batch.setProjectionMatrix(camera.combined);
        mass.batch.begin();
        this.
        mass.batch.end();

        debugRenderer.render(world, camera.combined);
        world.step(1 / 60f, 6, 2);

        mass.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
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
        hud.dispose();
    }

    private class InputHandler implements InputProcessor {

        private Vector2 startDrag;
        private Vector2 endDrag;

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            float x = camera.position.x - Gdx.graphics.getWidth()/2f + screenX;
            float y = camera.position.y - Gdx.graphics.getHeight()/2f + Gdx.graphics.getHeight() - screenY;
            startDrag = new Vector2(x, y);
            endDrag = startDrag;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            float x = camera.position.x - Gdx.graphics.getWidth()/2f + screenX;
            float y = camera.position.y - Gdx.graphics.getHeight()/2f + Gdx.graphics.getHeight() - screenY;
            createRectangle(startDrag, new Vector2(x, y));
            startDrag = null;
            endDrag = null;
            return true;
        }

        private void createRectangle(Vector2 start, Vector2 end) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            PolygonShape polygonShape = new PolygonShape();

            bodyDef.position.set(new Vector2((start.x + end.x)/2,(start.y + end.y)/2));
            polygonShape.setAsBox(Math.abs(start.x - end.x)/2, Math.abs(start.y - end.y)/2);
            world.createBody(bodyDef).createFixture(polygonShape,1.0f);
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            endDrag = new Vector2(screenX, screenY);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

}
