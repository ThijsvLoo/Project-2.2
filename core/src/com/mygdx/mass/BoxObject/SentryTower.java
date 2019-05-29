package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Data.MASS;

import java.util.concurrent.LinkedBlockingQueue;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.SENTRY_TOWER;

public class SentryTower extends BoxObject {

    private boolean alarm;

    public SentryTower (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        Filter filter = new Filter();
        filter.categoryBits = SENTRY_TOWER_BIT;
        filter.maskBits = GUARD_BIT | INTRUDER_BIT | LIGHT_BIT | VISUAL_FIELD_BIT;
        fixture.setFilterData(filter);
        objectType = SENTRY_TOWER;
    }

}
