package com.mygdx.mass.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.mass.BoxObject.Building;
import com.mygdx.mass.BoxObject.HidingArea;
import com.mygdx.mass.BoxObject.SentryTower;
import com.mygdx.mass.BoxObject.TargetArea;
import com.mygdx.mass.Data.MASS;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class IndividualMap extends Map {

    public IndividualMap(MASS mass) {
        super(mass);
        this.walls = mass.getMap().walls;
    }

    public IndividualMap(MASS mass, float width, float height) {
        super(mass, width, height);
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void addSentryTower(SentryTower sentryTower) {
        sentryTowers.add(sentryTower);
    }

    public void addHidingArea(HidingArea hidingArea) {
        hidingAreas.add(hidingArea);
    }

    public void addTargetArea(TargetArea targetArea) {
        targetAreas.add(targetArea);
    }

}
