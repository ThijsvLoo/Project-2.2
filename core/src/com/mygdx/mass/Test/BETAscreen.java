//package com.mygdx.mass.Screens;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//import com.badlogic.gdx.utils.viewport.FitViewport;
//import com.badlogic.gdx.utils.viewport.Viewport;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.physics.box2d.World;
//
//
//import static com.mygdx.mass.BoxObject.Wall.createWall;
//import static com.mygdx.mass.Test.Constants.*;
//
//
//public class BETAscreen implements Screen {
//
//
//    public String windowName = "Simulation";
//    private final Body bod;
//    private final OrthographicCamera camera;
//    private final Viewport viewPort;
//    private final World mWorld; //all physics world
//    private final SpriteBatch mBatch;
//    private final Box2DDebugRenderer mB2dr;
//
//
//
//    public static final String MAP_NAME = "new_map.tmx";
//
//    public BETAscreen() {
//        Gdx.graphics.setTitle(windowName);
//        Gdx.graphics.setWindowedMode(windowWidth,windowHeight);
//        mBatch = new SpriteBatch();
//        mWorld = new World(new Vector2(0, 0), true);
//        mB2dr = new Box2DDebugRenderer();
//        camera = new OrthographicCamera();
//        //viewPort = new ScreenViewport(camera);
//        camera.zoom = DEFAULT_ZOOM;
//        viewPort = new FitViewport(640/PPM,480/PPM,camera);
//        viewPort.apply();
//        //agent = ShapeFactory.createRectangle(new Vector2(0,0), new Vector2(64,64), BodyDef.BodyType.DynamicBody, mWorld, 0.4f);
//        bod = createWall(new Vector2(0,0), new Vector2(30,60),mWorld);
//
//
//
//    }
//
//    @Override
//    public void show() {
//
//    }
//
//    public void handleInput(float delta) {
//        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {            //testing
//            camera.position.x -= 300 * delta;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {            //testing
//            camera.position.x += 300 * delta;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {            //testing
//            camera.position.y += 300 * delta;
//        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {            //testing
//            camera.position.y -= 300 * delta;
//        }
//    }
//
//    public void update(float delta) {
//        camera.position.set(bod.getPosition(),0);
//        //handleInput(delta);
//        camera.update();
//        mWorld.step(delta, 6, 2);
//    }
//
//    @Override
//    public void render(float delta) {
//
//
//        Gdx.gl.glClearColor(0,0,0,1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        update(delta);
//        draw();
//        /*mass.batch.setProjectionMatrix(camera.combined);
//        mass.batch.begin();
//        mass.batch.draw(texture,0,0);
//        mass.batch.end();*/
//    }
//
//    public void draw() {
//        mBatch.setProjectionMatrix(camera.combined);
//        mB2dr.render(mWorld,camera.combined);
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        viewPort.update(width,height);
//    }
//
//    @Override
//    public void pause() {
//
//    }
//
//    @Override
//    public void resume() {
//
//    }
//
//    @Override
//    public void hide() {
//
//    }
//
//    @Override
//    public void dispose() {
//        mBatch.dispose();
//    }
//
//}
