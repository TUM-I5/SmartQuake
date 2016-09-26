package de.ferienakademie.smartquake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for the whole structure.
 */
public class Structure {

    private List<Node> nodes;
    private List<Beam> beams;
    // TODO: somebody plz initialize this array conDOF
    private List<Integer> conDOF ; //constraint dofs TODO: But how's the data structure?

    public double[] getBoundingBox() {
        return boundingBox;
    }

    // left/right X, top/bottom Y
    private double[] boundingBox = new double[4];

    private boolean lumped = true;  // default value!

    public boolean isLumped() {
        return lumped;
    }

    public void setLumped(boolean lumped) {
        this.lumped = lumped;
    }

    public Structure(List<Node> nodes, List<Beam> beams, List<Integer> conDOF) {
        this.nodes = nodes;
        this.beams = beams;
        this.conDOF = conDOF;
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


    public List<Integer> getConDOF() {
        return conDOF;
    }

    public void setConDOF(List<Integer> conDOF) {
        this.conDOF = conDOF;
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
}
