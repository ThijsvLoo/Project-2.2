package com.mygdx.mass.Agents;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Algorithms.PredictionModel;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.MapToGraph.TSP;

import java.util.ArrayList;
import com.mygdx.mass.Sensors.RayCastField;


public class Guard extends Agent {

    public enum State {NONE, EXPLORE, PATROL, CHASE, SEARCH};
    public State currentState;
    public State previousState;
    public static final float BASE_SPEED = 1.4f;
    public static final float TOWER_VIEW_ANGLE = 30.0f;
    public static final float DEFAULT_VISUAL_RANGE = 6.0f;
    public static final float TOWER_MIN_VISUAL_RANGE = 2.0f;
    public static final float TOWER_MAX_VISUAL_RANGE = 15.0f;

    private float deafDuration;

    private boolean onTower;

    private PredictionModel predictionModel;

    public Guard(MASS mass, Vector2 position) {
        super(mass, position);

        agentType = AgentType.GUARD;
        moveSpeed = BASE_SPEED;
        turnSpeed = 180f;
        visualRange = DEFAULT_VISUAL_RANGE;
        viewAngle = 45.0f;

        define(position);
        Filter filter = new Filter();
        filter.categoryBits = GUARD_BIT;
        filter.maskBits = WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT | VISUAL_FIELD_BIT | NOISE_FIELD_BIT;
        fixture.setFilterData(filter);

        currentState = State.NONE;
        previousState = State.NONE;

//        pointLight = new PointLight(mass.rayHandler, 360, new Color(0,0,1,1), 10, body.getPosition().x, body.getPosition().y);
//        if(CONE_ENABLED == true) coneLight = new ConeLight(mass.rayHandler, 45, new Color(0,0,1,1), visualRange*5, body.getPosition().x, body.getPosition().y, (float) (body.getAngle()*180/Math.PI), viewAngle/2);
//        if(CONE_ENABLED == true) coneLight.attachToBody(body);
//        if(CONE_ENABLED == true) coneLight.setContactFilter(LIGHT_BIT, (short) 0, (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT));
//        algorithm = new Random(this);
        predictionModel = new PredictionModel();
    }

    public void update(float delta) {
        super.update(delta);
        updateState();
        updateAction();
    }

    private void updateState() {
        if (!enemyInSight.isEmpty()) {
            currentState = State.CHASE;
        } else if (!predictionModel.getCapturePoints().isEmpty()){
            currentState = State.SEARCH;
        } else if (individualMap.getUnexploredPlaces().isEmpty()) {
            currentState = State.PATROL;
        } else {
            currentState = State.NONE;
        }

        if (!super.blind) {
            if (!onTower) {
                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | WINDOW_BIT);
                objectsTransparent = (short) (WINDOW_BIT);
                objectsWanted = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | WINDOW_BIT);
                rayCastFieldBuildings = new RayCastField(mass);
                super.doRayCasting(rayCastFieldBuildings, super.SIZE + 0.0000001f, super.VISIBLE_DISTANCE_BUILDING, viewAngle, "BUILDING");

                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT);
                objectsTransparent = (short) (SENTRY_TOWER_BIT);
                objectsWanted = (short) (SENTRY_TOWER_BIT);
                rayCastFieldTowers = new RayCastField(mass);
                super.doRayCasting(rayCastFieldTowers, super.SIZE + 0.0000001f, super.VISIBLE_DISTANCE_TOWER, viewAngle, "TOWER");

                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | INTRUDER_BIT | GUARD_BIT);
                objectsTransparent = (short) (INTRUDER_BIT | GUARD_BIT);
                objectsWanted = (short) (GUARD_BIT | INTRUDER_BIT);
                rayCastFieldAgents = new RayCastField(mass);
                super.doRayCasting(rayCastFieldAgents, super.SIZE + 0.0000001f, DEFAULT_VISUAL_RANGE, viewAngle, "AGENT");
            }
            else {
                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | GUARD_BIT | INTRUDER_BIT);
                objectsTransparent = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | GUARD_BIT | INTRUDER_BIT);
                objectsWanted = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | GUARD_BIT | INTRUDER_BIT);
                rayCastFieldAgents = new RayCastField(mass);
                super.doRayCasting(rayCastFieldAgents, TOWER_MIN_VISUAL_RANGE, TOWER_MAX_VISUAL_RANGE, TOWER_VIEW_ANGLE, "AGENT");
            }
        }

        processResultsFromRayCastFields();


//        if (capture != null) {
//            capture.increaseCounter(delta);
//        }
//        for (CapturePoint capturePoint : capture.getCapturePoints()) {
//            if (body.getPosition().dst(capturePoint.getPosition()) < 5.0f) {
//                capture.removePoint(capturePoint);
//                break;
//            }
//        }
    }

    private void updateAction() {
        switch (currentState) {
            case CHASE: {
                //destination = individualMap.getIntruders().get(0).getBody().getPosition();
                break;
            }
            case SEARCH: {
                break;
            }
            case PATROL: {
                break;
            }
            case EXPLORE: {
                if (previousState != State.EXPLORE) {
                    explore();
                    previousState = State.EXPLORE;
                }
                break;
            }
        }
    }

    //adapt the walk route to explore unexplored locations
    public void explore() {
        route.clear();
        TSP tsp = new TSP();
        ArrayList<Vector2> toBeExploredPoints = new ArrayList<Vector2>(individualMap.getUnexploredPlaces()); //need to adapt this to work with multiple agent
        ArrayList<Vector2> path = tsp.computePath(this, toBeExploredPoints);
        for (Vector2 wp : path) {
            addWaypoint(wp);
        }
    }

    public PredictionModel getPredictionModel() { return predictionModel; }

    public void setCurrentState(State state) { this.currentState = state; }
    public void setPreviousState(State state) { this.previousState = state; }

}