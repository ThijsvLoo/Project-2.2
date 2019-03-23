package com.mygdx.mass.Agents;
import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Algorithms.Algorithm;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.WorldObject;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Agent extends WorldObject implements java.io.Serializable{

    public static final float SIZE = 0.5f;
    public static final float MAX_TURN_SPEED = 180.0f;

    public enum AgentType {GUARD, INTRUDER};
    protected AgentType agentType;

    protected float moveSpeed;
    protected float turnSpeed;
    protected int turnSide; //or Counter Clock

    protected float visualRange;
    protected float viewAngle;
    protected PointLight pointLight;
    protected ConeLight coneLight;

    protected Vector2 destination;
    protected LinkedBlockingQueue<Vector2> route;
    protected Vector2 direction;
    protected Vector2 velocity;

    protected ArrayList<Object> collisions;

    protected Algorithm algorithm;

    public static int count = 0;

    public Agent(MASS mass, Vector2 position) {
        super(mass);
        define(position);
        destination = new Vector2();
        route = new LinkedBlockingQueue<Vector2>();
        direction = new Vector2();
        velocity = new Vector2();
        collisions = new ArrayList<Object>();
        count++;
    }

    //define the box2d body and put it into the box2d world
    private void define(Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(SIZE/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
    }

    public void update(float delta) {
        algorithm.act();
    }

    public void addWaypoint(Vector2 waypoint) {
        route.offer(waypoint);
    }

    //Need to recheck this part
    public void followRoute() {
        if (destination != null && atPosition(destination)) {
            destination = null;
        }
        if (destination == null && !route.isEmpty()) {
            destination = route.poll();
        }
        if (destination != null && !atPosition(destination)) {
            updateDirection();
            updateAngle();
            updateVelocity();
        } else {
            body.setLinearVelocity(0.0f,0.0f);
        }
    }

    public void updateDirection() {
        direction.x = destination.x - body.getPosition().x;
        direction.y = destination.y - body.getPosition().y;
    }

    //Turn the body to the destination direction
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

    public void updateVelocity() {
        if (turnSide == 0) {
            velocity.x = (float) Math.cos(body.getAngle())*moveSpeed;
            velocity.y = (float) Math.sin(body.getAngle())*moveSpeed;
            body.setLinearVelocity(velocity.x, velocity.y);
        } else {
            body.setLinearVelocity(0, 0);
        }
    }

    //point, coordinate, vector, position,
    public boolean atPosition(Vector2 vector2) {
        return body.getPosition().dst(vector2) < 0.05f;
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

    public void addCollision(Object collision) {
        collisions.add(collision);
    }

    public void removeCollision(Object collision) {
        collisions.remove(collision);
    }

    public AgentType getAgentType() { return agentType; }
    public float getMoveSpeed() { return moveSpeed; }
    public float getTurnSpeed() { return turnSpeed; }
    public float getVisualRange() { return visualRange; }
    public float getViewAngle() { return viewAngle; }
    public Vector2 getDestination() { return destination; }
    public LinkedBlockingQueue<Vector2> getRoute() { return route; }
    public Vector2 getDirection() { return direction; }
    public Vector2 getVelocity() { return velocity; }
    public ArrayList<Object> getCollisions() { return collisions; }

    public void setMoveSpeed(float moveSpeed) { this.moveSpeed = moveSpeed; }
    public void setTurnSpeed(float turnSpeed) { this.turnSpeed = turnSpeed; }
    public void setVisualRange(float visualRange) { this.visualRange = visualRange; }
    public void setViewAngle(float viewAngle) { this.viewAngle = viewAngle; }
    public void setDestination(Vector2 destination) { this.destination = destination; }
    public void setRoute(LinkedBlockingQueue<Vector2> route) { this.route = route; }
    public void setDirection(Vector2 direction) { this.direction = direction; }
    public void setVelocity(Vector2 velocity) { this.velocity = velocity; }
    public void setCollisions(ArrayList<Object> collisions) { this.collisions = collisions; }

    public String toString() {
        return "AgentType = "+this.agentType +" count of Agents = "+ count;
    }

}
