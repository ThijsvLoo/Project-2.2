package com.mygdx.mass.Graph;

import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;


import java.util.ArrayList;


public class Graph {

    private int Walls = 1;
    private int Buildings = 1;
    private int Towers = 1;
    public static ArrayList<Node> nodes;
    public static java.util.Map<Node, ArrayList<Edge>> adjVertices;
    public Map map;

    public Graph(MASS mass) {
        map = mass.getMap();
    }

    public void removeVertex(String name) {
        nodes.remove(name);
        adjVertices.remove(name);
    }

    public void convertMap() {
        for (int i = 0; i < map.getWorldObjects().size(); i++) {
            switch(map.getBoxObjects().get(i).getObjectType()){
                case WALL:
                    for (int j = 0; j < 4; j++) {
                        System.out.println("yeah");
                        Node newNode = new Node(Walls, map.getBoxObjects().get(i), map.getBoxObjects().get(i).getVertices()[j]);
                        nodes.add(newNode);
                        System.out.println("yeah2");
                        Walls++;
                    }
                    break;
                case BUILDING:
                    for (int j = 0; j < 4; j++) {
                        Node n = new Node(Buildings, map.getBoxObjects().get(i), map.getBoxObjects().get(i).getVertices()[j]);
                        Buildings++;
                        nodes.add(n);
                    }
                case SENTRY_TOWER:
                    for (int j = 0; j < 4; j++) {
                        Node n = new Node(Towers, map.getBoxObjects().get(i), map.getBoxObjects().get(i).getVertices()[j]);
                        Towers++;
                        nodes.add(n);
                    }
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
