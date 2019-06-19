package com.mygdx.mass.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class IndividualMap extends Map {

    private Agent agent;

    public IndividualMap(MASS mass) {
        super(mass);
    }

    public ArrayList<Vector2> unexploredPlaces;

    public IndividualMap(MASS mass, float width, float height, Agent agent) {
        super(mass, width, height);
        this.agent = agent;
        unexploredPlaces = new ArrayList<Vector2>();
        for (int i = 1; i < 20; i++) {
            for (int j = 1; j < 20; j++) {
                unexploredPlaces.add(new Vector2(i*10,j*10));
            }
        }
//        for (int i = 1; i < 40; i++) {
//            for (int j = 1; j < 40; j++) {
//                unexploredPlaces.add(new Vector2(i*5,j*5));
//            }
//        }
    }

    public void addGuard(Guard guard) { guards.add(guard); }

    public void addIntruder(Intruder intruder) { intruders.add(intruder); }

    public void add(BoxObject boxObject) {
        if (boxObject instanceof Building) {
            buildings.add((Building) boxObject);
        } else if (boxObject instanceof SentryTower) {
            sentryTowers.add((SentryTower) boxObject);
        } else if (boxObject instanceof HidingArea) {
            hidingAreas.add((HidingArea) boxObject);
        } else if (boxObject instanceof TargetArea) {
            targetAreas.add((TargetArea) boxObject);
        }

        //update the unexplored places, remove all vector2 inside the building
        ArrayList<Vector2> temp = new ArrayList<Vector2>();
        for (Vector2 place : unexploredPlaces) {
            if (boxObject.getRectangle().contains(place)) {
                temp.add(place);
            }
        }
        if (!temp.isEmpty()) {
            unexploredPlaces.removeAll(temp);
        }

        agent.setDestination(null);
    }

    private void updatePath(){
    	agent.recalculatePath();
    }

    public Agent getAgent() { return agent; }
    public ArrayList<Vector2> getUnexploredPlaces() { return unexploredPlaces; }

}
