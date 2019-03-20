package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.WorldObject;

public abstract class BoxObject extends WorldObject implements java.io.Serializable{

    public enum ObjectType {WALL, BUILDING, DOOR, WINDOW, SENTRY_TOWER, HIDING_AREA, TARGET_AREA};
    protected ObjectType objectType;

    protected Rectangle rectangle;

    public BoxObject (MASS mass, Rectangle rectangle) {
        super(mass);
        this.rectangle = rectangle;
        define();
    }

    //Define the box2d object and put it in the box2d world
    public void define() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(rectangle.getCenter(new Vector2()));

        ChainShape chainShape = new ChainShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-rectangle.width/2, -rectangle.height/2);
        vertices[1] = new Vector2(-rectangle.width/2, rectangle.height/2);
        vertices[2] = new Vector2(rectangle.width/2, rectangle.height/2);
        vertices[3] = new Vector2(rectangle.width/2, -rectangle.height/2);
        chainShape.createLoop(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.density = 1.0f;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        chainShape.dispose();
    }

    public ObjectType getObjectType() {
        return objectType;
    }
    public Rectangle getRectangle() {
        return rectangle;
    }

}
