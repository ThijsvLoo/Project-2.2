package com.mygdx.mass.Agents;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Algorithms.Random;
import com.mygdx.mass.MASS;

public class Intruder extends Agent {

    public Intruder(MASS mass, Vector2 position) {
        super(mass, position);
        type = Type.INTRUDER;
        pointLight = new PointLight(mass.rayHandler, 50, new Color(1,0,0,1), 20, position.x, position.y);
        velocity = 40;
        algorithm = new Random(this);
    }

    @Override
    public void update(float delta) {
        algorithm.act();
        pointLight.setPosition(body.getPosition());
    }

}