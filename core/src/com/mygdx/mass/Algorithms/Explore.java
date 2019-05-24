package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.MapToGraph.TSP;

import java.util.ArrayList;

public class Explore {

    public Explore() {}

    public void start(Agent agent) {
        TSP tsp = new TSP(agent.getBody().getPosition(), getExplorePoints());
        ArrayList<Vector2> route = tsp.computePath();
        agent.getRoute().clear();
        for (Vector2 waypoint : route) {
            agent.addWaypoint(waypoint);
        }
    }

    public ArrayList<Vector2> getExplorePoints() {
        ArrayList<Vector2> explorePoints = new ArrayList<Vector2>();
        for (int i = 1; i < 20; i++) {
            for (int j = 1; j < 20; j++) {
                explorePoints.add(new Vector2(i*10,j*10));
            }
        }
        return explorePoints;
    }

}
