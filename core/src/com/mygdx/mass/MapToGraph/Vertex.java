package com.mygdx.mass.MapToGraph;

import com.badlogic.gdx.math.Vector2;

public class Vertex {
    private Vector2 coordinates;
    public Vertex(float x, float y){
        this.coordinates = new Vector2(x,y);

    }

    public Vector2 getCoordinates() {
        return coordinates;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex other = (Vertex) obj;
        if (coordinates == null) {
            if (other.coordinates != null)
                return false;
        } else if (!coordinates.equals(other.coordinates))
            return false;
        return true;
    }
}
