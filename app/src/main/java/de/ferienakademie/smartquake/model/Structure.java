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
import java.util.Collections;
import java.util.List;
import de.ferienakademie.smartquake.managers.PreferenceReader;

/**
 * Class for the whole structure.
 */
public class Structure {

    private int numberOfDOF;
    private List<Node> nodes;
    private List<Beam> beams;

    // list of the constained dofs, set in the function StructureFactory.enumerateDOFs()
    private List<Integer> conDOF = new ArrayList<>();

    public double[] getBoundingBox() {
        return boundingBox;
    }


    // X, Y
    private double[] modelSize = {8, 8};
    // left/right X, top/bottom Y //TODO which one will be used?
    private double[] boundingBox = new double[4];

    private boolean lumped = false;  // default value!

    public Structure(List<Node> nodes, List<Beam> beams) {
        this.nodes = nodes;
        this.beams = beams;
    }

    public Structure(List<Node> nodes, List<Beam> beams, List<Integer> conDOF) {
        this.nodes = nodes;
        this.beams = beams;
        this.conDOF = conDOF;
        lumped = PreferenceReader.massMatrices();
    }

    public Structure() {
        this(new ArrayList<Node>(), new ArrayList<Beam>(), new ArrayList<Integer>());
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNodes(List<Node> nodes) {
        for (Node n: nodes) {
            this.addNode(n);
        }
    }

    public void addNodes(Node... nodes) {
        for (Node n: nodes) {
            this.addNode(n);
        }
    }

    public void addNode(Node node) {
        if (this.nodes.isEmpty()) {
            boundingBox[0] = node.getInitialX();
            boundingBox[1] = node.getInitialX();
            boundingBox[2] = node.getInitialY();
            boundingBox[3] = node.getInitialY();
        } else {
            if (node.getInitialX() < boundingBox[0]) {
                boundingBox[0] = node.getInitialX();
            }
            if (node.getInitialX() > boundingBox[1]) {
                boundingBox[1] = node.getInitialX();
            }
            if (node.getInitialY() < boundingBox[2]) {
                boundingBox[2] = node.getInitialY();
            }
            if (node.getInitialY() > boundingBox[3]) {
                boundingBox[3] = node.getInitialY();
            }
        }
        this.nodes.add(node);
    }



    public void resetHistoryOfNodes() {
        for (Node in : getNodes()) {
            in.resetHistory();
        }
    }



    public void resetBeams() {
        for (Beam in : getBeams()) {
            in.resetBeam();
        }
    }



    public void recallDisplacementOfStep(int i) {
        for (Node in : getNodes()) {
            in.recallDisplacementOfStep(i);
        }
    }




    public List<Integer> getConDOF() {
        return conDOF;
    }

    public void setConDOF(List<Integer> conDOF) {
        this.conDOF = conDOF;
    }


    public void addSingleConDOF(int conDOF) {
        this.conDOF.add(conDOF);
    }


    public void addBeams(List<Beam> beams) {
        this.beams.addAll(beams);
    }

    public void addBeams(Beam... beams) {
        Collections.addAll(this.beams, beams);
    }

    public void addBeam(Beam beam) {
        this.beams.add(beam);
    }
    
    public List<Beam> getBeams() {
        return beams;
    }

    public void clearAll() {
        nodes.clear();
        beams.clear();
    }
    public boolean isLumped() {
        return lumped;
    }

    public void setLumped(boolean lumped) {
        this.lumped = lumped;
    }

    public int getNumberOfDOF() {
        return numberOfDOF;
    }

    public void setNumberOfDOF(int numberOfDOF) {
        this.numberOfDOF = numberOfDOF;
    }
}
