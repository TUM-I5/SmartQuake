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
    private int[] conDOF ; //constraint dofs


    public Structure(List<Node> nodes,List<Beam> beams, int[] conDOF) {
        this.nodes = nodes;
        this.beams = beams;
        this.conDOF = conDOF;
    }

    public Structure() {
        this(new ArrayList<Node>(), new ArrayList<Beam>(), new int[] {});
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addNodes(List<Node> nodes) {
        this.nodes.addAll(nodes);
    }

    public void addNodes(Node... nodes) {
        Collections.addAll(this.nodes, nodes);
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }


    public int[] getConDOF() {
        return conDOF;
    }

    public void setConDOF(int[] conDOF) {
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
