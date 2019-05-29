package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.WALL;

public class Wall extends BoxObject {

    public static final float THICKNESS = 4.0f;

    public Wall (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = WALL;
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
        fixtureDef.filter.categoryBits = WALL_BIT;
        fixtureDef.filter.maskBits = GUARD_BIT | INTRUDER_BIT | LIGHT_BIT | VISUAL_FIELD_BIT;

        body = world.createBody(bodyDef);
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        polygonShape.dispose();
    }

}