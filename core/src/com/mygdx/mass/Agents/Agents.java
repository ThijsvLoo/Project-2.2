package com.mygdx.mass.Agents;
import java.awt.Point;

public abstract class Agents {

    Point position;
    String type;
    int state;
    int count;
    int velocity;


    public String toString(){
        return "Type = "+this.type+" count of Agents = "+this.count;
    }

}
