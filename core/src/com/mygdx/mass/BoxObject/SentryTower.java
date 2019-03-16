package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.Type.SENTRY_TOWER;

public class SentryTower extends BoxObject {

    public SentryTower (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = SENTRY_TOWER;
    }

}
