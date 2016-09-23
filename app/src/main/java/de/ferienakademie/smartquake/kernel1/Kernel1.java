package de.ferienakademie.smartquake.kernel1;

import android.util.Log;

import org.ejml.data.DenseMatrix64F;

import java.util.List;

import de.ferienakademie.smartquake.excitation.AccelerationProvider;
import de.ferienakademie.smartquake.model.Beam;
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

    private int numDOF;
    private Material material;

    Structure structure;
    AccelerationProvider accelerationProvider;

    public Kernel1(Structure structure, AccelerationProvider accelerationProvider) {
        this.structure = structure;
        this.accelerationProvider = accelerationProvider;
        //initialize displacement with zeros
        DisplacementVector = new DenseMatrix64F(getNumDOF(), 1);
        DisplacementVector.zero();
        initMatrices();
    }

    /**
     * return StiffnessMatrix
     */
    public DenseMatrix64F getStiffnessMatrix(){
        return StiffnessMatrix;
    }

    /**
     * return DampingMatrix
     */
    public DenseMatrix64F getDampingMatrix(){
        return DampingMatrix;
    }

    /**
     * return MassMatrix
     */
    public DenseMatrix64F getMassMatrix(){
        return MassMatrix;
    }

    /**
     * Calculate the stiffness, mass and damping matrices.
     */
    public void initMatrices() {
        StiffnessMatrix = new DenseMatrix64F(getNumDOF(), getNumDOF());
        MassMatrix = new DenseMatrix64F(getNumDOF(), getNumDOF());
        DampingMatrix = new DenseMatrix64F(getNumDOF(), getNumDOF());


        StiffnessMatrix.zero();
        MassMatrix.zero();
        DampingMatrix.zero();

        calcDampingMatrix();
        calcMassMatrix();
        calcStiffnessMatrix();
    }

    public void calcStiffnessMatrix() {
        for (int e = 0; e < structure.getBeams().size(); e++) {
            Beam beam = structure.getBeams().get(e);
            int[] dofs = beam.getDofs();
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    StiffnessMatrix.set(dofs[i], dofs[j], beam.getEleStiffnessMatrix().get(i, j));
                }
            }
        }
        Log.e("test", "test");
    }

    public void calcMassMatrix() {
    }

    public void calcDampingMatrix() {
    }

    public DenseMatrix64F getDisplacementVector() {
        return DisplacementVector;
    }

    public int getNumDOF() {
        //TODO: temporary solution. Changes if we add hinges.
        return structure.getNodes().size() * 3;
    }

    public Material getMaterial() {
        return material;
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
