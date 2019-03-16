package com.mygdx.mass.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.MASS;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable {

    public static final float DEFAULT_WIDTH = 200;
    public static final float DEFAULT_HEIGHT = 200;

    public static float width = DEFAULT_WIDTH;
    public static float height = DEFAULT_HEIGHT;

    private MASS mass;

    private ArrayList<Wall> walls;
    private ArrayList<Building> buildings;
    private ArrayList<Door> doors;
    private ArrayList<Window> windows;
    private ArrayList<SentryTower> sentryTowers;
    private ArrayList<HidingArea> hidingAreas;
    private ArrayList<TargetArea> targetAreas;

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

        guards = new ArrayList<Guard>();
        intruders = new ArrayList<Intruder>();
    }

    public Wall addWall(Rectangle rectangle) {
        Wall wall = new Wall(mass, rectangle);
        walls.add(wall);
        return wall;
    }

    public Building addBuilding(Rectangle rectangle) {
        Building building = new Building(mass, rectangle);
        buildings.add(building);
        return building;
    }

    public Door addDoor(Rectangle rectangle) {
        Door door = new Door(mass, rectangle);
        doors.add(door);
        return door;
    }

    public Window addWindow(Rectangle rectangle) {
        Window window = new Window(mass, rectangle);
        windows.add(window);
        return window;
    }

    public SentryTower addSentryTower(Rectangle rectangle) {
        SentryTower sentryTower = new SentryTower(mass, rectangle);
        sentryTowers.add(sentryTower);
        return sentryTower;
    }

    public HidingArea addHidingArea(Rectangle rectangle) {
        HidingArea hidingArea = new HidingArea(mass, rectangle);
        hidingAreas.add(hidingArea);
        return hidingArea;
    }

    public TargetArea addTargetArea(Rectangle rectangle) {
        TargetArea targetArea = new TargetArea(mass, rectangle);
        targetAreas.add(targetArea);
        return targetArea;
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

    public static float getWidth() { return width; }
    public static float getHeight() { return height; }
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
    public ArrayList<Intruder> getIntruders() {
        return intruders;
    }

    public void setWidth(float width) { Map.width = width; }
    public void setHeight(float height) { Map.height = height; }
    public void setWalls(ArrayList<Wall> walls) { this.walls = walls; }
    public void setBuildings(ArrayList<Building> buildings) { this.buildings = buildings; }
    public void setDoors(ArrayList<Door> doors) { this.doors = doors; }
    public void setWindows(ArrayList<Window> windows) { this.windows = windows; }
    public void setSentryTowers(ArrayList<SentryTower> sentryTowers) { this.sentryTowers = sentryTowers; }
    public void setHidingAreas(ArrayList<HidingArea> hidingAreas) { this.hidingAreas = hidingAreas; }
    public void setTargetAreas(ArrayList<TargetArea> targetAreas) { this.targetAreas = targetAreas; }
    public void setGuards(ArrayList<Guard> guards) { this.guards = guards; }
    public void setIntruders(ArrayList<Intruder> intruders) { this.intruders = intruders; }

}
