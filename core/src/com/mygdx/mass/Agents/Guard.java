package com.mygdx.mass.Agents;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Algorithms.PredictionModel;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.MapToGraph.TSP;

import java.util.ArrayList;
import java.util.TreeMap;

import com.mygdx.mass.Sensors.GapSensor;
import com.mygdx.mass.Sensors.RayCastField;
import com.mygdx.mass.World.Map;


public class Guard extends Agent {

    public enum State {NONE, EXPLORE, PATROL, CHASE, SEARCH, INTERCEPT};
    public State currentState;
    public static final float BASE_SPEED = 1.4f;
    public static final float TOWER_VIEW_ANGLE = 30.0f;
    public static final float DEFAULT_VISUAL_RANGE = 6.0f;
    public static final float TOWER_MIN_VISUAL_RANGE = 2.0f;
    public static final float TOWER_MAX_VISUAL_RANGE = 15.0f;

    private float deafDuration;

    private ArrayList<Integer> intrudersSeen; //the list of intruders which has been known so far, as unique hascode

    private boolean onTower;
    private boolean checkInsideBuilding;

    PredictionModel predictionModel;

//    private PredictionModel predictionModel;

    public Guard(MASS mass, Vector2 position) {
        super(mass, position);

        agentType = AgentType.GUARD;
        moveSpeed = BASE_SPEED;
        turnSpeed = 180f;
        visualRange = DEFAULT_VISUAL_RANGE;
        viewAngle = 45.0f;

        intrudersSeen = new ArrayList<Integer>();
        checkInsideBuilding = false;

        predictionModel = new PredictionModel(this);

        define(position);
        Filter filter = new Filter();
        filter.categoryBits = GUARD_BIT;
        filter.maskBits = WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT | VISUAL_FIELD_BIT | NOISE_FIELD_BIT;
        fixture.setFilterData(filter);

        currentState = State.NONE;

//        pointLight = new PointLight(mass.rayHandler, 360, new Color(0,0,1,1), 10, body.getPosition().x, body.getPosition().y);
//        if(CONE_ENABLED == true) coneLight = new ConeLight(mass.rayHandler, 45, new Color(0,0,1,1), visualRange*5, body.getPosition().x, body.getPosition().y, (float) (body.getAngle()*180/Math.PI), viewAngle/2);
//        if(CONE_ENABLED == true) coneLight.attachToBody(body);
//        if(CONE_ENABLED == true) coneLight.setContactFilter(LIGHT_BIT, (short) 0, (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT));
//        algorithm = new Random(this);
//        predictionModel = new PredictionModel();
    }

    public void update(float delta) {
        updateState();
        updateAction();
        gapsensor();
        raycast();
        super.update(delta);
    }

    private void updateState() {
        if (currentState == State.NONE) {
            // keep it this way until somebody presses a button
        }
        else if (!enemyInSight.isEmpty()) {
            if (currentState != State.CHASE) {
                currentState = State.CHASE;
                destination = null;
                route.clear();
            }
//        } else if (!MASS.map.getIntruders().isEmpty()) {
//            if (currentState != State.INTERCEPT) {
//                currentState = State.INTERCEPT;
//                destination = null;
//                route.clear();
//            }
        }
        else if (!intrudersSeen.isEmpty()){
            if (currentState != State.SEARCH) {
                currentState = State.SEARCH;
                destination = null;
                route.clear();
            }
        } else if (!individualMap.getUnexploredPlaces().isEmpty()) {
            if (currentState != State.EXPLORE) {
                currentState = State.EXPLORE;
                destination = null;
                route.clear();
            }
        } else {
            if (currentState != State.PATROL) {
                currentState = State.PATROL;
                destination = null;
                route.clear();
            }
        }



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




//        if (capture != null) {
//            capture.increaseCounter(delta);
//        }
//        for (CapturePoint capturePoint : capture.getCapturePoints()) {
//            if (body.getPosition().dst(capturePoint.getPosition()) < 5.0f) {
//                capture.removePoint(capturePoint);
//                break;
//            }
//        }

    private double angleDifference(float angle1, float angle2) {
        return Math.PI - Math.abs(Math.PI - Math.abs(angle1%(Math.PI*2) - angle2%(Math.PI*2)));
    }

    private void updateAction() {
        //this will update the intercept path when the intruder changed his direction by at least 10 degree
//        if (currentState == State.INTERCEPT) {
//            if (predictionModel.getInterceptTarget() != null
//                    && predictionModel.getInterceptTarget().getTurnSide() == 0
//                    && angleDifference(predictionModel.getInterceptTarget().getBody().getAngle(), predictionModel.getInterceptTargetAngle()) > Math.toRadians(10)
//                    && angleDifference(predictionModel.getInterceptTarget().getBody().getAngle(), (float) Math.atan2(body.getPosition().y - predictionModel.getInterceptTarget().getBody().getPosition().y, body.getPosition().x - predictionModel.getInterceptTarget().getBody().getPosition().x)) < Math.PI/2) {
//
//                setDestination(null);
//                route.clear();
//            }
//        }

        if (destination == null && route.isEmpty()) {
            switch (currentState) {
                case CHASE: {
                    destination = getEnemyInSight().get(0).getBody().getPosition();
                    break;
                }
//                case INTERCEPT: {
//                    intercept(MASS.map.getIntruders().get(0));
//                    break;
//                }
                case SEARCH: {
                    search();
                    break;
                }
                case PATROL: {
                    patrol();
                    break;
                }
                case EXPLORE: {
                    explore();
                    break;
                }
            }
        }
    }

    public void patrol() {
        destination = null;
        route.clear();
        float x = (float) Math.random()*(mass.getMap().width-10) + 5;
        float y = (float) Math.random()*(mass.getMap().height-10) + 5;
        if (!checkInsideBuilding) { //patrol also include within buildings
            while(insideBuilding(x,y)) {
                x = (float) Math.random()*(mass.getMap().width-10) + 5;
                y = (float) Math.random()*(mass.getMap().height-10) + 5;
            }
        }
        goTo(new Vector2(x,y));
    }

    private boolean insideBuilding(float x, float y) {
        for (Building building : individualMap.getBuildings()) {
            if (building.getRectangle().contains(x,y)) {
                return true;
            }
        }
        for (SentryTower sentryTower : individualMap.getSentryTowers()) {
            if (sentryTower.getRectangle().contains(x,y)) {
                return true;
            }
        }
        return false;
    }

    //if the target does change direction, calculate the intercept point and go
    public void intercept(Intruder target) {
        if (target.getTurnSide() == 0) {
            Vector2 interceptPoint = predictionModel.interceptPoint(this, target);
            if (interceptPoint != null) {
                goTo(interceptPoint);
            }
        }
    }

    public void search() {

    }

//    public PredictionModel getPredictionModel() { return predictionModel; }
    public void gapsensor() {
        drawGapSensor = true;
        if(gapSensorOn) {
            fireGapSensor();
            gapSensor.showGapSensorGraphs();
        }
    }

    public void raycast(boolean forceRayCast) {
        if(forceRayCast) isRayCastOff = false;
        raycast();
        isRayCastOff = true;
    }

    public void raycast() {
        if (!super.blind && !isRayCastOff) {
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
            super.processResultsFromRayCastFields();
        }
    }

    public void setCurrentState(State state) { this.currentState = state; }

    public PredictionModel getPredictionModel() { return predictionModel; }

}