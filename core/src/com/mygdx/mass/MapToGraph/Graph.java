package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;


import java.util.ArrayList;

public class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private Vertex start;
    private Vertex destination;
    private ArrayList<BoxObject> exploredBuilding;
    private Agent agent;
    public Graph(Vertex start, Vertex end, ArrayList<Vertex> vertices, ArrayList<Edge> edges, Agent agent){
        this.vertices = vertices;
        this.start = start;
        this.destination = end;
        this.edges = edges;
        this.agent = agent;

    }
    public boolean adjacent(Vertex vertex1, Vertex vertex2){
        for(Edge e: edges){
            if(vertex1.getCoordinates()== e.getVertex1().getCoordinates() && vertex2.getCoordinates() == e.getVertex2().getCoordinates()) return true;
            if(vertex1.getCoordinates()== e.getVertex2().getCoordinates() && vertex2.getCoordinates() == e.getVertex1().getCoordinates()) return true;
        }
        return false;
    }
    public void addVertex(Vertex newVertex){
        vertices.add(newVertex);
    }
    public void addEdge(Edge edge){

        edges.add(edge);

    }

    public void connectVerticesList(Vertex start, Vertex end, ArrayList<Vertex> vertices){

        boolean proceed = false;
        edges.clear();

        ArrayList<Vertex> doorFront = new ArrayList<Vertex>();
        Building startBuilding = inBuilding(start);
        Building endBuilding = inBuilding(end);
        if(startBuilding!=null && startBuilding==endBuilding){
            addEdge(new Edge(start, end));
        }
        if(startBuilding!=null){
            for(Door door: startBuilding.getDoors()){
                addEdge(new Edge(getDoorInside(door.getDoorCenter(),startBuilding),start));
            }
        }
        if(endBuilding!=null){
            for(Door door: endBuilding.getDoors()){
                addEdge(new Edge(end, getDoorInside(door.getDoorCenter(), endBuilding)));
            }
        }

        for (Building building : MASS.map.getBuildings()){
            doorFront.addAll(addDoors(building));
        }
        vertices.addAll(doorFront);
        BoxObject tmp = null;
        ArrayList<BoxObject> newBoxes = new ArrayList<BoxObject>();
        for(Vertex v1: vertices){
            for(Vertex v2: vertices){
                if(v1!=v2) {
                    tmp = connectVertices(v1,v2);
                    if (tmp==null) {
                        addEdge(new Edge(v1, v2));
                    }else{
                        if(!exploredBuilding.contains(tmp)){
                            newBoxes.add(tmp);
                            exploredBuilding.add(tmp);
                            proceed = true;
                        }
                    }


                }
            }
        }
        while (proceed){

            ArrayList<Vertex> newVertices = new ArrayList<Vertex>();
            for(BoxObject newBox: newBoxes){
              if (newBox != null) newVertices.addAll(add4Corners(newBox));
            }
            newBoxes.clear();

            proceed = false;
            for(Vertex newV: newVertices){
                for(Vertex v: vertices){
                    if(newV != v){
                        tmp = connectVertices(newV, v);
                        if (tmp==null){
                            addEdge(new Edge(newV, v));
                        }else{
                            if(!exploredBuilding.contains(tmp)){
                                newBoxes.add(tmp);
                                exploredBuilding.add(tmp);
                                proceed = true;
                            }

                        }
                    }
                }
            }

        }


    }
    private BoxObject connectVertices(Vertex v1, Vertex v2){
        for(Building building: agent.getIndividualMap().getBuildings()){
            if(Intersector.intersectSegmentRectangle(v1.getCoordinates(),v2.getCoordinates(), building.getRectangle())){

                    return building;
            }
        }
        for(SentryTower sentryTower: agent.getIndividualMap().getSentryTowers()){
            if(Intersector.intersectSegmentRectangle(v1.getCoordinates(),v2.getCoordinates(),sentryTower.getRectangle())){
                    return sentryTower;
            }
        }
        for(Wall wall: agent.getIndividualMap().getWalls()){
            if(Intersector.intersectSegmentRectangle(v1.getCoordinates(),v2.getCoordinates(),wall.getRectangle())){
                return wall;
            }
        }
        return null;
    }
    public ArrayList<Vertex> getPathVertices(Vertex start, Vertex destination) {
//        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        vertices.clear();
        this.start = start;
        this.destination = destination;
        this.exploredBuilding = new ArrayList<BoxObject>();
        vertices.add(start);
        vertices.add(destination);


        for (Building building : agent.getIndividualMap().getBuildings()) {

            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), destination.getCoordinates(),building.getRectangle())) {

                exploredBuilding.add(building);
                add4Corners(building);


            }
        }

        for (SentryTower sentryTower : agent.getIndividualMap().getSentryTowers()) {
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), destination.getCoordinates(),sentryTower.getRectangle())) {
                exploredBuilding.add(sentryTower);
                add4Corners(sentryTower);
            }
        }
        for (Wall wall: agent.getIndividualMap().getWalls()){
            if (Intersector.intersectSegmentRectangle(start.getCoordinates(), destination.getCoordinates(), wall.getRectangle())) {
                exploredBuilding.add(wall);
                add4Corners(wall);
            }
        }

        return vertices;
    }
    private Building inBuilding(Vertex vertex){
        for (Building building: MASS.map.getBuildings()){
            if(building.getRectangle().contains(vertex.getCoordinates().x,vertex.getCoordinates().y)){
                return building;
            }
        }
        return null;
    }
    private <T extends BoxObject> ArrayList<Vertex> add4Corners(T obj){
        ArrayList<Vertex> corners = new ArrayList<Vertex>();

            corners.add(new Vertex(obj.getRectangle().x-((float)0.5),obj.getRectangle().y-((float)0.5)));
            corners.add(new Vertex(obj.getRectangle().x-((float)0.5),obj.getRectangle().y+obj.getRectangle().height+((float)0.5)));
            corners.add(new Vertex(obj.getRectangle().x+obj.getRectangle().width+((float)0.5), obj.getRectangle().y+obj.getRectangle().height+((float)0.5)));
            corners.add(new Vertex(obj.getRectangle().x+obj.getRectangle().width+((float)0.5), obj.getRectangle().y-((float)0.5)));


            vertices.addAll(corners);

        return corners;
    }

    private ArrayList<Vertex> addDoors(Building building){
        ArrayList<Vertex> doorInside = new ArrayList<Vertex>();
        ArrayList<Vertex> doorFront = new ArrayList<Vertex>();

        ArrayList<Door> doors = new ArrayList<Door>();
        doors.addAll(building.getDoors());
        for(Door door: doors){
            doorInside.add(getDoorInside(door.getDoorCenter(), building));
        }
        for(Door door: doors){
            doorFront.add(addDoorFront(door.getDoorCenter(), building));
        }
        for(int i=0; i<doors.size(); i++){
            addEdge(new Edge(doorInside.get(i), doorFront.get(i)));
        }
        for(Vertex door: doorInside){
            for(Vertex door2: doorInside){
                if(door != door2){
                    addEdge(new Edge(door,door2));
                }
            }
        }


        return doorFront;
    }
    private Vertex getDoorInside(Vertex doorCenter, Building building){
        double x = doorCenter.getCoordinates().x;
        double y = doorCenter.getCoordinates().y;
        double inside_x = -10000;
        double inside_y = -10000;
        if (x == building.getRectangle().x){
            inside_x = (x + 0.5);
            inside_y = y;
//            System.out.println("added door");
//            System.out.println(x+ " "+ y);
        }
        else if(y == building.getRectangle().y){
            inside_y = (y + 0.5);
            inside_x = x;
//            System.out.println("added door");
//            System.out.println(x+ " "+ y);
        }
        else if(x == building.getRectangle().x+ building.getRectangle().width){
            inside_x = (x - 0.5);
            inside_y = y;
//            System.out.println("added door");
//            System.out.println(x+ " "+ y);
        }
        else if(y == building.getRectangle().y+building.getRectangle().height){
            inside_y = y - 0.5;
            inside_x = x;


        }
        else{
            System.out.println("not found!!");
            System.out.println(x+ " "+ y);
        }
        return new Vertex((float)inside_x, (float)inside_y);
    }
    private Vertex addDoorFront(Vertex doorCenter, Building building){
        double x = doorCenter.getCoordinates().x;
        double y = doorCenter.getCoordinates().y;
        double front_x = -10000;
        double front_y = -10000;
        if (x == building.getRectangle().x){
            front_x = (x - 0.5);
            front_y = y;

        }
        else if(y == building.getRectangle().y){
            front_y = (y - 0.5);
            front_x = x;

        }
        else if(x == building.getRectangle().x+ building.getRectangle().width){
            front_x = (x + 0.5);
            front_y = y;

        }
        else if(y == building.getRectangle().y+building.getRectangle().height){
            front_y = y + 0.5;
            front_x = x;


        }
        else{
            System.out.println("not found!!");
            System.out.println(x+ " "+ y);
        }
       return new Vertex((float)front_x,(float)front_y);
    }

    public void connectTSP(Vertex start){
        for(Vertex v: vertices){
            edges.add(new Edge(start,v));
        }


    }
    public ArrayList<Vertex> getVertices(){
        return vertices;
    }
    public ArrayList<Edge> getEdges(){
        return edges;
    }

    public Vertex getStart() {
        return start;
    }
    public Vertex getDestination() {
        return destination;
    }
}
