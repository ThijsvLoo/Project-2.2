package com.mygdx.mass.World;

public abstract class WorldObject {

    //Box2D Collision Bits, to identify which world object
    public static final short NOTHING_BIT = 0;
    public static final short WALL_BIT = 1;
    public static final short BUILDING_BIT = 2;
    public static final short DOOR_BIT = 2;
    public static final short SENTRY_TOWER_BIT = 4;
    public static final short HIDING_AREA_BIT = 8;
    public static final short TARGET_AREA_BIT = 16;
    public static final short SURVEILLANCE_AGENT_BIT = 64;
    public static final short INTRUDER_BIT = 128;

    public WorldObject() {}

}
