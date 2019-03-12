package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Map;

public class Random extends Algorithm {

    private Agent agent;

    public Random(Agent agent) {
        this.agent = agent;
        this.agent.setDestination(new Vector2((float) Math.random()*Map.WIDTH, (float) Math.random()*Map.HEIGHT));
    }

    @Override
    public void act() {
        if (Math.random() < 0.02) {
            agent.setDestination(new Vector2((float) Math.random()*Map.WIDTH, (float) Math.random()*Map.HEIGHT));
        }
        agent.move();
    }

}
