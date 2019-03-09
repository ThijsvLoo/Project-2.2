package com.mygdx.mass.Screens;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Surveillance;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Map;
import com.mygdx.mass.Scenes.HUD;

import java.util.ArrayList;

public class MapBuilderScreen implements Screen {

    public enum State {NONE, WALL, BUILDING, SENTRYTOWER, HIDINGAREA, TARGETAREA, SURVEILLANCE, INTRUDER};
    private State currentState;

    private MASS mass;

    //Camera and Viewport
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private float PPM = 3; //Pixels per meter

    //Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Map map;

    private RayHandler rayHandler;
    private PointLight pointLight;

    private ShapeRenderer shapeRenderer;

    private HUD hud;

    private InputHandler inputHandler;

    public MapBuilderScreen(MASS mass) {
        this.mass = mass;

        currentState = State.NONE;

        camera = new OrthographicCamera();
        camera.position.set(Map.WIDTH/2,Map.HEIGHT/2,0.0f);

        viewport = new ScreenViewport(camera);
        viewport.setUnitsPerPixel(1/PPM);

        //create our Box2D world, setting 0 gravity in X, 0 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, 0), true);
        //allows for debug lines of our box2d world.
        debugRenderer = new Box2DDebugRenderer();

        map = new Map(world);

        rayHandler = new RayHandler(world);
        rayHandler.setShadows(false);
        rayHandler.setAmbientLight(0.01f, 0.01f, 0.01f, 0.8f);
        rayHandler.setBlurNum(0);
        pointLight = new PointLight(rayHandler, 50, new Color(1,1,1,1), 200, camera.position.x, camera.position.y);

        shapeRenderer = new ShapeRenderer();

        hud = new HUD(this, mass.batch);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.stage);
        inputHandler = new InputHandler();
        inputMultiplexer.addProcessor(inputHandler);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void show() {
        createOuterWall();
    }

    private void createOuterWall() {
        Rectangle rectangle = new Rectangle();
        float thickness = 4;

        //North wall
        rectangle.set(0 - thickness, Map.HEIGHT, Map.WIDTH + 2*thickness, thickness);
        map.addWall(rectangle);

        //East wall
        rectangle.set(Map.WIDTH, 0 - thickness, thickness, Map.HEIGHT + 2*thickness);
        map.addWall(rectangle);

        //South wall
        rectangle.set(0 - thickness, 0 - thickness, Map.WIDTH + 2*thickness, thickness);
        map.addWall(rectangle);

        //West wall
        rectangle.set(0 - thickness, 0 - thickness, thickness, Map.HEIGHT + 2*thickness);
        map.addWall(rectangle);
    }

    public void update(float delta) {
        handleInput(delta); //handle user input through polling

        camera.update();

        pointLight.setPosition(camera.position.x, camera.position.y);

        hud.update(delta);
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
            PPM *= 1.01;
            viewport.setUnitsPerPixel(1/PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        } else if (Gdx.input.isKeyPressed(Input.Keys.O) && PPM > 1) {
            PPM /= 1.01;
            viewport.setUnitsPerPixel(1/PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        }
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

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

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
        rayHandler.dispose();
        pointLight.dispose();
        shapeRenderer.dispose();
        hud.dispose();
    }

    public void setCurrentState(State state) {
        currentState = state;
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
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRYTOWER
                    || currentState == State.HIDINGAREA || currentState == State.TARGETAREA) {
                float x = camera.position.x - Gdx.graphics.getWidth() / PPM / 2 + screenX / PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / PPM / 2 + (Gdx.graphics.getHeight() - screenY) / PPM;
                startDrag = new Vector2(x, y);
                endDrag = startDrag;
                return true;
            } else if (currentState == State.SURVEILLANCE || currentState == State.INTRUDER) {
                float x = camera.position.x - Gdx.graphics.getWidth() / PPM / 2 + screenX / PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / PPM / 2 + (Gdx.graphics.getHeight() - screenY) / PPM;
                Vector2 position = new Vector2(x, y);
                if (insideMap(position)) {
                    switch (currentState) {
                        case SURVEILLANCE:
                            map.addSurveillance(position);
                            break;
                        case INTRUDER:
                            map.addIntruder(position);
                            break;
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRYTOWER
                    || currentState == State.HIDINGAREA || currentState == State.TARGETAREA) {
                float x = camera.position.x - Gdx.graphics.getWidth() / PPM / 2 + screenX / PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / PPM / 2 + (Gdx.graphics.getHeight() - screenY) / PPM;
                endDrag = new Vector2(x, y);
                if (insideMap(startDrag) && insideMap(endDrag)) {
                    Rectangle rectangle = new Rectangle(Math.min(inputHandler.startDrag.x, inputHandler.endDrag.x),
                                                        Math.min(inputHandler.startDrag.y, inputHandler.endDrag.y),
                                                        Math.abs(inputHandler.startDrag.x - inputHandler.endDrag.x),
                                                        Math.abs(inputHandler.startDrag.y - inputHandler.endDrag.y));
                    switch (currentState) {
                        case BUILDING:
                            map.addBuilding(rectangle);
                            break;
                        case SENTRYTOWER:
                            map.addSentryTower(rectangle);
                            break;
                        case HIDINGAREA:
                            map.addHidingArea(rectangle);
                            break;
                        case TARGETAREA:
                            map.addTargetArea(rectangle);
                            break;
                    }
                }
                startDrag = null;
                endDrag = null;
                return true;
            }
            return false;
        }

        private boolean insideMap(Vector2 point) {
            return point.x >= 0 && point.x <= Map.WIDTH && point.y >= 0 && point.y <= Map.HEIGHT;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRYTOWER
                    || currentState == State.HIDINGAREA || currentState == State.TARGETAREA) {
                float x = camera.position.x - Gdx.graphics.getWidth() / PPM / 2 + screenX / PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / PPM / 2 + (Gdx.graphics.getHeight() - screenY) / PPM;
                endDrag = new Vector2(x, y);
                return true;
            }
            return false;
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
