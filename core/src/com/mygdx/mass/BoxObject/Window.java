package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.WINDOW;

public class Window extends BoxObject {

    public static final float SIZE = 1.5f; //1.5 meter
    public static final float THICKNESS = 0.5f;

    private Building building;

    public Window (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = WINDOW;
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
        fixtureDef.filter.categoryBits = WINDOW_BIT;
        fixtureDef.filter.maskBits = INTRUDER_BIT;
        fixtureDef.isSensor = true;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        polygonShape.dispose();
    }

}
