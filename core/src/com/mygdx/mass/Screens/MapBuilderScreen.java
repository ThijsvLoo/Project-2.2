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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Scenes.MapBuilderInfo;
import com.mygdx.mass.Sensors.RayCastField;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.Scenes.MapBuilderHUD;
import org.w3c.dom.css.Rect;

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

    public MapBuilderHUD hud;
    private MapBuilderInfo info;

    private InputMultiplexer inputMultiplexer;
    private InputHandler inputHandler;

    private Vector2 p1, p2, collision;
    private RayCastCallback callback;

    public int count = 0;
    public State randomState = MapBuilderScreen.State.BUILDING;
    public int maxObjects = 20;
    public int maxSize = 50;
    public int numberOfObjects = (int) (Math.random() * maxObjects);


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

        /*p1 = new Vector2();
        p2 = new Vector2();
        collision = new Vector2();

        p1.x = 99;
        p1.y = 99;
        p2.x = 199;
        p2.y = 199;

        callback = new RayCastCallback() {
            @Override
            public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
                // 'normal' = a point. To get line, start at 'point' to 'normal'
                // 'fraction' = fraction of line length between 'point' and the collision between 0-1
                System.out.println("Collision on ray at "+fraction*100+"% of the ray with fixture: "+fixture.getFilterData().categoryBits+" from body: "+fixture.getFilterData().maskBits);
                collision.set(point);
                return 1;
            }
        };*/
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        currentState = State.NONE;
        mass.map.addOuterWalls();


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
        } if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            mass.PPM = mass.MINIMAL_ZOOM;
            mass.viewport.setUnitsPerPixel(1/mass.PPM);
            mass.viewport.update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
            ((Game) Gdx.app.getApplicationListener()).setScreen(mass.mainMenuScreen);
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
        //drawRays();


        /*world.rayCast(callback, p1, p2);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(p1, p2);
        shapeRenderer.line(collision,new Vector2(100,220));
        shapeRenderer.end();*/

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
                    if (boxObject.getObjectType().equals(BoxObject.ObjectType.WALL) && boxObject.getRectangle().overlaps(rectangle)) {
                        shapeRenderer.setColor(Color.RED);
                        break;
                    }
//                    if (boxObject.getObjectType().equals(BoxObject.ObjectType.TARGET_AREA) && currentState == State.BUILDING))
                    if (boxObject.getRectangle().overlaps(rectangle) && currentState != State.TARGET_AREA &&
                    !(boxObject.getObjectType().equals(BoxObject.ObjectType.TARGET_AREA) && currentState == State.BUILDING)){
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
//							map.undo.add(map.getBoxObjects().get(index));
							break;
						case BUILDING:
							map.getBuildings().remove(map.getBoxObjects().get(index));
//                            map.undo.add(map.getBoxObjects().get(index));
							break;
						case HIDING_AREA:
							map.getHidingAreas().remove(map.getBoxObjects().get(index));
//                            map.undo.add(map.getBoxObjects().get(index));
							break;
						case SENTRY_TOWER:
							map.getSentryTowers().remove(map.getBoxObjects().get(index));
//                            map.undo.add(map.getBoxObjects().get(index));
							break;
						case TARGET_AREA:
							map.getTargetAreas().remove(map.getBoxObjects().get(index));
//                            map.undo.add(map.getBoxObjects().get(index));
							break;
						case DOOR:
							map.getDoors().remove(map.getBoxObjects().get(index));
//                            map.undo.add(map.getBoxObjects().get(index));
							break;
						case WINDOW:
							map.getWindows().remove(map.getBoxObjects().get(index));
//                            map.undo.add(map.getBoxObjects().get(index));
							break;
					}
//                    switch(map.getAgents().get(index).getAgentType()){
//                        case GUARD:
//                            map.getGuards().remove(map.getGuards().get(index));
//                            break;
//                        case INTRUDER:
//                            map.getIntruders().remove(map.getIntruders().get(index));
//                    }
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
                        if (boxObject.getObjectType().equals(BoxObject.ObjectType.WALL) && boxObject.getRectangle().overlaps(rectangle)){
                            overlap = true;
                            break;
                        }
                        if (boxObject.getRectangle().overlaps(rectangle) && currentState != State.TARGET_AREA &&
                                !(boxObject.getObjectType().equals(BoxObject.ObjectType.TARGET_AREA) && currentState == State.BUILDING)) {
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

    public MapBuilderInfo getInfo() {
        return info;
    }

    public void random() {
        if(!randomState.equals(State.GUARD) && !randomState.equals(State.INTRUDER)) {
            for (int i = 0; i < numberOfObjects; i++) {
                int width = (int) (Math.random() * maxSize + Door.SIZE);
                int height = (int) (Math.random() * maxSize + Door.SIZE);
                int x = (int) (Math.random() * 200 - width);
                int y = (int) (Math.random() * 200 - height);

//        System.out.println(x);
//        System.out.println(y);
//        System.out.println(width);
//        System.out.println(height);

                Rectangle rectangle = new Rectangle(x, y, width, height);
                boolean overlap = false;
                if(randomState.equals(State.WALL)){
                    if(rectangle.width > rectangle.height){
//                                rectangle.height = (int) (Math.random() * 5 + Door.SIZE);
                        rectangle.height = Wall.THICKNESS;
                    } else {
//                                rectangle.width = (int) (Math.random() * 5 + Door.SIZE);
                        rectangle.width = Wall.THICKNESS;
                    }
                }

                if (rectangle.x < 0 || rectangle.y < 0 || rectangle.x > 200 || rectangle.y > 200){
                    overlap = true;
                }

                for (BoxObject boxObject : map.getBoxObjects()) {
                    Rectangle bigOne = new Rectangle(rectangle.x - (Agent.SIZE * 4), rectangle.y - (Agent.SIZE * 4), rectangle.getWidth() + (Agent.SIZE * 8), rectangle.getHeight() + (Agent.SIZE * 8));
                    if (boxObject.getObjectType().equals(BoxObject.ObjectType.WALL)
                            && boxObject.getRectangle().overlaps(bigOne)) {
                        overlap = true;
                        break;
                    }
                    if (boxObject.getRectangle().overlaps(bigOne)
                            && currentState != State.TARGET_AREA
                            && !(boxObject.getObjectType().equals(BoxObject.ObjectType.TARGET_AREA) && currentState == State.BUILDING)) {
                        overlap = true;
                        break;
                    }
                }

                if(overlap && randomState.equals(State.TARGET_AREA) && map.getTargetAreas().isEmpty()){
                    random();
                }

                if (!overlap) {
                    switch (randomState) {
                        case WALL:
                            if(rectangle.width > rectangle.height){
//                                rectangle.height = (int) (Math.random() * 5 + Door.SIZE);
                                rectangle.height = Wall.THICKNESS;
                            } else {
//                                rectangle.width = (int) (Math.random() * 5 + Door.SIZE);
                                rectangle.width = Wall.THICKNESS;
                            }
                            map.addWall(rectangle);
                            break;
                        case BUILDING:
                            map.addBuilding(rectangle);
                            int numberOfDoors = (int) (Math.random() * 2 + 1);
                            int failSafe = 0;
                            while(map.getBuildings().get(map.getBuildings().size()-1).getDoors().size() < numberOfDoors) {
                                generateEntrances(rectangle, BoxObject.ObjectType.DOOR);
                                failSafe++;
                                if (failSafe > numberOfDoors * 5){
                                    break;
                                }
                            }

                            int numberOfWindows = (int) (Math.random() * 3);
//                            System.out.println(numberOfWindows);
                            failSafe = 0;
                            while(map.getBuildings().get(map.getBuildings().size()-1).getWindows().size() < numberOfWindows) {
                                generateEntrances(rectangle, BoxObject.ObjectType.WINDOW);
                                failSafe++;
                                if (failSafe > numberOfWindows * 5){
                                    break;
                                }
                            }
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
            }
        }

        if(randomState.equals(State.GUARD) || randomState.equals(State.INTRUDER)) {
            int x = (int) (Math.random() * 190 + 5);
            int y = (int) (Math.random() * 190 + 5);
            Vector2 position = new Vector2(x, y);
            boolean goodToGo = false;

            while(goodToGo = false) {
                boolean nope = false;
                for (BoxObject object : map.getBoxObjects()) {
                    if (object.getRectangle().contains(position) && !object.getObjectType().equals(BoxObject.ObjectType.BUILDING))
                        x = (int) (Math.random() * 190 + 5);
                        y = (int) (Math.random() * 190 + 5);
                        position = new Vector2(x, y);
                        nope = true;
                    break;
                }
                if(nope = false){
                    goodToGo = true;
                }
            }

            if (inputHandler.insideMap(position)) {
                switch (randomState) {
                    case GUARD:
                        map.addGuard(position);
                        break;
                    case INTRUDER:
                        map.addIntruder(position);
                        break;
                }
            }
        }
    }

    public void random(State state){
        this.randomState = state;
        random();
    }

    public void generateMap(int maxObjects, int maxSize, int maxGuards){
        mass.getMap().clearMap();
        mass.getMap().addOuterWalls();

        numberOfObjects = 1;
        this.maxSize = 10;
        random(State.TARGET_AREA);

        this.maxObjects = maxObjects;
        this.maxSize = maxSize;
        numberOfObjects = (int) (Math.random() * maxObjects);

        random(State.BUILDING);
        random(State.WALL);
        this.maxSize = 10;
        random(State.HIDING_AREA);
        random(State.SENTRY_TOWER);
        random(State.INTRUDER);

        int numGuards = maxGuards;
//        if(maxGuards != 0) {
//            numGuards = (int) (Math.random() * maxGuards + 1);
//        }
        for(int i = 0; i < numGuards; i++){
            random(State.GUARD);
        }
    }

    public void generateEntrances(Rectangle rectangle, BoxObject.ObjectType type){

            boolean overlap = false;

            Vector2 mouse = rectangle.getPosition(new Vector2());
            float minGreyArea = Math.min(rectangle.getHeight(), rectangle.getWidth() / 1);
            float maxGreyArea = Math.max(rectangle.getHeight(), rectangle.getWidth() / 1);
            if (rectangle.getWidth() > rectangle.getHeight()) {
                mouse.x += (int) (Math.random() * maxGreyArea);
                mouse.y += (int) (Math.random() * minGreyArea);
            } else {
                mouse.x += (int) (Math.random() * minGreyArea);
                mouse.y += (int) (Math.random() * maxGreyArea);
            }

            Vector2 pointA = mouse.x - rectangle.x < rectangle.width / 2 ? new Vector2(rectangle.x, mouse.y) : new Vector2(rectangle.x + rectangle.width, mouse.y);
            Vector2 pointB = mouse.y - rectangle.y < rectangle.height / 2 ? new Vector2(mouse.x, rectangle.y) : new Vector2(mouse.x, rectangle.y + rectangle.height);
            Vector2 nearest = mouse.dst(pointA) < mouse.dst(pointB) ? pointA : pointB;
            if (nearest.equals(pointA)) {
                Rectangle rect = new Rectangle(nearest.x - Door.THICKNESS / 2, nearest.y - Door.SIZE / 2, Door.THICKNESS, Door.SIZE);
//                            building.addDoor(new Door(door)); //Should add a door object instead of rectangle
                //need fix

                for(Door door : map.getBuildings().get(map.getBuildings().size()-1).getDoors()){
                    if (door.getRectangle().overlaps(rect)){
                        overlap = true;
                    }
                }
                for(Window window : map.getBuildings().get(map.getBuildings().size()-1).getWindows()){
                    if (window.getRectangle().overlaps(rect)){
                        overlap = true;
                    }
                }
                if(!overlap) {
                    if (type.equals(BoxObject.ObjectType.DOOR)){
                        map.addDoor(rect);
                    }
                    if (type.equals(BoxObject.ObjectType.WINDOW)){
                        map.addWindow(rect);
                    }
                }

            } else if (nearest.equals(pointB)) {
                Rectangle rect = (new Rectangle(nearest.x - Door.SIZE / 2, nearest.y - Door.THICKNESS / 2, Door.SIZE, Door.THICKNESS));
                for(Door door : map.getBuildings().get(map.getBuildings().size()-1).getDoors()){
                    if (door.getRectangle().overlaps(rect)){
                        overlap = true;
                    }
                }
                for(Window window : map.getBuildings().get(map.getBuildings().size()-1).getWindows()){
                    if (window.getRectangle().overlaps(rect)){
                        overlap = true;
                    }
                }
                if(!overlap) {
                    if (type.equals(BoxObject.ObjectType.DOOR)){
                        map.addDoor(rect);
                    }
                    if (type.equals(BoxObject.ObjectType.WINDOW)){
                        map.addWindow(rect);
                    }                }
            }

        }
}
