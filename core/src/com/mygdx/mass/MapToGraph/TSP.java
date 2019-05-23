package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class TSP {
    private Graph graph;
    private ArrayList<Vertex> vertices;

    private ArrayList<Edge> edges;

    private Vertex start;
    public TSP(Vector2 start, ArrayList<Vector2> toVisit){
        this.start = new Vertex(start.x, start.y);
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();

        for(Vector2 v: toVisit){
            vertices.add(new Vertex(v.x, v.y));
        }
        graph = new Graph(vertices, edges);
    }

    public ArrayList<Vector2> computePath(){

        ArrayList<Vector2> path = new ArrayList<Vector2>();
        while(vertices.size()>0){
            Vertex nextVertex = getClosest(start, vertices).getVertex2();
            start = nextVertex;
            path.add(nextVertex.getCoordinates());
            vertices.remove(nextVertex);

        }

        return path;

    }

    public Edge getClosest(Vertex start, ArrayList<Vertex> unvisited){
        Edge closest=null;
        for(Vertex v: unvisited){
            Edge edge = new Edge(start, v);
            edges.add(edge);
            if(closest==null) closest= edge;
            else if(edge.getWeight()<closest.getWeight()) closest = edge;


        }
        return closest;
    }

}
