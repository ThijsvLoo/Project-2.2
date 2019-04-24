package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.World.WorldObject;

public class NoiseField {

    protected Agent agent;

    protected Fixture fixture;

    public NoiseField(Agent agent){
        this.agent = agent;
        define();
    }

    public void define() {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = WorldObject.NOISE_FIELD_BIT;
        fixtureDef.filter.maskBits = WorldObject.GUARD_BIT | WorldObject.INTRUDER_BIT;

        fixture = agent.getBody().createFixture(fixtureDef);
        fixture.setUserData(this);

        circleShape.dispose();
    }

    public void update() {
        if (agent.getMoveSpeed() < 0.5) {
            fixture.getShape().setRadius(1);
        } else if (agent.getMoveSpeed() >= 0.5 && agent.getMoveSpeed() < 1) {
            fixture.getShape().setRadius(3);
        } else if (agent.getMoveSpeed() >= 1 && agent.getMoveSpeed() < 2) {
            fixture.getShape().setRadius(5);
        } else {
            fixture.getShape().setRadius(10);
        }
    }

    public Agent getAgent() { return agent; }

}
