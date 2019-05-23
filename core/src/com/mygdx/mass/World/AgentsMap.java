package com.mygdx.mass.World;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Data.MASS;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class AgentsMap {
    ArrayList<WorldObject> worldObjects;
   Queue toVisit;
    public AgentsMap(MASS mass){
//        super(mass);
        worldObjects = new ArrayList<WorldObject>();
        toVisit = new PriorityQueue<Vector2>();
        for (int i=0; i< 50; i ++) {
            for (int j = 0; j < 50; j++) {
                toVisit.add(new Vector2(i * 4, j * 4));
            }
        }
    }
//    public void removeVisitList(Vector2 vector2){
//        toVisit.remove(vector2);
//    }


    public void addToVisitList(Vector2 vector2){
        toVisit.add(vector2);
    }

    public void clearToVisitList() {toVisit.clear();}

    public Queue<Vector2> getToVisit(){
        return toVisit;
    }
}
