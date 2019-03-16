package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.World.WorldObject;

public abstract class BoxObject extends WorldObject implements java.io.Serializable{

    public enum ObjectType {WALL, BUILDING, DOOR, WINDOW, SENTRY_TOWER, HIDING_AREA, TARGET_AREA};
    protected ObjectType objectType;

    protected MASS mass;

    protected World world;
    protected Rectangle rectangle;
    protected Body body;
    protected Fixture fixture;

    public BoxObject (MASS mass, Rectangle rectangle) {
        this.mass = mass;
        world = mass.world;
        this.rectangle = rectangle;

        define();
    }

    //Define the box2d object and put it in the box2d world
    public void define() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(rectangle.getCenter(new Vector2()));

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(rectangle.getWidth()/2, rectangle.getHeight()/2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        polygonShape.dispose();
    }

    public ObjectType getObjectType() {
        return objectType;
    }
    public Rectangle getRectangle() {
        return rectangle;
    }
    public Body getBody(){
        return body;
    }

}
