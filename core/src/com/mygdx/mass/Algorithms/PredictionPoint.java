package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Guard;

import java.util.ArrayList;

public class PredictionPoint {

    Vector2 position;
    float direction;
    float time;

    public PredictionPoint(Vector2 position, float direction, float time) {
        this.position = position;
        this.direction = direction;
        this.time = time;
    }

//    public float computeScore(Guard guard, ArrayList<Guard> guards) {
//        float totalDistance = 0.0f;
//        for (Guard g : guards) {
//            if (g != guard) {
//                totalDistance += g.getBody().getPosition().dst(position);
//            }
//        }
//        float averageDistance = totalDistance / (guards.size() - 1);
//        return averageDistance - guard.getBody().getPosition().dst(position);
//    }

    public Vector2 getPosition() { return position; }
    public float getDirection() { return direction; }
    public float getTime() { return time; }

}
