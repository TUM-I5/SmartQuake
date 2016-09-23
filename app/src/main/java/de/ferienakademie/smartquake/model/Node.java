package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {
    //Current node position
    private double currX;
    private double currY;
    //Initial node position
    private double initX;
    private double initY;
    private List<Integer> DOF; //Degrees of freedom

    private List<Double> u; //Displacement

    private double radius = 15;

    /**
     * List of ALL adjacent beams
     */
    //This seems obsolete. The structure is already defined by startNode and endNode of the Beam class and this list isn't needed for computation.
    private List<Beam> beams = new ArrayList<>();

    public Node(double x, double y) {
        this.currX = x;
        this.currY = y;
        this.initX = x;
        this.initY = y;
    }


    public Node(double x, double y, List<Integer> DOF, List<Double> u) {
        this.initX=x;
        this.initY=y;
        this.DOF = DOF;
        this.u = u;
    }


    public double getInitX() {
        return initX;
    }

    public void setInitX(double initX) {
        this.initX = initX;
    }

    public double getInitY() {
        return initY;
    }

    public void setInitY(double initY) {
        this.initY = initY;
    }

    public List<Double> getU() {
        return u;
    }

    public void setU(List<Double> u) {
        this.u = u;
    }

    public List<Integer> getDOF() {
        return DOF;
    }

    public void setDOF(List<Integer> DOF) {
        this.DOF = DOF;
    }

    public double getCurrX() {
        return currX;
    }

    public void setCurrX(double currX) {
        this.currX = currX;
    }

    public double getCurrY() {
        return currY;
    }

    public void setCurrY(double currY) {
        this.currY = currY;
    }



    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    //Probably obsolete, look above.
    public void addBeam(Beam beam) {
        beams.add(beam);
    }

    //Probably obsolete, look above.
    public void clearBeams() {
        beams.clear();
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Node)) return false;

        Node node = (Node) obj;

        return node.currX == currX && node.currY == currY;
    }
}
