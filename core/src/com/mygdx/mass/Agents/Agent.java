package com.mygdx.mass.Agents;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Map;
import javafx.scene.PointLight;

public abstract class Agent{

    protected World world;

    protected Body body;
    protected Fixture fixture;

    protected Vector2 position;

//    String type;
//    int state;
//    int count;
//    int velocity;

    public Agent(World world, Vector2 position) {
        this.world = world;
        this.position = position;
        define();
    }

    private void define() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.5f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public Vector2 getPosition() {
        return position;
    }

//    public String toString() {
//        return "Type = "+this.type+" count of Agents = "+this.count;
//    }

}
