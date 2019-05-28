package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.BoxObject.Wall;
import com.mygdx.mass.Data.MASS;



import java.awt.geom.Point2D;
import java.util.ArrayList;

public class TSP {


    public TSP(){


    }

    public ArrayList<Vector2> computePath(Agent agent, ArrayList<Vector2> toVisit){
        Vector2 start = agent.getBody().getPosition();
        ArrayList<Vector2> path = new ArrayList<Vector2>();
        while(toVisit.size()>0){
            Vector2 nextVector = getClosest(start, toVisit);
            while (intersected(agent, start, nextVector)){
                toVisit.remove(nextVector);
                nextVector = getClosest(start, toVisit);
            }
            start = nextVector;
            path.add(nextVector);
            toVisit.remove(nextVector);

        }

        return path;

    }


    public Vector2 getClosest(Vector2 start, ArrayList<Vector2> unvisited){
        double closestDist=-1;
        Vector2 closestVector=null;
        for(Vector2 v: unvisited){
            double dist = Point2D.distance(start.x, start.y, v.x, v.y);

            if(closestDist==-1) {
                closestDist= dist;
                closestVector = v;
            }
            else if(dist<closestDist) {
                closestDist = dist;
                closestVector = v;
            }


        }
        return closestVector;
    }
    public boolean intersected(Agent agent, Vector2 start, Vector2 end){
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
        for (Wall wall : agent.getIndividualMap().getWalls()) {
            if (Intersector.intersectSegmentRectangle(start, end, wall.getRectangle())) {
                return true;
            }
        }
        return false;
    }

}
