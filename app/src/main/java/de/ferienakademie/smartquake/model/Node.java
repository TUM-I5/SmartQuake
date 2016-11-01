// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import de.ferienakademie.smartquake.managers.PreferenceReader;


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

    private List <List <Double>>  historyOfDisplacements;
    private List <double[]> historyOfGroundDisplacement;

    private final static double MASSLESS_RADIUS = 0.05;

    private boolean hinge = false;
    private double nodeMass = 0.0;

    private List<Beam> beams = new ArrayList<>();

    public Node(double x, double y) {
        this.initialX = x;
        this.initialY = y;
        displacements = new ArrayList<>();
        displacements.add(0.0);
        displacements.add(0.0);
        historyOfDisplacements = new ArrayList<>();
        historyOfGroundDisplacement = new ArrayList<>();
    }

    public Node(double x, double y, boolean hinged) {
        this(x, y);
        this.hinge = hinged;
    }

    public Node(double x, double y, double nodeMass) {
        this(x, y);
        this.nodeMass = nodeMass;
    }

    public Node(double x, double y, boolean hinged, double nodeMass) {
        this(x, y, hinged);
        this.nodeMass = nodeMass;
    }


    public Node(double x, double y, List<Integer> DOF) {
        this(x, y);
        this.DOF = DOF;
    }

    public List<List<Double>> getHistoryOfDisplacements() {
        return historyOfDisplacements;
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

    public void setInitialY(double initialY) {
        this.initialY = initialY;
    }

    public void setSingleDisplacement(int i, double value) {
        this.displacements.set(i,value );
    }

    public boolean[] getConstraints() {
        return constraint;
    }


    public double getSingleDisplacement(int i) {
        return this.displacements.get(i);
    }

    public double getDisplacementForDof(int i) {
        return this.displacements.get(DOF.indexOf(i));
    }

    public void addBeam(Beam beam) {
        beams.add(beam);
    }

    public List<Integer> getDOF() {
        return DOF;
    }

    public void setDOF(List<Integer> DOF) {
        this.DOF = DOF;
        for (int i = 0; i < DOF.size(); i++)
            displacements.add(0.0);
    }

    public double getCurrentX() {
        return initialX + displacements.get(0);
    }

    public float getCurrentXf() {
        return (float) (initialX + displacements.get(0));
    }

    public double getCurrentY() {
        return initialY + displacements.get(1);
    }

    public float getCurrentYf() {
        return (float) (initialY + displacements.get(1));
    }

    public double getNodeMass() {
        return nodeMass;
    }

    public void setNodeMass(double nodeMass) {
        this.nodeMass = nodeMass;
    }


    public double getRadius() {
        //Let's go into detail: normally it is like r² ~ A, so we take the sqrt.
        //Then some scaling b/c alone it would be probably too large. After that we simply add a logarithmic factor to reduce scaling even more.
        //And finally, we add the 0.05 which the zero mass had before. And that's it!
        return Math.log10(Math.sqrt(nodeMass) * .1 + 1) + 0.05;
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

        return node.initialX == initialX && node.initialY == initialY;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(getInitialX());
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getInitialY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (displacements != null ? displacements.hashCode() : 0);
        result = 31 * result + (DOF != null ? DOF.hashCode() : 0);
        temp = Double.doubleToLongBits(getRadius());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(constraint);
        result = 31 * result + (hinge ? 1 : 0);
        result = 31 * result + beams.hashCode();
        return result;
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


    public void saveTimeStepDisplacement() {
        List<Double> temp = new ArrayList<>();
        temp.addAll(displacements);
        historyOfDisplacements.add(temp);
    }



    public void resetHistory() {
        historyOfDisplacements.clear();
        historyOfGroundDisplacement.clear();
    }



    public void recallDisplacementOfStep(int i) {

        for (int j=0; j<displacements.size(); j++)
          displacements.set(j, historyOfDisplacements.get(i).get(j));

        // include ground displacements according to settings
        if (PreferenceReader.groundDisplacements()) {
            double[] groundDisplacements = historyOfGroundDisplacement.get(i);
            displacements.set(0, displacements.get(0) + groundDisplacements[0]);
            displacements.set(1, displacements.get(1) + groundDisplacements[1]);
        }
    }



    public int getLengthOfHistory() {
        return historyOfDisplacements.size();
    }



    public void saveTimeStepGroundDisplacement(double[] gD) {
        double[] groundDisplacements = new double[2];
        groundDisplacements[0] = gD[0];
        groundDisplacements[1] = gD[1];
        historyOfGroundDisplacement.add(groundDisplacements);
    }


}

