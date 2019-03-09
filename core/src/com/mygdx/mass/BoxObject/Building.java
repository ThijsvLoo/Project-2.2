package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;

public class Building extends BoxObject {

    public Building (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = Type.BUILDING;
    }

}
