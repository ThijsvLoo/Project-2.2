package com.mygdx.mass.World;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mass.Data.MASS;

import java.io.Serializable;

public abstract class WorldObject implements Serializable {

    //Box2D Collision Bits, to identify which world object
    public static final short NOTHING_BIT = 0;
    public static final short WALL_BIT = 1;
    public static final short BUILDING_BIT = 2;
    public static final short DOOR_BIT = 4;
    public static final short WINDOW_BIT = 8;
    public static final short SENTRY_TOWER_BIT = 16;
    public static final short HIDING_AREA_BIT = 32;
    public static final short TARGET_AREA_BIT = 64;
    public static final short GUARD_BIT = 128;
    public static final short INTRUDER_BIT = 256;

    protected MASS mass;

    protected World world;

    protected Body body;
    protected Fixture fixture;

    public WorldObject(MASS mass) {
        this.mass = mass;
        this.world = mass.world;
    }

    public void setCategoryBits(short categoryBits) {
        Filter filter = fixture.getFilterData();
        filter.categoryBits = categoryBits;
        fixture.setFilterData(filter);
    }

    public void setMaskBits(short maskBits) {
        Filter filter = fixture.getFilterData();
        filter.maskBits = maskBits;
        fixture.setFilterData(filter);
    }

    public Body getBody() { return body; }

}
