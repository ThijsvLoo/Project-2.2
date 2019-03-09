package com.mygdx.mass.Agents;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Map;

public class Surveillance extends Agent {

    public Surveillance(World world, Vector2 position) {
        super(world, position);
    }

}
