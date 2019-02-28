package com.mygdx.mass.Tools;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.mygdx.mass.Constants.PPM;

public class ShapeFactory {
    private ShapeFactory() {}

    public static Body createRectangle(final Vector2 position, final Vector2 size, final BodyDef.BodyType type, final World world, float density) {

        //define body
        final BodyDef bdef = new BodyDef();
        bdef.position.set(position.x/PPM, position.y/PPM);
        bdef.type = type;
        Body body = world.createBody(bdef);

        //define fixture
        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x/PPM, size.y/PPM);
        final FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = density;

        body.createFixture(fdef);
        shape.dispose();

        return body;
    }
}
