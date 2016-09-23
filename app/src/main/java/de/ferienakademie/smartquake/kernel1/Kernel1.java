package de.ferienakademie.smartquake.kernel1;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.List;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.model.Material;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.model.Structure;

/**
 * Created by alex on 22.09.16.
 */
public class Kernel1 {

    private DenseMatrix64F StiffnessMatrix;
    private DenseMatrix64F DampingMatrix;
    private DenseMatrix64F MassMatrix;

    private DenseMatrix64F LoadVector; // vector with the forces.
    private DenseMatrix64F DisplacementVector;  //project manager advic
    private List<Integer> conDOF ; //constraint dofs



    private List<Integer> DOF ;

    private int numDOF;
    private Material material;

    Structure structure;
    AccelerationProvider accelerationProvider;

    public Kernel1(Structure structure, AccelerationProvider accelerationProvider) {
        this.structure = structure;
        this.accelerationProvider = accelerationProvider;



        //initialize displacement with zeros
        DisplacementVector = new DenseMatrix64F(numDOF, 1);
        DisplacementVector.zero();

        initMatrices();

    }

    /**
     * Calculate the stiffness, mass and damping matrices.
     */
    public void initMatrices() {
        StiffnessMatrix = new DenseMatrix64F(numDOF, numDOF);
        MassMatrix = new DenseMatrix64F(numDOF, numDOF);
        DampingMatrix = new DenseMatrix64F(numDOF, numDOF);


        StiffnessMatrix.zero();
        MassMatrix.zero();
        DampingMatrix.zero();

        calcDampingMatrix();
        calcMassMatrix();
        calcStiffnessMatrix();
    }

    public void calcStiffnessMatrix() {
        for (int i = 0; i < numDOF - conDOF.size(); i++) {
            StiffnessMatrix.add(i, i, 1);

        }
        for (int e = 0; e < structure.getBeams().size(); e++) {

            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                //Todo assemble with Id matrix
                  StiffnessMatrix.print();
                }
            }
        }
    }

    public void calcMassMatrix() {
        for (int i = 0; i < numDOF - conDOF.size(); i++) {
            MassMatrix.add(i, i, 1);
        }
    }

    public void calcDampingMatrix() {
        for (int i = 0; i < numDOF - conDOF.size(); i++) {
            DampingMatrix.add(i, i, 1);
        }
    }

    public DenseMatrix64F getDisplacementVector() {
        return DisplacementVector;
    }

    public int getNumDOF() {
        return numDOF;
    }

    public List<Integer> getConDOF() {
        return conDOF;
    }

    public void setConDOF(List<Integer> conDOF) {
        this.conDOF = conDOF;
    }

    public Material getMaterial() {
        return material;
    }

    public List<Integer> getDOF() {
        return DOF;
    }

    public void setDOF(List<Integer> DOF) {
        this.DOF = DOF;
    }

    public void addDof(List<Integer> DOF) {
        this.DOF.addAll(DOF);
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public AccelerationProvider getAccelerationProvider() {
        return accelerationProvider;
    }

    public void setAccelerationProvider(AccelerationProvider accelerationProvider) {
        this.accelerationProvider = accelerationProvider;
    }

    /**
     * Update {@link Structure} that is displayed using values computed by {@link de.ferienakademie.smartquake.kernel2.TimeIntegration}
     * @param displacementVector a (3 * number of nodes) x 1 matrix. Three consequent values contain displacements in x, y, z direction.
     */
    public void updateStructure(DenseMatrix64F displacementVector) {
        for (int i = 0; i < structure.getNodes().size(); i++) {
            Node node = structure.getNodes().get(i);
            node.setCurrX(node.getInitX() + displacementVector.get(3*i, 0));
            node.setCurrY(node.getInitY() + displacementVector.get(3*i+1, 0));
        }
    }


    public DenseMatrix64F getLoadVector() {
        return LoadVector;
    }

    public void setLoadVector(DenseMatrix64F loadVector) {
        LoadVector = loadVector;
    }

    public void updateLoadVector() {
        double[] acceleration = accelerationProvider.getAcceleration();
        updateLoadVector(acceleration);
    }

    /**
     * Update the vector with forces using the acceleration values received from the {@link AccelerationProvider}
     * @param acceleration - view {@link AccelerationProvider} for details
     */
    void updateLoadVector(double[] acceleration) {
        //TODO: update load vector using acceleration values.
    }
}
