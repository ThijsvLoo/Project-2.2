package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.World.WorldObject;

import java.util.ArrayList;

public class Random extends Algorithm {

    private Agent agent;
    private float count;
    private ArrayList<Intruder> intruders = new ArrayList<Intruder>();

    public Random(Agent agent) {
        this.agent = agent;
//        this.agent.addWaypoint(new Vector2((float) Math.random()* MASS.map.getWidth(), (float) Math.random()*MASS.map.getHeight()));
    }

    @Override
    public void act() {
//        if (Math.random() < 0.001)
//            agent.setMoveSpeed((float) Math.random()*3);
        agent.setMoveSpeed(3.0f);
        if (agent.getAgentType() == Agent.AgentType.GUARD) {
            ArrayList<Agent> objectsInSight = agent.getEnemyInSight();

            //intruders.clear();
            if (!objectsInSight.isEmpty()) {
                for (WorldObject worldObject : objectsInSight) {
                    if (worldObject instanceof Intruder) {
                        if (!intruders.contains(worldObject)){
                            intruders.add((Intruder) worldObject);
                        }
                    }
                }
            }
            if (!intruders.isEmpty()) {
                agent.getRoute().clear();
                if(!objectsInSight.contains(intruders.get(0))){
                    agent.setDestination(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
                    agent.updateDirection();
                    agent.updateAngle();
                    agent.updateVelocity();
                    intruders.remove(intruders.get(0));
                }else{
                    agent.setDestination(intruders.get(0).getBody().getPosition());
                    agent.updateDirection();
                    agent.updateAngle();
                    agent.updateVelocity();
                }
            } else {
                if (agent.getRoute().isEmpty()) {
                    agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
                }
                agent.followRoute();
            }
        } else {
            if (agent.getRoute().isEmpty()) {
                agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
            }
            agent.followRoute();
        }
    }

}
