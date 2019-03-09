package com.mygdx.mass;

import com.badlogic.gdx.math.Vector2;

public class Constants {

    //Box2D Collision Bits
    public static final short NOTHING_BIT = 0;
    public static final short WALL_BIT = 1;
    public static final short BUILDING_BIT = 2;
    public static final short SENTRY_TOWER_BIT = 4;
    public static final short HIDING_AREA_BIT = 8;
    public static final short TARGET_AREA_BIT = 16;
    public static final short DOOR_BIT = 32;
    public static final short SURVEILLANCE_AGENT_BIT = 64;
    public static final short INTRUDER_BIT = 128;

    public static final float DEFAULT_ZOOM = 6f;
    public static final float PPM = 50.0f;
    public static final Vector2 RESOLUTION = new Vector2(640, 480);
    public static final int VELOCITY_ITERATION = 6;
    public static final int POSITION_ITERATION = 2;
    public static final int windowWidth = 1000;
    public static final int windowHeight = 1000;
}
