package com.mygdx.mass.MapToGraph;

import java.awt.geom.Point2D;

public class Edge {
    private Vertex vertex1;
    private Vertex vertex2;
    public double weight;
    public Edge(Vertex start, Vertex end){
        vertex1 = start;
        vertex2 = end;
        weight = Point2D.distance(start.getCoordinates().x, start.getCoordinates().y, end.getCoordinates().x, end.getCoordinates().y);
    }
public void setWeight(double weight){
        this.weight = weight;
}
    public double getWeight() {
        return weight;
    }

    public Vertex getVertex1() {
        return vertex1;
    }

    public Vertex getVertex2() {
        return vertex2;
    }
}
