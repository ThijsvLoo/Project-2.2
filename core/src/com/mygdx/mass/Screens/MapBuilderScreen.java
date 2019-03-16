package com.mygdx.mass.Screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.BoxObject.BoxObject;
import com.mygdx.mass.BoxObject.Wall;
import com.mygdx.mass.MASS;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.Scenes.HUD;

;

public class MapBuilderScreen implements Screen {

    public enum State {NONE, WALL, BUILDING, DOOR, WINDOW, SENTRY_TOWER, HIDING_AREA, TARGET_AREA, GUARD, INTRUDER, MOVE, DELETION};
    private State currentState;

    public MASS mass;

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    private SpriteBatch batch;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Map map;

    private RayHandler rayHandler;

    private ShapeRenderer shapeRenderer;

    private HUD hud;

    private InputHandler inputHandler;

    public MapBuilderScreen(MASS mass) {
        this.mass = mass;
        this.camera = mass.camera;
        this.viewport = mass.viewport;
        this.batch = mass.batch;
        this.world = mass.world;
        this.debugRenderer = mass.debugRenderer;
        this.map = mass.map;
        this.rayHandler = mass.rayHandler;
        this.shapeRenderer = mass.shapeRenderer;

        camera.position.set(Map.width/2,Map.height/2,0.0f);
        viewport.setUnitsPerPixel(1/mass.PPM);

        hud = new HUD(this, batch);

        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.stage);
        inputHandler = new InputHandler();
        inputMultiplexer.addProcessor(inputHandler);
        Gdx.input.setInputProcessor(inputMultiplexer);

        currentState = State.NONE;
    }

    @Override
    public void show() {
        createOuterWall();
    }

    private void createOuterWall() {
        //North wall
        Rectangle northWall = new Rectangle(0 - Wall.THICKNESS, Map.height, Map.width + 2*Wall.THICKNESS, Wall.THICKNESS);
        map.addWall(northWall);

        //East wall
        Rectangle eastWall = new Rectangle(Map.width, 0 - Wall.THICKNESS, Wall.THICKNESS, Map.height + 2*Wall.THICKNESS);
        map.addWall(eastWall);

        //South wall
        Rectangle southWall = new Rectangle(0 - Wall.THICKNESS, 0 - Wall.THICKNESS, Map.width + 2*Wall.THICKNESS, Wall.THICKNESS);
        map.addWall(southWall);

        //West wall
        Rectangle westWall = new Rectangle(0 - Wall.THICKNESS, 0 - Wall.THICKNESS, Wall.THICKNESS, Map.height + 2*Wall.THICKNESS);
        map.addWall(westWall);
    }

    public void update(float delta) {
        handleInput(delta);

        for (Agent agent : map.getAgents()) {
            agent.update(delta);
        }

        camera.update();

        hud.update(delta);
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= MASS.CAMERA_SPEED * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += MASS.CAMERA_SPEED * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += MASS.CAMERA_SPEED * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= MASS.CAMERA_SPEED * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.I) && mass.PPM < MASS.MAXIMAL_ZOOM) {
            mass.PPM *= 1.01;
            viewport.setUnitsPerPixel(1/mass.PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        } if (Gdx.input.isKeyPressed(Input.Keys.O) && mass.PPM > MASS.MINIMAL_ZOOM) {
            mass.PPM /= 1.01;
            viewport.setUnitsPerPixel(1/mass.PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0)); //anti aliasing

        //update the states
        update(delta);

        //draw the sprites
        drawSprites();

        //draw the light effects
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        //draw the box2d debug objects
        debugRenderer.render(world, camera.combined);
        world.step(1 / 60f, 6, 2);

        //draw the shapes and lines
        shapeRenderer.setProjectionMatrix(camera.combined);
        drawBoxObjects();
        drawAgents();
        drawAgentPaths();
        drawMouseArea();

        //draw the hud
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    private void drawSprites() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.end();
    }

    private void drawBoxObjects() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BoxObject boxObject : map.getBoxObjects()) {
            switch (boxObject.getType()) {
                case WALL:
                    shapeRenderer.setColor(0.50f, 0.50f, 0.50f, 1.00f);
                    break;
                case BUILDING:
                    shapeRenderer.setColor(0.93f, 0.93f, 0.93f, 1.00f);
                    break;
                case SENTRY_TOWER:
                    shapeRenderer.setColor(1.00f, 0.81f, 0.00f, 1.00f);
                    break;
                case HIDING_AREA:
                    shapeRenderer.setColor(0.00f, 0.50f, 0.00f, 1.00f);
                    break;
                case TARGET_AREA:
                    shapeRenderer.setColor(1.00f, 0.45f, 0.45f, 1.00f);
                    break;
            }
            Rectangle rectangle = boxObject.getRectangle();
            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        shapeRenderer.end();
    }

    private void drawAgents() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent: map.getAgents()) {
            switch (agent.getType()) {
                case GUARD:
                    shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f);
                    break;
                case INTRUDER:
                    shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
                    break;
            }
            shapeRenderer.circle(agent.getBody().getPosition().x, agent.getBody().getPosition().y, Agent.SIZE/2);
        }
        shapeRenderer.end();
    }

    private void drawAgentPaths() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Agent agent : map.getAgents()) {
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.line(agent.getBody().getPosition(), agent.getDestination());
        }
        shapeRenderer.end();
    }

    private void drawMouseArea() {
        if (inputHandler.startDrag != null && inputHandler.endDrag != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(Math.min(inputHandler.startDrag.x, inputHandler.endDrag.x),
                    Math.min(inputHandler.startDrag.y, inputHandler.endDrag.y),
                    Math.abs(inputHandler.startDrag.x - inputHandler.endDrag.x),
                    Math.abs(inputHandler.startDrag.y - inputHandler.endDrag.y));
            shapeRenderer.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        hud.resize(width, height);
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
        hud.dispose();
    }

    public void setCurrentState(State state) {
        currentState = state;
    }

    //This is the part where mouse and keyboard events are being handle
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
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                    || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA) {
                float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
                startDrag = new Vector2(x, y);
                endDrag = startDrag;
                return true;
            } else if (currentState == State.GUARD || currentState == State.INTRUDER) {
                float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
                Vector2 position = new Vector2(x, y);
                if (insideMap(position)) {
                    switch (currentState) {
                        case GUARD:
                            map.addGuard(position);
                            break;
                        case INTRUDER:
                            map.addIntruder(position);
                            break;
                    }
                }
                return true;
            } else if (currentState== State.DELETION) {
                Vector2 position = toWorldCoordinate(screenX, screenY);
                int index =-1;
                for(int i = 0; i<map.getBoxObjects().size(); i++){
                    if(map.getBoxObjects().get(i).getRectangle().x<position.x &&map.getBoxObjects().get(i).getRectangle().y<position.y && map.getBoxObjects().get(i).getRectangle().width+map.getBoxObjects().get(i).getRectangle().x> position.x &&
                            map.getBoxObjects().get(i).getRectangle().height + map.getBoxObjects().get(i).getRectangle().y > position.y){
                      index =i;
                    }
                }
                mass.world.destroyBody( map.getBoxObjects().get(index).getBody());
                map.getBoxObjects().remove(index);
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                    || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA) {
                endDrag = toWorldCoordinate(screenX, screenY);
                if (insideMap(startDrag) && insideMap(endDrag)) {
                    Rectangle rectangle = new Rectangle(Math.min(inputHandler.startDrag.x, inputHandler.endDrag.x),
                                                        Math.min(inputHandler.startDrag.y, inputHandler.endDrag.y),
                                                        Math.abs(inputHandler.startDrag.x - inputHandler.endDrag.x),
                                                        Math.abs(inputHandler.startDrag.y - inputHandler.endDrag.y));
                    switch (currentState) {
                        case WALL:
                            map.addWall(rectangle);
                            break;
                        case BUILDING:
                            map.addBuilding(rectangle);
                            break;
                        case SENTRY_TOWER:
                            map.addSentryTower(rectangle);
                            break;
                        case HIDING_AREA:
                            map.addHidingArea(rectangle);
                            break;
                        case TARGET_AREA:
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

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                    || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA) {
                endDrag = toWorldCoordinate(screenX, screenY);
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

        private Vector2 toWorldCoordinate(int screenX, int screenY) {
            float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
            float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
            return new Vector2(x,y);
        }

        private boolean insideMap(Vector2 point) {
            return point.x >= 0 && point.x <= Map.width && point.y >= 0 && point.y <= Map.height;
        }
    }
}
