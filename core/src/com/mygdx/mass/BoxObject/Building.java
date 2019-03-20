package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.BUILDING;

public class Building extends BoxObject {

    ArrayList<Door> doors;
    ArrayList<Window> windows;

    public Building (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        Filter filter = new Filter();
        filter.categoryBits = BUILDING_BIT;
        filter.maskBits = GUARD_BIT | INTRUDER_BIT;
        fixture.setFilterData(filter);
        objectType = BUILDING;
        doors = new ArrayList<Door>();
        windows = new ArrayList<Window>();
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    public void addWindow(Window window) {
        windows.add(window);
    }

    public ArrayList<Door> getDoors() { return doors; }
    public ArrayList<Window> getWindows() { return windows; }

    public void setDoors(ArrayList<Door> doors) { this.doors = doors; }
    public void setWindows(ArrayList<Window> windows) { this.windows = windows; }

}
