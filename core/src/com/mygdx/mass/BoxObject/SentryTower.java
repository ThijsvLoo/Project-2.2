package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;

public class SentryTower extends BoxObject {

    public SentryTower (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = Type.SENTRY_TOWER;
    }

}
