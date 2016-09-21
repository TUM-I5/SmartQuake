package de.ferienakademie.smartquake.model;

import java.util.Collections;
import java.util.List;

import org.ejml.data.*;

/**
 * Class for the whole structure.
 */
public class Structure {
    private List<Node>         nodes;
    private List<Beam>         beams;
    private DenseMatrix64F     StiffnessMatrix;
    private DenseMatrix64F     DampingMatrix;
    private DenseMatrix64F     MassMatrix;
    private DenseMatrix64F     LoadVector;
    private int numDOF;

    public int[] getConDOF() {
        return conDOF;
    }

    public void setConDOF(int[] conDOF) {
        this.conDOF = conDOF;
    }

    private int[] conDOF ; //constraint dofs
    private int numconDOF= conDOF.length; //

    public void Structure(List<Node> nodes,List<Beam> beams){
        this.nodes= nodes;
        this.beams= beams;
        this.numDOF = 3*nodes.size();
        initMatrices();
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
        for (int i = 0; i < numDOF-numconDOF; i++) {
            StiffnessMatrix.add(i,i,1);
        }
    };
    public void calcMassMatrix(){
        for (int i = 0; i < numDOF-numconDOF; i++) {
            MassMatrix.add(i,i,1);
        }
    };
    public void calcDampingMatrix(){
        for (int i = 0; i < numDOF-numconDOF; i++) {
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
