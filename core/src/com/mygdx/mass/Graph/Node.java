package com.mygdx.mass.Graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String name;
    public ArrayList<Edge> connections;

    public Node(String name) {
        this.name = name;
        Graph.nodes.add(this);
        Graph.adjVertices.put(this,connections);
    }

    public void connect(Node connected) {
        Edge e = new Edge();
        e.start = this;
        e.end = connected;
        connections.add(e);
    }
}
