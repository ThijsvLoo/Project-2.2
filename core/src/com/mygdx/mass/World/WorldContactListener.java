package com.mygdx.mass.World;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Graph.Edge;
import com.mygdx.mass.Sensors.NoiseField;
import com.mygdx.mass.Sensors.VisualField;

import static com.mygdx.mass.BoxObject.Door.State.CLOSED;
import static com.mygdx.mass.BoxObject.Door.State.OPEN;
import static com.mygdx.mass.World.WorldObject.*;

//This class is where all collisions are handled
public class WorldContactListener implements ContactListener {

    MASS mass;

    public WorldContactListener(MASS mass) {
        this.mass = mass;
    }

    // When a collision between 2 objects starts
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (collisionDefinition) {
            case GUARD_BIT | WALL_BIT :
            case GUARD_BIT | BUILDING_BIT :
            case GUARD_BIT | SENTRY_TOWER_BIT :
            case GUARD_BIT | DOOR_BIT : {
                Guard guard = null;
                Door door = null;
                if(fixtureA.getUserData() instanceof Guard){

                   guard = (Guard)fixtureA.getUserData();
                     door = (Door) fixtureB.getUserData();

                }
                else if(fixtureA.getUserData() instanceof Door){
                    guard = (Guard)fixtureB.getUserData();
                     door = (Door) fixtureA.getUserData();

                }
                else {

                    break;
                }
//                System.out.println(fixtureA.getUserData().getClass());
                guard.setDoor(door);
                if (door.getCurrentState() == CLOSED) {
                    guard.setDoorUnlockTime(Intruder.DOOR_UNLOCK_TIME_SLOW + (float) Math.random()*4 - 2);
                }
                break;
            }
            case INTRUDER_BIT | WALL_BIT :
            case INTRUDER_BIT | BUILDING_BIT :
            case INTRUDER_BIT | SENTRY_TOWER_BIT :
                break;
            case INTRUDER_BIT | DOOR_BIT : {
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                Door door = fixtureA.getUserData() instanceof Door ? (Door) fixtureA.getUserData() : (Door) fixtureB.getUserData();
                intruder.setDoor(door);
                if (door.getCurrentState() == CLOSED) {
                    intruder.setDoorUnlockTime(Intruder.DOOR_UNLOCK_TIME_SLOW + (float) Math.random()*4 - 2);
                }
                break;
            }
            case INTRUDER_BIT | WINDOW_BIT : {
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                Window window = fixtureA.getUserData() instanceof Window ? (Window) fixtureA.getUserData() : (Window) fixtureB.getUserData();
                intruder.setWindow(window);
                break;
            }

            case VISUAL_FIELD_BIT | DOOR_BIT :
            case VISUAL_FIELD_BIT | WINDOW_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.AGENT) {
                    BoxObject boxObject = fixtureA.getUserData() instanceof BoxObject ? (BoxObject) fixtureA.getUserData() : (BoxObject) fixtureB.getUserData();
                    Agent agent = visualField.getAgent();
                    agent.getBoxObjectsInSight().add(boxObject);
                }
                break;
            }

            // When an agent sees an enemy, register it to the enemy in sight arraylist
            case VISUAL_FIELD_BIT | GUARD_BIT :
            case VISUAL_FIELD_BIT | INTRUDER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.AGENT) {
                    Agent agent1 = visualField.getAgent();
                    Agent agent2 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                    if (agent1.getAgentType() != agent2.getAgentType()) {
//                        if(agent1.isRayCastOff()) agent1.getEnemyInSight().add(agent2);
                        agent1.getEnemyInSight().add(agent2);
                        agent1.getRoute().clear();
                        //the following is hard coded for the case of global communications
                        if (agent1 instanceof Guard) {
                            for (Guard guard : mass.getMap().getGuards()) {
                                if (!guard.getIndividualMap().getIntruders().contains(agent2)) {
                                    guard.getIndividualMap().getIntruders().add((Intruder) agent2);
                                }
                            }
                        } else {
                            for (Intruder intruder : mass.getMap().getIntruders()) {
                                if (!intruder.getIndividualMap().getGuards().contains(agent2)) {
                                    intruder.getIndividualMap().getGuards().add((Guard) agent2);
                                }
                            }
                        }
                    }
                }
                break;
            }

                // When an agent sees a boxObject like building, sentry tower, hiding area, target area
            case VISUAL_FIELD_BIT | BUILDING_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.BUILDING) {
                    Building building = fixtureA.getUserData() instanceof Building ? (Building) fixtureA.getUserData() : (Building) fixtureB.getUserData();
                    Agent agent = visualField.getAgent();
                    if (agent.isRayCastOff()) agent.getBoxObjectsInSight().add(building);
                    if (agent instanceof Guard) {
                        for (Guard guard : mass.getMap().getGuards()) { //Global communication, share with all other agent
                            if (!guard.getIndividualMap().getBuildings().contains(building)) {
                                guard.getIndividualMap().add(building);
                            }
                        }
                    } else {
                        for (Intruder intruder : mass.getMap().getIntruders()) {
                            if (!intruder.getIndividualMap().getBuildings().contains(building)) {
                                intruder.getIndividualMap().add(building);
                            }
                        }
                    }
                }
                break;
            }

            case VISUAL_FIELD_BIT | SENTRY_TOWER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.TOWER) {
                    SentryTower sentryTower = fixtureA.getUserData() instanceof SentryTower ? (SentryTower) fixtureA.getUserData() : (SentryTower) fixtureB.getUserData();
                    Agent agent = visualField.getAgent();
                    if (agent.isRayCastOff()) agent.getBoxObjectsInSight().add(sentryTower);
                    if (agent instanceof Guard) {
                        for (Guard guard : mass.getMap().getGuards()) { //Global communication, share with all other agent
                            if (!guard.getIndividualMap().getSentryTowers().contains(sentryTower)) {
                                guard.getIndividualMap().add(sentryTower);
                            }
                        }
                    } else {
                        for (Intruder intruder : mass.getMap().getIntruders()) {
                            if (!intruder.getIndividualMap().getSentryTowers().contains(sentryTower)) {
                                intruder.getIndividualMap().add(sentryTower);
                            }
                        }
                    }
                }
                break;
            }
            case VISUAL_FIELD_BIT | HIDING_AREA_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.AGENT) {
                    HidingArea hidingArea = fixtureA.getUserData() instanceof HidingArea ? (HidingArea) fixtureA.getUserData() : (HidingArea) fixtureB.getUserData();
                    Agent agent = visualField.getAgent();
                    if (agent instanceof Guard) {
                        for (Guard guard : mass.getMap().getGuards()) { //Global communication, share with all other agent
                            if (!guard.getIndividualMap().getHidingAreas().contains(hidingArea)) {
                                guard.getIndividualMap().add(hidingArea);
                            }
                        }
                    } else {
                        for (Intruder intruder : mass.getMap().getIntruders()) {
                            if (!intruder.getIndividualMap().getHidingAreas().contains(hidingArea)) {
                                intruder.getIndividualMap().add(hidingArea);
                            }
                        }
                    }
                }
                break;
            }
            case VISUAL_FIELD_BIT | TARGET_AREA_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.AGENT && visualField.getAgent() instanceof Intruder) {
                    TargetArea targetArea = fixtureA.getUserData() instanceof TargetArea ? (TargetArea) fixtureA.getUserData() : (TargetArea) fixtureB.getUserData();
                    for (Intruder intruder : mass.getMap().getIntruders()) {
                        if (!intruder.getIndividualMap().getTargetAreas().contains(targetArea)) {
                            intruder.getIndividualMap().add(targetArea);
                            intruder.setEntryPoint(targetArea.getObjCenter().getCoordinates());
                        }
                    }
                }
                break;
            }

            case NOISE_FIELD_BIT | GUARD_BIT :
            case NOISE_FIELD_BIT | INTRUDER_BIT : {
                NoiseField noiseField = fixtureA.getUserData() instanceof NoiseField ? (NoiseField) fixtureA.getUserData() : (NoiseField) fixtureB.getUserData();
                Agent agent1 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                Agent agent2 = noiseField.getAgent();
                agent1.getUnknownSounds().add(agent2);
                agent1.getRoute().clear();
                break;
            }
        }
    }

    // When a collision between 2 objects ends
    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (collisionDefinition) {
            case GUARD_BIT | WALL_BIT :
            case GUARD_BIT | BUILDING_BIT :
            case GUARD_BIT | SENTRY_TOWER_BIT :
            case GUARD_BIT | DOOR_BIT : {

                Guard guard = null;

                if(fixtureA.getUserData() instanceof Guard){

                    guard = (Guard)fixtureA.getUserData();


                }
                else if(fixtureA.getUserData() instanceof Door){
                    guard = (Guard)fixtureB.getUserData();


                }
                else if(fixtureA.getUserData() instanceof Building){
                    guard = (Guard)fixtureB.getUserData();


                }
//                System.out.println(fixtureA.getUserData().getClass());
                if (guard != null) {
                    guard.setDoor(null);
                    guard.setBreakThroughProgress(0.0f);
                    guard.setDoorUnlockTime(0.0f);
                }
                break;
            }
            case INTRUDER_BIT | WALL_BIT :
            case INTRUDER_BIT | BUILDING_BIT :
            case INTRUDER_BIT | SENTRY_TOWER_BIT :
                break;
            case INTRUDER_BIT | DOOR_BIT : {
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                Door door = fixtureA.getUserData() instanceof Door ? (Door) fixtureA.getUserData() : (Door) fixtureB.getUserData();
                intruder.setDoor(null);
                intruder.setBreakThroughProgress(0.0f);
                intruder.setDoorUnlockTime(0.0f);
                break;
            }
            case INTRUDER_BIT | WINDOW_BIT : {
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                intruder.setWindow(null);
                intruder.setBreakThroughProgress(0.0f);
                break;
            }

            case VISUAL_FIELD_BIT | DOOR_BIT :
            case VISUAL_FIELD_BIT | WINDOW_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.AGENT) {
                    BoxObject boxObject = fixtureA.getUserData() instanceof BoxObject ? (BoxObject) fixtureA.getUserData() : (BoxObject) fixtureB.getUserData();
                    Agent agent = visualField.getAgent();
                    agent.getBoxObjectsInSight().remove(boxObject);
                }
                break;
            }

            // When an enemy get out of sight of another agent, remove it from the enemy in sight arraylist
            case VISUAL_FIELD_BIT | GUARD_BIT :
            case VISUAL_FIELD_BIT | INTRUDER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.AGENT) {
                    Agent agent1 = visualField.getAgent();
                    Agent agent2 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                    if (agent1.getAgentType() != agent2.getAgentType()) {
                        agent1.getEnemyInSight().remove(agent2);
                        //the following is hardcoded for global communications
                        if (agent1.getAgentType() == Agent.AgentType.GUARD) {
                            for (Guard guard : mass.getMap().getGuards()) {
                                if (guard != agent1 && guard.getEnemyInSight().contains(agent2)) {
                                    break;
                                }
                            }
                            for (Guard guard : mass.getMap().getGuards()) {
                                if (guard.getIndividualMap().getIntruders().contains(agent2)) {
                                    guard.getIndividualMap().getIntruders().remove(agent2);
                                }
                            }
                        } else {
                            for (Intruder intruder : mass.getMap().getIntruders()) {
                                if (intruder != agent1 && intruder.getEnemyInSight().contains(agent2)) {
                                    break;
                                }
                            }
                            for (Intruder intruder : mass.getMap().getIntruders()) {
                                if (intruder.getIndividualMap().getGuards().contains(agent2)) {
                                    intruder.getIndividualMap().getGuards().remove(agent2);
                                }
                            }
                        }
                    }
                }
                break;
            }

            //remove a boxObject from the box object in sight list when its out of vision
            case VISUAL_FIELD_BIT | BUILDING_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                Building building = fixtureA.getUserData() instanceof Building ? (Building) fixtureA.getUserData() : (Building) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.BUILDING) {
                    visualField.getAgent().getBoxObjectsInSight().remove(building);
                }
                break;
            }
            case VISUAL_FIELD_BIT | SENTRY_TOWER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                SentryTower sentryTower = fixtureA.getUserData() instanceof SentryTower ? (SentryTower) fixtureA.getUserData() : (SentryTower) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.TOWER) {
                    visualField.getAgent().getBoxObjectsInSight().remove(sentryTower);
                }
                break;
            }

            case NOISE_FIELD_BIT | GUARD_BIT :
            case NOISE_FIELD_BIT | INTRUDER_BIT : {
                NoiseField noiseField = fixtureA.getUserData() instanceof NoiseField ? (NoiseField) fixtureA.getUserData() : (NoiseField) fixtureB.getUserData();
                Agent agent1 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                Agent agent2 = noiseField.getAgent();
                agent1.getUnknownSounds().remove(agent2);
                break;
            }
        }
    }

    // Thing that get handle before a collision starts, which allow you to disable collision before it starts
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (collisionDefinition) {

            //if an agent is behind a building or tower, it can't be detected by the visual field
            case VISUAL_FIELD_BIT | GUARD_BIT :
            case VISUAL_FIELD_BIT | INTRUDER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                Agent agent1 = visualField.getAgent();
                Agent agent2 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                if (!agent1.getBoxObjectsInSight().isEmpty()) {
                    for (BoxObject boxObject : agent1.getBoxObjectsInSight()) {
                        if (boxObject instanceof Building) {
                            if (Intersector.intersectSegmentRectangle(agent1.getBody().getPosition(), agent2.getBody().getPosition(), boxObject.getRectangle())) {
                                contact.setEnabled(false);
                                //this essentially say if an open door or window is within the path of vision, he can see the other agent
                                //however this might not behave correctly, since it has to first see the door and the target to register in its objects insight list
                                //if this does indeed cause an incorrect detection, a way to fix is to check the list of all building every frame, which might lag
                                //but i assume it wont, since the agent should register the door before it register the target as being seen
                                for (BoxObject boxObject1 : agent1.getBoxObjectsInSight()) {
                                    if (boxObject1 instanceof Door) {
                                        if (((Door) boxObject1).getCurrentState() == Door.State.OPEN && Intersector.intersectSegmentRectangle(agent1.getBody().getPosition(), agent2.getBody().getPosition(), boxObject1.getRectangle())) {
                                            contact.setEnabled(true);
                                        }
                                    } else if (boxObject1 instanceof Window) {
                                        if (Intersector.intersectSegmentRectangle(agent1.getBody().getPosition(), agent2.getBody().getPosition(), boxObject1.getRectangle())) {
                                            contact.setEnabled(true);
                                        }
                                    }
                                }
                            }
                        } else if (boxObject instanceof SentryTower) {
                            if (Intersector.intersectSegmentRectangle(agent1.getBody().getPosition(), agent2.getBody().getPosition(), boxObject.getRectangle())) {
                                contact.setEnabled(false);
                            }
                        }
                    }
                }
                break;
            }

            case GUARD_BIT | WALL_BIT :

            case GUARD_BIT | SENTRY_TOWER_BIT :
            case INTRUDER_BIT | WALL_BIT :
                break;

                //if an intruder break a door or window, its collision with the building is disable, so it can go through
            case INTRUDER_BIT | BUILDING_BIT : {
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                if (intruder.getDoor() != null && intruder.getDoor().getCurrentState() == OPEN) {
                    contact.setEnabled(false);
                } else if (intruder.getWindow() != null && intruder.getBreakThroughProgress() >= 100.0f) {
                    contact.setEnabled(false);
                }
                break;
            }
            case GUARD_BIT | BUILDING_BIT : {
                Guard guard = fixtureA.getUserData() instanceof Guard ? (Guard) fixtureA.getUserData() : (Guard) fixtureB.getUserData();
                if (guard.getDoor() != null && guard.getDoor().getCurrentState() == OPEN) {
                    contact.setEnabled(false);
                }
                break;
            }
            case INTRUDER_BIT | SENTRY_TOWER_BIT :
            case INTRUDER_BIT | DOOR_BIT :
                break;
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
