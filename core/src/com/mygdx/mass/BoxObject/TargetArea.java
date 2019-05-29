package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.TARGET_AREA;

// unknown to guards
// if intruder stands in this area for 3 seconds they win
// if intruder visits this area twice with more than 3 seconds apart, they also win

public class TargetArea extends BoxObject {

    public TargetArea (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        Filter filter = new Filter();
        filter.categoryBits = TARGET_AREA_BIT;
        filter.maskBits = INTRUDER_BIT | VISUAL_FIELD_BIT;
        fixture.setFilterData(filter);
        fixture.setSensor(true);
        objectType = TARGET_AREA;
    }

}
