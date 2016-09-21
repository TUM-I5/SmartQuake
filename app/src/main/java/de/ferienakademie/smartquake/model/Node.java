package de.ferienakademie.smartquake.model;

import java.util.List;

import de.ferienakademie.smartquake.model.Beam;

/**
 * Created by yuriy on 21/09/16.
 */
public class Node {

    private double x;
    private double y;

    private double displacementX = 0;
    private double displacementY = 0;

    private double radius = 15;

    /**
     * List of ALL adjacent beams
     */
    private List<Beam> beams;

    public Node(double x, double y, List<Beam> beams) {
        this.x = x;
        this.y = y;
        this.beams = beams;
    }

    public Node(double x, double y, double displacementX, double displacementY, List<Beam> beams) {
        this.x = x;
        this.y = y;
        this.displacementX = displacementX;
        this.displacementY = displacementY;
        this.beams = beams;
    }

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
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

    public double getDisplacementX() {
        return displacementX;
    }

    public void setDisplacementX(double displacementX) {
        this.displacementX = displacementX;
    }

    public double getDisplacementY() {
        return displacementY;
    }

    public void setDisplacementY(double displacementY) {
        this.displacementY = displacementY;
    }

    public List<Beam> getBeams() {
        return beams;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void addBeam(Beam beam) {
        beams.add(beam);
    }

    public void clearBeams() {
        beams.clear();
    }
}
