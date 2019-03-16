package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.DOOR;

public class Door extends BoxObject {

    public Door (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = DOOR;
    }

}
