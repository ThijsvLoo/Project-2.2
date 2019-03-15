package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Test.Constants;

public class SentryTower extends BoxObject {

    public SentryTower (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = Constants.SENTRY_TOWER;
    }

}
