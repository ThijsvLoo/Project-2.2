package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.mass.Agents.Intruder;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.DOOR;

public class Door extends BoxObject {

    public static final float SIZE = 3.0f; //2 meter
    public static final float UNLOCK_TIME = 12.0f;

    private boolean locked;
    private float unlockTimeRemain;

    private ArrayList<Intruder> intruders;

    public Door (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        Filter filter = new Filter();
        filter.categoryBits = DOOR_BIT;
        filter.maskBits = GUARD_BIT | INTRUDER_BIT;
        fixture.setFilterData(filter);
        fixture.setSensor(true);
        objectType = DOOR;
        locked = true;
        unlockTimeRemain = UNLOCK_TIME;
        intruders = new ArrayList<Intruder>();
    }

    public void update(float delta) {
        unlock(delta);
    }

    public void unlock(float delta) {
        if (intruders.size() > 0 && unlockTimeRemain > 0) {
            unlockTimeRemain -= intruders.size()*delta;
            if (unlockTimeRemain <= 0) {
                locked = false;
                for (Intruder intruder : intruders) {
                    short maskBits = GUARD_BIT | INTRUDER_BIT;
                    intruder.setMaskBits(maskBits);
                }
            }
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public ArrayList<Intruder> getIntruders() { return intruders; }

}
