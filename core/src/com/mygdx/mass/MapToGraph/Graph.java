package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Intersector;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.Data.MASS;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private Vertex start;
    private Vertex destination;
    public Graph(ArrayList<Vertex> vertices, ArrayList<Edge> edges){
        this.vertices = vertices;
        this.edges = edges;
    }
    public boolean adjacent(Vertex vertex1, Vertex vertex2){
        for(Edge e: edges){
            if(vertex1.getCoordinates()== e.getVertex1().getCoordinates() && vertex2.getCoordinates() == e.getVertex2().getCoordinates()) return true;
            if(vertex1.getCoordinates()== e.getVertex2().getCoordinates() && vertex2.getCoordinates() == e.getVertex1().getCoordinates()) return true;
        }
        return false;
    }
    public void addVertex(Vertex newVertex){
        vertices.add(newVertex);
    }
    public void addEdge(Edge edge){

        edges.add(edge);

    }
//    public Graph computeGraph(ArrayList<Vertex> vertices, Vertex start, Vertex end){
//        if....
//
//    }
    public void connectVerticesList(ArrayList<Vertex> vertices){
        edges.clear();
        for(Vertex v1: vertices){
            for(Vertex v2: vertices){
                if(v1!=v2 && connectVertices(v1, v2)){
                    addEdge(new Edge(v1,v2));
                }
            }
        }

    }
    private boolean connectVertices(Vertex v1, Vertex v2){
        for(Building building: MASS.map.getBuildings()){
            if(Intersector.intersectSegmentRectangle(v1.getCoordinates(),v2.getCoordinates(), building.getRectangle())){
                return false;
            }
        }
        for(SentryTower sentryTower: MASS.map.getSentryTowers()){
            if(Intersector.intersectSegmentRectangle(v1.getCoordinates(),v2.getCoordinates(),sentryTower.getRectangle())){
                return false;
            }
        }
        return true;
    }
    public ArrayList<Vertex> getPathVertices(Vertex start, Vertex destination) {
//        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        vertices.clear();
        this.start = start;
        this.destination = destination;
        vertices.add(start);
        vertices.add(destination);

        for (Building building : MASS.map.getBuildings()) {
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), destination.getCoordinates(), building.getRectangle())) {
                vertices.add(new Vertex(building.getRectangle().x-1,building.getRectangle().y-1));
                vertices.add(new Vertex(building.getRectangle().x-1,building.getRectangle().y+building.getRectangle().height+1));
                vertices.add(new Vertex(building.getRectangle().x+building.getRectangle().width+1, building.getRectangle().y+building.getRectangle().height+1));
                vertices.add(new Vertex(building.getRectangle().x+building.getRectangle().width+1, building.getRectangle().y-1));
            }
        }

        for (SentryTower sentryTower : MASS.map.getSentryTowers()) {
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), destination.getCoordinates(), sentryTower.getRectangle())) {
                vertices.add(new Vertex(sentryTower.getRectangle().x-1,sentryTower.getRectangle().y-1));
                vertices.add(new Vertex(sentryTower.getRectangle().x-1,sentryTower.getRectangle().y+sentryTower.getRectangle().height+1));
                vertices.add(new Vertex(sentryTower.getRectangle().x+sentryTower.getRectangle().width+1, sentryTower.getRectangle().y+sentryTower.getRectangle().height+1));
                vertices.add(new Vertex(sentryTower.getRectangle().x+sentryTower.getRectangle().width+1, sentryTower.getRectangle().y-1));
            }
        }

        return vertices;
    }
    public ArrayList<Vertex> getVertices(){
        return vertices;
    }
    public ArrayList<Edge> getEdges(){
        return edges;
    }

    public Vertex getStart() {
        return start;
    }
    public Vertex getDestination() {
        return destination;
    }
}
