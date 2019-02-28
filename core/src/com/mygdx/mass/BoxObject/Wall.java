package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.awt.*;

import static com.mygdx.mass.Constants.PPM;

public class Wall extends BoxObject {

    private Point topLeft;
    private Point botomRight;
    private String type;
    private Body mWall;

    private Wall() {}

    public static Body createWall(final Vector2 position, final Vector2 size, final World world) {

        //define body
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x / PPM, position.y / PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        //define fixture
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x / PPM, size.y / PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 0.4f;

        body.createFixture(fdef);
        shape.dispose();

        return body;

    }

}