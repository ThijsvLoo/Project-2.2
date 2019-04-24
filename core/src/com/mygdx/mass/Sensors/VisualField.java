package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.World.WorldObject;

public class VisualField {

    public enum VisualFieldType {AGENT, BUILDING, TOWER};
    protected VisualFieldType visualFieldType;

    protected Agent agent;

    protected Fixture fixture;

    protected Vector2[] vertices;

    public VisualField(Agent agent, VisualFieldType visualFieldType){
        this.agent = agent;
        this.visualFieldType = visualFieldType;
        define();
    }

    public void define() {
        switch (visualFieldType) {
            case AGENT: {
                ChainShape chainShape = new ChainShape();
                vertices = new Vector2[3];
                vertices[0] = new Vector2(0, 0);
                vertices[1] = new Vector2((float) Math.cos(Math.toRadians(agent.getViewAngle()/2))*agent.getVisualRange(), (float) Math.sin(Math.toRadians(agent.getViewAngle()/2))*agent.getVisualRange());
                vertices[2] = new Vector2((float) Math.cos(Math.toRadians(-agent.getViewAngle()/2))*agent.getVisualRange(), (float) Math.sin(Math.toRadians(-agent.getViewAngle()/2))*agent.getVisualRange());
                chainShape.createLoop(vertices);

                FixtureDef fixtureDef = new FixtureDef();
                fixtureDef.shape = chainShape;
                fixtureDef.isSensor = true;
                fixtureDef.filter.categoryBits = WorldObject.VISUAL_FIELD_BIT;
                fixtureDef.filter.maskBits = WorldObject.GUARD_BIT | WorldObject.INTRUDER_BIT;

                fixture = agent.getBody().createFixture(fixtureDef);
                fixture.setUserData(this);

                chainShape.dispose();
            }
        }
    }

    public Agent getAgent() { return agent; }

}
