package com.mygdx.mass.Agents;

import box2dLight.ConeLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Algorithms.Capture;
import com.mygdx.mass.Algorithms.Random;
import com.mygdx.mass.Data.MASS;

public class Guard extends Agent {

    public enum State {EXPLORE, PATROL, CHASE, HUNT};
    public static final float BASE_SPEED = 1.4f;
    public static final float TOWER_VIEW_ANGLE = 30.0f;
    public static final float DEFAULT_VISUAL_RANGE = 6.0f;
    public static final float TOWER_MIN_VISUAL_RANGE = 2.0f;
    public static final float TOWER_MAX_VISUAL_RANGE = 15.0f;

    private float deafDuration;

    private boolean onTower;

    private Capture capture;

    public Guard(MASS mass, Vector2 position) {
        super(mass, position);

        agentType = AgentType.GUARD;
        moveSpeed = BASE_SPEED;
        turnSpeed = 180f;
        visualRange = DEFAULT_VISUAL_RANGE;
        viewAngle = 45.0f;

        define(position);
        Filter filter = new Filter();
        filter.categoryBits = GUARD_BIT;
        filter.maskBits = WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT | VISUAL_FIELD_BIT | NOISE_FIELD_BIT;
        fixture.setFilterData(filter);

//        pointLight = new PointLight(mass.rayHandler, 360, new Color(0,0,1,1), 10, body.getPosition().x, body.getPosition().y);
        coneLight = new ConeLight(mass.rayHandler, 45, new Color(0,0,1,1), visualRange*5, body.getPosition().x, body.getPosition().y, (float) (body.getAngle()*180/Math.PI), viewAngle/2);
        coneLight.attachToBody(body);
        coneLight.setContactFilter(LIGHT_BIT, (short) 0, (short) (WALL_BIT | BUILDING_BIT | DOOR_BIT | SENTRY_TOWER_BIT));
//        algorithm = new Random(this);
        capture = new Capture();
    }

    public void update(float delta) {
        super.update(delta);
        if (capture != null) {
            capture.increaseCounter(delta);
        }
    }

    public Capture getCapture() { return capture; }

}