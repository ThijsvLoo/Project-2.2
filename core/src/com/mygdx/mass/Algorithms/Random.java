package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.World.Map;

public class Random extends Algorithm {

    private Agent agent;

    private float count;

    public Random(Agent agent) {
        this.agent = agent;
        this.agent.setDestination(new Vector2((float) Math.random()*Map.WIDTH, (float) Math.random()*Map.HEIGHT));
        count = 0;
    }

    @Override
    public void act() {
        count += Gdx.graphics.getDeltaTime();
        if (count >= 2) {
            agent.setDestination(new Vector2((float) Math.random()*Map.WIDTH, (float) Math.random()*Map.HEIGHT));
            count = 0;
        }
        agent.move();
    }

}
