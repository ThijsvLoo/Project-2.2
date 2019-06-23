package com.mygdx.mass.Graph;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.BoxObject.BoxObject;

import java.util.ArrayList;

public class Node {
    private Node parent;
    private ArrayList<Node> children;
    public String name;
    public ArrayList<Edge> connections;
    private Vector2 position;
    private boolean visited;



    private int indexOfNode;


    private boolean primitive;


    private Gap gap;

    /*public Node(int i, BoxObject type, Vector2 position) {
        connections = new ArrayList<Edge>();
        this.name = type.getObjectType().toString() + i;
        this.position = position;
        //System.out.println("node " + name + " created " + position );
    }*/

    public Node(Vector2 position) {
        connections = new ArrayList<Edge>();
        this.position = position;
        this.visited = true;
    }

    public Node(Node parent, Gap gap, Vector2 position) {
        this.gap = gap;
        connections = new ArrayList<Edge>();
        this.position = position;
        this.visited = false;
        if (parent != null) {
            this.parent = parent;
            parent.addChild(this);
            connect(parent);
        }

    }

    public void connect(Node connected) {
        Edge e = new Edge(connected, this);
        connections.add(e);
        connected.connections.add(e);
    }

    public Node getParent(){
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public ArrayList<Node> getChildren(){
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Gap getGap() {
        return gap;
    }

    public void setGap(Gap gap) {
        this.gap = gap;
    }

    public boolean isPrimitive() {
        return primitive;
    }

    public void setPrimitive(boolean primitive) {
        this.primitive = primitive;
    }

    public int getIndexOfNode() {
        return indexOfNode;
    }

    public void setIndexOfNode(int indexOfNode) {
        this.indexOfNode = indexOfNode;
    }

    public boolean isVisited() { return visited; }

    public void setVisited(boolean visited) { this.visited = visited; }
}