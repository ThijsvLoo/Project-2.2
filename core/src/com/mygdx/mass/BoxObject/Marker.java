package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.mass.Data.MASS;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.mygdx.mass.BoxObject.BoxObject.ObjectType.MARKER;

public class Marker extends BoxObject {

    public static final float SIZE = 1.5f; //1.5 meter
    public static final float THICKNESS = 0.5f;


    public Marker(MASS mass, Rectangle rectangle) {
        super(mass, rectangle);
        objectType = MARKER;
    }
}
