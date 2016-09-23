package de.ferienakademie.smartquake.model;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {
    //Current node position

    //Initial node position
    private double initX;
    private double initY;
    private List<Integer> DOF; //Degrees of freedom

    private List<Double> u; //Displacement (current)

    private double radius = 15;




    public Node(double x, double y) {
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


    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;

    }
}
