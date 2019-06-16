

/*public class Exploration extends Algorithm {

    private Agent agent;

    private float count;

    public Exploration(Agent agent) {
        this.agent = agent;
        this.agent.addWaypoint(new Vector2((float) Math.random()* MASS.map.getWidth(), (float) Math.random()*MASS.map.getHeight()));
    }

    @Override
    public void act() {
        count=0;
        if (agent.getAgentType() == Agent.AgentType.GUARD) {
            if (agent.getDestination() == null && agent.getAgentsMap().getToVisit().size() > 0 && count<agent.getAgentsMap().getToVisit().size()) {
//                agent.getRoute().clear();
                agent.setDestination(agent.getAgentsMap().getToVisit().get(0));
                agent.updateDirection();
                agent.updateAngle();
                agent.updateVelocity();
                agent.followRoute();


            } else {
                if (agent.getRoute().isEmpty()) {
                    agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
                }
                agent.followRoute();
            }
        }
        else {
            if (agent.getRoute().isEmpty()) {
                agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
            }
            agent.followRoute();
        }
    }

} */

package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.Map;
import com.mygdx.mass.World.WorldObject;

import java.util.ArrayList;

public class Exploration extends Algorithm {

    private Agent agent;

    private float count;

    public Exploration(Agent agent) {
        this.agent = agent;
        this.agent.addWaypoint(new Vector2((float) Math.random()* MASS.map.getWidth(), (float) Math.random()*MASS.map.getHeight()));
    }

    @Override
    public void act() {
//        if (Math.random() < 0.001)
//            agent.setMoveSpeed((float) Math.random()*3);
//        if (agent.getAgentType() == Agent.AgentType.GUARD) {
//            ArrayList<WorldObject> objectsInSight = agent.getObjectsInSight();
//            ArrayList<Intruder> intruders = new ArrayList<Intruder>();
//            if (!objectsInSight.isEmpty()) {
//                for (WorldObject worldObject : objectsInSight) {
//                    if (worldObject instanceof Intruder) {
//                        intruders.add((Intruder) worldObject);
//                    }
//                }
//            }
//            if (!intruders.isEmpty()) {
//                agent.getRoute().clear();
//                agent.setDestination(intruders.get(0).getBody().getPosition());
//                agent.updateDirection();
//                agent.updateAngle();
//                agent.updateVelocity();
//            }
//            else {
//                if (agent.getRoute().isEmpty()) {
//                    agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
//                }
//
////                agent.setDestination(agent.getAgentsMap().getToVisit().remove());
//                agent.updateDirection();
//                agent.updateAngle();
//                agent.updateVelocity();
////                agent.followRoute();
//            }
//        } else {
//            if (agent.getRoute().isEmpty()) {
//                agent.addWaypoint(new Vector2((float) Math.random() * MASS.map.getWidth(), (float) Math.random() * MASS.map.getHeight()));
//            }
//            agent.followRoute();
//        }
    }

}

