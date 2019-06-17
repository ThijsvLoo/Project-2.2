package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
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

    public ArrayList<Vector2> computePath(Agent agent, ArrayList<Vector2> list){
        ArrayList<Vertex> toVisit = new ArrayList<Vertex>();
        for(Vector2 vector: list){
            toVisit.add(new Vertex(vector.x,vector.y));
        }
        Vertex start = new Vertex(agent.getBody().getPosition().x, agent.getBody().getPosition().y);
        ArrayList<Vector2> path = new ArrayList<Vector2>();
        while(toVisit.size()>0){
            Vertex nextVertex = getClosest(start, toVisit);
            while (containsPoint(agent, nextVertex)){
                toVisit.remove(nextVertex);
                nextVertex = getClosest(start, toVisit);
            }

            if(nextVertex != null) { // added this to solve the nullpointerexception error
                Graph graph = new Graph(new ArrayList<Vertex>(), new ArrayList<Edge>(), agent);
                graph.getPathVertices(start, nextVertex);

                Dijkstra dijkstra = new Dijkstra(graph);

                ArrayList<Vector2> bestPath = dijkstra.computePath();
                start = nextVertex;
                path.addAll(bestPath);
                toVisit.remove(nextVertex);
            }
        }

        return path;

    }


    public Vertex getClosest(Vertex start, ArrayList<Vertex> unvisited){
        double closestDist=-1;
        Vertex closestVertex=null;
        for(Vertex v: unvisited){
            Edge edge = new Edge(start, v);
            double dist = edge.getWeight();

            if(closestDist==-1) {
                closestDist= dist;
                closestVertex = v;
            }
            else if(dist<closestDist) {
                closestDist = dist;
                closestVertex= v;
            }

        }
        return closestVertex;
    }
    public boolean intersected(Agent agent, Vertex start, Vertex end){
        for (Building building : agent.getIndividualMap().getBuildings()) {
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), end.getCoordinates(), building.getRectangle())) {
                return true;
            }
        }
        for (SentryTower sentryTower : agent.getIndividualMap().getSentryTowers()) {
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), end.getCoordinates(), sentryTower.getRectangle())) {
                return true;
            }
        }
        for (Wall wall : agent.getIndividualMap().getWalls()) {
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), end.getCoordinates(), wall.getRectangle())) {
                return true;
            }
        }
        return false;
    }
    public boolean containsPoint(Agent agent, Vertex vertex){
        for (Building building : agent.getIndividualMap().getBuildings()) {
            if (building.getRectangle().contains(vertex.getCoordinates())) {

                return true;
            }
        }
        for (SentryTower sentryTower : agent.getIndividualMap().getSentryTowers()) {
            if (sentryTower.getRectangle().contains(vertex.getCoordinates())) {

                return true;
            }
        }
        for (Wall wall : agent.getIndividualMap().getWalls()) {
            if (wall.getRectangle().contains(vertex.getCoordinates())) {

                return true;
            }
        }
        return false;
    }

}
