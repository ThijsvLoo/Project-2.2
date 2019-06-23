package com.mygdx.mass.Sensors;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Graph.Gap;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;

public class GapSensor {

    // 0 to 2pi radian, where 0 is on the right, COUNTER CLOCKWISE (ALWAYS)

    public final static float twoPI = (float) (2*Math.PI);

    private float rayRange, deltaDistanceThreshold, deltaAngleRadThreshold;
    private Vector2 locationAgent;

    private TreeMap<Float, Float> angleDistanceCloudPoints;
    private TreeMap<Float, DeltaCloudPoint> angleDeltaDistanceCloudPoints;
    private TreeMap<Float, Gap> gapList;
    private ArrayList<Gap> cyclicalOrderedGapList;
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

    public ArrayList<Gap> createGapList(ArrayList<Gap> previousGaps) {
        cyclicalOrderedGapList = new ArrayList<Gap>();
        if (createGapList() > 0) {
            if (previousGaps != null) { // we need to order the gaps in the same way
                int gapDifference = gapList.size() - previousGaps.size(); // 1 = gap appeared, 0 = same amount of gaps, -1 = gap disappeared
                if (gapDifference < -1 || gapDifference > 1) {
                    System.out.println("Unfortunately multiple critical events are happening at the same time! Going to throw a NullPointerException!");
                    return null;
                }
                System.out.println();
                System.out.println("Now showing TreeMap: ");
                ArrayList<Gap> gapArrayList = new ArrayList<Gap>();
                for (java.util.Map.Entry<Float, Gap> currentEntry : gapList.entrySet()) {
                    gapArrayList.add(currentEntry.getValue());
                    System.out.print(currentEntry.getValue().getAngleRayAgainstObstacle()+ " + "+currentEntry.getValue().getLocation().x+"/"+currentEntry.getValue().getLocation().y+" | ");
                }
                System.out.println();
                // previous gap must be row vector for matrix
                // gapArrayList must be column vector for matrix
                cyclicalOrderedGapList = alignGapsCOPAP(previousGaps, gapArrayList, gapDifference);
                /*ArrayList<Float> totalMatrixCost = new ArrayList<Float>(previousGaps.size());
                float difference = Math.abs(1f - 2f);
                float cost = (difference > Math.PI) ? twoPI - difference : difference;*/
                /*System.out.println("Previous gap were at angle + distance: ");
                for (Gap g : previousGaps) {
                    System.out.print(g.getAngleRayAgainstObstacle()+ " + "+g.getDistance()+" | ");
                }
                System.out.println();
                System.out.println("New gaps are at angle + distance: ");
                for (Gap g : cyclicalOrderedGapList) {
                    System.out.print(g.getAngleRayAgainstObstacle()+ " + "+g.getDistance()+" | ");
                }
                System.out.println();*/
            } else { // initial start, so no previous gaps to compare to
                for (java.util.Map.Entry<Float, Gap> currentEntry : gapList.entrySet()) {
                    cyclicalOrderedGapList.add(currentEntry.getValue());
                }
            }
        }
        return cyclicalOrderedGapList;
    }

    private ArrayList<Gap> alignGapsCOPAP(ArrayList<Gap> rowVector, ArrayList<Gap> columnVector, int gapDifference) {
        ArrayList<Gap> copyRowVector = new ArrayList<Gap>(rowVector);
        ArrayList<Gap> copyColumnVector = new ArrayList<Gap>(columnVector);
        int columnVectorRotateIndex = Integer.MAX_VALUE;

        if (gapDifference == 0) {
            columnVectorRotateIndex = findLowestCostColumnRotateLOPAP(copyRowVector, copyColumnVector);
            Collections.rotate(copyColumnVector, columnVectorRotateIndex);
            return copyColumnVector;
        }

        else if (gapDifference == -1) { // gap disappearance, meaning we need to remove a row gap before aligning
            float[] columnRotateCost = new float[rowVector.size()];
            int[] rowRemoveIndex = new int[rowVector.size()];
            for (int i = 0 ; i < rowVector.size() ; i++) {
                copyRowVector = new ArrayList<Gap>(rowVector);
                copyColumnVector = new ArrayList<Gap>(columnVector);
                copyRowVector.remove(i);
                rowRemoveIndex[i] = findLowestCostColumnRotateLOPAP(copyRowVector, copyColumnVector); // how many columns rotations for this particular removed row?
                Collections.rotate(copyColumnVector, rowRemoveIndex[i]); // actually rotate the column
                columnRotateCost[i] = costDiagonalSquareMatrix(copyRowVector, copyColumnVector); // get the cost of the rotated matrix
            }
            float rotateCost = Float.MAX_VALUE;
            for (int i = 0 ; i < rowVector.size() ; i++) { // here find the best matching column rotate overall
                if (columnRotateCost[i] < rotateCost) {
                    rotateCost = columnRotateCost[i];
                    columnVectorRotateIndex = i;
                }
            }
            copyColumnVector = new ArrayList<Gap>(columnVector);
            Collections.rotate(copyColumnVector, columnVectorRotateIndex);
            return copyColumnVector;
        }

        else if (gapDifference == 1) { // gap appearing, meaning we need to remove a column gap before aligning
            float[] columnRotateCost = new float[columnVector.size()];
            int[] columnRemoveIndex = new int[columnVector.size()];
            for (int j = 0 ; j < columnVector.size() ; j++) {
                copyRowVector = new ArrayList<Gap>(rowVector);
                copyColumnVector = new ArrayList<Gap>(columnVector);
                copyColumnVector.remove(j);
                columnRemoveIndex[j] = findLowestCostColumnRotateLOPAP(copyRowVector, copyColumnVector); // how many columns rotations for this particular removed column?
                Collections.rotate(copyColumnVector, columnRemoveIndex[j]); // actually rotate the column
                columnRotateCost[j] = costDiagonalSquareMatrix(copyRowVector, copyColumnVector); // get the cost of the rotated matrix
            }
            float rotateCost = Float.MAX_VALUE;
            for (int j = 0 ; j < columnVector.size() ; j++) { // here find the best matching column rotate overall
                if (columnRotateCost[j] < rotateCost) {
                    rotateCost = columnRotateCost[j];
                    columnVectorRotateIndex = j;
                }
            }
            copyColumnVector = new ArrayList<Gap>(columnVector);
            Collections.rotate(copyColumnVector, columnVectorRotateIndex);
            return copyColumnVector;
        }
        return null;
    }

    private int findLowestCostColumnRotateLOPAP(ArrayList<Gap> copyRowVector, ArrayList<Gap> copyColumnVector) {
        float[] columnRotationCosts = new float[copyColumnVector.size()];
        //System.out.println();
        //System.out.println("Float array size: "+columnRotationCosts.length);
        for (int i = 0 ; i < copyColumnVector.size() ; i++) {
            columnRotationCosts[i] = costDiagonalSquareMatrix(copyRowVector, copyColumnVector);
            Collections.rotate(copyColumnVector, 1);
            //System.out.println("First loop");
        }
        float minimumValue = Float.MAX_VALUE;
        int minimumValueIndex = Integer.MAX_VALUE;
        //System.out.println(Arrays.toString(columnRotationCosts));
        for (int i = 0 ; i < columnRotationCosts.length ; i++) {
            if (columnRotationCosts[i] < minimumValue) {
                minimumValue = columnRotationCosts[i];
                minimumValueIndex = i;
                //System.out.println("Second loop");
            }
        }
        return minimumValueIndex;
    }

    private float costDiagonalSquareMatrix(ArrayList<Gap> rowVector, ArrayList<Gap> columnVector) {
        int n = rowVector.size();
        float cost = 0;
        for (int i = 0 ; i < n; i++) {
            float difference = Math.abs(rowVector.get(i).getAngleRayAgainstObstacle() - columnVector.get(i).getAngleRayAgainstObstacle());
            cost += (difference > Math.PI) ? twoPI - difference : difference;
        }
        return cost;
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

    public ArrayList<Gap> getCyclicalOrderedGapList() {
        return cyclicalOrderedGapList;
    }
}
