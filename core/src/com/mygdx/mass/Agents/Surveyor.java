package com.mygdx.mass.Agents;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Algorithms.Random;
import com.mygdx.mass.MASS;
import com.mygdx.mass.Test.Constants;

public class Surveyor extends Agent {

    public static final float BASE_SPEED = 1.4f;

    public Surveyor(MASS mass, Vector2 position) {
        super(mass, position);
        type = Constants.SURVEYOR;
        moveSpeed = BASE_SPEED;
        turnSpeed = 180f;
        visualRange = 30.0f;
        viewAngle = 45.0f;
        pointLight = new PointLight(mass.rayHandler, 360, new Color(0,0,1,1), 10, body.getPosition().x, body.getPosition().y);
        coneLight = new ConeLight(mass.rayHandler, 45, new Color(0,0,1,1), visualRange*10, body.getPosition().x, body.getPosition().y, (float) (body.getAngle()*180/Math.PI), viewAngle/2);
        coneLight.attachToBody(body);
        algorithm = new Random(this);
    }

}