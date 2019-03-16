package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.HIDING_AREA;

public class HidingArea extends BoxObject {

    public HidingArea (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = HIDING_AREA;
    }

}
