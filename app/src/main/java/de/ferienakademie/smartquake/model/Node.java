package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {
    //Current node position
    //private double currentX;
    //private double currentY;

    //private List<Double> currentRotations; //List of all rotations at the node

    //Initial node position
    private double initialX;
    private double initialY;


    private List<Integer> DOF; //Degrees of freedom
    private List<Double> displacements; //List of all displacements at the node


    private double radius = 0.1;

    private List<Beam> beams = new ArrayList<>();

    public Node(double x, double y) {
        //this.currentX = x;
        //this.currentY = y;
        this.initialX = x;
        this.initialY = y;
        displacements = new ArrayList<>(6);
        //currentRotations.add(0.0);
    }


    public Node(double x, double y, List<Integer> DOF) {
        this(x, y);
        this.DOF = DOF;
        displacements = new ArrayList<>(6);

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

//    public List<Double> getCurrentRotations() {
//        return currentRotations;
//    }
//
//    public void setCurrentRotations(List<Double> currentRotations) {
//        this.currentRotations = currentRotations;
//    }
//
//    public void setSingleRotation(int i, double rotation) {
//        this.currentRotations.set(i,rotation );
//    }



    public void setSingleDisplacement(int i, double value) {
        this.displacements.set(i,value );
    }



    public double getSingleDisplacement(int i) {
        return this.displacements.get(i);
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
        return initialX + displacements.get(0);
    }



    public float getCurrentXf() {
        return (float)(initialX + displacements.get(0));
    }



    public double getCurrentY() {
        return initialY + displacements.get(1);
    }



    public float getCurrentYf() {
        return (float)(initialY + displacements.get(1));
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

        //return node.currentX == currentX && node.currentY == currentY;
        return node.initialX == initialX && node.initialY == initialY;
    }
}

