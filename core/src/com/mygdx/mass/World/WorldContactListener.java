package com.mygdx.mass.World;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Guard;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Sensors.VisualField;

import static com.mygdx.mass.BoxObject.Door.State.CLOSED;
import static com.mygdx.mass.BoxObject.Door.State.OPEN;
import static com.mygdx.mass.World.WorldObject.*;

//This class is where all collissions are handled
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

            // When an agent sees another agent
            case VISUAL_FIELD_BIT | GUARD_BIT :
            case VISUAL_FIELD_BIT | INTRUDER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                Agent agent1 = visualField.getAgent();
                Agent agent2 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                if (agent1.getAgentType() != agent2.getAgentType()) {
                    agent1.getObjectsInSight().add(agent2);
                }
                break;
            }

            // When an agent sees a boxObject like building, sentry tower, hiding area, target area
            case VISUAL_FIELD_BIT | BUILDING_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                if (visualField.getVisualFieldType() == VisualField.VisualFieldType.BUILDING) {
                    Building building = fixtureA.getUserData() instanceof Building ? (Building) fixtureA.getUserData() : (Building) fixtureB.getUserData();
                    Agent agent = visualField.getAgent();
                    if (agent instanceof Guard) {
                        for (Guard guard : mass.getMap().getGuards()) { //Global communication, share with all other agent
                            if (!guard.getIndividualMap().getBuildings().contains(building)) {
                                guard.getIndividualMap().addBuilding(building);
                            }
                        }
                    } else {
                        for (Intruder intruder : mass.getMap().getIntruders()) {
                            if (!intruder.getIndividualMap().getBuildings().contains(building)) {
                                intruder.getIndividualMap().addBuilding(building);
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
                    if (agent instanceof Guard) {
                        for (Guard guard : mass.getMap().getGuards()) { //Global communication, share with all other agent
                            if (!guard.getIndividualMap().getSentryTowers().contains(sentryTower)) {
                                guard.getIndividualMap().addSentryTower(sentryTower);
                            }
                        }
                    } else {
                        for (Intruder intruder : mass.getMap().getIntruders()) {
                            if (!intruder.getIndividualMap().getSentryTowers().contains(sentryTower)) {
                                intruder.getIndividualMap().addSentryTower(sentryTower);
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
                                guard.getIndividualMap().addHidingArea(hidingArea);
                            }
                        }
                    } else {
                        for (Intruder intruder : mass.getMap().getIntruders()) {
                            if (!intruder.getIndividualMap().getHidingAreas().contains(hidingArea)) {
                                intruder.getIndividualMap().addHidingArea(hidingArea);
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
                            intruder.getIndividualMap().addTargetArea(targetArea);
                        }
                    }
                }
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

            // When an agent get out of sight of another agent, remove it from its individual map if its an opponent
            case VISUAL_FIELD_BIT | GUARD_BIT :
            case VISUAL_FIELD_BIT | INTRUDER_BIT : {
                VisualField visualField = fixtureA.getUserData() instanceof VisualField ? (VisualField) fixtureA.getUserData() : (VisualField) fixtureB.getUserData();
                Agent agent1 = visualField.getAgent();
                Agent agent2 = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
                if (agent1.getAgentType() != agent2.getAgentType()) {
                    if (agent2 instanceof Guard) {
                        agent1.getIndividualMap().getIntruders().remove(agent2);
                    }

                }
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
            case GUARD_BIT | WALL_BIT :
            case GUARD_BIT | BUILDING_BIT :
            case GUARD_BIT | SENTRY_TOWER_BIT :
            case INTRUDER_BIT | WALL_BIT :
                break;
            case INTRUDER_BIT | BUILDING_BIT : {
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                if (intruder.getDoor() != null && intruder.getDoor().getCurrentState() == OPEN) {
                    contact.setEnabled(false);
                } else if (intruder.getWindow() != null && intruder.getBreakThroughProgress() >= 100.0f) {
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
