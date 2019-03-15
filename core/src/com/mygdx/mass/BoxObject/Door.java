package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Test.Constants;

public class Door extends BoxObject {

    public Door (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = Constants.DOOR;
    }

}
