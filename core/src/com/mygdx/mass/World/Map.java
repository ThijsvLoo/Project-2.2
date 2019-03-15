package com.mygdx.mass.World;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Agents.Surveillance;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Tools.MapData;

import java.io.Serializable;
import java.util.ArrayList;

public class Map implements Serializable {

    public static final float WIDTH = 200;
    public static final float HEIGHT = 200;

    private MASS mass;

    private ArrayList<BoxObject> mapObjects;
    private ArrayList<Surveillance> surveyors;
    private ArrayList<Intruder> intruders;

    public Map(MASS mass) {
        this.mass = mass;

        mapObjects = new ArrayList<BoxObject>();
        surveyors = new ArrayList<Surveillance>();
        intruders = new ArrayList<Intruder>();
    }

    public MapData GetMapData(){
        MapData mapData = new MapData();

        return mapData;
    }

    public void addWall(Rectangle rectangle) {
        Wall wall = new Wall(mass, rectangle);
        mapObjects.add(wall);
    }

    public void addBuilding(Rectangle rectangle) {
        Building building = new Building(mass, rectangle);
        mapObjects.add(building);
    }

    public void addSentryTower(Rectangle rectangle) {
        SentryTower sentryTower = new SentryTower(mass, rectangle);
        mapObjects.add(sentryTower);
    }

    public void addHidingArea(Rectangle rectangle) {
        HidingArea hidingArea = new HidingArea(mass, rectangle);
        mapObjects.add(hidingArea);
    }

    public void addTargetArea(Rectangle rectangle) {
        TargetArea targetArea = new TargetArea(mass, rectangle);
        mapObjects.add(targetArea);
    }

    public Surveillance addSurveillance(Vector2 position) {
        Surveillance surveillance = new Surveillance(mass, position);
        surveyors.add(surveillance);
        return surveillance;
    }

    public Intruder addIntruder(Vector2 position) {
        Intruder intruder = new Intruder(mass, position);
        intruders.add(intruder);
        return intruder;
    }

    public ArrayList<BoxObject> getMapObjects() {
        return mapObjects;
    }

    public ArrayList<Agent> getAgents() {
        ArrayList<Agent> agents = new ArrayList<Agent>();
        agents.addAll(intruders);
        agents.addAll(surveyors);
        return agents;
    }

    public ArrayList<Surveillance> getSurveyors() {
        return surveyors;
    }

    public ArrayList<Intruder> getIntruders() {
        return intruders;
    }

}
