package de.ferienakademie.smartquake.model;

import org.ejml.data.DenseMatrix64F;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for the whole structure.
 */
public class Structure {
    private DenseMatrix64F     StiffnessMatrix;
    private DenseMatrix64F     DampingMatrix;
    private DenseMatrix64F     MassMatrix;
    private DenseMatrix64F     LoadVector;
    private int numDOF;
    private List<Node> nodes;
    private List<Beam> beams;

    public int[] getConDOF() {
        return conDOF;
    }

    public void setConDOF(int[] conDOF) {
        this.conDOF = conDOF;
    }

    private int[] conDOF ; //constraint dofs

    public Structure(List<Node> nodes,List<Beam> beams) {
        this.nodes = nodes;
        this.beams = beams;
        this.numDOF = 3 * nodes.size();
        initMatrices();
    }

    public Structure() {
        this(new ArrayList<Node>(), new ArrayList<Beam>());
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

    public void initMatrices(){
        calcDampingMatrix();
        calcMassMatrix();
        calcStiffnessMatrix();
    };

    public void calcStiffnessMatrix(){
        for (int i = 0; i < numDOF-conDOF.length; i++) {
            StiffnessMatrix.add(i,i,1);
        }
    };
    public void calcMassMatrix(){
        for (int i = 0; i < numDOF-conDOF.length; i++) {
            MassMatrix.add(i,i,1);
        }
    };
    public void calcDampingMatrix(){
        for (int i = 0; i < numDOF-conDOF.length; i++) {
            DampingMatrix.add(i,i,1);
        }
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
