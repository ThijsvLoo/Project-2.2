package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.WALL;

public class Wall extends BoxObject {

    public Wall (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = WALL;
    }

}