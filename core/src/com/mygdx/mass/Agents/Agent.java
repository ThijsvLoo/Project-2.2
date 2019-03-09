package com.mygdx.mass.Agents;
import box2dLight.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.MASS;

public abstract class Agent{

    public enum Type {SURVEILLANCE, INTRUDER};
    protected Type type;

    protected MASS mass;

    protected World world;

    protected Body body;
    protected Fixture fixture;

    protected Vector2 position;

    protected PointLight pointLight;

//    String type;
//    int state;
//    int count;
//    int velocity;

    public Agent(MASS mass, Vector2 position) {
        this.mass = mass;
        world = mass.world;
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

    public Type getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

//    public String toString() {
//        return "Type = "+this.type+" count of Agents = "+this.count;
//    }

}
