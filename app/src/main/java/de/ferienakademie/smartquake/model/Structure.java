package de.ferienakademie.smartquake.model;

import java.util.Collections;
import java.util.List;

/**
 * Class for the whole structure.
 */
public class Structure {
    private List<Node> nodes;
    private List<Beam> beams;

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

    public void initMatrices(this.nodes,this.beams){
        public void calcStiffnessMatrix();
        public void calcMassMatrix();
    };

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
