package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.BUILDING;

public class Building extends BoxObject {

    public Building (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = BUILDING;
    }

}
