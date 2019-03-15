package com.mygdx.mass.Agents;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Algorithms.Algorithm;
import com.mygdx.mass.MASS;

import java.util.ArrayList;

public abstract class Agent implements java.io.Serializable{

    public static final float MAX_TURN_SPEED = 180.0f;

    protected int type;

    public MASS mass;

    protected World world;

    protected Body body;
    protected Fixture fixture;

    protected float moveSpeed;
    protected float turnSpeed;
    protected int turnSide; //or Counter Clock

    protected float visualRange;
    protected float viewAngle;
    protected PointLight pointLight;
    protected ConeLight coneLight;

    protected Vector2 destination;
    protected ArrayList<Vector2> path;
    protected Vector2 direction;
    protected Vector2 velocity;

    protected Algorithm algorithm;

    public static int count = 0;

    public Agent(MASS mass, Vector2 position) {
        this.mass = mass;
        world = mass.world;
        define(position);
        destination = new Vector2();
        path = new ArrayList<Vector2>();
        direction = new Vector2();
        velocity = new Vector2();
        count++;
    }

    //define the box2d body
    private void define(Vector2 position) {
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

    public void update(float delta) {
        algorithm.act();
        pointLight.setPosition(body.getPosition());
        coneLight.setPosition(body.getPosition());
        coneLight.setDirection((float) (body.getAngle()*180/Math.PI));
        coneLight.setDistance(visualRange*10);
    };

    //Move towards a destination with constant velocity
    public void move() {
        updateDirection();
        updateAngle();
        if (turnSide == 0) {
            updateVelocity();
            body.setLinearVelocity(velocity.x, velocity.y);
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    public void updateDirection() {
        direction.x = destination.x - body.getPosition().x;
        direction.y = destination.y - body.getPosition().y;
    }

    public void updateAngle() {
        float angle;
        if (Math.PI - Math.abs(Math.PI - Math.abs(Math.atan2(direction.y, direction.x) - body.getAngle())) < 5*Math.PI/180) {
            turnSide = 0;
            angle = (float) Math.atan2(direction.y, direction.x);
        } else {
            turnSide = ((Math.atan2(direction.y, direction.x) + 2 * Math.PI - body.getAngle()) % (2 * Math.PI) < Math.PI) ? 1 : -1;
            angle = (float) ((body.getAngle() + turnSide * turnSpeed * Math.PI / 180 * Gdx.graphics.getDeltaTime()) % (2 * Math.PI));
        }
        body.setTransform(body.getWorldCenter(), angle);
    }

    //So that direction always point towards destination
    public void updateVelocity() {
        velocity.x = (float) Math.cos(body.getAngle())*moveSpeed;
        velocity.y = (float) Math.sin(body.getAngle())*moveSpeed;
    }

    //get the unit vector with length 1
//    private Vector2 normalise(Vector2 vector2) {
//        float magnitude = magnitude(vector2);
//        return new Vector2(vector2.x*(1/magnitude), vector2.y*(1/magnitude));
//    }

    //magnitude is the length of a vector
//    private float magnitude(Vector2 vector2) {
//        return (float) Math.sqrt(Math.pow(vector2.x, 2) + Math.pow(vector2.y, 2));
//    }

    public int getType() { return type; }
    public Body getBody() { return body; }
    public float getMoveSpeed() { return moveSpeed; }
    public float getTurnSpeed() { return turnSpeed; }
    public float getVisualRange() { return visualRange; }
    public float getViewAngle() { return viewAngle; }
    public Vector2 getDestination() { return destination; }
    public ArrayList<Vector2> getPath() { return path; }
    public Vector2 getDirection() { return direction; }
    public Vector2 getVelocity() { return velocity; }

    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public void setTurnSpeed(float turnSpeed) { this.turnSpeed = turnSpeed; }
    public void setVisualRange(float visualRange) { this.visualRange = visualRange; }
    public void setViewAngle(float viewAngle) { this.viewAngle = viewAngle; }
    public void setDestination(Vector2 destination) { this.destination = destination; }
    public void setPath(ArrayList<Vector2> path) { this.path = path; }
    public void setDirection(Vector2 direction) { this.direction = direction; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }

    public String toString() {
        return "Type = "+this.type+" count of Agents = "+ count;
    }

}