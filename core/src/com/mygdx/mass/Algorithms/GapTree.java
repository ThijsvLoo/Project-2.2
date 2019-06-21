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

public class GapTree {

    public static ArrayList<Node> nodes;
    public static HashMap<Node, ArrayList<Edge>> adjVertices;
    private Node parent;
    private Agent agent;
    protected Vector2 P;
    protected double slackVar;
    protected ArrayList<Vector2> previousGapsPositions;
    protected boolean TOP = false, BOTTOM = false, LEFT = false, RIGHT = false;
    protected int Top, Bottom, Left, Right;
    protected int previousGapsNumber;

    public GapTree(Agent agent) {
        this.agent = agent;
        parent = null;
        P = new Vector2();
        slackVar = 1;
        previousGapsPositions = new ArrayList<Vector2>();
    }

    public void initializeGT(Vector2 startingPosition, ArrayList<Gap> detectedGaps) {
        Node start = new Node(startingPosition);
        previousGapsNumber = detectedGaps.size();
        for (Gap g : detectedGaps) {
            addGap(g, start);
            previousGapsPositions.add(g.getLocation());
        }
    }

    public void chaseGap(Gap gapToChase, ArrayList<Gap> detectedGaps) {
        agent.goTo(gapToChase.getOffsetLocation());
        //now we need to update detectedGaps after moving to offsetLocation
        agent.fi;
        int gapsDifference = previousGapsNumber - agent.getGapSensor().getGapList().size();
        for (int i = 0; i < detectedGaps.size(); i++) {
            if (checkIfNewGap(detectedGaps.get(i))) {
                if ()
            }
        }
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
        for (Vector2 V : previousGapsPositions) {
            if ( (Math.sqrt(P.x - V.x) + Math.sqrt(P.y - V.y)) <= Math.sqrt(slackVar)) {
                return false;
            }
        }
        Vector2 newGapLocation = new Vector2(P.x,P.y);
        previousGapsPositions.add(newGapLocation);
        return true;
    }

    public void Appearance(boolean T, boolean B, boolean L, boolean R, Gap gap) {
        if (T, !B, L, !R) {

        }
    }

    public void countSides(ArrayList<Gap> gaps) {
        for (Gap g : gaps) {

        }
    }


}

