package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;

public class PredictionModel {

    private Intruder interceptTarget;
    private float interceptTargetAngle;
    public ArrayList<PredictionPoint> guardMoves = new ArrayList<PredictionPoint>(); //for the sake of visualisation
    public ArrayList<PredictionPoint> intruderMoves = new ArrayList<PredictionPoint>(); //for the sake of visualisation

    private Agent agent;

    public PredictionModel(Agent agent) {
        this.agent = agent;
    }

    //advance the growth of the prediction points
    private boolean expandRandom(ArrayList<PredictionPoint> allPoints, float delta) {
        ArrayList<PredictionPoint> tempPoints = new ArrayList<PredictionPoint>();
        for (PredictionPoint predictionPoint : allPoints) {
            float direction = predictionPoint.getDirection();
            Vector2 destination = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction) * delta * 1.4f),
                                        (float) (predictionPoint.getPosition().y + Math.sin(direction) * delta * 1.4f));
            if (!isPathBlocked(predictionPoint.getPosition(), destination)) {
                addTempPoint(allPoints, tempPoints, destination, direction, predictionPoint.getTime() + delta);
            }

            float direction2 = (float) (predictionPoint.getDirection() + Math.PI / 4);
            Vector2 destination2 = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction2) * (delta - 0.25f) * 1.4f),
                                        (float) (predictionPoint.getPosition().y + Math.sin(direction2) * (delta - 0.25f) * 1.4f));
            if (!isPathBlocked(predictionPoint.getPosition(), destination2)) {
                addTempPoint(allPoints, tempPoints, destination2, direction2, predictionPoint.getTime() + delta);
            }

            float direction3 = (float) (predictionPoint.getDirection() - Math.PI / 4);
            Vector2 destination3 = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction3) * (delta - 0.25f) * 1.4f),
                                        (float) (predictionPoint.getPosition().y + Math.sin(direction3) * (delta - 0.25f) * 1.4f));
            if (!isPathBlocked(predictionPoint.getPosition(), destination3)) {
                addTempPoint(allPoints, tempPoints, destination3, direction3, predictionPoint.getTime() + delta);
            }
        }
        allPoints.addAll(tempPoints);
        return !tempPoints.isEmpty();
    }

    private boolean expandStraight(ArrayList<PredictionPoint> allPoints, float delta) {
        ArrayList<PredictionPoint> tempPoints = new ArrayList<PredictionPoint>();
        for (PredictionPoint predictionPoint : allPoints) {
            float direction = predictionPoint.getDirection();
            Vector2 destination = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction) * delta * 1.4f),
                    (float) (predictionPoint.getPosition().y + Math.sin(direction) * delta * 1.4f));
            if (!isPathBlocked(predictionPoint.getPosition(), destination)) {
                addTempPoint(allPoints, tempPoints, destination, direction, predictionPoint.getTime() + delta);
            }
        }
        allPoints.addAll(tempPoints);
        return !tempPoints.isEmpty();
    }

    private void addTempPoint(ArrayList<PredictionPoint> allPoints, ArrayList<PredictionPoint> tempPoints, Vector2 position, float direction, float time) {
        if (!insideMap(position)) {
            return;
        }
        for (PredictionPoint predictionPoint : allPoints) {
            if (position.dst(predictionPoint.getPosition()) < 6.0f) {
                return;
            }
        }
        for (PredictionPoint predictionPoint : tempPoints) {
            if (position.dst(predictionPoint.getPosition()) < 6.0f) {
                return;
            }
        }
        tempPoints.add(new PredictionPoint(position, direction, time));
    }

//    public PredictionPoint getBestPoint(Guard guard, ArrayList<Guard> guards) {
//        PredictionPoint bestPoint = predictionPoints.get(0);
//        for (PredictionPoint predictionPoint : predictionPoints) {
//            if (predictionPoint.computeScore(guard, guards) > bestPoint.computeScore(guard, guards)) {
//                bestPoint = predictionPoint;
//            }
//        }
//        return bestPoint;
//    }

//    public void increaseCounter(float delta) {
//        if (run) {
//            counter += delta;
//            if (counter >= 6.0f) {
//                expand(6.0f);
//                counter = 0.0f;
//            }
//        }
//    }

//    public void update() {
//        if (predictionModel != null) {
//            predictionModel.increaseCounter(delta);
//        }
//        for (PredictionPoint predictionPoint : predictionModel.getPredictionPoints()) {
//            if (body.getPosition().dst(predictionPoint.getPosition()) < 5.0f) {
//                predictionModel.removePoint(predictionPoint);
//                break;
//            }
//        }
//    }

    //calculate the point where a guard can intercept an intruder
    public Vector2 interceptPoint(Agent guard, Intruder intruder) {
        this.interceptTarget = intruder;
        this.interceptTargetAngle = intruder.getBody().getAngle();

        ArrayList<PredictionPoint> guardMoves = new ArrayList<PredictionPoint>();
        ArrayList<PredictionPoint> intruderMoves = new ArrayList<PredictionPoint>();

        this.guardMoves = guardMoves; //for the sake of testing the visual
        this.intruderMoves = intruderMoves; //for the sake of testing the visual

        //this basically give the angle that point towards the direction where the intruder is headed
        float distance = guard.getBody().getPosition().dst(interceptTarget.getBody().getPosition());
        Vector2 estimatePoint = new Vector2((float) (interceptTarget.getBody().getPosition().x + Math.cos(interceptTarget.getBody().getAngle() * distance)),
                                            (float) (interceptTarget.getBody().getPosition().y + Math.sin(interceptTarget.getBody().getAngle() * distance)));
        float guardAngle = (float) Math.atan2(estimatePoint.y - guard.getBody().getPosition().y, estimatePoint.x - guard.getBody().getPosition().x);

        guardMoves.add(new PredictionPoint(guard.getBody().getPosition(), guardAngle,0));
        intruderMoves.add(new PredictionPoint(intruder.getBody().getPosition(), intruder.getBody().getAngle(),0));
        Vector2 interceptPoint;
        while ((interceptPoint = getInterceptPoint(guardMoves, intruderMoves)) == null) {
            if (!expandRandom(guardMoves, 6.0f)) {
                return null;
            }
            if (!expandStraight(intruderMoves, 6.0f)) {
                return null;
            }
        }
        return interceptPoint;
    }

    private Vector2 getInterceptPoint(ArrayList<PredictionPoint> guardMoves, ArrayList<PredictionPoint> intruderMoves) {
        for (PredictionPoint guardMove : guardMoves) {
            for (PredictionPoint intruderMove : intruderMoves) {
                if (guardMove.getPosition().dst(intruderMove.getPosition()) < 6.0f && guardMove.getTime() == intruderMove.getTime()) {
                    return guardMove.getPosition();
                }
            }
        }
        return null;
    }

    //check if a path is blocked by a wall or building etc
    private boolean isPathBlocked(Vector2 start, Vector2 end) {
        for (Building building : agent.getIndividualMap().getBuildings()) {
            if (Intersector.intersectSegmentRectangle(start, end, building.getRectangle())) {
                return true;
            }
        }
        for (SentryTower sentryTower : agent.getIndividualMap().getSentryTowers()) {
            if (Intersector.intersectSegmentRectangle(start, end, sentryTower.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    //check if a vector2 point is inside the map
    private boolean insideMap(Vector2 position) {
        return position.x > 0 && position.x < MASS.map.width && position.y > 0 && position.y < MASS.map.height;
    }

    public Intruder getInterceptTarget() { return interceptTarget; }
    public float getInterceptTargetAngle() { return interceptTargetAngle; }

}
