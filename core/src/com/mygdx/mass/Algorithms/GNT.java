package com.mygdx.mass.Algorithms;

import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.World.Map;

import java.util.ArrayList;

public class GNT extends Algorithm{

    Map map;
    ArrayList<Agent> agents = new ArrayList<Agent>();

    public GNT(Map map) {
        this.map = map;
    }

    public void loadAllAgents() {
        for (Agent a: map.getAgents()) {
            agents.add(a);
        }
    }

    public void act () {
        for (Agent a : agents) {
            oneIterationGNTagent(a);
        }
    }

    public void oneIterationGNTagent (Agent agent) {
        agent.fireGapSensor();
        agent.getAllRayCastFields().clear();
        agent.getAllRayCastFields().add(agent.getRayCastFieldGapSensor());
        //agent.getGapSensor().showGapSensorGraphs();
    }
}
