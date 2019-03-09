package com.mygdx.mass.BoxObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class BoxObject {

    protected World world;
    protected Rectangle rectangle;

    protected Body body;
    protected Fixture fixture;

    public BoxObject (World world, Rectangle rectangle) {
        this.world = world;
        this.rectangle = rectangle;

        define();
    }

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

}
