package com.mygdx.mass.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private ScreenViewport viewport;
    private float PPM = 3; //Pixels per meter

    //Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private ShapeRenderer shapeRenderer;

    private HUD hud;

    private InputHandler inputHandler;

    public MapBuilderScreen(MASS mass) {
        this.mass = mass;

        camera = new OrthographicCamera();
        camera.position.set(Map.WIDTH/2,Map.HEIGHT/2,0.0f);

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1/PPM);

        //create our Box2D world, setting 0 gravity in X, 0 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);
        //allows for debug lines of our box2d world.
        debugRenderer = new Box2DDebugRenderer();

        shapeRenderer = new ShapeRenderer();

        hud = new HUD(mass.batch);

        inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);
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
            PPM += 0.02;
            viewport.setUnitsPerPixel(1/PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        } else if (Gdx.input.isKeyPressed(Input.Keys.O) && PPM > 1) {
            PPM -= 0.02;
            viewport.setUnitsPerPixel(1/PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        }
    }

    public void update(float delta) {
        handleInput(delta); //handle user input through polling

        camera.update();

        hud.update(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(delta);

        mass.batch.setProjectionMatrix(camera.combined);
        mass.batch.begin();
        mass.batch.end();

        debugRenderer.render(world, camera.combined);
        world.step(1 / 60f, 6, 2);

        if (inputHandler.startDrag != null && inputHandler.endDrag != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(Math.min(inputHandler.startDrag.x, inputHandler.endDrag.x),
                    Math.min(inputHandler.startDrag.y, inputHandler.endDrag.y),
                    Math.abs(inputHandler.startDrag.x - inputHandler.endDrag.x),
                    Math.abs(inputHandler.startDrag.y - inputHandler.endDrag.y));
            shapeRenderer.end();
        }

        mass.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
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
            float x = camera.position.x - Gdx.graphics.getWidth()/PPM/2 + screenX/PPM;
            float y = camera.position.y - Gdx.graphics.getHeight()/PPM/2 + (Gdx.graphics.getHeight() - screenY)/PPM;
            startDrag = new Vector2(x, y);
            endDrag = startDrag;
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            float x = camera.position.x - Gdx.graphics.getWidth()/PPM/2 + screenX/PPM;
            float y = camera.position.y - Gdx.graphics.getHeight()/PPM/2 + (Gdx.graphics.getHeight() - screenY)/PPM;
            endDrag = new Vector2(x, y);
            createRectangle(startDrag, endDrag);
            startDrag = null;
            endDrag = null;
            return true;
        }

        private void createRectangle(Vector2 start, Vector2 end) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(new Vector2((start.x + end.x)/2,(start.y + end.y)/2));

            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(Math.abs(start.x - end.x)/2, Math.abs(start.y - end.y)/2);

            world.createBody(bodyDef).createFixture(polygonShape,1.0f);
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            float x = camera.position.x - Gdx.graphics.getWidth()/PPM/2 + screenX/PPM;
            float y = camera.position.y - Gdx.graphics.getHeight()/PPM/2 + (Gdx.graphics.getHeight() - screenY)/PPM;
            endDrag = new Vector2(x, y);
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
