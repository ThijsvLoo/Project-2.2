package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.mass.Data.MASS;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.WINDOW;

public class Window extends BoxObject {

    public Window (MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = WINDOW;
    }

}
