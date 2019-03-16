package com.mygdx.mass.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;

//This class is where all collissions are handled
public class WorldContactListener implements ContactListener {
    
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if (fixtureA.getUserData() instanceof Agent) {
            ((Agent) fixtureA.getUserData()).setDestination(new Vector2((float) Math.random() * Map.width, (float) Math.random() * Map.height));
        }

        if (fixtureB.getUserData() instanceof Agent) {
            ((Agent) fixtureB.getUserData()).setDestination(new Vector2((float) Math.random() * Map.width, (float) Math.random() * Map.height));
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
