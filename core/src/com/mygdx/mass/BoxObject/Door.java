package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.DOOR;

public class Door extends BoxObject {

    public enum State {OPEN, CLOSED};
    protected State currentState;

    public static final float SIZE = 3.0f; //3 meter
    public static final float THICKNESS = 0.5f;

    private Building building;

    public Door (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = DOOR;
        currentState = State.CLOSED;
    }

    //Define the box2d object and put it in the box2d world
    public void define() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(rectangle.getCenter(new Vector2()));

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.width/2, rectangle.height/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = DOOR_BIT;
        fixtureDef.filter.maskBits = GUARD_BIT | INTRUDER_BIT | LIGHT_BIT;
        fixtureDef.isSensor = true;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        polygonShape.dispose();
    }

    public State getCurrentState() {
        return currentState;
    }
    public Building getBuilding() { return this.building; }

    public void setCurrentState(Door.State state) {
        currentState = state;
    }

}