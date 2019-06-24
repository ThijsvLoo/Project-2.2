package com.mygdx.mass.Agents;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Algorithms.PredictionModel;
import com.mygdx.mass.BoxObject.Door;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.MapToGraph.TSP;

import java.util.ArrayList;
import com.mygdx.mass.Sensors.RayCastField;

import static com.mygdx.mass.BoxObject.Door.State.CLOSED;


public class Guard extends Agent {

    public enum State {NONE, EXPLORE, PATROL, CHASE, SEARCH, INTERCEPT};
    public State currentState;
    public State previousState;
    public static final float BASE_SPEED = 1.4f;
    public static final float TOWER_VIEW_ANGLE = 30.0f;
    public static final float DEFAULT_VISUAL_RANGE = 6.0f;
    public static final float TOWER_MIN_VISUAL_RANGE = 2.0f;
    public static final float TOWER_MAX_VISUAL_RANGE = 15.0f;

    private float deafDuration;
    protected Door door;
    protected float doorUnlockTime;
    protected float breakThroughProgress;

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
//        predictionModel = new PredictionModel();
    }

    public void update(float delta) {
        updateState();
        updateAction();
        super.update(delta);
        if (door != null) {
            unlockDoorSlow(delta);
        }
    }

    private void updateState() {
        if (!enemyInSight.isEmpty()) {
            currentState = State.CHASE;
            mass.mapSimulatorScreen.hud.guardState.setSelected(currentState);
//            System.out.println(currentState);
        }
//        else if (!predictionModel.getCapturePoints().isEmpty()){
//            currentState = State.SEARCH;
//        }
        else if (individualMap.getUnexploredPlaces().size() > 0) {
            currentState = State.EXPLORE;
            mass.mapSimulatorScreen.hud.guardState.setSelected(currentState);
        } else {
            currentState = State.PATROL;
            mass.mapSimulatorScreen.hud.guardState.setSelected(currentState);
        }

//        if (!super.blind && !isRayCastOff) {
//            if (!onTower) {
//                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | WINDOW_BIT);
//                objectsTransparent = (short) (WINDOW_BIT);
//                objectsWanted = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | WINDOW_BIT);
//                rayCastFieldBuildings = new RayCastField(mass);
//                super.doRayCasting(rayCastFieldBuildings, super.SIZE + 0.0000001f, super.VISIBLE_DISTANCE_BUILDING, viewAngle, "BUILDING");
//
//                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT);
//                objectsTransparent = (short) (SENTRY_TOWER_BIT);
//                objectsWanted = (short) (SENTRY_TOWER_BIT);
//                rayCastFieldTowers = new RayCastField(mass);
//                super.doRayCasting(rayCastFieldTowers, super.SIZE + 0.0000001f, super.VISIBLE_DISTANCE_TOWER, viewAngle, "TOWER");
//
//                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | INTRUDER_BIT | GUARD_BIT);
//                objectsTransparent = (short) (INTRUDER_BIT | GUARD_BIT);
//                objectsWanted = (short) (GUARD_BIT | INTRUDER_BIT);
//                rayCastFieldAgents = new RayCastField(mass);
//                super.doRayCasting(rayCastFieldAgents, super.SIZE + 0.0000001f, DEFAULT_VISUAL_RANGE, viewAngle, "AGENT");
//            }
//            else {
//                objectsToCheck = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | GUARD_BIT | INTRUDER_BIT);
//                objectsTransparent = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | GUARD_BIT | INTRUDER_BIT);
//                objectsWanted = (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | GUARD_BIT | INTRUDER_BIT);
//                rayCastFieldAgents = new RayCastField(mass);
//                super.doRayCasting(rayCastFieldAgents, TOWER_MIN_VISUAL_RANGE, TOWER_MAX_VISUAL_RANGE, TOWER_VIEW_ANGLE, "AGENT");
//            }
//        }
//
//        processResultsFromRayCastFields();
    }
    private void unlockDoorSlow(float delta) {
        if (door != null && door.getCurrentState() == CLOSED) {
            breakThroughProgress += delta*100/doorUnlockTime;
            if (breakThroughProgress >= 100.0f) {
                door.setCurrentState(Door.State.OPEN);
            }
        }
    }

    private void updateAction() {
        //updated
        switch (currentState) {
            case CHASE: {
                chase();
                break;
            }
//                case INTERCEPT: {
//                    intercept(MASS.map.getIntruders().get(0));
//                    break;
//                }
            case SEARCH: {
                if (destination == null && route.isEmpty()) {
                    search();
                }
                break;
            }
            case PATROL: {
                if (destination == null && route.isEmpty()) {
                    patrol();
                }
                break;
            }
            case EXPLORE: {
                if (destination == null && route.isEmpty()) {
                    explore();
                }
                break;
            }
        }
    }

    private void chase() {
        if (!enemyInSight.isEmpty()) {
            route.clear();
            Agent closest = enemyInSight.get(0);
            for (Agent agent : getEnemyInSight()) {
                if (agent.getBody().getPosition().dst(body.getPosition()) > closest.getBody().getPosition().dst(body.getPosition())) {
                    closest = agent;
                }
            }
            destination = closest.getBody().getPosition();
        }
    }

    //adapt the walk route to explore unexplored locations
    public void explore() {
        destination = null;
        route.clear();
        TSP tsp = new TSP();
        ArrayList<Vector2> toBeExploredPoints = new ArrayList<Vector2>(individualMap.getUnexploredPlaces());
		if(toBeExploredPoints.size() == 361){
			ArrayList<Guard> tmpGuards = (ArrayList<Guard>) mass.getMap().getGuards().clone();
			for(Guard guard: tmpGuards){
				guard.getIndividualMap().getUnexploredPlaces().clear();
			}
			int maxExplorationPoints = (int)Math.ceil((float)toBeExploredPoints.size() / mass.getMap().getGuards().size());
			for(Vector2 explorationPoint: toBeExploredPoints){
				Guard closestGuard = tmpGuards.get(0);
				double minDistance = Integer.MAX_VALUE;
				for(Guard guard: tmpGuards){
					double distance = calcDistance(guard.getBody().getPosition(), explorationPoint);
					if( distance < minDistance){
						closestGuard = guard;
						minDistance = distance;
					}
				}
				closestGuard.getIndividualMap().getUnexploredPlaces().add(explorationPoint);
				if(closestGuard.getIndividualMap().getUnexploredPlaces().size() >= maxExplorationPoints){
					tmpGuards.remove(closestGuard);
				}
			}
		}

		toBeExploredPoints = new ArrayList<Vector2>(individualMap.getUnexploredPlaces());
		ArrayList<Vector2> path = tsp.computePath(this, toBeExploredPoints);
		for (Vector2 wp : path) {
			addWaypoint(wp);
		}
    }

	double calcDistance(Vector2 object1, Vector2 object2){
		return Math.sqrt(Math.pow((object2.x - object1.x), 2) + Math.pow((object2.y - object1.y), 2));
	}

    public void patrol() {
        destination = null;
        route.clear();
        goTo(new Vector2((float) Math.random()*(mass.getMap().width-10) + 5, (float) Math.random()*(mass.getMap().height-10) + 5));
    }

    public void search() {

    }

    public PredictionModel getPredictionModel() { return predictionModel; }
    public Door getDoor() { return door; }
    public void setCurrentState(State state) { this.currentState = state; }
    public void setDoorUnlockTime(float doorUnlockTime) { this.doorUnlockTime = doorUnlockTime; }
    public void setPreviousState(State state) { this.previousState = state; }
    public void setDoor(Door door) { this.door = door; }


    public void setBreakThroughProgress(float breakThroughProgress) { this.breakThroughProgress = breakThroughProgress; }
    public void resetState() {
        currentState = State.NONE;
        previousState = State.NONE;
    }
    public State getCurrentState(){
        return currentState;
    }
}
