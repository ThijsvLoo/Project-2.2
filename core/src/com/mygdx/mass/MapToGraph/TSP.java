package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Vector2;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class TSP {

    private ArrayList<Vector2> toVisit;



    private Vector2 start;
    public TSP(Vector2 start, ArrayList<Vector2> toVisit){
        this.start = start;
        this.toVisit = toVisit;



    }

    public ArrayList<Vector2> computePath(){

        ArrayList<Vector2> path = new ArrayList<Vector2>();
        while(toVisit.size()>0){
            Vector2 nextVector = getClosest(start, toVisit);
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

}
