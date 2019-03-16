package com.mygdx.mass.Agents;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Algorithms.Random;
import com.mygdx.mass.MASS;

import static com.mygdx.mass.Agents.Agent.Type.INTRUDER;

public class Intruder extends Agent {

    public static final float BASE_SPEED = 1.4f;
    public static final float SPRINT_SPEED = 3.0f;

    protected float restTime;
    protected boolean sprinting;

    public Intruder(MASS mass, Vector2 position) {
        super(mass, position);
        type = INTRUDER;
        moveSpeed = BASE_SPEED;
        turnSpeed = 180f;
        visualRange = 7.5f;
        viewAngle = 45.0f;
        pointLight = new PointLight(mass.rayHandler, 360, new Color(1,0,0,1), 10, body.getPosition().x, body.getPosition().y);
        coneLight = new ConeLight(mass.rayHandler, 45, new Color(1,0,0,1), visualRange*10, body.getPosition().x, body.getPosition().y, (float) (body.getAngle()*180/Math.PI), viewAngle/2);
        coneLight.attachToBody(body);
        algorithm = new Random(this);
    }

}