package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;

public class HidingArea extends BoxObject {

    public HidingArea (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = Type.HIDING_AREA;
    }

}
