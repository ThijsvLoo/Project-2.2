package com.mygdx.mass.Graph;

import java.awt.geom.Point2D;

public class Edge {

    private Node start;
    private Node end;
    public double time;
    public double distance;
    protected double x1,x2,y1,y2;

    public Edge(Node start, Node end) {
        this.start = start;
        this.end = end;
        x1 = start.getPosition().x;
        x2 = end.getPosition().x;
        y1 = start.getPosition().y;
        y2 = end.getPosition().y;
        this.distance = Point2D.distance(this.start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
    }

    public double getWeight() {
        return distance;
    }

    public Node getNode1() {
        return start;
    }

    public Node getNode2() {
        return end;
    }
}

