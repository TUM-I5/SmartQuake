package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {

    //Initial node position
    private double initialX;
    private double initialY;

    private boolean[] constraint = new boolean[3];

    private List<Integer> DOF; //Degrees of freedom
    private List<Double> displacements; //List of all displacements at the node
    private double[] groundDisplacement = new double[2];

    private List <List <Double>>  historyOfDisplacements;

    private double radius = 0.1;



    private boolean hinge = false;

    private List<Beam> beams = new ArrayList<>();


    public Node(double x, double y) {
        this.initialX = x;
        this.initialY = y;
        displacements = new ArrayList<>();
        historyOfDisplacements = new ArrayList<>();
    }


    public Node(double x, double y, boolean hinged) {
        this(x, y);
        this.hinge = hinged;
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




    public void setSingleDisplacement(int i, double value) {
        this.displacements.set(i,value );

    }




    public double getSingleDisplacement(int i) {
        return this.displacements.get(i);
    }




    public double getDisplacementForDof(int i) {
        return this.displacements.get( DOF.indexOf(i) );
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
        for (int i=0; i<DOF.size(); i++)
            displacements.add(0.0);
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

    public void setSingleConstraint(int i, boolean constraint) {
        this.constraint[i] = constraint;
    }


    public void saveTimeStepData() {

        historyOfDisplacements.add(displacements);

    }




}

