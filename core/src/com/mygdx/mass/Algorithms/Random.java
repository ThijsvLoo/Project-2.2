package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;

public class Random extends Algorithm {

    private Agent agent;

    private float count;

    public Random(Agent agent) {
        this.agent = agent;
        this.agent.addWaypoint(new Vector2((float) Math.random()* MASS.map.getWidth(), (float) Math.random()*MASS.map.getHeight()));
    }

    @Override
    public void act() {
        if (agent.getRoute().isEmpty()) {
            agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
        }
        agent.followRoute();
    }

}
