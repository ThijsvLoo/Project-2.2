package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.World.WorldObject;

public class VisualField {

    public enum VisualFieldType {AGENT, BUILDING, TOWER};
    public VisualFieldType visualFieldType;

    protected Agent agent;

    protected Fixture fixture;

    protected Vector2[] vertices;

    public VisualField(Agent agent, VisualFieldType visualFieldType){
        this.agent = agent;
        this.visualFieldType = visualFieldType;
        define();
    }

    public void define() {
        PolygonShape polygonShape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        switch (visualFieldType) {
            case AGENT: {
                vertices = new Vector2[3];
                vertices[0] = new Vector2(0, 0);
                vertices[1] = new Vector2((float) Math.cos(Math.toRadians(agent.getViewAngle()/2))*agent.getVisualRange(), (float) Math.sin(Math.toRadians(agent.getViewAngle()/2))*agent.getVisualRange());
                vertices[2] = new Vector2((float) Math.cos(Math.toRadians(-agent.getViewAngle()/2))*agent.getVisualRange(), (float) Math.sin(Math.toRadians(-agent.getViewAngle()/2))*agent.getVisualRange());
                fixtureDef.filter.categoryBits = WorldObject.VISUAL_FIELD_BIT;
                fixtureDef.filter.maskBits = WorldObject.GUARD_BIT | WorldObject.INTRUDER_BIT | WorldObject.WALL_BIT | WorldObject.HIDING_AREA_BIT |WorldObject.TARGET_AREA_BIT;
                break;
            }
            case BUILDING: {
                vertices = new Vector2[3];
                vertices[0] = new Vector2(0, 0);
                vertices[1] = new Vector2((float) Math.cos(Math.toRadians(agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_BUILDING, (float) Math.sin(Math.toRadians(agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_BUILDING);
                vertices[2] = new Vector2((float) Math.cos(Math.toRadians(-agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_BUILDING, (float) Math.sin(Math.toRadians(-agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_BUILDING);
                fixtureDef.filter.categoryBits = WorldObject.VISUAL_FIELD_BIT;
                fixtureDef.filter.maskBits = WorldObject.BUILDING_BIT;
                break;
            }
            case TOWER: {
                vertices = new Vector2[3];
                vertices[0] = new Vector2(0, 0);
                vertices[1] = new Vector2((float) Math.cos(Math.toRadians(agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_TOWER, (float) Math.sin(Math.toRadians(agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_TOWER);
                vertices[2] = new Vector2((float) Math.cos(Math.toRadians(-agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_TOWER, (float) Math.sin(Math.toRadians(-agent.getViewAngle()/2))*Agent.VISIBLE_DISTANCE_TOWER);
                fixtureDef.filter.categoryBits = WorldObject.VISUAL_FIELD_BIT;
                fixtureDef.filter.maskBits = WorldObject.SENTRY_TOWER_BIT;
                break;
            }
        }
        polygonShape.set(vertices);
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;
        fixture = agent.getBody().createFixture(fixtureDef);
        fixture.setUserData(this);
        polygonShape.dispose();
    }

    public Agent getAgent() { return agent; }
    public VisualFieldType getVisualFieldType() { return visualFieldType; }
    public Fixture getFixture() { return fixture; }

}
