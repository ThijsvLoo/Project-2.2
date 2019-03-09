package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

// unknown to guards
// if intruder stands in this area for 3 seconds they win
// if intruder visits this area twice with more than 3 seconds apart, they also win

public class TargetArea extends BoxObject {

    public TargetArea (World world, Rectangle rectangle) {
        super(world, rectangle);
    }

}
