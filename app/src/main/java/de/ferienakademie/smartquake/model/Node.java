package de.ferienakademie.smartquake.model;

import java.util.List;

import de.ferienakademie.smartquake.model.Beam;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {

    private double x;
    private double y;


    private List<Integer>[] DOF; //Degrees of freedom



    private List<Double>[] u; //Displacement

    private double radius = 15;

    /**
     * List of ALL adjacent beams
     */
     //This seems obsolete. The structure is already defined by startNode and endNode of the Beam class and this list isn't needed for computation.
    private List<Beam> beams;



    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public Node(double x, double y, List<Beam> beams) {
        this.x = x;
        this.y = y;
        this.beams = beams;
    }

    public Node(double x, double y, List<Integer>[] DOF, List<Double>[] u, List<Beam> beams) {
        this.x = x;
        this.y = y;
        this.DOF = DOF;
        this.u = u;
        this.beams = beams;
    }


    public List<Double>[] getU() {
        return u;
    }

    public void setU(List<Double>[] u) {
        this.u = u;
    }

    public List<Integer>[] getDOF() {
        return DOF;
    }

    public void setDOF(List<Integer>[] DOF) {
        this.DOF = DOF;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    //Probably obsolete, look above.
    public List<Beam> getBeams() {
        return beams;
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
}
