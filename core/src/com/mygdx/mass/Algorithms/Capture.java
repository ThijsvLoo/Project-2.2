package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.Data.MASS;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;

import java.util.ArrayList;

public class Capture {

    public boolean run = false; //for testing

    public ArrayList<CapturePoint> capturePoints;
    public ArrayList<CapturePoint> latestPoints;
    public ArrayList<CapturePoint> tempPoints;

    public float counter;

    public Capture() {
        capturePoints = new ArrayList<CapturePoint>();
        latestPoints = new ArrayList<CapturePoint>();
        tempPoints = new ArrayList<CapturePoint>();
        latestPoints.add(new CapturePoint(new Vector2(100,100), (float) Math.PI/2));
        counter = 0.0f;
    }

    public boolean insideMap(Vector2 position) {
        return position.x > 0 && position.x < MASS.map.width && position.y > 0 && position.y < MASS.map.height;
    }

    public void addTempPoint(Vector2 position, float direction) {
        if (!insideMap(position)) {
            return;
        }
        for (CapturePoint capturePoint : capturePoints) {
            if (position.dst(capturePoint.getPosition()) < 3.0f) {
                return;
            }
        }
        for (CapturePoint capturePoint : latestPoints) {

        }
        tempPoints.add(new CapturePoint(position, direction));
    }

    public boolean intersect(Vector2 start, Vector2 end) {
        for (Building building : MASS.map.getBuildings()) {
            if (Intersector.intersectSegmentRectangle(start, end, building.getRectangle())) {
                return true;
            }
        }
        for (SentryTower sentryTower : MASS.map.getSentryTowers()) {
            if (Intersector.intersectSegmentRectangle(start, end, sentryTower.getRectangle())) {
                return true;
            }
        }
        return false;
    }

    public void removePoint(CapturePoint capturePoint) {
        capturePoints.remove(capturePoint);
    }

    //the distance is not correct, need to adapt with pathfinding
    public void expand(float timePassed) {
        tempPoints.clear();
        for (CapturePoint capturePoint : latestPoints) {
            float direction = capturePoint.getDirection();
            Vector2 position = new Vector2((float) (capturePoint.getPosition().x + Math.cos(direction) * timePassed * 1.4f),
                                        (float) (capturePoint.getPosition().y + Math.sin(direction) * timePassed * 1.4f));
            if (!intersect(position, capturePoint.getPosition())) {
                addTempPoint(position, direction);
            }

            float direction2 = (float) (capturePoint.getDirection() + Math.PI / 4);
            Vector2 position2 = new Vector2((float) (capturePoint.getPosition().x + Math.cos(direction2) * (timePassed - 0.25f) * 1.4f),
                                        (float) (capturePoint.getPosition().y + Math.sin(direction2) * (timePassed - 0.25f) * 1.4f));
            if (!intersect(position2, capturePoint.getPosition())) {
                addTempPoint(position2, direction2);
            }

            float direction3 = (float) (capturePoint.getDirection() - Math.PI / 4);
            Vector2 position3 = new Vector2((float) (capturePoint.getPosition().x + Math.cos(direction3) * (timePassed - 0.25f) * 1.4f),
                                        (float) (capturePoint.getPosition().y + Math.sin(direction3) * (timePassed - 0.25f) * 1.4f));
            if (!intersect(position3, capturePoint.getPosition())) {
                addTempPoint(position3, direction3);
            }
        }
        latestPoints = new ArrayList<CapturePoint>(tempPoints);
        capturePoints.addAll(tempPoints);
    }

    public CapturePoint getBestPoint(Guard guard, ArrayList<Guard> guards) {
        CapturePoint bestPoint = capturePoints.get(0);
        for (CapturePoint capturePoint : capturePoints) {
            if (capturePoint.computeScore(guard, guards) > bestPoint.computeScore(guard, guards)) {
                bestPoint = capturePoint;
            }
        }
        return bestPoint;
    }

    public ArrayList<CapturePoint> getCapturePoints() {
        return capturePoints;
    }

    public void setCapturePoints(ArrayList<CapturePoint> capturePoints) {
        this.capturePoints = capturePoints;
    }

    public void increaseCounter(float delta) {
        if (run) {
            counter += delta;
            if (counter >= 3.0f) {
                expand(3.0f);
                counter = 0.0f;
            }
        }
    }

}
