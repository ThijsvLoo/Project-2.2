package com.mygdx.mass.Algorithms;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.mass.Agents.Agent;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Graph.Edge;
import com.mygdx.mass.Graph.Gap;
import com.mygdx.mass.Graph.Node;

import javax.swing.text.Position;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class GapTree {

    public static ArrayList<Node> nodes;
    public static HashMap<Node, ArrayList<Edge>> adjVertices;
    private Node parent;
    private Agent agent;
    protected Vector2 P;
    protected double slackVar;
    protected ArrayList<Double> previousGapsAngles;
    protected TreeMap<Float, Gap> appearingGaps;
    protected TreeMap<Float, Gap> disappearingGaps;
    protected TreeMap<Float, Gap> previousGaps;
    protected ArrayList<Double> degrees;
    protected Node chasedGap;
    protected float chasedGapAngle;
    protected double slackAngle;
    protected int previousIterationGapAmount;
    int gapIndex;

    public GapTree(Agent agent) {
        this.agent = agent;
        parent = null;
        slackVar = 1;
        previousGapsAngles = new ArrayList<Double>();
        slackAngle = 2;
        gapIndex = 0;
    }

    public void initializeGT(Vector2 startingPosition, TreeMap<Float, Gap> detectedGaps) {
        Node start = new Node(startingPosition);
        previousIterationGapAmount = detectedGaps.size();
        for (Map.Entry<Float, Gap> E : detectedGaps.entrySet()) {
            addGap(E.getValue(), start);
            double temp;
            temp = Math.toDegrees(E.getKey());
            previousGapsAngles.add(temp);
            previousGaps.put(E.getKey(), E.getValue());
        }
        //chaseGap();
    }

    public void chaseGap(Node nodeToChase, TreeMap<Float, Gap> detectedGaps) {
        agent.goTo(nodeToChase.getGap().getOffsetLocation());
        nodeToChase.setVisited(true);
        parent = nodes.get(nodes.indexOf(nodeToChase));
        //now we need to update detectedGaps after moving to offsetLocation
        agent.fireGapSensor();

        /*
        //add all gaps degrees into its arraylist
        for (Map.Entry<Float, Gap> entry : agent.getGapSensor().getGapList().entrySet()) {
            double temp = entry.getKey();
            degrees.add(Math.toDegrees(temp));
        }

        //add all but chased gaps, that disappeared into its arraylist
        for (Map.Entry<Float, Gap> gapToCheck : previousGaps.entrySet()) { //Disappeareance
            if (gapToCheck.getValue() == nodeToChase.getGap()) continue;
            if (checkIfDisappear(gapToCheck.getValue(), detectedGaps)) {
                disappearingGaps.put(gapToCheck.getKey(), gapToCheck.getValue());
            }
        }

        //add all never existing gaps into arraylist with all gaps, and add all new gaps for this iteration into its arraylist
        for (Map.Entry<Float, Gap> g : detectedGaps.entrySet()) {
            if (checkIfNewGap(g.getValue())) { //Appeareance
                appearingGaps.put(g.getKey(), g.getValue());
            }
        }*/
        if(Math.abs(previousIterationGapAmount - agent.getGapSensor().getGapList().size()) < 0) {
            checkCriticalEvents(previousIterationGapAmount, agent.getGapSensor().getGapList(), agent.getGapSensor().getGapList().size());
        }
    }

    public void removeGap(Node node) {
        int i = nodes.indexOf(node);
        nodes.get(i).getParent().getChildren().remove(i);
        nodes.get(i).connections.clear();
        nodes.remove(i);
    }

    public Node addGap(Gap gap) {
        Node newNode = new Node(this.parent, gap, gap.getLocation());
        //this.parent = newNode;
        newNode.setIndexOfNode(gapIndex);
        gapIndex++;
        nodes.add(newNode);
        return newNode;
    }

    public Node addGap(Gap gap, Node parent) {
        Node newNode = new Node(parent, gap, gap.getLocation());
        newNode.setIndexOfNode(gapIndex);
        gapIndex++;
        nodes.add(newNode);
        return newNode;
    }

    public Node mergeGap(Node gap1, Node gap2, Gap gapMerged, Vector2 mergeArea) {
        Node newNode = new Node(parent, gapMerged, mergeArea);
        nodes.add(newNode);
        newNode.addChild(gap1);
        newNode.addChild(gap2);
        return newNode;
    }

    //this one is definitely wrong, still have to think about it
    public void splitGap(Node gapToSplit) {
        if (gapToSplit.getChildren().size() != 0) {
            nodes.add(gapToSplit.getChildren().get(0));
            nodes.add(gapToSplit.getChildren().get(1));
        }
    }

    //true if gap hasn't been found before
    /*public boolean checkIfNewGap(Gap gap) {
        P = gap.getLocation();
        for (Vector2 V : allGapsPositions) { //not sure if allGapsPosition or previousIterationGapsPosition
            if ( (Math.sqrt(P.x - V.x) + Math.sqrt(P.y - V.y)) <= Math.sqrt(slackVar)) {
                return false;
            }
        }
        Vector2 newGapLocation = new Vector2(P.x,P.y);
        allGapsPositions.add(newGapLocation);
        return true;
    }*/

    public boolean checkIfDisappear(Gap gapToCheck, TreeMap<Float, Gap> currentGaps) {
        for (Map.Entry<Float, Gap> g : currentGaps.entrySet()) {
            if ( (Math.sqrt(gapToCheck.getLocation().x - g.getValue().getLocation().x) + Math.sqrt(gapToCheck.getLocation().y - g.getValue().getLocation().y)) <= Math.sqrt(slackVar)) {
                return false;
            }
        }
        return true;
    }

    public void checkCriticalEvents(int previousIterationGapAmount, TreeMap<Float, Gap> gapsDetected, int gapsAmount) {
        Float[] degreesF = gapsDetected.keySet().toArray(new Float[gapsDetected.size()]);
        Double[] degrees = new Double[degreesF.length];
        for (int j = 0; j < degreesF.length; j++) {
            degrees[j] = Math.toDegrees(degreesF[j]);
        }
        if ((gapsAmount - previousIterationGapAmount) > 0) { //Gap appeared
           for (int i = 0; i < degrees.length; i++) {
               if (Math.abs(previousGapsAngles.get(i) - degrees[i]) > slackAngle) {
                   if (Math.abs(degrees[i+1] - degrees[i]) > slackAngle) {
                       //need to check neighbors if its a split
                   }
                   addGap(gapsDetected.get(degreesF[i]));
               }
               else {
                   i++;
               }
           }
        }
        else { //Gap disappeared
            for (int i = 0; i < degrees.length; i++) {
                if (Math.abs(previousGapsAngles.get(i) - degrees[i]) > slackAngle) {
                    if (Math.abs(degrees[i+1] - degrees[i]) > slackAngle) {
                        //need to check neighbors if its a merge
                    }
                    removeGap(nodes.get(i));
                }
                else {
                    i++;
                }
            }
        }


    }



}

