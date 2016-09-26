package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {
    //Current node position
    private double currentX;
    private double currentY;

    private List<Double> currentRotations; //List of all rotations at the node

    //Initial node position
    private double initialX;
    private double initialY;
    private List<Integer> DOF; //Degrees of freedom

    private double radius = 0.1;

    private boolean[] constraint = new boolean[3];



    private boolean hinge = false;

    private List<Beam> beams = new ArrayList<>();

    public Node(double x, double y) {
        this.currentX = x;
        this.currentY = y;
        this.initialX = x;
        this.initialY = y;
        currentRotations = new ArrayList<>();
    }


    public Node(double x, double y, List<Integer> DOF) {
        this(x, y);
        this.DOF = DOF;
    }


    public double getInitialX() {
        return initialX;
    }

    public void setInitialX(double initialX) {
        this.initialX = initialX;
    }

    public double getInitialY() {
        return initialY;
    }

    public List<Double> getCurrentRotations() {
        return currentRotations;
    }

    public void setRotations(List<Double> currentRotations) {
        this.currentRotations = currentRotations;
    }

    public void setSingleRotation(int i, double rotation) {
        this.currentRotations.set(i,rotation );
    }



    public void setInitialY(double initialY) {
        this.initialY = initialY;
    }

    public void addBeam(Beam beam) {
        beams.add(beam);
    }
    public List<Integer> getDOF() {
        return DOF;
    }

    public void setDOF(List<Integer> DOF) {
        this.DOF = DOF;
    }

    public double getCurrentX() {
        return currentX;
    }

    public float getCurrentXf() {
        return (float) currentX;
    }

    public void setCurrentX(double currentX) {
        this.currentX = currentX;
    }

    public double getCurrentY() {
        return currentY;
    }

    public float getCurrentYf() {
        return (float) currentY;
    }

    public void setCurrentY(double currentY) {
        this.currentY = currentY;
    }


    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void clearBeams() {
        beams.clear();
    }

    public List<Beam> getBeams() {
        return beams;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Node)) return false;

        Node node = (Node) obj;

        return node.currentX == currentX && node.currentY == currentY;
    }

    public boolean isHinge() {
        return hinge;
    }

    public void setHinge(boolean hinge) {
        this.hinge = hinge;
    }

    public boolean getConstraint(int i) {
        return constraint[i];
    }

    public void setConstraint(boolean[] constraint) {
        this.constraint = constraint;
    }
}

