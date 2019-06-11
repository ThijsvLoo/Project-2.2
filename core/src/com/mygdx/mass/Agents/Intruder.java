package com.mygdx.mass.Agents;

import box2dLight.ConeLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Algorithms.Random;
import com.mygdx.mass.BoxObject.Door;
import com.mygdx.mass.BoxObject.Window;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.Agents.Agent.AgentType.INTRUDER;
import static com.mygdx.mass.BoxObject.Door.State.CLOSED;

public class Intruder extends Agent {

    public enum State {SEARCH, ESCAPE, HIDE, COMMUNICATE, REST};
    public State currentState;

    public static final float SPRINT_SPEED = 3.0f;

    public static final float DEFAULT_VISUAL_RANGE = 7.5f;

    public static final float SPRINT_MAX_TURN_SPEED = 10.0f;
    public static final float SPRINT_REST_TIME = 10.0f;
    public static final float SPRINT_MAX_DURATION = 5.0f;

    public static final float DOOR_UNLOCK_TIME_SLOW = 12.0f;
    public static final float DOOR_UNLOCK_TIME_FAST = 5.0f;
    public static final float WINDOW_BREAK_THROUGH_TIME = 3.0f;

    protected float restTime;
    protected boolean sprinting;
    protected float sprintDuration;

    protected Door door;
    protected Window window;

    protected float doorUnlockTime;
    protected float breakThroughProgress;

    private boolean detected;
    private Vector2 entryPoint;

    public Intruder(MASS mass, Vector2 position) {
        super(mass, position);

        agentType = INTRUDER;
        moveSpeed = BASE_SPEED;
        turnSpeed = 180f;
        visualRange = DEFAULT_VISUAL_RANGE;
        viewAngle = 45.0f;
        restTime = 0;
        sprintDuration = 5.0f;
        detected = false;

        define(position);
        Filter filter = new Filter();
        filter.categoryBits = INTRUDER_BIT;
        filter.maskBits = WALL_BIT | BUILDING_BIT | DOOR_BIT | WINDOW_BIT | SENTRY_TOWER_BIT | TARGET_AREA_BIT | VISUAL_FIELD_BIT | NOISE_FIELD_BIT;
        fixture.setFilterData(filter);

//        pointLight = new PointLight(mass.rayHandler, 360, new Color(1,0,0,1), 10, body.getPosition().x, body.getPosition().y);
//        if(CONE_ENABLED == true) coneLight = new ConeLight(mass.rayHandler, 45, new Color(1,0,0,1), visualRange*5, body.getPosition().x, body.getPosition().y, (float) (body.getAngle()*180/Math.PI), viewAngle/2);
//        if(CONE_ENABLED == true) coneLight.attachToBody(body);
//        if(CONE_ENABLED == true) coneLight.setContactFilter(LIGHT_BIT, (short) 0, (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT));
        algorithm = new Random(this);
    }

    public void update(float delta) {
        if (moveSpeed > 1.4f && isMoving()) {
            if (sprintDuration > 0.0f) {
                sprintDuration -= delta;
                if (sprintDuration < 0.0f) {
                    sprintDuration = 0.0f;
                    setMoveSpeed(1.4f);
                }
            } else {
                setMoveSpeed(1.4f);
            }
        }
        if (!isMoving() && sprintDuration < SPRINT_MAX_DURATION) {
            sprintDuration += delta * SPRINT_MAX_DURATION / SPRINT_REST_TIME;
            if (sprintDuration > SPRINT_MAX_DURATION) {
                sprintDuration = SPRINT_MAX_DURATION;
            }
        }
        super.update(delta);
        if (door != null) {
            unlockDoorSlow(delta);
        }
        if (window != null) {
            breakThroughWindow(delta);
        }
    }

    public void act() {
        updateState();
        switch (currentState) {
            case SEARCH: {
                //if
            }
            case ESCAPE: {
                if (atPosition(entryPoint)) {
                    //wait 3 sec
                } else {
                    goTo(entryPoint);
                }
                break;
            }
        }
    }

    public void updateState() {
        if (!detected) { //hasn't been detected yet
            if (individualMap.getTargetAreas().isEmpty()) { //hasn't found any Target area yet
                currentState = State.SEARCH;
            } else {
                currentState = State.ESCAPE;
            }
        } else {
            if (individualMap.getTargetAreas().isEmpty()) { //hasn't found any Target area yet
                currentState = State.HIDE;
            } else {
                currentState = State.ESCAPE;
            }
        }
    }

    private void unlockDoorSlow(float delta) {
        if (door != null && door.getCurrentState() == CLOSED) {
            breakThroughProgress += delta*100/doorUnlockTime;
            if (breakThroughProgress >= 100.0f) {
                door.setCurrentState(Door.State.OPEN);
            }
        }
    }

    private void breakThroughWindow(float delta) {
        if (window != null && breakThroughProgress < 100.0f) {
            breakThroughProgress += delta*100/WINDOW_BREAK_THROUGH_TIME;
        }
    }

    public Door getDoor() { return door; }
    public Window getWindow() { return window; }
    public float getDoorUnlockTime() { return doorUnlockTime; }
    public float getBreakThroughProgress() { return breakThroughProgress; }
    public boolean getDetected() { return detected; }

    public void setDoor(Door door) { this.door = door; }
    public void setWindow(Window window) { this.window = window; }
    public void setDoorUnlockTime(float doorUnlockTime) { this.doorUnlockTime = doorUnlockTime; }
    public void setBreakThroughProgress(float breakThroughProgress) { this.breakThroughProgress = breakThroughProgress; }
    public void setDetected(boolean detected) { this.detected = detected; }

}