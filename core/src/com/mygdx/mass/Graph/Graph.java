package com.mygdx.mass.Graph;

import sun.security.provider.certpath.Vertex;

import java.util.ArrayList;
import java.util.Map;

public class Graph {
    public static ArrayList<Node> nodes;
    public static Map<Node, ArrayList<Edge>> adjVertices;

    public void removeVertex(String name) {
        nodes.remove(name);
        adjVertices.remove(name);
    }

}
