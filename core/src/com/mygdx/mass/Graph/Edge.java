package com.mygdx.mass.Graph;
import java.lang.Math.*;

public class Edge {

    public Node start;
    public Node end;
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
        distance = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }
}
