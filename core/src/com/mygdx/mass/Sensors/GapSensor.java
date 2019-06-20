package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Graph.Gap;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.TreeMap;

public class GapSensor {

    // 0 to 2pi radian, where 0 is on the right, COUNTER CLOCKWISE (ALWAYS)

    public final static float twoPI = (float) (2*Math.PI);

    private float rayRange, deltaDistanceThreshold, deltaAngleRadThreshold;
    private Vector2 locationAgent;

    private TreeMap<Float, Float> angleDistanceCloudPoints;
    private TreeMap<Float, DeltaCloudPoint> angleDeltaDistanceCloudPoints;
    private TreeMap<Float, Gap> gapList;
    private TreeMap<Float, Float> problemAreasDeltaAngle; // either reached maximum distance of sensor or is lacking data. Key = start, Value = delta

    public GapSensor(float rayRange, float deltaDistanceThreshold, float deltaAngleRadThreshold, Vector2 locationAgent) {
        angleDistanceCloudPoints = new TreeMap<Float, Float>();
        angleDeltaDistanceCloudPoints = new TreeMap<Float, DeltaCloudPoint>();
        gapList = new TreeMap<Float, Gap>();
        problemAreasDeltaAngle = new TreeMap<Float, Float>();
        this.rayRange = rayRange;
        this.deltaDistanceThreshold = deltaDistanceThreshold;
        this.deltaAngleRadThreshold = deltaAngleRadThreshold;
        this.locationAgent = locationAgent;
    }

    // GapSensor needs:
    // 1. complete 360 angle+distance points
    // 2. convert to delta distance
    // 3. extract gaps according to threshold
    // 4. detect delta-angles for which the distance is equal to the maximum distance of ray (dark-spots)

    // - control agent to stand still and 'scan' 360
    // -

    public void addEntry(float angleRad, float distance) {
        // first convert angle to a value of [0, 2pi)
        while ( angleRad < 0) {
            angleRad += twoPI;
        }
        angleRad = (angleRad % twoPI);

        // make sure distance is positive or else 0
        if(distance < 0) distance = 0f;

        this.angleDistanceCloudPoints.put(angleRad, distance);
    }

    public void convertToDeltaDistanceCloudPoints() {
        angleDeltaDistanceCloudPoints.clear();
        if (this.angleDistanceCloudPoints.size() < 1) return; // no entries so we stop immediately
        for (java.util.Map.Entry<Float, Float> currentEntry : this.angleDistanceCloudPoints.entrySet()) {
            float currentKeyAngle = currentEntry.getKey();
            float currentValueDistance = currentEntry.getValue();
            java.util.Map.Entry<Float, Float> nextEntry = this.angleDistanceCloudPoints.higherEntry(currentKeyAngle);
            if (nextEntry == null) return; // we have reached the end of the point cloud, we stop here
            float nextKeyAngle = nextEntry.getKey();
            float nextValueDistance = nextEntry.getValue();

            float deltaAngle = nextKeyAngle - currentKeyAngle;
            float deltaDistance = nextValueDistance - currentValueDistance;

            if (deltaAngle > deltaAngleRadThreshold || currentValueDistance >= rayRange) {
                // the angle between two points is too large or the ray distance is too large, we need to add it to the problem area TreeMap
                problemAreasDeltaAngle.put(currentKeyAngle, deltaAngle);
            }

            angleDeltaDistanceCloudPoints.put(currentKeyAngle, new DeltaCloudPoint(deltaAngle, deltaDistance, currentValueDistance, nextValueDistance));
        }
    }

    public int createGapList() {
        int gaps = 0;
        gapList.clear();
        for (java.util.Map.Entry<Float, DeltaCloudPoint> currentEntry : this.angleDeltaDistanceCloudPoints.entrySet()) {
            DeltaCloudPoint valuePoint = currentEntry.getValue();
            float currentAngle = currentEntry.getKey();
            float deltaAngle = valuePoint.deltaAngle;
            float deltaDistance = valuePoint.deltaDistance;
            float firstDistance = valuePoint.firstDistance;
            float secondDistance = valuePoint.secondDistance;

            if(Math.abs(deltaDistance) >= deltaDistanceThreshold) { // check if we see a gap
                gaps++;
                Gap.GapSide side = null;

                // going CCW and nextDistance - currentDistance, at a negative delta this means LEFT gap
                side = (deltaDistance < 0) ? Gap.GapSide.LEFT : Gap.GapSide.RIGHT;
                gapList.put(currentAngle, new Gap(currentAngle, deltaAngle, side, firstDistance, secondDistance, locationAgent));
            }
        }
        return gaps;
    }

    private class DeltaCloudPoint {
        private float deltaAngle, deltaDistance, firstDistance, secondDistance;

        public DeltaCloudPoint(float deltaAngle, float deltaDistance, float firstDistance, float secondDistance) {
            this.deltaAngle = deltaAngle;
            this.deltaDistance = deltaDistance;
            this.firstDistance = firstDistance;
            this.secondDistance = secondDistance;
        }

        public float getDeltaAngle() {
            return deltaAngle;
        }

        public float getFirstDistance() {
            return firstDistance;
        }

        public float getSecondDistance() {
            return secondDistance;
        }
    }

    public void showGapSensorGraphs() {
        // display three graphs
        // 1. angleDistanceCloudPoints
        // 2. angleDeltaDistanceCloudPoints
        // 3. gapList --> USE BubbleChart style

        double[] x1, x2, y1, y2;
        int oneCount, twoCount;

        if (this.angleDistanceCloudPoints.size() == 0) return;
        x1 = new double[this.angleDistanceCloudPoints.size()];
        y1 = new double[this.angleDistanceCloudPoints.size()];
        oneCount = 0;
        x2 = new double[this.angleDeltaDistanceCloudPoints.size()];
        y2 = new double[this.angleDeltaDistanceCloudPoints.size()];
        twoCount = 0;

        for (java.util.Map.Entry<Float, Float> currentEntry : this.angleDistanceCloudPoints.entrySet()) {
            x1[oneCount] = currentEntry.getKey();
            y1[oneCount] = currentEntry.getValue();
            oneCount++;
        }

        for (java.util.Map.Entry<Float, DeltaCloudPoint> currentEntry : this.angleDeltaDistanceCloudPoints.entrySet()) {
            x2[twoCount] = currentEntry.getKey();
            y2[twoCount] = currentEntry.getValue().deltaDistance;
            twoCount++;
        }

        XYChart chart = QuickChart.getChart("Agent Visibility", "Angle around agent (Radians)", "Distance (meters)", "Collision points", x1, y1);
        XYChart chart2 = QuickChart.getChart("Depth difference around agent", "Angle around agent (Radians)", "Delta distance (meters)", "Delta collision points", x2, y2);

        new SwingWrapper(chart).displayChart();
        new SwingWrapper(chart2).displayChart();

    }

    public TreeMap<Float, Gap> getGapList() {
        return gapList;
    }
}
