package com.mygdx.mass.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.BoxObject.Door;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.World.WorldObject.*;

//This class is where all collissions are handled
public class WorldContactListener implements ContactListener {
    
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
//                Agent agent = fixtureA.getUserData() instanceof Agent ? (Agent) fixtureA.getUserData() : (Agent) fixtureB.getUserData();
//                if (agent.getDestination() == null) {
//                    agent.setDestination(new Vector2((float) Math.random()*Map.DEFAULT_WIDTH, (float) Math.random()*Map.DEFAULT_HEIGHT));
//                }
                break;
            case INTRUDER_BIT | DOOR_BIT :
                System.out.println("Intruder collide Wall");
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                Door door = fixtureA.getUserData() instanceof Door ? (Door) fixtureA.getUserData() : (Door) fixtureB.getUserData();
                door.getIntruders().add(intruder);
                if (!door.isLocked()) {
                    short maskBits = GUARD_BIT | INTRUDER_BIT;
                    intruder.setMaskBits(maskBits);
                }
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionDefinition = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (collisionDefinition) {
            case INTRUDER_BIT | DOOR_BIT :
                Intruder intruder = fixtureA.getUserData() instanceof Intruder ? (Intruder) fixtureA.getUserData() : (Intruder) fixtureB.getUserData();
                Door door = fixtureA.getUserData() instanceof Door ? (Door) fixtureA.getUserData() : (Door) fixtureB.getUserData();
                door.getIntruders().remove(intruder);
                if (!door.isLocked()) {
//                    short maskBits = WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT | HIDING_AREA_BIT | TARGET_AREA_BIT | GUARD_BIT | INTRUDER_BIT;
//                    intruder.setMaskBits(maskBits);
                }
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
