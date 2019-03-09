package com.mygdx.mass.BoxObject;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.awt.*;

import static com.mygdx.mass.Constants.PPM;

public class Wall extends BoxObject {

    public Wall (World world, Rectangle rectangle) {
        super(world, rectangle);
    }

}