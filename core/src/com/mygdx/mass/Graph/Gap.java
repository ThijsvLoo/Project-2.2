package com.mygdx.mass.Graph;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;

public class Gap {
    public enum GapSide {LEFT, RIGHT};
    private enum UnitCircleSide {TOP, BOTTOM, LEFT, RIGHT, Y, X}; // X and Y are exactly on the axis
    private enum UnitCircleQuadrant {TR, TL, BL, BR};
    private enum TypeOfNode {PARENT, LEAF};
    private enum Label {PRIMITIVE, NONPRIMITIVE, CLEARED, CONTAMINATED, RECONTAMINATED};

    // Vector 2 location of Gap
    private Vector2 location;
    private Vector2 offsetLocation; // Gap location with an offset so the agent can move there without running into an object
    private GapSide gapSide;
    private float angleRayAgainstObstacle, distance;
    private UnitCircleQuadrant quadrant;
    private Color color = new Color(); // set color green = left gap and right = red gap
    private Gap childOne, childTwo;


    public Gap (float angleRad, float deltaAngle, GapSide gapSide, float firstDistance, float secondDistance, Vector2 locationAgent) {
        this.gapSide = gapSide;
        this.color = (gapSide == GapSide.RIGHT) ? new Color().add(Color.RED) : new Color().add(Color.GREEN);
        this.angleRayAgainstObstacle = (gapSide == GapSide.RIGHT) ? angleRad : (angleRad + deltaAngle); // pick which ray is the shortest in distance (for angle)
        distance = (gapSide == GapSide.LEFT) ? secondDistance : firstDistance; // pick which ray is the shortest in distance (for distance)
        Vector2 locationAgentLocalCopy = new Vector2(locationAgent); // making sure to make a local copy to prevent Vector2 from acting up
        location = new Vector2(locationAgentLocalCopy.add(new Vector2(distance, 0).rotateRad(angleRad))); // taking ray distance in x position, then rotating it and finally adding it to the location of the agent
        calculateCircleQuadrant(angleRad);
        offsetCalculation((float)(Agent.SIZE*1.5),(float)(Agent.SIZE*1.5));
    }

    private void calculateCircleQuadrant(float angleRad) {
        UnitCircleQuadrant gapIsInQuadrant = null;
        UnitCircleSide xAxis;
        UnitCircleSide yAxis;
        double cosYmirror = Math.cos((double)angleRad); // is this on the left/right of the Y-axis or exactly on the Y axis?
        double sinXmirror = Math.sin((double)angleRad); // is this on the top/bottom of the X-axis or exactly on the X axis?

        if (cosYmirror > 0) yAxis = UnitCircleSide.RIGHT;
        else if (cosYmirror < 0 ) yAxis = UnitCircleSide.LEFT;
        else if (cosYmirror == 0) yAxis = UnitCircleSide.Y;
        else return;

        if (sinXmirror > 0) xAxis = UnitCircleSide.TOP;
        else if (sinXmirror < 0) xAxis = UnitCircleSide.BOTTOM;
        else if (sinXmirror == 0) xAxis = UnitCircleSide.X;
        else return;

        if (yAxis == UnitCircleSide.RIGHT) {
            if (xAxis == UnitCircleSide.TOP) gapIsInQuadrant = UnitCircleQuadrant.TR;
            else if (xAxis == UnitCircleSide.BOTTOM) gapIsInQuadrant = UnitCircleQuadrant.BR;
            else if (xAxis == UnitCircleSide.X) gapIsInQuadrant = UnitCircleQuadrant.TR; // angle rad delta can never pass 2pi, so it'll always be in the TopRight quadrant!
        }
        else if (yAxis == UnitCircleSide.LEFT) {
            if (xAxis == UnitCircleSide.TOP) gapIsInQuadrant = UnitCircleQuadrant.TL;
            else if (xAxis == UnitCircleSide.BOTTOM) gapIsInQuadrant = UnitCircleQuadrant.BL;
            else if (xAxis == UnitCircleSide.X) gapIsInQuadrant = (gapSide == GapSide.RIGHT) ? UnitCircleQuadrant.TL : UnitCircleQuadrant.BL; // angle is exactly pi
        }
        else if (yAxis == UnitCircleSide.Y) {
            if (xAxis == UnitCircleSide.TOP) gapIsInQuadrant = (gapSide == GapSide.RIGHT) ? UnitCircleQuadrant.TR : UnitCircleQuadrant.TL;
            else if (xAxis == UnitCircleSide.BOTTOM) gapIsInQuadrant = (gapSide == GapSide.RIGHT) ? UnitCircleQuadrant.BL : UnitCircleQuadrant.BR;
            else if (xAxis == UnitCircleSide.X) gapIsInQuadrant = null; // something went seriously wrong.
        }


        quadrant = gapIsInQuadrant;
    }

    private void offsetCalculation(float width, float height) {
        Vector2 offset = new Vector2(location);
        if (gapSide == GapSide.LEFT) {
            if (quadrant == UnitCircleQuadrant.TR) offset.add(new Vector2(width, -height));
            else if (quadrant == UnitCircleQuadrant.TL) offset.add(new Vector2(width, height));
            else if (quadrant == UnitCircleQuadrant.BL) offset.add(new Vector2(-width, height));
            else if (quadrant == UnitCircleQuadrant.BR) offset.add(new Vector2(-width, -height));
        }
        else if (gapSide == GapSide.RIGHT) {
            if (quadrant == UnitCircleQuadrant.TR) offset.add(new Vector2(-width, height));
            else if (quadrant == UnitCircleQuadrant.TL) offset.add(new Vector2(-width, -height));
            else if (quadrant == UnitCircleQuadrant.BL) offset.add(new Vector2(width, -height));
            else if (quadrant == UnitCircleQuadrant.BR) offset.add(new Vector2(width, height));
        }
        offsetLocation = offset;
    }


    public Color getColor() {
        return color;
    }

    public Vector2 getOffsetLocation() {
        return new Vector2(offsetLocation);
    }

    public Vector2 getLocation() {
        return new Vector2(location);
    }

    public GapSide getGapSide() {
        return gapSide;
    }

    public float getAngleRayAgainstObstacle() {
        return angleRayAgainstObstacle;
    }

    public float getDistance() {
        return distance;
    }
}
