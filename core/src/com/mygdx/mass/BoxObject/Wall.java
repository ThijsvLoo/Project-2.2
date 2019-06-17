package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.mass.Data.MASS;
import jdk.internal.dynalink.support.BottomGuardingDynamicLinker;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.WALL;

public class Wall extends BoxObject {

    public static final float THICKNESS = 4.0f;

    public Wall (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = WALL;
    }

    //Define the box2d object and put it in the box2d world
    public void define() {
        /*
           x1  x2             x3  x4
        y1 O---O--------------O---O
           | TL|     TOP      | TR|
        y2 O---O--------------O---O
           |   |              | R |
           | L |              | I |
           | E |      0       | G |
           | F |              | H |
           | T |              | T |
        y3 O---O--------------O---O
           | BL|    BOTTOM    | BR|
        y4 O---O--------------O---O

         */

        float width = rectangle.width;
        float height = rectangle.height;
        float density = 1.0f;
        float thickness = 1.0f;
        float halfThickness = thickness/2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(rectangle.getCenter(new Vector2()));

        PolygonShape tl = new PolygonShape(), top = new PolygonShape(), tr = new PolygonShape(), left = new PolygonShape(),
                right = new PolygonShape(), bl = new PolygonShape(), bottom = new PolygonShape(), br = new PolygonShape();
        tl.setAsBox(halfThickness,halfThickness, new Vector2(0-width/2,0+height/2),0);
        tr.setAsBox(halfThickness,halfThickness, new Vector2(0+width/2,0+height/2),0);
        bl.setAsBox(halfThickness,halfThickness, new Vector2(0-width/2,0-height/2),0);
        br.setAsBox(halfThickness,halfThickness, new Vector2(0+width/2,0-height/2),0);
        top.setAsBox(width/2-halfThickness,halfThickness, new Vector2(0,0+height/2),0);
        bottom.setAsBox(width/2-halfThickness,halfThickness, new Vector2(0,0-height/2),0);
        left.setAsBox(halfThickness,height/2-halfThickness, new Vector2(0-width/2,0),0);
        right.setAsBox(halfThickness,height/2-halfThickness, new Vector2(0+width/2,0),0);

        //PolygonShape polygonShape = new PolygonShape();
        //polygonShape.setAsBox(rectangle.width/2, rectangle.height/2);

        FixtureDef Ftl = new FixtureDef(), Ftop = new FixtureDef(), Ftr = new FixtureDef(), Fleft = new FixtureDef(),
                Fright = new FixtureDef(), Fbl = new FixtureDef(), Fbottom = new FixtureDef(), Fbr = new FixtureDef();

        createFixture(Ftl, tl, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Ftop, top, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Ftr, tr, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Fleft, left, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Fright, right, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Fbl, bl, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Fbottom, bottom, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));
        createFixture(Fbr, br, density, (short) (WALL_BIT), (short) (GUARD_BIT | INTRUDER_BIT | LIGHT_BIT));

        /*FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = WALL_BIT;
        fixtureDef.filter.maskBits = GUARD_BIT | INTRUDER_BIT | LIGHT_BIT;*/

        //fixtureDef.filter.maskBits = GUARD_BIT | INTRUDER_BIT | LIGHT_BIT | VISUAL_FIELD_BIT;

        body = world.createBody(bodyDef);
        //fixture = body.createFixture(fixtureDef);
        body.createFixture(Ftl).setUserData(this);
        body.createFixture(Ftop).setUserData(this);
        body.createFixture(Ftr).setUserData(this);
        body.createFixture(Fleft).setUserData(this);
        body.createFixture(Fright).setUserData(this);
        body.createFixture(Fbl).setUserData(this);
        body.createFixture(Fbottom).setUserData(this);
        body.createFixture(Fbr).setUserData(this);
        //fixture.setUserData(this);



        //polygonShape.dispose();
    }

    public void createFixture(FixtureDef fixture, Shape shape, float density, short category, short mask) {
        fixture.shape = shape;
        fixture.density = density;
        fixture.filter.categoryBits = category;
        fixture.filter.maskBits = mask;
    }

}