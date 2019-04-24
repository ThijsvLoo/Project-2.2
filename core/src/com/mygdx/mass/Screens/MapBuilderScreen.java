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
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Scenes.MapBuilderInfo;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.Scenes.MapBuilderHUD;

import static com.mygdx.mass.BoxObject.Door.State.CLOSED;

public class MapBuilderScreen implements Screen {

    public enum State {NONE, WALL, BUILDING, DOOR, WINDOW, SENTRY_TOWER, HIDING_AREA, TARGET_AREA, GUARD, INTRUDER, MOVE, DELETION}
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

    private MapBuilderHUD hud;
    private MapBuilderInfo info;

    private InputMultiplexer inputMultiplexer;
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

        camera.position.set(map.getWidth()/2,map.getHeight()/2,0.0f);
        viewport.setUnitsPerPixel(1/mass.PPM);

        hud = new MapBuilderHUD(this, batch);
        info = new MapBuilderInfo(mass);

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.stage);
        inputHandler = new InputHandler();
        inputMultiplexer.addProcessor(inputHandler);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        currentState = State.NONE;
        createOuterWall();
    }

    private void createOuterWall() {
        //North wall
        Rectangle northWall = new Rectangle(0 - Wall.THICKNESS, map.getHeight(), map.getWidth() + 2*Wall.THICKNESS, Wall.THICKNESS);
        map.addWall(northWall);

        //East wall
        Rectangle eastWall = new Rectangle(map.getWidth(), 0 - Wall.THICKNESS, Wall.THICKNESS, map.getHeight() + 2*Wall.THICKNESS);
        map.addWall(eastWall);

        //South wall
        Rectangle southWall = new Rectangle(0 - Wall.THICKNESS, 0 - Wall.THICKNESS, map.getWidth() + 2*Wall.THICKNESS, Wall.THICKNESS);
        map.addWall(southWall);

        //West wall
        Rectangle westWall = new Rectangle(0 - Wall.THICKNESS, 0 - Wall.THICKNESS, Wall.THICKNESS, map.getHeight() + 2*Wall.THICKNESS);
        map.addWall(westWall);
    }

    private void update(float delta) {
        camera.update();
        hud.update(delta);
        info.update(delta);
    }

    private void handleInput(float delta) {
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
//        drawAgentPaths();
        drawBoxObjects();
        drawAgents();
        drawPreviewActionResult();

        //draw the hud
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
            shapeRenderer.circle(agent.getBody().getPosition().x, agent.getBody().getPosition().y, Agent.SIZE/2);
        }
        shapeRenderer.end();
    }

    private void drawPreviewActionResult() {
        Gdx.gl.glLineWidth(4);
        if ((currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA)
                && inputHandler.startDrag != null && inputHandler.endDrag != null) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            Rectangle rectangle = new Rectangle(Math.min(inputHandler.startDrag.x, inputHandler.endDrag.x),
                                                Math.min(inputHandler.startDrag.y, inputHandler.endDrag.y),
                                                Math.abs(inputHandler.startDrag.x - inputHandler.endDrag.x),
                                                Math.abs(inputHandler.startDrag.y - inputHandler.endDrag.y));

            if (rectangle.width > Door.SIZE && rectangle.height > Door.SIZE) {
                shapeRenderer.setColor(0.0f, 1.0f, 1.0f, 1.0f);
                for (BoxObject boxObject : map.getBoxObjects()) {
                    if (boxObject.getRectangle().overlaps(rectangle)) {
                        shapeRenderer.setColor(Color.RED);
                        break;
                    }
                }
            } else {
                shapeRenderer.setColor(Color.RED);
            }

            shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            shapeRenderer.end();
        } else if (currentState == State.DOOR) {
            Vector2 mouse = inputHandler.toWorldCoordinate(Gdx.input.getX(), Gdx.input.getY());
            for (Building building : map.getBuildings()) {
                Rectangle rectangle = building.getRectangle();
                if (rectangle.contains(mouse)) {
                    Vector2 pointA = mouse.x - rectangle.x < rectangle.width/2 ? new Vector2(rectangle.x, mouse.y) : new Vector2(rectangle.x + rectangle.width, mouse.y);
                    Vector2 pointB = mouse.y - rectangle.y < rectangle.height/2 ? new Vector2(mouse.x, rectangle.y) : new Vector2(mouse.x, rectangle.y + rectangle.height);
                    Vector2 nearest = mouse.dst(pointA) < mouse.dst(pointB) ? pointA : pointB;
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    shapeRenderer.setColor(1.00f, 1.00f, 0.00f, 1.00f);
                    if (nearest.equals(pointA)) {
                        shapeRenderer.rect(nearest.x - Door.THICKNESS/2, nearest.y - Door.SIZE/2, Door.THICKNESS, Door.SIZE);
                    } else if (nearest.equals(pointB)) {
                        shapeRenderer.rect(nearest.x - Door.SIZE/2, nearest.y - Door.THICKNESS/2, Door.SIZE, Door.THICKNESS);
                    }
                    shapeRenderer.end();
                }
            }
        } else if (currentState == State.WINDOW) {
            Vector2 mouse = inputHandler.toWorldCoordinate(Gdx.input.getX(), Gdx.input.getY());
            for (Building building : map.getBuildings()) {
                Rectangle rectangle = building.getRectangle();
                if (rectangle.contains(mouse)) {
                    Vector2 pointA = mouse.x - rectangle.x < rectangle.width/2 ? new Vector2(rectangle.x, mouse.y) : new Vector2(rectangle.x + rectangle.width, mouse.y);
                    Vector2 pointB = mouse.y - rectangle.y < rectangle.height/2 ? new Vector2(mouse.x, rectangle.y) : new Vector2(mouse.x, rectangle.y + rectangle.height);
                    Vector2 nearest = mouse.dst(pointA) < mouse.dst(pointB) ? pointA : pointB;
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                    shapeRenderer.setColor(1.00f, 1.00f, 1.00f, 1.00f);
                    if (nearest.equals(pointA)) {
                        shapeRenderer.rect(nearest.x - Window.THICKNESS/2, nearest.y - Window.SIZE/2, Window.THICKNESS, Window.SIZE);
                    } else if (nearest.equals(pointB)) {
                        shapeRenderer.rect(nearest.x - Window.SIZE/2, nearest.y - Window.THICKNESS/2, Window.SIZE, Window.THICKNESS);
                    }
                    shapeRenderer.end();
                }
            }
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
                startDrag = toWorldCoordinate(screenX, screenY);
                endDrag = startDrag;
                return true;
            } else if (currentState == State.DOOR) {
                Vector2 mouse = toWorldCoordinate(screenX, screenY);
                for (Building building : map.getBuildings()) {
                    Rectangle rectangle = building.getRectangle();
                    if (rectangle.contains(mouse)) {
                        Vector2 pointA = mouse.x - rectangle.x < rectangle.width/2 ? new Vector2(rectangle.x, mouse.y) : new Vector2(rectangle.x + rectangle.width, mouse.y);
                        Vector2 pointB = mouse.y - rectangle.y < rectangle.height/2 ? new Vector2(mouse.x, rectangle.y) : new Vector2(mouse.x, rectangle.y + rectangle.height);
                        Vector2 nearest = mouse.dst(pointA) < mouse.dst(pointB) ? pointA : pointB;
                        if (nearest.equals(pointA)) {
                            Rectangle rect = new Rectangle(nearest.x - Door.THICKNESS/2, nearest.y - Door.SIZE/2, Door.THICKNESS, Door.SIZE);
//                            building.addDoor(new Door(door)); //Should add a door object instead of rectangle
                            //need fix
                            map.addDoor(rect);
                        } else if (nearest.equals(pointB)) {
                            map.addDoor(new Rectangle(nearest.x - Door.SIZE/2, nearest.y - Door.THICKNESS/2, Door.SIZE, Door.THICKNESS));
                        }
                    }
                }
            } else if (currentState == State.WINDOW) {
                Vector2 mouse = toWorldCoordinate(screenX, screenY);
                for (Building building : map.getBuildings()) {
                    Rectangle rectangle = building.getRectangle();
                    if (rectangle.contains(mouse)) {
                        Vector2 pointA = mouse.x - rectangle.x < rectangle.width/2 ? new Vector2(rectangle.x, mouse.y) : new Vector2(rectangle.x + rectangle.width, mouse.y);
                        Vector2 pointB = mouse.y - rectangle.y < rectangle.height/2 ? new Vector2(mouse.x, rectangle.y) : new Vector2(mouse.x, rectangle.y + rectangle.height);
                        Vector2 nearest = mouse.dst(pointA) < mouse.dst(pointB) ? pointA : pointB;
                        if (nearest.equals(pointA)) {
                            Rectangle rect = new Rectangle(nearest.x - Window.THICKNESS/2, nearest.y - Window.SIZE/2, Window.THICKNESS, Window.SIZE);
//                            building.addWindow(new Window(window)); //Should add a window object instead of rectangle
                            //need fix
                            map.addWindow(rect);
                        } else if (nearest.equals(pointB)) {
                            map.addWindow(new Rectangle(nearest.x - Window.SIZE/2, nearest.y - Window.THICKNESS/2, Window.SIZE, Window.THICKNESS));
                        }
                    }
                }
            } else if (currentState == State.GUARD || currentState == State.INTRUDER) {
                Vector2 position = toWorldCoordinate(screenX, screenY);
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
                for(int i = 0; i < map.getBoxObjects().size(); i++){
                    if(map.getBoxObjects().get(i).getRectangle().x<position.x &&map.getBoxObjects().get(i).getRectangle().y<position.y && map.getBoxObjects().get(i).getRectangle().width+map.getBoxObjects().get(i).getRectangle().x> position.x &&
                            map.getBoxObjects().get(i).getRectangle().height + map.getBoxObjects().get(i).getRectangle().y > position.y){
                      index = i;
                      break;
                    }
                }
                if(index!=-1) {
                    mass.world.destroyBody(map.getBoxObjects().get(index).getBody());
                    //map.getBoxObjects().remove(index);
					switch(map.getBoxObjects().get(index).getObjectType()){
						case WALL:
							map.getWalls().remove(map.getBoxObjects().get(index));
							break;
						case BUILDING:
							map.getBuildings().remove(map.getBoxObjects().get(index));
							break;
						case HIDING_AREA:
							map.getHidingAreas().remove(map.getBoxObjects().get(index));
							break;
						case SENTRY_TOWER:
							map.getSentryTowers().remove(map.getBoxObjects().get(index));
							break;
						case TARGET_AREA:
							map.getTargetAreas().remove(map.getBoxObjects().get(index));
							break;
						case DOOR:
							map.getDoors().remove(map.getBoxObjects().get(index));
							break;
						case WINDOW:
							map.getWindows().remove(map.getBoxObjects().get(index));
							break;
					}
                }

            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if (currentState == State.WALL || currentState == State.BUILDING || currentState == State.SENTRY_TOWER
                    || currentState == State.HIDING_AREA || currentState == State.TARGET_AREA) {
                endDrag = toWorldCoordinate(screenX, screenY);
                if (insideMap(startDrag) && insideMap(endDrag) && Math.abs(startDrag.x - endDrag.x) > Door.SIZE && Math.abs(startDrag.y - endDrag.y) > Door.SIZE) {
                    Rectangle rectangle = new Rectangle(Math.min(startDrag.x, endDrag.x),
                                                        Math.min(startDrag.y, endDrag.y),
                                                        Math.abs(startDrag.x - endDrag.x),
                                                        Math.abs(startDrag.y - endDrag.y));
                    boolean overlap = false;
                    for (BoxObject boxObject : map.getBoxObjects()) {
                        if (boxObject.getRectangle().overlaps(rectangle)) {
                            overlap = true;
                            break;
                        }
                    }
                    if (!overlap) {
                        switch (currentState) {
                            case WALL:
                                map.addWall(rectangle);
                                //graph.convertMap();
                                break;
                            case BUILDING:
                                map.addBuilding(rectangle);
                                //graph.convertMap();
                                break;
                            case SENTRY_TOWER:
                                map.addSentryTower(rectangle);
                                //graph.convertMap();
                                break;
                            case HIDING_AREA:
                                map.addHidingArea(rectangle);
                                break;
                            case TARGET_AREA:
                                map.addTargetArea(rectangle);
                                break;
                        }
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
            return point.x >= 0 && point.x <= map.getWidth() && point.y >= 0 && point.y <= map.getHeight();
        }
    }

}
