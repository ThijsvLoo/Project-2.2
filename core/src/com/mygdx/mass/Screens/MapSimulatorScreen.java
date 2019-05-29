package com.mygdx.mass.Screens;

import box2dLight.RayHandler;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Algorithms.CapturePoint;
import com.mygdx.mass.Algorithms.Explore;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Scenes.MapSimulatorHUD;
import com.mygdx.mass.Scenes.MapSimulatorInfo;
import com.mygdx.mass.World.IndividualMap;
import com.mygdx.mass.World.Map;

import java.util.ArrayList;

import static com.mygdx.mass.BoxObject.Door.State.CLOSED;

public class MapSimulatorScreen implements Screen {

    public MASS mass;

    private OrthographicCamera camera;
    private ScreenViewport viewport;

    private SpriteBatch batch;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Map map;

    private RayHandler rayHandler;

    private ShapeRenderer shapeRenderer;

    private MapSimulatorHUD hud;
    private MapSimulatorInfo info;

    private InputMultiplexer inputMultiplexer;
    private InputHandler inputHandler;

    public MapSimulatorScreen(MASS mass) {
        this.mass = mass;
        this.camera = mass.camera;
        this.viewport = mass.viewport;
        this.batch = mass.batch;
        this.world = mass.world;
        this.debugRenderer = mass.debugRenderer;
        this.map = mass.getMap();
        this.rayHandler = mass.rayHandler;
        this.shapeRenderer = mass.shapeRenderer;

        camera.position.set(map.getWidth()/2,map.getHeight()/2,0.0f);
        viewport.setUnitsPerPixel(1/mass.PPM);

        hud = new MapSimulatorHUD(this);
        info = new MapSimulatorInfo(this, batch);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.stage);
        inputHandler = new InputHandler();
        inputMultiplexer.addProcessor(inputHandler);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private float accumulator = 0;
    private int worldSpeedFactor = 10; //how fast the world update per time unit, more steps etc
    private int unitSpeedFactor = 1; //how fast the agents update per world step, should pretty much always be 1

    public void update(float delta) {
        float timePassed = Math.min(delta, 0.20f);
        accumulator += timePassed;
        while (accumulator >= MASS.FIXED_TIME_STEP) {
            for (int i = 0; i < worldSpeedFactor; i++) {
                for (Guard guard : map.getGuards()) {
                    guard.update(MASS.FIXED_TIME_STEP*unitSpeedFactor);
                }
                for (Intruder intruder : map.getIntruders()) {
                    intruder.update(MASS.FIXED_TIME_STEP*unitSpeedFactor);
                }
                world.step(MASS.FIXED_TIME_STEP*unitSpeedFactor, 6, 2);
            }
            accumulator -= MASS.FIXED_TIME_STEP;
        }

        camera.update();

        hud.update(delta);
        info.update(delta);
    }

    public void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= MASS.CAMERA_SPEED * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += MASS.CAMERA_SPEED * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += MASS.CAMERA_SPEED * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= MASS.CAMERA_SPEED * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            ArrayList<Intruder> intuders = map.getIntruders();
            if(!intuders.isEmpty()){
                Body body = intuders.get(0).getBody();
                body.setTransform(body.getWorldCenter(), (float)((body.getAngle()-(Math.PI)*delta)));
            }

        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            ArrayList<Intruder> intuders = map.getIntruders();
            if(!intuders.isEmpty()){
                Body body = intuders.get(0).getBody();
                body.setTransform(body.getWorldCenter(), (float)(body.getAngle()+(Math.PI)*delta));
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            ArrayList<Intruder> intuders = map.getIntruders();
            if(!intuders.isEmpty()) {
                Body body = intuders.get(0).getBody();
                float x = (float) Math.cos(body.getAngle()) * Agent.BASE_SPEED*2;
                float y = (float) Math.sin(body.getAngle()) * Agent.BASE_SPEED*2;
                body.setLinearVelocity(x, y);
            }
        } else { //this block of code will always get call, messing with the general agent control, which is why i disable it
            ArrayList<Intruder> intuders = map.getIntruders();
            if(!intuders.isEmpty()) {
                Body body = intuders.get(0).getBody();
//                body.setLinearVelocity(0, 0);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.I) && mass.PPM < MASS.MAXIMAL_ZOOM) {
            mass.PPM *= 1.01;
            viewport.setUnitsPerPixel(1/mass.PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        } else if (Gdx.input.isKeyPressed(Input.Keys.O) && mass.PPM > MASS.MINIMAL_ZOOM) {
            mass.PPM /= 1.01;
            viewport.setUnitsPerPixel(1/mass.PPM);
            viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        }

//        for test purpose
//        if (Gdx.input.justTouched()) {
//            for (Agent agent : map.getAgents()) {
//                agent.getRoute().clear();
//                agent.setDestination(inputHandler.toWorldCoordinate(Gdx.input.getX(), Gdx.input.getY()));
//            }
//        }
    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0)); //anti aliasing

        Gdx.gl.glLineWidth(1);
        debugRenderer.render(world, camera.combined);

        //draw the sprites
        drawSprites();

        //draw the light effects
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();

        //draw the shapes and lines
        shapeRenderer.setProjectionMatrix(camera.combined);
        drawAgentPaths();
        drawBoxObjects();
        drawAgents();
        drawCapturePoints();

        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        mass.batch.setProjectionMatrix(info.stage.getCamera().combined);
        info.stage.draw();

        update(delta);
    }

    private void drawSprites() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.end();
    }

    private void drawAgentPaths() {
        Gdx.gl.glLineWidth(1);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.PURPLE);
        for (Agent agent : map.getAgents()) {
            Vector2 start = agent.getBody().getPosition();
            Vector2 end = agent.getDestination();
            if (end != null) {
                shapeRenderer.line(start, end);
            }
            for (Vector2 waypoint : agent.getRoute()) {
                start = end;
                end = waypoint;
                if (agent.getDestination() != null && start != null) {
                    shapeRenderer.line(start, end);
                }
            }
        }
        shapeRenderer.end();
    }

    private void drawBoxObjects() {
        Gdx.gl.glLineWidth(4);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (BoxObject boxObject : map.getBoxObjects()) {
            switch (boxObject.getObjectType()) {
                case WALL:
                    shapeRenderer.setColor(0.50f, 0.50f, 0.50f, 1.00f);
                    break;
                case BUILDING:
                    shapeRenderer.setColor(0.41f, 0.59f, 0.73f, 1.00f);
                    break;
                case DOOR:
                    Door door = (Door) boxObject;
                    if (door.getCurrentState() == CLOSED) {
                        shapeRenderer.setColor(1.00f, 1.00f, 0.00f, 1.00f);
                    } else {
                        shapeRenderer.setColor(0.00f, 1.00f, 0.00f, 1.00f);
                    }
                    break;
                case WINDOW:
                    shapeRenderer.setColor(1.00f, 1.00f, 1.00f, 1.00f);
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
        Gdx.gl.glLineWidth(4);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Agent agent: map.getAgents()) {
            switch (agent.getAgentType()) {
                case GUARD:
                    shapeRenderer.setColor(0.0f, 0.0f, 1.0f, 1.0f);
                    break;
                case INTRUDER:
                    shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
                    break;
            }
            shapeRenderer.circle(agent.getBody().getPosition().x, agent.getBody().getPosition().y, 1.0f);
        }
        shapeRenderer.end();
    }

    public void drawCapturePoints() {
        if (map instanceof IndividualMap && ((IndividualMap) map).getAgent() instanceof Guard) {
            Gdx.gl.glLineWidth(4);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
            if (((Guard) ((IndividualMap) map).getAgent()).getCapture() != null) {
                for (CapturePoint capturePoint : ((Guard) ((IndividualMap) map).getAgent()).getCapture().getCapturePoints()) {
                    shapeRenderer.circle(capturePoint.getPosition().x, capturePoint.getPosition().y, 1);
                }
            }
            shapeRenderer.end();
        }
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
//        hud.dispose();
    }

    //This is the part where mouse and keyboard events are being handle
    private class InputHandler implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Input.Keys.SPACE) {
                Explore explore = new Explore();
                for (Agent agent : MASS.map.getAgents()) {
                    explore.start(agent);
                }
            } else if (keycode == Input.Keys.T) {
                if (!mass.getMap().getAgents().isEmpty()) {
                    setMap(mass.getMap().getAgents().get(0).getIndividualMap());
                }
            } else if (keycode == Input.Keys.B) {
                setMap(mass.getMap());
            } else if (keycode == Input.Keys.M) {
                for (Intruder intruder : map.getIntruders()) {
                    intruder.setMoveSpeed(3.0f);
                }
            } else if (keycode == Input.Keys.C) {
                if (map instanceof IndividualMap && ((IndividualMap) map).getAgent() instanceof Guard) {
                    ((Guard) ((IndividualMap) map).getAgent()).getCapture().run = true;
                }
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) { return false; }

        @Override
        public boolean keyTyped(char character) { return false; }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector2 coordinates = toWorldCoordinate(screenX, screenY);
            for (int i = 0; i< mass.getMap().getBoxObjects().size(); i++) {
//                System.out.println(mass.getMap().getBoxObjects().get(i).getRectangle().toString());
                if (mass.getMap().getBoxObjects().get(i).getRectangle().contains(coordinates) ||
                    coordinates.x < 0 || coordinates.x > 200 || coordinates.y < 0 || coordinates.y > 200){
                    System.out.println("Agent cannot travel to " + coordinates.toString());
                    return true;
                }
            }
            for (Agent agent : map.getAgents()) {
                agent.goTo(coordinates);
            }
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

        @Override
        public boolean mouseMoved(int screenX, int screenY) { return false; }

        @Override
        public boolean scrolled(int amount) { return false; }

        private Vector2 toWorldCoordinate(int screenX, int screenY) {
            float x = camera.position.x - Gdx.graphics.getWidth() / mass.PPM / 2 + screenX / mass.PPM;
            float y = camera.position.y - Gdx.graphics.getHeight() / mass.PPM / 2 + (Gdx.graphics.getHeight() - screenY) / mass.PPM;
            return new Vector2(x,y);
        }

        private boolean insideMap(Vector2 point) {
            return point.x >= 0 && point.x <= map.getWidth() && point.y >= 0 && point.y <= map.getHeight();
        }
    }

    public void setMap(Map map) {
        this.map = map;
    }

}
