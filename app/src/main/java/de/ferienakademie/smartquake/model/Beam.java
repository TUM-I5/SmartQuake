package de.ferienakademie.smartquake.model;

import android.util.Pair;

/**
 * Created by yuriy on 21/09/16.
 */

public class Beam {

    private Node startNode;
    private Pair<Float, Float> oneThirdPoint;
    private Pair<Float, Float> twoThirdPoint;
    private Node endNode;

    private float thickness;


    public Pair<Float, Float> getOneThirdPoint() {
        return oneThirdPoint;
    }

    public Pair<Float, Float> getTwoThirdPoint() {
        return twoThirdPoint;
    }

    public Beam(Node startNode, Node endNode, float thickness) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.thickness = thickness;

        double startX = startNode.getCurrX();
        double startY = startNode.getCurrY();
        double endX = endNode.getCurrX();
        double endY = endNode.getCurrY();

        // additional displacement is just to see something happening for now
        this.oneThirdPoint = new Pair<>((float) ((2 * startX + endX) / 3 + 50), (float) ((2 * startY + endY) / 3 - 50));
        this.twoThirdPoint = new Pair<>((float) ((startX + 2 * endX) / 3 + 50), (float) ((startY + 2 * endY) / 3 + 50));
    }

    public Beam(Node startNode, Node endNode, float thickness, Material material) {
        this(startNode, endNode, thickness);
    }

    public Beam(Node startNode, Node endNode) {
        this(startNode, endNode, 10);
    }

    public Beam(double startX, double startY, double endX, double endY) {
        this(new Node(startX, startY), new Node(endX, endY));
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

}
