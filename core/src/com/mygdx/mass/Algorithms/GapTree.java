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
    protected ArrayList<Vector2> allGapsPositions;
    protected ArrayList<Vector2> previousIterationGapsPosition;
    protected TreeMap<Float, Gap> appearingGaps;
    protected TreeMap<Float, Gap> disappearingGaps;
    protected TreeMap<Float, Gap> previousGaps;
    protected ArrayList<Double> degrees;
    protected Node chasedGap;
    protected float chasedGapAngle;
    protected double slackAngle;

    public GapTree(Agent agent) {
        this.agent = agent;
        parent = null;
        P = new Vector2();
        slackVar = 1;
        allGapsPositions = new ArrayList<Vector2>();
        slackAngle = 35;
    }

    public void initializeGT(Vector2 startingPosition, TreeMap<Float, Gap> detectedGaps) {
        Node start = new Node(startingPosition);
        for (Map.Entry<Float, Gap> E : detectedGaps.entrySet()) {
            addGap(E.getValue(), start);
            allGapsPositions.add(E.getValue().getLocation());
            previousIterationGapsPosition.add(E.getValue().getLocation());
            previousGaps.put(E.getKey(), E.getValue());
        }
        chaseGap();
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
        }*/

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
        }

        checkCriticalEvents(appearingGaps, disappearingGaps, chasedGap, chasedGapAngle);
        check If Merged or Split
        gap splits if there are two new gaps that are in similar angle, same with merging, gaps merge into one if they were similar angle,
        otherwise they only disappeared

    }

    public void removeGap(Node node) {
        int i = nodes.indexOf(node);
        nodes.get(i).getParent().getChildren().remove(i);
        nodes.get(i).connections.clear();
        nodes.remove(i);
    }

    public Node addGap(Gap gap) {
        Node newNode = new Node(parent, gap, gap.getLocation());
        //this.parent = newNode;
        nodes.add(newNode);
        return newNode;
    }

    public Node addGap(Gap gap, Node parent) {
        Node newNode = new Node(parent, gap, gap.getLocation());
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
    public boolean checkIfNewGap(Gap gap) {
        P = gap.getLocation();
        for (Vector2 V : allGapsPositions) { //not sure if allGapsPosition or previousIterationGapsPosition
            if ( (Math.sqrt(P.x - V.x) + Math.sqrt(P.y - V.y)) <= Math.sqrt(slackVar)) {
                return false;
            }
        }
        Vector2 newGapLocation = new Vector2(P.x,P.y);
        allGapsPositions.add(newGapLocation);
        return true;
    }

    public boolean checkIfDisappear(Gap gapToCheck, TreeMap<Float, Gap> currentGaps) {
        for (Map.Entry<Float, Gap> g : currentGaps.entrySet()) {
            if ( (Math.sqrt(gapToCheck.getLocation().x - g.getValue().getLocation().x) + Math.sqrt(gapToCheck.getLocation().y - g.getValue().getLocation().y)) <= Math.sqrt(slackVar)) {
                return false;
            }
        }
        return true;
    }

    public void checkCriticalEvents(TreeMap<Float, Gap> appearingGaps, TreeMap<Float, Gap> disappearingGaps, Node chasedGap, float chasedGapAngle) {
        if (appearingGaps.isEmpty()) chasedGap.setPrimitive(true);
        for (Map.Entry<Float, Gap> g : appearingGaps.entrySet()) {
            double degrees;
            degrees = Math.toDegrees(g.getKey());
            Double[] values = disappearingGaps.keySet().toArray(new Double[disappearingGaps.size()]);
            double degrees1 = Integer.MAX_VALUE;
            double degrees2 = Integer.MAX_VALUE;
            for (int i = 0; i < values.length; i++) {
                double temp1 = Math.toDegrees(values[i]);
                if (Math.abs(degrees - temp1) < degrees1) {
                        degrees1 = temp1;
                    }
                }
        }
        for (Map.Entry<Float, Gap> g : appearingGaps.entrySet()) {
                addGap(g.getValue(), chasedGap);
            }



    }



}

