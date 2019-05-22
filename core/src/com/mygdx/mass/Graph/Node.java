package com.mygdx.mass.Graph;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.BoxObject.BoxObject;

import java.util.ArrayList;

public class Node {
    public String name;
    public ArrayList<Edge> connections;
    private Vector2 position;

    public Node(int i, BoxObject type, Vector2 position) {
        connections = new ArrayList<Edge>();
        this.name = type.getObjectType().toString() + i;
        this.position = position;
        //System.out.println("node " + name + " created " + position );
    }

    public void connect(Node connected) {
        Edge e = new Edge(this, connected);
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