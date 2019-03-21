package com.mygdx.mass.Graph;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.BoxObject.BoxObject;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String name;
    public ArrayList<Edge> connections;
    public Vector2 position;

    public Node(int i, BoxObject type) {
        this.name = type.getObjectType().toString() + i;
        //Graph.nodes.add(this);
        //Graph.adjVertices.put(this,connections);
    }

    public void connect(Node connected) {
        Edge e = new Edge();
        e.start = this;
        e.end = connected;
        connections.add(e);
        //connected.connections.add(e);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }
}
