package de.ferienakademie.smartquake.model;

import java.lang.reflect.Array;
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
}
