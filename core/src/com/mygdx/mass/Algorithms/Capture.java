package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Guard;

import java.util.ArrayList;

public class Capture {

    public ArrayList<CapturePoint> capturePoints;

    public Capture() {
        capturePoints = new ArrayList<CapturePoint>();
    }

    public void addPoint(Vector2 position, float direction) {
        CapturePoint capturePoint = new CapturePoint(position, direction);
        capturePoints.add(capturePoint);
    }

    public void removePoint(CapturePoint capturePoint) {
        capturePoints.remove(capturePoint);
    }

    //the distance is not correct, need to adapt with pathfinding
    public void expand(float timePassed) {
        for (CapturePoint capturePoint : capturePoints) {
            float direction = capturePoint.getDirection();
            Vector2 position = new Vector2((float) (capturePoint.getPosition().x + Math.cos(direction) * timePassed * 1.4f),
                                        (float) (capturePoint.getPosition().y + Math.sin(direction) * timePassed * 1.4f));
            addPoint(position, direction);

            float direction2 = (float) (capturePoint.getDirection() + Math.PI / 4);
            Vector2 position2 = new Vector2((float) (capturePoint.getPosition().x + Math.cos(direction2) * (timePassed - 0.25f) * 1.4f),
                                        (float) (capturePoint.getPosition().y + Math.sin(direction2) * (timePassed - 0.25f) * 1.4f));
            addPoint(position2, direction2);

            float direction3 = (float) (capturePoint.getDirection() - Math.PI / 4);
            Vector2 position3 = new Vector2((float) (capturePoint.getPosition().x + Math.cos(direction3) * (timePassed - 0.25f) * 1.4f),
                                        (float) (capturePoint.getPosition().y + Math.sin(direction3) * (timePassed - 0.25f) * 1.4f));
            addPoint(position3, direction3);
        }
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

}
