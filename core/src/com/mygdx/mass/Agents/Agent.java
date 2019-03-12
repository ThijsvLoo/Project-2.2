package com.mygdx.mass.Agents;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Algorithms.Algorithm;
import com.mygdx.mass.MASS;

public abstract class Agent{

    public enum Type {SURVEILLANCE, INTRUDER};
    protected Type type;

    public MASS mass;

    protected Algorithm algorithm;

    protected World world;

    protected Body body;
    protected Fixture fixture;

    protected Vector2 position;
    protected Vector2 destination;
    protected Vector2 direction;
    protected float velocity;

    protected PointLight pointLight;
    protected ConeLight coneLight;

    public static int count = 0;

    public Agent(MASS mass, Vector2 position) {
        this.mass = mass;
        world = mass.world;
        this.position = position;
        direction = new Vector2();
        define();
        count++;
    }

    //define the box2d body
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

    public abstract void update(float delta);

    //Move towards a destination with constant velocity
    public void move() {
        updateDirection();
        Vector2 unitVector = normalise(direction);
        body.setLinearVelocity(unitVector.x*velocity, unitVector.y*velocity);
    }

    //So that direction always point towards destination
    public void updateDirection() {
        direction.x = destination.x - body.getPosition().x;
        direction.y = destination.y - body.getPosition().y;
    }

    //get the unit vector with length 1
    private Vector2 normalise(Vector2 vector2) {
        float magnitude = magnitude(vector2);
        return new Vector2(vector2.x*(1/magnitude), vector2.y*(1/magnitude));
    }

    //magnitude is the length of a vector
    private float magnitude(Vector2 vector2) {
        return (float) Math.sqrt(Math.pow(vector2.x, 2) + Math.pow(vector2.y, 2));
    }

    public Type getType() { return type; }
    public Body getBody() { return body; }
    public Vector2 getPosition() { return position; }
    public Vector2 getDestination() { return destination; }
    public Vector2 getDirection() { return direction; }
    public float getVelocity() { return velocity; }

    public void setDestination(Vector2 destination) { this.destination = destination; }
    public void setDirection(Vector2 direction) { this.direction = direction; }
    public void setVelocity(float velocity) { this.velocity = velocity; }

    public String toString() {
        return "Type = "+this.type+" count of Agents = "+ count;
    }

}
