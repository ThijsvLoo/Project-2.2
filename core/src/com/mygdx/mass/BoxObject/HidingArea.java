package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.HIDING_AREA;

public class HidingArea extends BoxObject {

    public HidingArea (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        Filter filter = new Filter();
        filter.categoryBits = HIDING_AREA_BIT;
        filter.maskBits = VISUAL_FIELD_BIT;
        fixture.setFilterData(filter);
        fixture.setSensor(true);
        objectType = HIDING_AREA;
    }

}
