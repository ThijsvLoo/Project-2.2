package com.mygdx.mass.Graph;

import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;


import java.util.ArrayList;
import java.util.HashMap;


public class Graph {

    private int Walls;
    private int Buildings;
    private int Towers;
    public static ArrayList<Node> nodes;
    public static HashMap<Node, ArrayList<Edge>> adjVertices;
    public Map map;

    public Graph(MASS mass) {
        map = mass.getMap();
    }

    public void removeVertex(String name) {
        nodes.remove(name);
        adjVertices.remove(name);
    }

    public void convertMap() {
        nodes = new ArrayList<Node>();
        adjVertices = new HashMap<Node, ArrayList<Edge>>();
        Walls = 1;
        Buildings = 1;
        Towers = 1;
        for (int i = 0; i < map.getWorldObjects().size(); i++) {
            switch(map.getBoxObjects().get(i).getObjectType()){
                case WALL:
                    for (int j = 0; j < 4; j++) {
                        Node newNode = new Node(Walls, map.getBoxObjects().get(i), map.getBoxObjects().get(i).getVertices()[j]);
                        nodes.add(newNode);
                        Walls++;
                    }
                    break;
                case BUILDING:
                    for (int j = 0; j < 4; j++) {
                        Node newNode = new Node(Buildings, map.getBoxObjects().get(i), map.getBoxObjects().get(i).getVertices()[j]);
                        nodes.add(newNode);
                        Buildings++;
                    }
                    break;
                case SENTRY_TOWER:
                    for (int j = 0; j < 4; j++) {
                        Node newNode = new Node(Towers, map.getBoxObjects().get(i), map.getBoxObjects().get(i).getVertices()[j]);
                        nodes.add(newNode);
                        Towers++;
                    }
                    break;
                case HIDING_AREA:
                    break;
                case TARGET_AREA:
                    break;
                case DOOR:
                    break;
                case WINDOW:
                    break;
            }

        }
        connectNodes();
    }

    public void connectNodes() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) continue;
                nodes.get(i).connect(nodes.get(j));
            }
            adjVertices.put(nodes.get(i), nodes.get(i).connections);
        }
    }

}

