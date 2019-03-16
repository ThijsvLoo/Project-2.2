package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.Type.WALL;

public class Wall extends BoxObject {

    public static final float THICKNESS = 4.0f;

    public Wall (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        type = WALL;
    }

}