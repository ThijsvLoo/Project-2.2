package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.SENTRY_TOWER;

public class SentryTower extends BoxObject {

    public SentryTower (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = SENTRY_TOWER;
    }

}
