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
import com.mygdx.mass.MASS;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.Scenes.HUD;

;

public class MapBuilderScreen implements Screen {

    public enum State {NONE, WALL, BUILDING, SENTRY_TOWER, HIDING_AREA, TARGET_AREA, SURVEILLANCE, INTRUDER, DELETION};
    private State currentState;

    public MASS mass;

    //Camera and Viewport
    private OrthographicCamera camera;
    private ScreenViewport viewport;

    private SpriteBatch batch;

    //Box2D
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Map map;

    private RayHandler rayHandler;

    private ShapeRenderer shapeRenderer;

    private HUD hud;

    private InputHandler inputHandler;



    public MapBuilderScreen(MASS mass) {
        this.mass = mass;
        camera = mass.camera;
        viewport = mass.viewport;
        batch = mass.batch;
        world = mass.world;
        debugRenderer = mass.debugRenderer;
        map = mass.map;
        rayHandler = mass.rayHandler;
        shapeRenderer = mass.shapeRenderer;

        camera.position.set(Map.WIDTH/2,Map.HEIGHT/2,0.0f);
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
        float thickness = 4;

        //North wall
        Rectangle northWall = new Rectangle(0 - thickness, Map.HEIGHT, Map.WIDTH + 2*thickness, thickness);
        map.addWall(northWall);

        //East wall
        Rectangle eastWall = new Rectangle(Map.WIDTH, 0 - thickness, thickness, Map.HEIGHT + 2*thickness);
        map.addWall(eastWall);

        //South wall
        Rectangle southWall = new Rectangle(0 - thickness, 0 - thickness, Map.WIDTH + 2*thickness, thickness);
        map.addWall(southWall);

        //West wall
        Rectangle westWall = new Rectangle(0 - thickness, 0 - thickness, thickness, Map.HEIGHT + 2*thickness);
        map.addWall(westWall);
    }

    public void update(float delta) {
        handleInput(delta); //handle user input through polling

        for (Agent agent : map.getAgents()) {
            agent.update(delta);
        }

        camera.update();

        hud.update(delta);
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= 100 * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += 100 * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += 100 * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= 100 * delta;
        } if (Gdx.input.isKeyPressed(Input.Keys.I) && mass.PPM < 30) {
            mass.PPM *= 1.01;
            viewport.setUnitsPerPixel(1/mass.PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        } if (Gdx.input.isKeyPressed(Input.Keys.O) && mass.PPM > 1) {
            mass.PPM /= 1.01;
            viewport.setUnitsPerPixel(1/mass.PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        //anti aliasing
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.end();

        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        debugRenderer.render(world, camera.combined);
        world.step(1 / 60f, 6, 2);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BoxObject boxObject : map.getMapObjects()) {
            Rectangle rectangle = boxObject.getRectangle();
            float alpha;
            if (rectangle.contains(camera.position.x, camera.position.y)) {
                alpha = 0.2f;
            } else {
                alpha = 1.0f;
            }
            switch (boxObject.getType()) {
                case WALL:
                    shapeRenderer.setColor(0.5f, 0.5f, 0.5f, alpha);
                    break;
                case BUILDING:
                    shapeRenderer.setColor(0.8f, 0.8f, 0.8f, alpha);
                    break;
                case SENTRY_TOWER:
                    shapeRenderer.setColor(1.0f, 1.0f, 0.0f, alpha);
                    break;
                case HIDING_AREA:
                    shapeRenderer.setColor(0.0f, 1.0f, 0.0f, alpha);
                    break;
                case TARGET_AREA:
                    shapeRenderer.setColor(1.0f, 0.0f, 1.0f, alpha);
                    break;
            }
            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
        for (Agent agent: map.getAgents()) {
            switch (agent.getType()) {
                case SURVEILLANCE:
                    shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f);
                    break;
                case INTRUDER:
                    shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
                    break;
            }
            shapeRenderer.circle(agent.getBody().getPosition().x, agent.getBody().getPosition().y, 1.0f);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Agent agent : map.getAgents()) {
            shapeRenderer.setColor(Color.PURPLE);
            shapeRenderer.line(agent.getBody().getPosition(), agent.getDestination());
        }
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (inputHandler.startDrag != null && inputHandler.endDrag != null) {
//            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(Math.min(inputHandler.startDrag.x, inputHandler.endDrag.x),
                    Math.min(inputHandler.startDrag.y, inputHandler.endDrag.y),
                    Math.abs(inputHandler.startDrag.x - inputHandler.endDrag.x),
                    Math.abs(inputHandler.startDrag.y - inputHandler.endDrag.y));
            shapeRenderer.end();
        }

        batch.setProjectionMatrix(hud.stage.getCamera().combined);
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
            } else if (currentState == State.SURVEILLANCE || currentState == State.INTRUDER) {
                float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
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
            } else if (currentState== State.DELETION) {
                float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
                Vector2 position = new Vector2(x, y);
                int index =-1;
                for(int i=0; i<map.getMapObjects().size(); i++){
                    if(map.getMapObjects().get(i).getRectangle().x<position.x &&map.getMapObjects().get(i).getRectangle().y<position.y && map.getMapObjects().get(i).getRectangle().width+map.getMapObjects().get(i).getRectangle().x> position.x &&
                            map.getMapObjects().get(i).getRectangle().height + map.getMapObjects().get(i).getRectangle().y > position.y){
                      index =i;
                    }
                }
                mass.world.destroyBody( map.getMapObjects().get(index).getBody());
                map.getMapObjects().remove(index);
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                    || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA) {
                float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
                endDrag = new Vector2(x, y);
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

        private boolean insideMap(Vector2 point) {
            return point.x >= 0 && point.x <= Map.WIDTH && point.y >= 0 && point.y <= Map.HEIGHT;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                    || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA) {
                float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
                float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
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
