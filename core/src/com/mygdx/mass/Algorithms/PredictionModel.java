package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;

public class PredictionModel {

    private Agent agent;

    private ArrayList<PredictionPoint> allPoints;
    private ArrayList<PredictionPoint> latestPoints;


    public PredictionModel(Agent agent) {
        this.agent = agent;

        allPoints = new ArrayList<PredictionPoint>();
        latestPoints = new ArrayList<PredictionPoint>();
    }

//    //advance the growth of the prediction points
//    private void expandRandom(ArrayList<PredictionPoint> predictionPoints, float delta) {
//        ArrayList<PredictionPoint> tempPoints = new ArrayList<PredictionPoint>();
//        for (PredictionPoint predictionPoint : predictionPoints) {
//            float direction = predictionPoint.getDirection();
//            Vector2 destination = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction) * delta * 1.4f),
//                                        (float) (predictionPoint.getPosition().y + Math.sin(direction) * delta * 1.4f));
//            if (!isPathBlocked(predictionPoint.getPosition(), destination)) {
//                addTempPoint(destination, direction, predictionPoint.getTime() + delta);
//            }
//
//            float direction2 = (float) (predictionPoint.getDirection() + Math.PI / 4);
//            Vector2 destination2 = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction2) * (delta - 0.25f) * 1.4f),
//                                        (float) (predictionPoint.getPosition().y + Math.sin(direction2) * (delta - 0.25f) * 1.4f));
//            if (!isPathBlocked(predictionPoint.getPosition(), destination2)) {
//                addTempPoint(destination2, direction2, predictionPoint.getTime() + delta);
//            }
//
//            float direction3 = (float) (predictionPoint.getDirection() - Math.PI / 4);
//            Vector2 destination3 = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction3) * (delta - 0.25f) * 1.4f),
//                                        (float) (predictionPoint.getPosition().y + Math.sin(direction3) * (delta - 0.25f) * 1.4f));
//            if (!isPathBlocked(predictionPoint.getPosition(), destination3)) {
//                addTempPoint(destination3, direction3, predictionPoint.getTime() + delta);
//            }
//        }
//        allPoints.addAll(tempPoints);
//        latestPoints = new ArrayList<PredictionPoint>(tempPoints);
//        tempPoints.clear();
//    }
//
//    public void expandStraight(float delta) {
//        for (PredictionPoint predictionPoint : latestPoints) {
//            float direction = predictionPoint.getDirection();
//            Vector2 destination = new Vector2((float) (predictionPoint.getPosition().x + Math.cos(direction) * delta * 1.4f),
//                    (float) (predictionPoint.getPosition().y + Math.sin(direction) * delta * 1.4f));
//            if (!isPathBlocked(predictionPoint.getPosition(), destination)) {
//                addTempPoint(destination, direction, predictionPoint.getTime() + delta);
//            }
//        }
//        allPoints.addAll(tempPoints);
//        latestPoints = new ArrayList<PredictionPoint>(tempPoints);
//        tempPoints.clear();
//    }
//
//    private void addTempPoint(Vector2 position, float direction, float time) {
//        if (!insideMap(position)) {
//            return;
//        }
//        for (PredictionPoint predictionPoint : allPoints) {
//            if (position.dst(predictionPoint.getPosition()) < 6.0f) {
//                return;
//            }
//        }
//        for (PredictionPoint predictionPoint : tempPoints) {
//            if (position.dst(predictionPoint.getPosition()) < 6.0f) {
//                return;
//            }
//        }
//        tempPoints.add(new PredictionPoint(position, direction, time));
//    }
//
////    public PredictionPoint getBestPoint(Guard guard, ArrayList<Guard> guards) {
////        PredictionPoint bestPoint = predictionPoints.get(0);
////        for (PredictionPoint predictionPoint : predictionPoints) {
////            if (predictionPoint.computeScore(guard, guards) > bestPoint.computeScore(guard, guards)) {
////                bestPoint = predictionPoint;
////            }
////        }
////        return bestPoint;
////    }
//
////    public void increaseCounter(float delta) {
////        if (run) {
////            counter += delta;
////            if (counter >= 6.0f) {
////                expand(6.0f);
////                counter = 0.0f;
////            }
////        }
////    }
//
////    public void update() {
////        if (predictionModel != null) {
////            predictionModel.increaseCounter(delta);
////        }
////        for (PredictionPoint predictionPoint : predictionModel.getPredictionPoints()) {
////            if (body.getPosition().dst(predictionPoint.getPosition()) < 5.0f) {
////                predictionModel.removePoint(predictionPoint);
////                break;
////            }
////        }
////    }
//
//    //calculate the point where a guard can intercept an intruder
//    public Vector2 interceptPoint(Agent guard, Agent intruder) {
//        ArrayList<PredictionPoint> guardMoves = new ArrayList<PredictionPoint>();
//        ArrayList<PredictionPoint> intruderMoves = new ArrayList<PredictionPoint>();
//        guardMoves.add(new PredictionPoint(guard.getBody().getPosition(), guard.getBody().getAngle(),0));
//        intruderMoves.add(new PredictionPoint(intruder.getBody().getPosition(), intruder.getBody().getAngle(),0));
//
//
//        next();
//    }
//
//    private void next(ArrayList<PredictionPoint> guardMoves, ArrayList<PredictionPoint> intruderMoves) {
//        for (PredictionPoint guardMove : guardMoves) {
//            for (PredictionPoint intruderMove : intruderMoves) {
//                if (guardMove.getPosition().dst(intruderMove.getPosition()) < 6.0f) {
//                    return;
//                }
//            }
//        }
//        if (expandRandom(guardMoves) | expandStraight(intruderMoves)) {
//            next(guardMoves, intruderMoves);
//        } else {
//            return;
//        }
//    }
//
//    //check if a path is blocked by a wall or building etc
//    private boolean isPathBlocked(Vector2 start, Vector2 end) {
//        for (Building building : agent.getIndividualMap().getBuildings()) {
//            if (Intersector.intersectSegmentRectangle(start, end, building.getRectangle())) {
//                return true;
//            }
//        }
//        for (SentryTower sentryTower : agent.getIndividualMap().getSentryTowers()) {
//            if (Intersector.intersectSegmentRectangle(start, end, sentryTower.getRectangle())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    //check if a vector2 point is inside the map
//    private boolean insideMap(Vector2 position) {
//        return position.x > 0 && position.x < MASS.map.width && position.y > 0 && position.y < MASS.map.height;
//    }
//
//    private void reset() {
//        allPoints.clear();
//        latestPoints.clear();
//        tempPoints.clear();
//    }

}
