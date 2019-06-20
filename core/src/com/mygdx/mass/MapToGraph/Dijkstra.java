package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Vector2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Dijkstra {
    private Vertex start;
    private Vertex destination;
    private final ArrayList<Vertex> vertices;
    private final ArrayList<Edge> edges;

    private ArrayList<Vertex> visitedNodes;
    private ArrayList<Vertex> unvisitedNodes;
    private Map<Vertex, Double> distance;
    private Map<Vertex, Vertex> predecessors;

    public Dijkstra(Graph graph){
        this.vertices = graph.getVertices();
        this.edges = graph.getEdges();
        graph.connectVerticesList(graph.getStart(),graph.getDestination(),vertices);
        this.start = graph.getStart();
        this.destination = graph.getDestination();

        this.distance = new HashMap<Vertex, Double>();
        distance.put(start, 0.0);

        this.unvisitedNodes = new ArrayList<Vertex>();
        unvisitedNodes.add(start);

        this.visitedNodes = new ArrayList<Vertex>();

        this.predecessors = new HashMap<Vertex,Vertex>();


    }
    public ArrayList<Vertex> getNeighbours(Vertex vertex) {
        ArrayList<Vertex> neighbours = new ArrayList<Vertex>();
        for(Edge e: edges){
            if(e.getVertex1().equals(vertex))
                neighbours.add(e.getVertex2());
            else if(e.getVertex2().equals(vertex)){
                neighbours.add(e.getVertex1());
            }

        }
        return neighbours;
    }
    public void exploreVertex(Vertex source){
        ArrayList<Vertex> neighbours = getNeighbours(source);
        for(Vertex neighbour: neighbours){
            double newDistance = distance.get(source) + getNeighbourDistance(source, neighbour);
            if(distance.get(neighbour)==null || distance.get(neighbour) > newDistance){
                distance.put(neighbour, newDistance); // update distance
                predecessors.put(neighbour, source);
                unvisitedNodes.add(neighbour);
            }
        }
    }

    // get the next vertex to explore (which is the smallest one)
    public Vertex getNextVertex(ArrayList<Vertex> unvisitedNodes){
        Vertex smallest = null;
        for(Vertex vertex: unvisitedNodes){
            if(smallest==null)
                smallest = vertex;
            else if(distance.get(vertex)< distance.get(smallest))
                smallest = vertex;
        }
        return smallest;
    }
    // get the distance (weight) of two connected vertexes
    public double getNeighbourDistance(Vertex v1, Vertex v2){
        for(Edge edge: edges){
            if(v1 == edge.getVertex1() && v2 == edge.getVertex2())
                return edge.getWeight();
            if(v1 == edge.getVertex2() && v2 == edge.getVertex1())
                return edge.getWeight();
        }
        return -1;
    }

    public ArrayList<Vector2> computePath(){
        ArrayList<Vector2> path = new ArrayList<Vector2>();
        for(Edge edge: edges){
            if(start==edge.getVertex1()&&destination==edge.getVertex2()) {
                path.add(new Vector2(destination.getCoordinates()));
                return path;
            }

            if(start==edge.getVertex2()&&destination==edge.getVertex1()) {
                path.add(new Vector2(destination.getCoordinates()));
                return path;
            }
        }

        while(unvisitedNodes.size()>0){

            Vertex vertex = getNextVertex(unvisitedNodes);
            exploreVertex(vertex);
            visitedNodes.add(vertex);
            unvisitedNodes.remove(vertex);
        }




        Vertex tmp = destination;
        if(predecessors.get(destination)==null) return path; //if weird then this is null
        path.add(destination.getCoordinates());
        while(predecessors.get(tmp)!=null){
            tmp = predecessors.get(tmp);
            path.add(tmp.getCoordinates());
        }
        Collections.reverse(path);
        return path;
    }

}
