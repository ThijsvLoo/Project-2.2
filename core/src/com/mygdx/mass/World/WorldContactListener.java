package com.mygdx.mass.World;

import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.*;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.Door.State.CLOSED;
import static com.mygdx.mass.BoxObject.Door.State.OPEN;
import static com.mygdx.mass.World.WorldObject.*;

//This class is where all collissions are handled
public class WorldContactListener implements ContactListener {

    MASS mass;

    public WorldContactListener(MASS mass) {
        this.mass = mass;
    }
    
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
        }
    }

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
        }
    }

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
