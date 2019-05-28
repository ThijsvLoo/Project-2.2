package com.mygdx.mass.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;

public class Map implements Serializable {

    public static final float DEFAULT_WIDTH = 200;
    public static final float DEFAULT_HEIGHT = 200;
    public float width = DEFAULT_WIDTH;
    public float height = DEFAULT_WIDTH;

    private MASS mass;

    public ArrayList<Wall> walls;
    public ArrayList<Building> buildings;
    public ArrayList<Door> doors;
    public ArrayList<Window> windows;
    public ArrayList<SentryTower> sentryTowers;
    public ArrayList<HidingArea> hidingAreas;
    public ArrayList<TargetArea> targetAreas;
    public ArrayList<Marker> markers;
    public ArrayList<Marker> removedMarkers;

    public Stack<BoxObject> undo = new Stack();
    public Stack<BoxObject> redo = new Stack();

    private ArrayList<Guard> guards;
    private ArrayList<Intruder> intruders;

    public Map(MASS mass) {
        this.mass = mass;

        walls = new ArrayList<Wall>();
        buildings = new ArrayList<Building>();
        doors = new ArrayList<Door>();
        windows = new ArrayList<Window>();
        sentryTowers = new ArrayList<SentryTower>();
        hidingAreas = new ArrayList<HidingArea>();
        targetAreas = new ArrayList<TargetArea>();
        markers = new ArrayList<Marker>();
        removedMarkers = new ArrayList<Marker>();

        guards = new ArrayList<Guard>();
        intruders = new ArrayList<Intruder>();
    }

	public Map(MASS mass, float width, float height) {
		this.mass = mass;

		walls = new ArrayList<Wall>();
		buildings = new ArrayList<Building>();
		doors = new ArrayList<Door>();
		windows = new ArrayList<Window>();
		sentryTowers = new ArrayList<SentryTower>();
		hidingAreas = new ArrayList<HidingArea>();
		targetAreas = new ArrayList<TargetArea>();
		markers = new ArrayList<Marker>();
        removedMarkers = new ArrayList<Marker>();

		guards = new ArrayList<Guard>();
		intruders = new ArrayList<Intruder>();
		this.width = width;
		this.height = height;
	}

    public Wall addWall(Rectangle rectangle) {
        Wall wall = new Wall(mass, rectangle);
        walls.add(wall);
        undo.add(wall);
        return wall;
    }

    public Building addBuilding(Rectangle rectangle) {
        Building building = new Building(mass, rectangle);
        buildings.add(building);
        undo.add(building);
        return building;
    }

    public Door addDoor(Rectangle rectangle) {
        Door door = new Door(mass, rectangle);
        doors.add(door);
        undo.add(door);
        return door;
    }

    public Window addWindow(Rectangle rectangle) {
        Window window = new Window(mass, rectangle);
        windows.add(window);
        undo.add(window);
        return window;
    }

    public SentryTower addSentryTower(Rectangle rectangle) {
        SentryTower sentryTower = new SentryTower(mass, rectangle);
        sentryTowers.add(sentryTower);
        undo.add(sentryTower);
        return sentryTower;
    }

    public HidingArea addHidingArea(Rectangle rectangle) {
        HidingArea hidingArea = new HidingArea(mass, rectangle);
        hidingAreas.add(hidingArea);
        undo.add(hidingArea);
        return hidingArea;
    }

    public TargetArea addTargetArea(Rectangle rectangle) {
        TargetArea targetArea = new TargetArea(mass, rectangle);
        targetAreas.add(targetArea);
        undo.add(targetArea);
        return targetArea;
    }

//    public Marker addMarker(Rectangle rectangle) {
//        Marker marker = new Marker(mass, rectangle, color);
//        markers.add(marker);
//        return marker;
//    }

    public void removeMarker(Marker marker){
        removedMarkers.add(marker);
        markers.remove(marker);
    }


    public Guard addGuard(Vector2 position) {
        Guard guard = new Guard(mass, position);
        guards.add(guard);
        return guard;
    }

    public Intruder addIntruder(Vector2 position) {
        Intruder intruder = new Intruder(mass, position);
        intruders.add(intruder);
        return intruder;
    }

    public ArrayList<WorldObject> getWorldObjects () {
        ArrayList<WorldObject> worldObjects = new ArrayList<WorldObject>();
        worldObjects.addAll(getBoxObjects());
        worldObjects.addAll(getAgents());
        return worldObjects;
    }

    public ArrayList<BoxObject> getBoxObjects() {
        ArrayList<BoxObject> boxObjects = new ArrayList<BoxObject>();
        boxObjects.addAll(walls);
        boxObjects.addAll(buildings);
        boxObjects.addAll(doors);
        boxObjects.addAll(windows);
        boxObjects.addAll(sentryTowers);
        boxObjects.addAll(hidingAreas);
        boxObjects.addAll(targetAreas);
        return boxObjects;
    }

    public ArrayList<Agent> getAgents() {
        ArrayList<Agent> agents = new ArrayList<Agent>();
        agents.addAll(guards);
        agents.addAll(intruders);
        return agents;
    }

    // Remove certain world obj from the reference arraylist, doesn't destroy the box2d body associated
    public void remove(WorldObject worldObject) {
//        for now only guard and intruder, since boxObjects are static
        if (worldObject instanceof Guard) {
            guards.remove(worldObject);
        } else if (worldObject instanceof Intruder) {
            intruders.remove(worldObject);
        }
    }

    public void destroy(WorldObject worldObject) {
        mass.world.destroyBody(worldObject.getBody());
    }

    public void clearMap(){
        mass.rayHandler.removeAll();
        for(BoxObject box: walls){
            mass.world.destroyBody(box.getBody());
        }
        walls.clear();
        for(BoxObject box: buildings){
            mass.world.destroyBody(box.getBody());
        }
        buildings.clear();
        for(BoxObject box: doors){
            mass.world.destroyBody(box.getBody());
        }
        doors.clear();
        for(BoxObject box: windows){
            mass.world.destroyBody(box.getBody());
        }
        windows.clear();
        for(BoxObject box: sentryTowers){
            mass.world.destroyBody(box.getBody());
        }
        sentryTowers.clear();
        for(BoxObject box: hidingAreas){
            mass.world.destroyBody(box.getBody());
        }
        hidingAreas.clear();
        for(BoxObject box: targetAreas){
            mass.world.destroyBody(box.getBody());
        }
        targetAreas.clear();
        for(Agent agent: intruders){
            mass.world.destroyBody(agent.getBody());
        }
        intruders.clear();
        for(Agent agent: guards){
            mass.world.destroyBody(agent.getBody());
        }
        guards.clear();
    }


    public void addOuterWalls(){
        //North wall
        Rectangle northWall = new Rectangle(0 - Wall.THICKNESS, this.getHeight(), this.getWidth() + 2*Wall.THICKNESS, Wall.THICKNESS);
        this.addWall(northWall);

        //East wall
        Rectangle eastWall = new Rectangle(this.getWidth(), 0 - Wall.THICKNESS, Wall.THICKNESS, this.getHeight() + 2*Wall.THICKNESS);
        this.addWall(eastWall);

        //South wall
        Rectangle southWall = new Rectangle(0 - Wall.THICKNESS, 0 - Wall.THICKNESS, this.getWidth() + 2*Wall.THICKNESS, Wall.THICKNESS);
        this.addWall(southWall);

        //West wall
        Rectangle westWall = new Rectangle(0 - Wall.THICKNESS, 0 - Wall.THICKNESS, Wall.THICKNESS, this.getHeight() + 2*Wall.THICKNESS);
        this.addWall(westWall);
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public ArrayList<Wall> getWalls() { return walls; }
    public ArrayList<Building> getBuildings() { return buildings; }
    public ArrayList<Door> getDoors() { return doors; }
    public ArrayList<Window> getWindows() { return windows; }
    public ArrayList<SentryTower> getSentryTowers() { return sentryTowers; }
    public ArrayList<HidingArea> getHidingAreas() { return hidingAreas; }
    public ArrayList<TargetArea> getTargetAreas() { return targetAreas; }
    public ArrayList<Guard> getGuards() {
        return guards;
    }
    public ArrayList<Marker> getMarkers() { return markers; }
    public ArrayList<Marker> getRemovedMarkers() {return removedMarkers; }

    public ArrayList<Intruder> getIntruders() {
        return intruders;
    }

    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }
    public void setWalls(ArrayList<Wall> walls) { this.walls = walls; }
    public void setBuildings(ArrayList<Building> buildings) { this.buildings = buildings; }
    public void setDoors(ArrayList<Door> doors) { this.doors = doors; }
    public void setWindows(ArrayList<Window> windows) { this.windows = windows; }
    public void setSentryTowers(ArrayList<SentryTower> sentryTowers) { this.sentryTowers = sentryTowers; }
    public void setHidingAreas(ArrayList<HidingArea> hidingAreas) { this.hidingAreas = hidingAreas; }
    public void setTargetAreas(ArrayList<TargetArea> targetAreas) { this.targetAreas = targetAreas; }
    public void setGuards(ArrayList<Guard> guards) { this.guards = guards; }
    public void setIntruders(ArrayList<Intruder> intruders) { this.intruders = intruders; }
    public void setRemovedMarkers(ArrayList<Marker> removedMarkers) { this.removedMarkers = removedMarkers; }
    public void setMarkers(ArrayList<Marker> markers) { this.markers = markers; }
}
