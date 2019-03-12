package com.mygdx.mass.Agents;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Algorithms.Random;
import com.mygdx.mass.MASS;

public class Surveillance extends Agent {

    public Surveillance(MASS mass, Vector2 position) {
        super(mass, position);
        type = Type.SURVEILLANCE;
        pointLight = new PointLight(mass.rayHandler, 50, new Color(0,0,1,1), 20, body.getPosition().x, body.getPosition().y);
//        coneLight = new ConeLight(mass.rayHandler, 50, new Color(0,0,1,1), 50, body.getPosition().x, body.getPosition().y, 270.0f, 360.0f);
        velocity = 40;
        algorithm = new Random(this);
    }

    @Override
    public void update(float delta) {
        algorithm.act();
        pointLight.setPosition(body.getPosition());
//        coneLight.setPosition(body.getPosition());
//        System.out.println(body.getAngle());
    }

}
